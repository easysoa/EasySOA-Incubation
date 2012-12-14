/**
 * 
 */
package org.easysoa.registry.integration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.easysoa.registry.rest.integration.EndpointStateService;
import org.easysoa.registry.rest.integration.ServiceLevelHealth;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicator;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;
import org.easysoa.registry.types.Endpoint;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

/**
 * Endpoint state service implementation
 * 
 * @author jguillemotte
 * 
 */
@Path("easysoa/endpointStateService")
public class EndpointStateServiceImpl implements EndpointStateService {

    // Servlet context
    @Context
    HttpServletRequest request;
    
    /**
     * @see org.easysoa.registry.rest.integration.EndpointStateService#updateSlaOlaIndicators(SlaOrOlaIndicator[])
     */
    @Override
    public void updateSlaOlaIndicators(SlaOrOlaIndicators slaOrOlaIndicators) throws Exception {

        DirectoryService directoryService = Framework.getService(DirectoryService.class);
        
        if(slaOrOlaIndicators != null){

            // Open a session on slaOrOlaIndicator directory
            Session session = directoryService.open("slaOrOlaIndicator");
            if(session == null){
                throw new Exception("Unable to open a new session on directory 'slaOrOlaIndicator'");
            }

            try{
                // Update each indicator
                for(SlaOrOlaIndicator indicator : slaOrOlaIndicators.getSlaOrOlaIndicatorList()){
                    // get the indicator if exists
                    Map<String, Serializable> parameters = new HashMap<String, Serializable>();
                    parameters.put("slaOrOlaName", indicator.getSlaOrOlaName());
                    parameters.put("endpointId", indicator.getEndpointId());                    
                    DocumentModelList documentModelList = session.query(parameters);
                    DocumentModel indicatorModel;
                    
                    if(documentModelList != null && documentModelList.size() > 0){
                        // Update existing indicator
                        indicatorModel = documentModelList.get(0);
                        indicatorModel.setPropertyValue("serviceLeveHealth", indicator.getServiceLevelHealth().toString());
                        indicatorModel.setPropertyValue("serviceLevelViolation", String.valueOf(indicator.isServiceLevelViolation()));
                        indicatorModel.setPropertyValue("timestamp", indicator.getTimestamp());
                        session.updateEntry(indicatorModel);
                    } else {
                        // Create new indicator
                        Map<String, Object> properties = new HashMap<String, Object>();
                        properties.put("endpointId", indicator.getEndpointId());
                        properties.put("slaOrOlaName", indicator.getSlaOrOlaName());
                        properties.put("serviceLeveHealth", indicator.getServiceLevelHealth().toString());    
                        properties.put("serviceLevelViolation", indicator.isServiceLevelViolation());
                        if(indicator.getTimestamp() != null){
                            GregorianCalendar calendar = new GregorianCalendar();
                            calendar.setTime(indicator.getTimestamp());
                            properties.put("timestamp", calendar);
                        }
                        session.createEntry(properties);
                    }
                }
                session.commit();
                session.close();
            }
            catch(Exception ex){
                 session.rollback();
                 session.close();
                 // Return the exception and cancel the transaction
                 throw new Exception("Failed to update SLA or OLA indicators ", ex);                   
            }
        }
    }

    /**
     * @see org.easysoa.registry.rest.integration.EndpointStateService#getSlaOrOlaIndicatorsByEnv(String, String, Date,
     *      Date, int, int)
     */
    @Override
    public SlaOrOlaIndicators getSlaOrOlaIndicatorsByEnv(String environment, String projectId,
            Date periodStart, Date periodEnd, int pageSize, int pageStart) throws Exception {
        // TODO NXQL query returning all endpoints where environment & projectId
        // TODO put them in call to getSlaOrOlaIndicators(String endpointIds...) and return it
        
        CoreSession documentManager = SessionFactory.getSession(request);

        if(environment != null && !"".equals(environment) && projectId != null && !"".equals(projectId)){
            throw new IllegalArgumentException("Environment or projectid parameter must not be null or empty");
        }
        
        // Fetch SoaNode list
        ArrayList<String> parameters = new ArrayList<String>(); 
        StringBuffer query = new StringBuffer(); 
        query.append("SELECT * FROM Endpoint WHERE ");
 
        // Search parameters
        if(environment != null && !"".equals(environment)){
            query.append(Endpoint.XPATH_ENDP_ENVIRONMENT + " like '?' ");
            parameters.add(environment);
        }
        
        if(projectId != null && !"".equals(projectId)){
            if(environment != null && !"".equals(environment)){
                query.append(" AND ");
            }
            query.append("endp:projectid" + " like '?' ");
            parameters.add(projectId);
        }
        
        // Execute query
        String nxqlQuery = NXQLQueryBuilder.getQuery(query.toString(), parameters.toArray(), false, true);
        DocumentModelList soaNodeModelList = documentManager.query(nxqlQuery);        
        
        // Get endpoints list
        List<String> endpointsList = new ArrayList<String>();
        for(DocumentModel documentModel : soaNodeModelList){
            endpointsList.add((String)documentModel.getPropertyValue(Endpoint.XPATH_UUID));
        }
        
        // Get endpoints indicators
        return this.getSlaOrOlaIndicators(endpointsList, periodStart, periodEnd, pageSize, pageStart);
    }
    
