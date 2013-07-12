/**
 * EasySOA Registry
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

import java.util.ArrayList;
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
public class LastCodeDiscoveryIndicatorProvider extends IndicatorProviderBase {

    public LastCodeDiscoveryIndicatorProvider(String category){
        super(category);
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session, String subprojectId, Map<String, IndicatorValue> computedIndicators, String visibility) throws Exception {

    	DocumentService documentService = Framework.getService(DocumentService.class);

        // Init the result map
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();

        // Select all applications
        StringBuilder query = new StringBuilder();
        //query.append("SELECT DISTINCT " + Deliverable.XPATH_APPLICATION + " FROM "); // NOO DISTINCT doesn't work here ?!
        query.append(DocumentService.NXQL_SELECT_FROM);
        query.append(Deliverable.DOCTYPE);
        // set the subproject criteria
        String subProjectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
        query.append(subProjectCriteria);
        DocumentModelList delList = documentService.query(session, query.toString(), true, false);

        // get those with latest modified date
        HashMap<String, Date> appToModifiedMap = new HashMap<String, Date>(delList.size());

        // For each app
        for(DocumentModel del : delList){
            Date lastModificationDate = del.getProperty("dc:modified").getValue(Date.class);
            String application = (String) del.getPropertyValue(Deliverable.XPATH_APPLICATION);

            if (!appToModifiedMap.containsKey(application)
            		|| lastModificationDate.after(appToModifiedMap.get(application))) {
        		appToModifiedMap.put(application, lastModificationDate);
            }
        }
        for (String application : appToModifiedMap.keySet()) {
        	Date lastModificationDate = appToModifiedMap.get(application);
            // TODO : modify the IndicatorValue to be able to pass dates as value : Done but not with the best solution => LATER Date + int in the same value attribute
        	// TODO : i18n
            //newIndicator(indicators, "Date de la dernière découverte code/analyse pour l'application " + application, lastModificationDate);
            newIndicator(indicators, "dateOfLastCodeDiscovery", lastModificationDate).addArg(application);
        }

        return indicators;
    }

}
