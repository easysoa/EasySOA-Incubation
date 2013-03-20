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
    public IndicatorValue compute(CoreSession session,  String subprojectId,
            Map<String, IndicatorValue> computedIndicators, String visibility) throws ClientException {
        //subprojectId = SubprojectServiceImpl.getSubprojectIdOrCreateDefault(session, subprojectId);
        // TODO default or not ??
        String subprojectPathCriteria;
        if (subprojectId == null) {
            subprojectPathCriteria = "";
        } else {
            //TODO : To replace by SubprojectServiceImpl.buildCriteriaSeenFromSubproject(getSubprojectById(CoreSession documentManager, String subprojectId))
            if(ContextVisibility.DEPTH.getValue().equals(visibility)){
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(SubprojectServiceImpl.getSubprojectById(session, subprojectId));                                
            } else {
                subprojectPathCriteria = DocumentService.NXQL_AND
                    + SubprojectServiceImpl.buildCriteriaInSubproject(subprojectId);                
            }            
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
