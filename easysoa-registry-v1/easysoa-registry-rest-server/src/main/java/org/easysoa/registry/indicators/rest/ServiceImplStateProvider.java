package org.easysoa.registry.indicators.rest;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.utils.ContextVisibility;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class ServiceImplStateProvider implements IndicatorProvider {

    private static final String SERVICEIMPL_DOCTYPE_INDICATOR = DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE);
    
    @Override
    public List<String> getRequiredIndicators() {
        return Arrays.asList(SERVICEIMPL_DOCTYPE_INDICATOR);
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        DocumentService documentService = Framework.getService(DocumentService.class);

        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            if(ContextVisibility.STRICT.getValue().equals(visibility)){
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(SubprojectServiceImpl.getSubprojectById(session, subprojectId));
            }
        }

        // Count indicators - ServiceImplementation-specific
        final int IDEAL_DOCUMENTATION_LINES = 40, DOCUMENTATION_LINES_TOLERANCE = 20;
        int undocumentedServiceImpls = 0, documentationLines = 0;
        int serviceImplCount = computedIndicators.get(SERVICEIMPL_DOCTYPE_INDICATOR).getCount();
        int serviceImplsDocQuality = 0;
        Map<Serializable, Boolean> hasMock = new HashMap<Serializable, Boolean>();
        int mockedImplsCount = 0, testedImplsCount = 0, nonMockImplsCount = 0;
        DocumentModelList serviceImplModels = session.query(DocumentService.NXQL_SELECT_FROM
                + ServiceImplementation.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY + subprojectPathCriteria);
        
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
        indicators.put("Undocumented service implementations", 
                new IndicatorValue(undocumentedServiceImpls, -1));
        indicators.put("Lines of documentation per documented service impl. (average)",
                new IndicatorValue((serviceImplCount - undocumentedServiceImpls > 0) ? (documentationLines / (serviceImplCount - undocumentedServiceImpls)) : -1, -1));
        indicators.put("Service impls without mocks", 
                new IndicatorValue(nonMockImplsCount - mockedImplsCount,
                        (nonMockImplsCount > 0) ? (100 * (nonMockImplsCount - mockedImplsCount) / nonMockImplsCount) : -1));
        indicators.put("Service impls without tests", 
                new IndicatorValue(nonMockImplsCount - testedImplsCount,
                        (nonMockImplsCount > 0) ? (100 * (nonMockImplsCount - testedImplsCount) / nonMockImplsCount) : -1));
        indicators.put("Documented service implementations documentation quality", 
                new IndicatorValue(-1, (serviceImplCount - undocumentedServiceImpls > 0) ?
                        (100 * serviceImplsDocQuality / ((IDEAL_DOCUMENTATION_LINES - DOCUMENTATION_LINES_TOLERANCE) * (serviceImplCount - undocumentedServiceImpls))) : -1));

        // TODO model consistency ex. impl without service
        // TODO for one ex. impl of ONE service => prop to query
        
        return indicators;
    }

    private int computeLines(String documentation) {
        return documentation.split("\n").length;
    }
    
}
