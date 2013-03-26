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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceConsumption;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.ContextVisibility;
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
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            if(ContextVisibility.STRICT.getValue().equals(visibility)){
                subprojectPathCriteria = DocumentService.NXQL_AND + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(
                        SubprojectServiceImpl.getSubprojectById(session, subprojectId));                
            }
        }
        
        List<SoaNodeId> servicesIds = documentService.createSoaNodeIds(session.query(
                DocumentService.NXQL_SELECT_FROM + InformationService.DOCTYPE
                + DocumentService.NXQL_WHERE_NO_PROXY
                + subprojectPathCriteria).toArray(new DocumentModel[]{}));
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
