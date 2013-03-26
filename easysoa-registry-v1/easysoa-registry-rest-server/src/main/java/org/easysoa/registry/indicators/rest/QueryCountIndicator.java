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

import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.utils.ContextVisibility;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.query.sql.NXQL;

/**
 * NXQL-based indicator provider
 *
 * @author mkalam-alami
 *
 */
public abstract class QueryCountIndicator extends Indicator {

    private String valueQuery = null;
    private String totalQuery = null;

    public QueryCountIndicator(String valueQuery) {
        this.valueQuery = valueQuery;
    }

    public QueryCountIndicator(String valueQuery, String totalQuery) {
        this.valueQuery = valueQuery;
        this.totalQuery = totalQuery;
    }

    @Override
    public IndicatorValue compute(CoreSession session, String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws ClientException {
        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            //TODO : To replace by SubprojectServiceImpl.buildCriteriaSeenFromSubproject(getSubprojectById(CoreSession documentManager, String subprojectId))
            if (ContextVisibility.STRICT.getValue().equals(visibility)) {
                subprojectPathCriteria = DocumentService.NXQL_AND
                        + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND
                        + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(SubprojectServiceImpl.getSubprojectById(session, subprojectId));
            }
        }

        IterableQueryResult queryResult = session.queryAndFetch(valueQuery + subprojectPathCriteria, NXQL.NXQL);
        try {
            if (this.totalQuery == null) {
                return new IndicatorValue((int) queryResult.size(), -1);
            } else {
                IterableQueryResult totalQueryResult = null;
                try {
                    totalQueryResult = session.queryAndFetch(totalQuery, NXQL.NXQL);

                    // Compute the percentage value
                    int percentage = Math.round(queryResult.size() * 100 / totalQueryResult.size());
                    
                    //TODO : Total query stored in the percentage value - how is calculated the percentage ??
                    //return new IndicatorValue((int) queryResult.size(), (int) totalQueryResult.size());
                    return new IndicatorValue((int) queryResult.size(), percentage);
                } finally {
                    if (totalQueryResult != null) {
                        totalQueryResult.close();
                    }
                }
            }
        } finally {
            queryResult.close();
        }
    }
}
