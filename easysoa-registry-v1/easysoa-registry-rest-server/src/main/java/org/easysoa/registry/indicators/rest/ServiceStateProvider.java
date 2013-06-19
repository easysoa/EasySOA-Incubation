/**
 * EasySOA Registry
 * Copyright 2012-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.indicators.rest;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.matching.MatchingHelper;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.utils.ContextVisibility;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

// XXX Outdated (Relied on the Service doctype)
public class ServiceStateProvider implements IndicatorProvider {

    //private static final String SERVICE_DOCTYPE_INDICATOR = DoctypeCountProvider.getName(InformationService.DOCTYPE);
    private static final String SERVICE_DOCTYPE_INDICATOR = "Nombre de " + InformationService.DOCTYPE;

    private String category;

    ServiceStateProvider(String category){
        if(category == null){
            this.category = "";
        } else {
            this.category = category;
        }
    }

    @Override
    public List<String> getRequiredIndicators() {
        return Arrays.asList(SERVICE_DOCTYPE_INDICATOR);
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);

        String subprojectCriteria;
        if (subprojectId == null) {
            subprojectCriteria = "";
        } else {
            if(ContextVisibility.STRICT.getValue().equals(visibility)){
                subprojectCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);
            } else {
                subprojectCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(SubprojectServiceImpl.getSubprojectById(session, subprojectId));
            }
        }

        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        DocumentModelList serviceList = documentService.query(session, DocumentService.NXQL_SELECT_FROM
                + InformationService.DOCTYPE + subprojectCriteria, true, false);

        // Count indicators - Service-specific
        int servicesCount = computedIndicators.get(SERVICE_DOCTYPE_INDICATOR).getCount();
        int serviceWithoutActorNb = 0;
        int serviceWithoutInterfaceNb = 0;
        int serviceWithoutComponentNb = 0;
        int serviceWithMockImplementationNb = 0;
        int serviceWithTestedImplementationNb = 0;
        int serviceWithoutImplementationNb = 0;
        int serviceWithImplementationWhithoutEndpointNb = 0;
        int serviceWithImplementationWhithoutProductionEndpointNb = 0;
        
        // TODO business services sans interface (InformationService) & in PhaseProgress indicator & governanceIndicators page
        
        for (DocumentModel service : serviceList) {

        	// Specified services
        	String servicePhase = SubprojectServiceImpl.parseSubprojectId(
        			SubprojectServiceImpl.getSubprojectId(service)).getSubprojectName();
        	if (Subproject.SPECIFICATIONS_SUBPROJECT_NAME.equals(servicePhase)) {
	            // Find component
	        	String componentId = (String) service.getPropertyValue(InformationService.XPATH_COMPONENT_ID);
	        	//DocumentModel component = null;
	        	if (componentId == null || componentId.isEmpty()) {
	        		//component = session.getDocument(new IdRef(componentId));
	                serviceWithoutComponentNb++;
	        	}
	
	            // Find actor
	        	String providerActorId = (String) service.getPropertyValue(InformationService.XPATH_PROVIDER_ACTOR);
	        	if (providerActorId == null || providerActorId.isEmpty()) {
	                serviceWithoutActorNb++;
	        	}
        	}
        	
        	// Specified and non-specified (technical) services
        	if (MatchingHelper.isWsdlInfo(service) && MatchingHelper.isRestInfo(service)) {
        		serviceWithoutInterfaceNb++;
        	}

            // finding (all) its implems and then their endpoints
            List<DocumentModel> serviceImpls = documentService.query(session, DocumentService.NXQL_SELECT_FROM
            		+ ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
                    + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + "='" + service.getId() + "'"
                    + DocumentService.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + "<>'true'", true, false); // WARNING 'true' doesn't work in junit
            if (serviceImpls.isEmpty()) {
                serviceWithoutImplementationNb++;
                //serviceWhithoutImplementationIdSet.add(service.getId());
            } else {
                boolean isAServiceWithImplementationWhithoutEndpoint = true;
                boolean isAServiceWithMockImplementation = false;
                boolean isAServiceWithTestedImplementation = false;
                for (DocumentModel serviceImplModel : serviceImpls) {
                    ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);

                    DocumentModel productionEndpoint = documentService.getEndpointOfImplementation(serviceImplModel, Endpoint.ENV_PRODUCTION, subprojectId);
                    if (serviceImpl.isMock()) {
                    	isAServiceWithMockImplementation = true;
                    } else {
                    	Serializable[] tests = (Serializable[]) serviceImplModel.getPropertyValue(ServiceImplementation.XPATH_TESTS);
                    	if (tests.length != 0) {
                    		isAServiceWithTestedImplementation = true;
                    	}
                    	if (productionEndpoint == null) {
                            serviceWithImplementationWhithoutProductionEndpointNb++; // at most one production endpoint (else should pass through boolean)
                    	}
                    }

                    List<DocumentModel> endpoints = documentService.getEndpointsOfImplementation(serviceImplModel, subprojectId);
                    if (!serviceImpl.isMock() && (endpoints == null || endpoints.isEmpty())) {
                        isAServiceWithImplementationWhithoutEndpoint = false;
                    }
                }
                if (isAServiceWithImplementationWhithoutEndpoint) {
                    serviceWithImplementationWhithoutEndpointNb++;
                }
                if (isAServiceWithMockImplementation) {
                	serviceWithMockImplementationNb++;
                }
                if (isAServiceWithTestedImplementation) {
                	serviceWithTestedImplementationNb++;
                }
            }
        }

        // Indicators results registration
        indicators.put("serviceWithoutActor",
                new IndicatorValue("", category, serviceWithoutActorNb,
                        (servicesCount > 0) ? (100 * serviceWithoutActorNb / servicesCount) : 0));
        indicators.put("serviceWithoutInterface",
                new IndicatorValue("", category, serviceWithoutInterfaceNb,
                        (servicesCount > 0) ? (100 * serviceWithoutInterfaceNb / servicesCount) : 0));
        indicators.put("serviceWithoutComponent",
                new IndicatorValue("", category, serviceWithoutComponentNb,
                        (servicesCount > 0) ? (100 * serviceWithoutComponentNb / servicesCount) : 0));

        // TODO "main" vs "test" implementation
        indicators.put("serviceWithoutImplementation",
                new IndicatorValue("", category, serviceWithoutImplementationNb,
                        (servicesCount > 0) ? (100 * serviceWithoutImplementationNb / servicesCount) : 0));
        int serviceWithImplementationNb = servicesCount - serviceWithoutImplementationNb;
        indicators.put("serviceWithImplementation",
                new IndicatorValue("", category, serviceWithImplementationNb,
                        (servicesCount > 0) ? (100 * serviceWithImplementationNb / servicesCount) : 0)); // for governance completion

        indicators.put("serviceWithMockImplementation",
                new IndicatorValue("", category, serviceWithMockImplementationNb,
                        (servicesCount > 0) ? (100 * serviceWithMockImplementationNb / servicesCount) : 0)); // for governance completion
        indicators.put("serviceWithTestedImplementation",
                new IndicatorValue("", category, serviceWithTestedImplementationNb,
                        (servicesCount > 0) ? (100 * serviceWithTestedImplementationNb / servicesCount) : 0)); // for governance completion
        
        // TODO "test", "integration", "staging" ("design", "dev")
        indicators.put("serviceWithImplementationWhithoutEndpoint",
                new IndicatorValue("", category, serviceWithImplementationWhithoutEndpointNb,
                        (servicesCount - serviceWithoutImplementationNb > 0) ? (100 * serviceWithImplementationWhithoutEndpointNb / serviceWithImplementationNb) : 0));

        indicators.put("serviceWithImplementationWhithoutProductionEndpoint",
                new IndicatorValue("", category, serviceWithImplementationWhithoutProductionEndpointNb,
                        (servicesCount - serviceWithoutImplementationNb > 0) ? (100 * serviceWithImplementationWhithoutProductionEndpointNb / serviceWithImplementationNb) : 0));

        int serviceWhithoutEndpointNb = serviceWithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb;
        indicators.put("serviceWithoutEndpoint",
                new IndicatorValue("", category, serviceWhithoutEndpointNb,
                        (servicesCount > 0) ? (100 * serviceWhithoutEndpointNb / servicesCount) : 0));
        int serviceWithEndpointNb = servicesCount - serviceWhithoutEndpointNb;
        indicators.put("serviceWithEndpoint",
                new IndicatorValue("", category, serviceWithEndpointNb,
                        (servicesCount > 0) ? (100 * serviceWithEndpointNb / servicesCount) : 0)); // for governance completion

        int serviceWhithoutProductionEndpointNb = serviceWithoutImplementationNb + serviceWithImplementationWhithoutProductionEndpointNb;
        indicators.put("serviceWithoutProductionEndpoint",
                new IndicatorValue("", category, serviceWhithoutProductionEndpointNb,
                        (servicesCount > 0) ? (100 * serviceWhithoutProductionEndpointNb / servicesCount) : 0));
        int serviceWithProductionEndpointNb = servicesCount - serviceWhithoutProductionEndpointNb;
        indicators.put("serviceWithProductionEndpoint",
                new IndicatorValue("", category, serviceWithProductionEndpointNb,
                        (servicesCount > 0) ? (100 * serviceWithProductionEndpointNb / servicesCount) : 0)); // for governance completion

        return indicators;
    }

}
