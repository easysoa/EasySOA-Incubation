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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceConsumption;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 */
public class ServiceConsumptionIndicatorProvider extends IndicatorProviderBase {
    
    public ServiceConsumptionIndicatorProvider(String category){
        super(category);
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
        
        List<SoaNodeId> servicesIds = documentService.createSoaNodeIds(documentService.query(session,
                DocumentService.NXQL_SELECT_FROM + InformationService.DOCTYPE
                + subprojectCriteria, true, false).toArray(new DocumentModel[]{}));
        List<SoaNodeId> unconsumedServiceIds = new ArrayList<SoaNodeId>(servicesIds);
        DocumentModelList serviceConsumptionModels = documentService.query(session,
                DocumentService.NXQL_SELECT_FROM + ServiceConsumption.DOCTYPE
                + subprojectCriteria, true, false);
        for (DocumentModel serviceConsumptionModel : serviceConsumptionModels) {
            ServiceConsumption serviceConsumption = serviceConsumptionModel.getAdapter(ServiceConsumption.class);
            List<SoaNodeId> consumableServiceImpls = serviceConsumption.getConsumableServiceImpls();
            for (SoaNodeId consumableServiceImpl : consumableServiceImpls) {
                DocumentModel consumableParent = documentService.getParentInformationService(documentService.findSoaNode(session, consumableServiceImpl));
                if (consumableParent != null) {
                    unconsumedServiceIds.remove(documentService.createSoaNodeId(consumableParent));
                }
            }
        }
        
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        newIndicator(indicators, "serviceWithoutConsumption", unconsumedServiceIds.size(), servicesIds.size());
        
        return indicators;
    }

}
