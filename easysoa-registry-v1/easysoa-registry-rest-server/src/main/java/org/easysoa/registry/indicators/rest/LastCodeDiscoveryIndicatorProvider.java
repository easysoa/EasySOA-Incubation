/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

/**
 * Compute the last Code Discovery Indicator.
 * @author jguillemotte
 */
public class LastCodeDiscoveryIndicatorProvider implements IndicatorProvider {

    private String category;
    
    public LastCodeDiscoveryIndicatorProvider(String category){
        if(category == null){
            this.category = "";
        } else {
            this.category = category;
        }
    }
    
    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId, Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {
        
        DocumentService documentService = Framework.getService(DocumentService.class);        
        
        // Init the result map
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        
        // Select all applications
        StringBuilder query = new StringBuilder();
        query.append(DocumentService.NXQL_SELECT_FROM);
        query.append(Deliverable.DOCTYPE);
        // set the subproject criteria
        String subProjectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
        if(!"".equals(subProjectCriteria)){
            query.append(DocumentService.NXQL_AND);
            query.append(subProjectCriteria);
        }
        DocumentModelList appList = documentService.query(session, query.toString(), true, false);
        
        // For each app
        for(DocumentModel app : appList){
            Date lastModificationDate = app.getProperty("dc:modified").getValue(Date.class);
            
            // TODO : modify the IndicatorValue to be able to pass dates as value
            IndicatorValue iValue = new IndicatorValue("Date de la dernière découverte code/analyse pour l'application " + app.getTitle(), category, -1, -1);
            iValue.setDate(lastModificationDate);
            indicators.put("Date de la dernière découverte code/analyse pour l'application " + app.getTitle(), iValue);
        }
        
        return indicators;
    }

}