    /**
     * 
     * not REST, used by above 
     * 
     * @param endpointIds
     * @param periodStart
     * @param periodEnd
     * @param pageSize
     * @param pageStart
     * @return
     * @throws Exception
     */
    protected SlaOrOlaIndicators getSlaOrOlaIndicators(List<String> endpointIds,
            Date periodStart, Date periodEnd, int pageSize, int pageStart) throws Exception {

        // For each endpoint, get the corresponding indicators and returns the indicator list
        SlaOrOlaIndicators slaOrOlaIndicators = new SlaOrOlaIndicators();
        for(String endpointId : endpointIds){
            slaOrOlaIndicators.getSlaOrOlaIndicatorList().addAll(getSlaOrOlaIndicators(endpointId, "", periodStart, periodEnd, pageSize, pageStart).getSlaOrOlaIndicatorList());
        }
        return slaOrOlaIndicators;        
    }

    /**
     * @see org.easysoa.registry.rest.integration.EndpointStateService#getSlaOrOlaIndicators(String, String, Date,
     *      Date, int, int)
     */
    @Override
    public SlaOrOlaIndicators getSlaOrOlaIndicators(String endpointId, 
            String slaOrOlaName, Date periodStart, Date periodEnd, int pageSize, int pageStart) throws Exception {
        
        DirectoryService directoryService = Framework.getService(DirectoryService.class);        
        Session session = directoryService.open("slaOrOlaIndicator");        
        
        /*
        * Returns level indicators, in the given period (default : daily)
        * OPT paginated navigation
        * @param periodStart : if null day start, if both null returns all in the current day
        * @param periodEnd : if null now, if both null returns all in the current day
        * @param pageSize OPT pagination : number of indicators per page
        * @param pageStart OPT pagination : index of the first indicator to return (starts with 0)
        * @return SlaOrOlaIndicators array of SlaOrOlaIndicator
        */
        
        if(session == null){
            throw new Exception("Unable to open a new session on directory 'slaOrOlaIndicator'");
        }

        Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        
        if(endpointId != null && !"".equals(endpointId)){
            parameters.put("endpointId", endpointId);
        }
        
        if(slaOrOlaName != null && !"".equals(slaOrOlaName)){
            parameters.put("slaOrOlaName", slaOrOlaName);
        }

        SlaOrOlaIndicators slaOrOlaIndicators = new SlaOrOlaIndicators();
        // Execute query        
        try {
            DocumentModelList soaNodeModelList = session.query(parameters);            
            SlaOrOlaIndicator indicator;
            for(DocumentModel model : soaNodeModelList){
                indicator = new SlaOrOlaIndicator();
                indicator.setEndpointId((String)model.getPropertyValue(org.easysoa.registry.types.SlaOrOlaIndicator.XPATH_ENDPOINT_ID));
                indicator.setServiceLevelHealth(ServiceLevelHealth.valueOf((String)model.getPropertyValue(org.easysoa.registry.types.SlaOrOlaIndicator.XPATH_SERVICE_LEVEL_HEALTH)));
                indicator.setServiceLevelViolation((Boolean)model.getPropertyValue(org.easysoa.registry.types.SlaOrOlaIndicator.XPATH_SERVICE_LEVEL_VIOLATION));
                indicator.setSlaOrOlaName((String)model.getPropertyValue(org.easysoa.registry.types.SlaOrOlaIndicator.XPATH_SLA_OR_OLA_NAME));
                GregorianCalendar calendar = (GregorianCalendar)model.getPropertyValue(org.easysoa.registry.types.SlaOrOlaIndicator.XPATH_TIMESTAMP);                
                indicator.setTimestamp(calendar.getTime());
                slaOrOlaIndicators.getSlaOrOlaIndicatorList().add(indicator);
            }
        } catch (ClientException ex) {
            ex.printStackTrace();
            throw ex;
        }
        
        // TODO : add pagination
        //int itemStartIndex = pageSize * pageStart;
        //slaOrOlaIndicators.setSlaOrOlaIndicatorList(indicatorList.subList(itemStartIndex, itemStartIndex + pageSize)) ;                
        
        return slaOrOlaIndicators;
    }
    
}
