package org.easysoa.registry.indicators.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceConsumption;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class ServiceConsumptionIndicatorProvider implements IndicatorProvider {

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            subprojectPathCriteria = " " + SubprojectServiceImpl.buildCriteriaFromId(subprojectId);
        }
        
        List<SoaNodeId> servicesIds = documentService.createSoaNodeIds(session.query(
                DocumentService.NXQL_SELECT_FROM + InformationService.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY + subprojectPathCriteria)
                    .toArray(new DocumentModel[]{}));
        List<SoaNodeId> unconsumedServiceIds = new ArrayList<SoaNodeId>(servicesIds);
        DocumentModelList serviceConsumptionModels = session.query(DocumentService.NXQL_SELECT_FROM + ServiceConsumption.DOCTYPE + DocumentService.NXQL_WHERE_NO_PROXY);
        for (DocumentModel serviceConsumptionModel : serviceConsumptionModels) {
            ServiceConsumption serviceConsumption = serviceConsumptionModel.getAdapter(ServiceConsumption.class);
            List<SoaNodeId> consumableServiceImpls = serviceConsumption.getConsumableServiceImpls();
            for (SoaNodeId consumableServiceImpl : consumableServiceImpls) {
                DocumentModelList consumableParents = documentService.findAllParents(session, documentService.find(session, consumableServiceImpl));
                for (DocumentModel consumableParent : consumableParents) {
                    if (InformationService.DOCTYPE.equals(consumableParent.getType())) {
                        unconsumedServiceIds.remove(documentService.createSoaNodeId(consumableParent));
                    }
                }
            }
        }
        
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        indicators.put("Never consumed services",
                new IndicatorValue(unconsumedServiceIds.size(), (servicesIds.size() > 0) ? 100 * unconsumedServiceIds.size() / servicesIds.size() : -1));
        
        return indicators;
    }

}
