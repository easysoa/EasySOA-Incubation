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
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.easysoa.registry.wsdl.WsdlBlob;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.runtime.api.Framework;

/**
 * Computes various indicators on services implementations, including doc quality
 *
 * @author mkalam-alami; mdutoo
 */
public class ServiceImplStateProvider extends IndicatorProviderBase {

    //private static final String SERVICEIMPL_DOCTYPE_INDICATOR = DoctypeCountProvider.getName(ServiceImplementation.DOCTYPE);

    // TODO : Add a key and a name in indicators
    private static final String SERVICEIMPL_DOCTYPE_INDICATOR = ServiceImplementation.DOCTYPE;
    private static final String SERVICE_DOCTYPE_INDICATOR = InformationService.DOCTYPE;

    /**
     *
     * @param category
     */
    public ServiceImplStateProvider(String category){
    	super(category);
    }

    @Override
    public List<String> getRequiredIndicators() {
        return Arrays.asList(SERVICEIMPL_DOCTYPE_INDICATOR);
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        DocumentService documentService = Framework.getService(DocumentService.class);

        String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);

        // Count indicators - ServiceImplementation-specific
        final int IDEAL_DOCUMENTATION_LINES = 40, DOCUMENTATION_LINES_TOLERANCE = 20;
        int undocumentedServiceImpls = 0, documentationLines = 0;
        long serviceImplCount = computedIndicators.get(SERVICEIMPL_DOCTYPE_INDICATOR).getCount();
        long serviceCount = computedIndicators.get(SERVICE_DOCTYPE_INDICATOR).getCount();
        int serviceImplsDocQuality = 0;
        Map<Serializable, Boolean> hasMock = new HashMap<Serializable, Boolean>();
        int mockedImplsCount = 0, testedImplsCount = 0, nonMockImplsCount = 0;

        // % de services documentés
        int serviceWithDocumentation = this.computeServiceIndicators(session, indicators, subprojectCriteria);

        // Get service impls
        DocumentModelList serviceImplModels = session.query(DocumentService.NXQL_SELECT_FROM
                + ServiceImplementation.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY + subprojectCriteria);

        for (DocumentModel serviceImplModel : serviceImplModels) {
            ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
            if (!serviceImpl.isPlaceholder()) {

                // Documentation info
                String documentation = (String) serviceImpl.getProperty(ServiceImplementation.XPATH_DOCUMENTATION);
                if (documentation != null && !documentation.isEmpty()) {
                    documentationLines += computeLines(documentation);

                    for (OperationInformation operation : serviceImpl.getOperations()) {
                        String operationDocumentation = operation.getDocumentation();
                        documentationLines += computeLines(operationDocumentation);
                    }

                    serviceImplsDocQuality += IDEAL_DOCUMENTATION_LINES - DOCUMENTATION_LINES_TOLERANCE
                            - Math.max(0, Math.abs(IDEAL_DOCUMENTATION_LINES - computeLines(documentation))
                                    - DOCUMENTATION_LINES_TOLERANCE);
                }
                else {
                    undocumentedServiceImpls++;
                }

                // Mock info
                String parentServiceId = null;
                DocumentModelList implParents = documentService.findAllParents(session, serviceImplModel);
                for (DocumentModel implParent : implParents) {
                    if (InformationService.DOCTYPE.equals(implParent.getType())) {
                        parentServiceId = (String) implParent.getPropertyValue(InformationService.XPATH_SOANAME);
                        break;
                    }
                }
                if (parentServiceId != null) {
                    if (serviceImpl.isMock()) {
                        hasMock.put(parentServiceId, true);
                    }
                    else if (!hasMock.containsKey(parentServiceId)) {
                        hasMock.put(parentServiceId, false);
                    }
                }

                if (!serviceImpl.isMock()) {
                    nonMockImplsCount++;

                    // Tests info
                    if (!serviceImpl.getTests().isEmpty()) {
                        testedImplsCount++;
                    }
                }

            }
        }
        for (Boolean isMock : hasMock.values()) {
            if (isMock) {
                mockedImplsCount++;
            }
        }

        // Indicators results registration

        //% de doc seulement sur les services qui en ont / Qualité de doc moyenne en%
        //indicateurs documentation : % d'éléments doc'és (pour service, impl ; non test ; LATER pour consumer)
        newIndicator(indicators, "serviceWithDocumentation", serviceWithDocumentation, serviceCount);

        newIndicator(indicators, "serviceImplementationWithoutDocumentation", undocumentedServiceImpls, serviceImplCount);
        newIndicator(indicators, "serviceImplementationDocumentationLineAverage", (serviceImplCount - undocumentedServiceImpls > 0) ? (int) (documentationLines / (serviceImplCount - undocumentedServiceImpls)) : -1);
        newIndicator(indicators, "serviceImplementationWithoutMock", nonMockImplsCount - mockedImplsCount, nonMockImplsCount);
        newIndicator(indicators, "serviceImplementationWithoutTest", nonMockImplsCount - testedImplsCount, nonMockImplsCount);
        newIndicator(indicators, "documentedServiceImplementationDocumentationQuality", (serviceImplCount - undocumentedServiceImpls > 0) ?
                        (int) (100 * serviceImplsDocQuality / ((IDEAL_DOCUMENTATION_LINES - DOCUMENTATION_LINES_TOLERANCE) * (serviceImplCount - undocumentedServiceImpls))) : -1);

        // TODO model consistency ex. impl without service
        // TODO for one ex. impl of ONE service => prop to query

        return indicators;
    }

    private int computeLines(String documentation) {
        return documentation.split("\n").length;
    }

    private int computeServiceIndicators(CoreSession session, Map<String, IndicatorValue> indicators, String subprojectCriteria) throws Exception {

        DocumentService documentService = Framework.getService(DocumentService.class);

        int serviceWithDocumentation = 0;
        // Get services
        DocumentModelList serviceModels = session.query(DocumentService.NXQL_SELECT_FROM
                + InformationService.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY + subprojectCriteria);
        for (DocumentModel serviceModel : serviceModels) {
            boolean hasDoc = false;

            // Check if there is a joined file or child file (except resource file or wsdl file)
            WsdlBlob wsdlBlob = new WsdlBlob(serviceModel);
            // ??
            if(wsdlBlob.getBlob() != null && !wsdlBlob.getBlob().getFilename().endsWith("wsdl")){
                hasDoc = true;
            }

            if(!hasDoc){
                Blob fileBlob;
                List<?> files = (List<?>) serviceModel.getPropertyValue("files:files");
                if (files != null && !files.isEmpty()) {
                    for (Object fileInfoObject : files) {
                        Map<?, ?> fileInfoMap = (Map<?, ?>) fileInfoObject;
                        fileBlob = (Blob) fileInfoMap.get("file");
                        // TODO : Add a check for resource file
                        if (!fileBlob.getFilename().endsWith("wsdl")) {
                            hasDoc = true;
                            break;
                        }
                    }
                }
            }
            // Increase counter
            if(hasDoc){
                serviceWithDocumentation++;
            }
        }
        return serviceWithDocumentation;
    }

}
