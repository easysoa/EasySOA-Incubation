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

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

/**
 * Computes various indicators on services implementations, including doc quality
 *
 * @author mkalam-alami; mdutoo
 */
public class ServiceImplStateProvider extends IndicatorProviderBase {

    private static Logger logger = Logger.getLogger(ServiceImplStateProvider.class);

    //private static final String SERVICEIMPL_DOCTYPE_INDICATOR = DoctypeCountProvider.getName(ServiceImplementation.DOCTYPE);

    // TODO : Add a key and a name in indicators
    private static final String SERVICEIMPL_DOCTYPE_INDICATOR = DoctypeCountProvider.buildName(ServiceImplementation.DOCTYPE);
    private static final String SERVICE_DOCTYPE_INDICATOR = DoctypeCountProvider.buildName(InformationService.DOCTYPE);

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

        /**
         * 1 unit = 40 characters (half standard 80 character line)
         * Ideal doc => 4 units for a class
         * and for each operations 2 units + 1 unit for each parameter ....
         *
         */
        final int DOC_LINE_CHARACTERS_NUMBER_UNIT = 40;

        int serviceimplsMinIdealDocUnitTotal = 0; // sum of each class' minimum number of doc units to be considered ideally doc'd
        int serviceImplsRealDocUnitTotal = 0;
        int serviceImplsUnitDocQuality = 0;
        int undocumentedServiceImpls = 0, documentationLines = 0;
        long serviceImplCount = computedIndicators.get(SERVICEIMPL_DOCTYPE_INDICATOR).getCount();
        long serviceCount = computedIndicators.get(SERVICE_DOCTYPE_INDICATOR).getCount();
        int serviceImplsDocQuality = 0;
        Map<Serializable, Boolean> hasMock = new HashMap<Serializable, Boolean>();
        int mockedImplsCount = 0, testedImplsCount = 0, nonMockImplsCount = 0;

        // Get service impls
        DocumentModelList serviceImplModels = session.query(DocumentService.NXQL_SELECT_FROM
                + ServiceImplementation.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY + subprojectCriteria);

        for (DocumentModel serviceImplModel : serviceImplModels) {
            ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
            if (!serviceImpl.isPlaceholder()) {

                int idealMinDocUnitNumber;
                int realDocUnitNumber;

                // Documentation info
                String documentation = (String) serviceImpl.getProperty(ServiceImplementation.XPATH_DOCUMENTATION);
                if (documentation != null && !documentation.isEmpty()) {

                    documentationLines += computeLines(documentation);
                    idealMinDocUnitNumber = 4; // For a class => documentation must be at least equivalent to 4 doc units
                    realDocUnitNumber = Math.round(documentation.length() / DOC_LINE_CHARACTERS_NUMBER_UNIT);

                    // Operation documentation
                    for (OperationInformation operation : serviceImpl.getOperations()) {
                        String operationDocumentation = operation.getDocumentation();
                        documentationLines += computeLines(operationDocumentation);
                        idealMinDocUnitNumber += 2; // For a method, at least 2 doc units

                        String parameters[] = operation.getParameters().split(",");
                        //totalNbOfParameters += parameters.length; // WARNING not necessarily meaningful,
                        // ex. ContactSvcSoap.client(name, email, age...) vs PrecomptePartenaireWebService.creerPrecompte(CreerPrecompte) means the same
                        idealMinDocUnitNumber += parameters.length; // 1 doc unit for each parameter
                        realDocUnitNumber += Math.round(operationDocumentation.length() / DOC_LINE_CHARACTERS_NUMBER_UNIT);
                    }


                    // computing doc quality : = 20 if in the tolerance range, otherwise drops down to 0 beyond twice the tolerance range
                    int serviceImplDocQuality = Math.max(0,
                            IDEAL_DOCUMENTATION_LINES - DOCUMENTATION_LINES_TOLERANCE
                            - Math.max(0,
                            Math.abs(IDEAL_DOCUMENTATION_LINES - documentationLines)
                                    - DOCUMENTATION_LINES_TOLERANCE)); // 0 => 0, 10 => 10, 20 => 20 same up to 60 => 20, 70 => 10, 80 => 0, 100 => -20
                    logger.debug("Doc quality for service impl " + serviceImpl.getName() + " : " + serviceImplDocQuality);
                    serviceImplsDocQuality += serviceImplDocQuality;

                    serviceImplsRealDocUnitTotal += realDocUnitNumber;
                    serviceimplsMinIdealDocUnitTotal += idealMinDocUnitNumber;

                    int serviceImplUnitDocQuality = Math.round(100 * realDocUnitNumber / idealMinDocUnitNumber);
                    if(serviceImplUnitDocQuality > 100){
                        serviceImplUnitDocQuality = 100; // Set to the max % value if there is too much doc in the class
                    }
                    serviceImplsUnitDocQuality += serviceImplUnitDocQuality;
                    logger.debug("Doc units quality for service impl " + serviceImpl.getName() + " : " + serviceImplUnitDocQuality + "%");
                    logger.debug("Ideal Doc units for service impl " + serviceImpl.getName() + " : " + idealMinDocUnitNumber);
                    logger.debug("Real Doc units for service impl " + serviceImpl.getName() + " : " + realDocUnitNumber);
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
        newIndicator(indicators, "serviceImplementationWithoutDocumentation", undocumentedServiceImpls, serviceImplCount);
        newIndicator(indicators, "serviceImplementationDocumentationLineAverage", (serviceImplCount - undocumentedServiceImpls > 0) ? (int) (documentationLines / (serviceImplCount - undocumentedServiceImpls)) : -1, -1);
        newIndicator(indicators, "serviceImplementationWithoutMock", nonMockImplsCount - mockedImplsCount, nonMockImplsCount);
        newIndicator(indicators, "serviceImplementationWithoutTest", nonMockImplsCount - testedImplsCount, nonMockImplsCount);
        // Obsolete indicator with old formula
        //newIndicator(indicators, "documentedServiceImplementationDocumentationQuality", (serviceImplCount - undocumentedServiceImpls > 0) ?
        //                (int) (100 * serviceImplsDocQuality / ((IDEAL_DOCUMENTATION_LINES - DOCUMENTATION_LINES_TOLERANCE) * (serviceImplCount - undocumentedServiceImpls))) : 0);
        // New indicator with doc unit formula
        newIndicator(indicators, "documentedServiceImplementationDocumentationQuality", (serviceImplCount - undocumentedServiceImpls > 0) ?
                        (int) (serviceImplsUnitDocQuality / (serviceImplCount - undocumentedServiceImpls)) : 0);

        // TODO model consistency ex. impl without service
        // TODO for one ex. impl of ONE service => prop to query

        return indicators;
    }

    private int computeLines(String documentation) {
        if(documentation != null){
            return documentation.split("\n").length;
        }
        return 0;
    }

}
