/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicator;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.testng.Assert;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Test for Endpoint state service
 * 
 * @author jguillemotte
 *
 */
@Deploy("org.easysoa.registry.rest.server")
@LocalDeploy({ "org.easysoa.registry.rest.server:OSGI-INF/SlaOlaIndicatorsTest.xml" })
@RepositoryConfig(cleanup = Granularity.CLASS)
public class EndpointStateServiceTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(EndpointStateServiceTest.class);

    private EndpointStateServiceHelper endpointStateService = new EndpointStateServiceHelper(this);
    
    @Inject
    DirectoryService directoryService;
    
    public final static String ENDPOINT_ID = "test";
    
    public final static String INDICATOR_NAME = "testSlaIndicator";
    
    public final static String SERVICE_LEVEL_HEALTH = "gold";
    
    /**
     * Init the tests
     * @throws Exception
     */
    @Before
    public void init() throws Exception {
        
        Session session = directoryService.open("slaOrOlaIndicator");
       
        try{
            //DocumentModel indicatorModel = new DocumentModelImpl(org.easysoa.registry.types.SlaOrOlaIndicator.DOCTYPE); 

            Map<String, Object> properties = new HashMap<String, Object>();
            /*indicatorModel.setPropertyValue("ind:endpointId", "testEndpoint");
            indicatorModel.setPropertyValue("ind:slaOrOlaName", "testSlaIndicator");
            indicatorModel.setPropertyValue("serviceLeveHealth", "gold");
            indicatorModel.setPropertyValue("serviceLevelViolation", false);
            indicatorModel.setPropertyValue("timestamp", "2012-12-13T16:15:28-00:00");*/
            properties.put("endpointId", ENDPOINT_ID);
            properties.put("slaOrOlaName", INDICATOR_NAME);
            properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
            properties.put("serviceLevelViolation", false);
            Calendar calendar = new GregorianCalendar();
            calendar.set(2012, 12, 13, 17, 17, 45);
            properties.put("timestamp", calendar);
            //  "2012-12-13T16:15:28-00:00"
            
            session.createEntry(properties);
            session.commit();
            session.close();
        }
        catch(Exception ex){
            session.rollback();
            session.close();
            ex.printStackTrace();
        }
    }

    /**
     * Test the getSlaOrOlaIndicators REST operation
     */
    @Test
    public void getSlaOrOlaIndicatorsTest(){
        logTestName(logger);
        
        // Run first test request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators"); //.queryParam("endpointId", "test").queryParam("slaOrOlaName", "testSlaIndicator");
        SlaOrOlaIndicators slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        SlaOrOlaIndicator indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);

        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals(SERVICE_LEVEL_HEALTH, indicator.getServiceLevelHealth().toString());
        
    }    
    
    /**
     * Test the updateSlaOlaIndicators REST operation
     */
    @Test
    @Ignore
    public void updateSlaOlaIndicatorsTest(){
        logTestName(logger);
        
        // Run first test request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/updateSlaOlaIndicators");
        //MultivaluedMap formData = new MultivaluedMapImpl();
        //formData.add("name1", "val1");
        //formData.add("name2", "val2");        
        //ServiceInformations serviceInformations = discoveryRequest.pos .post(ServiceInformations.class, formData);
        
    }
    
}
