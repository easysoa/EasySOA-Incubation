/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.easysoa.registry.rest.integration.EndpointStateService;
import org.easysoa.registry.rest.integration.ServiceLevelHealth;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicator;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;

/**
 * @author jguillemotte
 *
 */
public class EndpointStateServiceImpl implements EndpointStateService {

    //
    @Context HttpServletRequest request;    
    
    /**
     * @see
     */
    @Override
    public void updateSlaOlaIndicators(SlaOrOlaIndicator[] SlaOrOlaIndicators) throws Exception {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see 
     */
    @Override
    public SlaOrOlaIndicator[] getSlaOrOlaIndicators(Date periodStart, Date periodEnd, int pageSize, int pageStart) {
        
        CoreSession documentManager = SessionFactory.getSession(request);

        ArrayList<String> parameters = new ArrayList<String>();
        StringBuffer query = new StringBuffer(); 
        query.append("SELECT * FROM SLA, OLA ");
        
        /*
        * Returns level indicators, in the given period (default : daily)
        * OPT paginated navigation
        * @param periodStart : if null day start, if both null returns all in the current day
        * @param periodEnd : if null now, if both null returns all in the current day
        * @param pageSize OPT pagination : number of indicators per page
        * @param pageStart OPT pagination : index of the first indicator to return (starts with 0)
        * @return SlaOrOlaIndicators array of SlaOrOlaIndicator
        */
        
        // TODO : Add parameters
        if(periodStart == null){
        
        } else {
        
        }
        
        if(periodEnd == null){
        
        } else {
        
        }
        
        SlaOrOlaIndicator[] indicatorArray = new SlaOrOlaIndicator[0];     
        // Execute query        
        try {
            String nxqlQuery = NXQLQueryBuilder.getQuery(query.toString(), parameters.toArray(), false, true);
            DocumentModelList soaNodeModelList = documentManager.query(nxqlQuery);            
            //indicatorArray = new SlaOrOlaIndicator[soaNodeModelList.size()];
            
            SlaOrOlaIndicator indicator;
            ArrayList<SlaOrOlaIndicator> indicatorList = new ArrayList<SlaOrOlaIndicator>();
            for(DocumentModel model : soaNodeModelList){
                indicator = new SlaOrOlaIndicator();
                indicator.setEndpointId("");
                indicator.setServiceLevelHealth(ServiceLevelHealth.gold);
                indicator.setServiceLevelViolation(false);
                indicator.setSlaOrOlaName((String)model.getPropertyValue("soan:name"));
                indicator.setTimestamp(0);
                indicatorList.add(indicator);
            }
            indicatorArray = indicatorList.toArray(indicatorArray);
        } catch (ClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // TODO : add pagination
        
        return indicatorArray;
    }

}
