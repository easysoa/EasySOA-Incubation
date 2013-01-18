package org.easysoa.registry.indicators.rest;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
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
    public IndicatorValue compute(CoreSession session,  String subprojectId,
            Map<String, IndicatorValue> computedIndicators) throws ClientException {
        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            subprojectPathCriteria = " " + IndicatorProvider.NXQL_PATH_STARTSWITH + session.getDocument(new IdRef(subprojectId)).getPathAsString() + "'";
        }
        
        IterableQueryResult queryResult = session.queryAndFetch(valueQuery + subprojectPathCriteria, NXQL.NXQL);
        try {
            if (this.totalQuery == null) {
                return new IndicatorValue((int) queryResult.size(), -1);
            }
            else {
                IterableQueryResult totalQueryResult = null;
                try {
                    totalQueryResult = session.queryAndFetch(totalQuery, NXQL.NXQL);
                return new IndicatorValue((int) queryResult.size(), (int) totalQueryResult.size());
                    } finally {
                    if (totalQueryResult != null) {
                        totalQueryResult.close();
                    }
                }
            }
        }
        finally {
            queryResult.close();
        }
    }
    
}
