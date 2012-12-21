/**
 * 
 */
package org.easysoa.registry.integration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.integration.ServiceLevelHealth;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicator;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;
import org.junit.Before;
import org.junit.Test;
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
    public final static String ANOTHER_INDICATOR_NAME = "anotherTestSlaIndicator";
    
    public final static String SERVICE_LEVEL_HEALTH = "gold";
    
    private static boolean initDone = false;
    
    /**
     * Init the tests
     * @throws Exception
     */
    @Before
    public void init() throws Exception {
        
        if(!initDone){
            
            Session session = directoryService.open(org.easysoa.registry.types.SlaOrOlaIndicator.DOCTYPE);
           
            try{
                // Create a fisrt SlaOrOlaIndicator
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("endpointId", ENDPOINT_ID);
                properties.put("slaOrOlaName", INDICATOR_NAME);
                properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
                properties.put("serviceLevelViolation", false);
                Calendar calendar = new GregorianCalendar();
                properties.put("timestamp", calendar);
                session.createEntry(properties);

                // Create a second SlaOrOlaIndicator                
                properties = new HashMap<String, Object>();
                properties.put("endpointId", ENDPOINT_ID);
                properties.put("slaOrOlaName", ANOTHER_INDICATOR_NAME);
                properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
                properties.put("serviceLevelViolation", true);
                calendar = new GregorianCalendar();
                calendar.set(2012, 11, 25, 8, 24, 37);
                properties.put("timestamp", calendar);                
                session.createEntry(properties);

                // Create a third SlaOrOlaIndicator                
                properties = new HashMap<String, Object>();
                properties.put("endpointId", "anotherEndpointID");
                properties.put("slaOrOlaName", "AgainAnotherIndicatorName");
                properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
                properties.put("serviceLevelViolation", true);
                calendar = new GregorianCalendar();
                calendar.set(2012, 11, 12, 16, 7, 10);
                properties.put("timestamp", calendar);                
                session.createEntry(properties);            

                // Create a fourth SlaOrOlaIndicator                
                properties = new HashMap<String, Object>();
                properties.put("endpointId", "anotherEndpointID");
                properties.put("slaOrOlaName", "IndicatorNameTest4");
                properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
                properties.put("serviceLevelViolation", true);
                calendar = new GregorianCalendar();
                calendar.set(2012, 11, 8, 9, 46, 43);
                properties.put("timestamp", calendar);
                session.createEntry(properties);
                
                // Create a fifth SlaOrOlaIndicator                
                properties = new HashMap<String, Object>();
                properties.put("endpointId", "anotherEndpointID");
                properties.put("slaOrOlaName", "IndicatorNameTest5");
                properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
                properties.put("serviceLevelViolation", false);
                calendar = new GregorianCalendar();
                calendar.set(2012, 11, 14, 17, 27, 17);
                properties.put("timestamp", calendar);                
                session.createEntry(properties);  
                
                session.commit();
                session.close();
            }
            catch(Exception ex){
                session.rollback();
                session.close();
                ex.printStackTrace();
            }
            initDone = true;
        }
    }

    /**
     * Test the getSlaOrOlaIndicators REST operation with no query params
     */
    @Test
    public void getSlaOrOlaIndicatorsSimpleTest(){
        logTestName(logger);
        
        // Run first test request to get all indicators
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators");
        SlaOrOlaIndicators slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        SlaOrOlaIndicator indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);

        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals(SERVICE_LEVEL_HEALTH, indicator.getServiceLevelHealth().toString());
    }

    /**
     * Test the getSlaOrOlaIndicators REST operation with query params
     */
    @Test
    public void getSlaOrOlaIndicatorsComplexTest(){
        logTestName(logger);
        
        // Run first test request with endpointId and slaOrOlaName params
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("endpointId", "test").queryParam("slaOrOlaName", INDICATOR_NAME);
        SlaOrOlaIndicators slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        SlaOrOlaIndicator indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);

        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals(SERVICE_LEVEL_HEALTH, indicator.getServiceLevelHealth().toString());
        
        // Run second test request with endpointId and date range params
        Calendar periodStart = new GregorianCalendar();
        periodStart.clear();
        periodStart.set(2012, 11, 1, 0, 0, 1);
        Calendar periodEnd = new GregorianCalendar();
        periodEnd.clear();
        periodEnd.set(2012, 11, 30, 23, 59, 59);
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("endpointId", "test").queryParam("periodStart", dateFormater.format(periodStart.getTime())).queryParam("periodEnd", dateFormater.format(periodEnd.getTime()));
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(2, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);        

        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals(SERVICE_LEVEL_HEALTH, indicator.getServiceLevelHealth().toString());
        
    }
    
    /**
     * Test the updateSlaOlaIndicators REST operation
     */
    @Test
    public void updateSlaOlaIndicatorsTest(){
        logTestName(logger);
        
        // Check original values
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("endpointId", "test").queryParam("slaOrOlaName", "testSlaIndicator");
        SlaOrOlaIndicators slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        SlaOrOlaIndicator indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);
        Assert.assertEquals("test", indicator.getEndpointId());
        Assert.assertEquals("testSlaIndicator", indicator.getSlaOrOlaName());
        Assert.assertEquals("gold", indicator.getServiceLevelHealth().toString());
        Assert.assertEquals(false, indicator.isServiceLevelViolation());
        
        // Run update test request
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/updateSlaOlaIndicators");
        
        SlaOrOlaIndicators slaOrOlaIndicatorsUpdate = new SlaOrOlaIndicators();
        SlaOrOlaIndicator indicatorUpdate = new SlaOrOlaIndicator();
        indicatorUpdate.setEndpointId(ENDPOINT_ID);
        indicatorUpdate.setSlaOrOlaName(INDICATOR_NAME);
        indicatorUpdate.setServiceLevelViolation(true);
        indicatorUpdate.setServiceLevelHealth(ServiceLevelHealth.bronze);
        Calendar calendar = new GregorianCalendar();
        //calendar.set(2012, 12, 13, 17, 17, 45);
        indicatorUpdate.setTimestamp(calendar.getTime());
        slaOrOlaIndicatorsUpdate.getSlaOrOlaIndicatorList().add(indicatorUpdate);
        discoveryRequest.post(slaOrOlaIndicatorsUpdate);

        // Check update
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("endpointId", "test").queryParam("slaOrOlaName", "testSlaIndicator");
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);
        Assert.assertEquals("test", indicator.getEndpointId());
        Assert.assertEquals("testSlaIndicator", indicator.getSlaOrOlaName());
        Assert.assertEquals("bronze", indicator.getServiceLevelHealth().toString());
        Assert.assertEquals(true, indicator.isServiceLevelViolation());        
        

        // Run creation test request
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/updateSlaOlaIndicators");
        
        SlaOrOlaIndicators slaOrOlaIndicatorsCreate = new SlaOrOlaIndicators();
        SlaOrOlaIndicator indicatorCreate = new SlaOrOlaIndicator();
        indicatorCreate.setEndpointId("anotherEndpoint");
        indicatorCreate.setSlaOrOlaName("anotherIndicator");
        indicatorCreate.setServiceLevelViolation(true);
        indicatorCreate.setServiceLevelHealth(ServiceLevelHealth.silver);
        calendar = new GregorianCalendar();
        //calendar.set(2012, 10, 10, 23, 47, 13);
        indicatorCreate.setTimestamp(calendar.getTime());
        slaOrOlaIndicatorsCreate.getSlaOrOlaIndicatorList().add(indicatorCreate);
        discoveryRequest.post(slaOrOlaIndicatorsCreate);        

        // Check creation
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators");
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(2, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("endpointId", "anotherEndpoint").queryParam("slaOrOlaName", "anotherIndicator");
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);
        Assert.assertEquals("anotherEndpoint", indicator.getEndpointId());
        Assert.assertEquals("anotherIndicator", indicator.getSlaOrOlaName());
        Assert.assertEquals("silver", indicator.getServiceLevelHealth().toString());
        Assert.assertEquals(true, indicator.isServiceLevelViolation());        
    }
    
    @Test
    public void getSlaOrOlaIndicatorsTestWithDateRangeAndPagination(){
        logTestName(logger);
        
        // Required to avoid problem with indicators and periodEnd in the same second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Run first test request
        Client client = createAuthenticatedHTTPClient();
        
        // Run test request with date range params
        Calendar periodStart = new GregorianCalendar();
        periodStart.clear();
        periodStart.set(2012, 0, 1, 0, 0, 1);
        Calendar periodEnd = new GregorianCalendar();
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        WebResource discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("periodStart", dateFormater.format(periodStart.getTime())).queryParam("periodEnd", dateFormater.format(periodEnd.getTime()));
        SlaOrOlaIndicators slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        // with no pagination, expect to get 5 indicators
        Assert.assertEquals(5, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());

        // Re-Run test request with date range params and pagination params to get the first page
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("periodStart", dateFormater.format(periodStart.getTime())).queryParam("periodEnd", dateFormater.format(periodEnd.getTime())).queryParam("pageSize", "3").queryParam("pageStart", "0");
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        // Get the first page, expect to get three indicators
        Assert.assertEquals(3, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());

        // Re-Run test request with date range params and pagination params to get the second page
        discoveryRequest = client.resource(endpointStateService.getRootURL()).path("/getSlaOrOlaIndicators").queryParam("periodStart", dateFormater.format(periodStart.getTime())).queryParam("periodEnd", dateFormater.format(periodEnd.getTime())).queryParam("pageSize", "3").queryParam("pageStart", "1");
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        // Get the first page, expect to get three indicators
        Assert.assertEquals(2, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());        
        
    }    
    
    
}
