/**
 * EasySOA Registry
 * Copyright 2012 Open Wide
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
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
        int serviceWhithoutImplementationNb = 0;
        int servicesCount = computedIndicators.get(SERVICE_DOCTYPE_INDICATOR).getCount();
        //HashSet<String> serviceWhithoutImplementationIdSet = new HashSet<String>(servicesCount);
        int serviceWithImplementationWhithoutEndpointNb = 0;
        int serviceWithImplementationWhithoutProductionEndpointNb = 0;
        int serviceWhithoutComponentNb = 0;
        for (DocumentModel service : serviceList) {

            // Find component
        	String componentId = (String) service.getPropertyValue("acomp:componentId");
        	//DocumentModel component = null;
        	if (componentId == null || componentId.isEmpty()) {
        		//component = session.getDocument(new IdRef(componentId));
                serviceWhithoutComponentNb++;
        	}

            // finding (all) its implems and then their endpoints
            List<DocumentModel> serviceImpls = documentService.query(session, DocumentService.NXQL_SELECT_FROM
            		+ ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
                    + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + "='" + service.getId() + "'"
                    + DocumentService.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + "<>'true'", true, false); // WARNING 'true' doesn't work in junit
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationNb++;
                //serviceWhithoutImplementationIdSet.add(service.getId());
            } else {
                boolean isAServiceWithImplementationWhithoutEndpoint = true;
                for (DocumentModel serviceImplModel : serviceImpls) {
                    ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);

                    DocumentModel productionEndpoint = documentService.getEndpointOfImplementation(serviceImplModel, Endpoint.ENV_PRODUCTION, subprojectId);
                    if (!serviceImpl.isMock() && productionEndpoint == null) {
                        serviceWithImplementationWhithoutProductionEndpointNb++; // at most one production endpoint (else should pass through boolean)
                    }

                    List<DocumentModel> endpoints = documentService.getEndpointsOfImplementation(serviceImplModel, subprojectId);
                    if (!serviceImpl.isMock() && (endpoints == null || endpoints.isEmpty())) {
                        isAServiceWithImplementationWhithoutEndpoint = false;
                    }
                }
                if (isAServiceWithImplementationWhithoutEndpoint) {
                    serviceWithImplementationWhithoutEndpointNb++;
                }
            }
        }

        // Indicators results registration
        indicators.put("serviceWhithoutComponent",
                new IndicatorValue("", "", serviceWhithoutComponentNb,
                        (servicesCount > 0) ? (100 * serviceWhithoutComponentNb / servicesCount) : 0));//TODO

        // TODO "main" vs "test" implementation
        indicators.put("serviceWhithoutImplementation",
                new IndicatorValue("", "", serviceWhithoutImplementationNb,
                        (servicesCount > 0) ? (100 * serviceWhithoutImplementationNb / servicesCount) : 0));

        // TODO "test", "integration", "staging" ("design", "dev")
        indicators.put("serviceWithImplementationWhithoutEndpoint",
                new IndicatorValue("", "", serviceWithImplementationWhithoutEndpointNb,
                        (servicesCount - serviceWhithoutImplementationNb > 0) ? (100 * serviceWithImplementationWhithoutEndpointNb / (servicesCount - serviceWhithoutImplementationNb)) : 0));

        indicators.put("serviceWithImplementationWhithoutProductionEndpoint",
                new IndicatorValue("", "", serviceWithImplementationWhithoutProductionEndpointNb,
                        (servicesCount - serviceWhithoutImplementationNb > 0) ? (100 * serviceWithImplementationWhithoutProductionEndpointNb / (servicesCount - serviceWhithoutImplementationNb)) : 0));

        indicators.put("serviceWhithoutEndpoint",
                new IndicatorValue("", "", serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb,
                        (servicesCount > 0) ? (100 * (serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb) / servicesCount) : 0));

        indicators.put("serviceWhithoutProductionEndpoint",
                new IndicatorValue("", "", serviceWhithoutImplementationNb + serviceWithImplementationWhithoutProductionEndpointNb,
                        (servicesCount > 0) ? (100 * (serviceWhithoutImplementationNb + serviceWithImplementationWhithoutProductionEndpointNb) / servicesCount) : 0));

        return indicators;
    }

}
