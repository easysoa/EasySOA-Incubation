/**
 * 
 */
package org.easysoa.registry.integration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.integration.ServiceLevelHealth;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicator;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.RepositorySettings;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Test for Endpoint state service, also provide SQL Directory test helpers
 * 
 * @author jguillemotte
 *
 */
@Deploy("org.easysoa.registry.rest.server")
@LocalDeploy({ "org.easysoa.registry.rest.server:OSGI-INF/SlaOlaIndicatorsTest.xml" })
// NB. no @RepositoryConfig(cleanup = Granularity.CLASS) because some methods change state
// (update...() would make simple...() fail if executed first as it might be on jdk7 see #134 )
@RepositoryConfig(cleanup = Granularity.METHOD) // truly unitary tests :
// don't keep Nuxeo repository state between test methods
// (but doesn't clean up SQL directories by itself)
public class EndpointStateServiceTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(EndpointStateServiceTest.class);

    private String endpointStateServiceUrl = this.getURL(EndpointStateServiceImpl.class);
    
    @Inject
    DirectoryService directoryService;
    
    @Inject
    RepositorySettings repositorySettings;

    private boolean initDone = false;
    
    public final static String ENDPOINT_ID = "test";
    
    public final static String INDICATOR_NAME = "testSlaIndicator";
    public final static String ANOTHER_INDICATOR_NAME = "anotherTestSlaIndicator";
    
    public final static String SERVICE_LEVEL_HEALTH = "gold";

    
    /**
     * Cleans up the directory after each test if @RepositoryConfig(cleanup = Granularity.METHOD)
     * LATER reuse it in other directory tests
     * @throws Exception
     */
    @After
    public void cleanup() throws Exception {
        if (this.repositorySettings.getGranularity() == Granularity.METHOD) {
            Session session = directoryService.open(org.easysoa.registry.types.SlaOrOlaIndicator.DOCTYPE);
           
            try{
                DocumentModelList entries = session.getEntries();
                for (DocumentModel entry : entries) {
                    session.deleteEntry(entry);
                }
    
            } finally {
                // NB. container manages transaction, so no need to commit or rollback
                // (see doc of deprecated session.commit())
                session.close();
            }
        }
    }
    
    /**
     * Init the tests before each test or only once if not @RepositoryConfig(cleanup = Granularity.METHOD)
     * LATER reuse it in other directory tests
     * 
     * @throws Exception
     */
    @Before // NB. can't be @BeforeClass else can't access injected directoryService
    public void initDirectory() throws Exception {
        
        if(this.repositorySettings.getGranularity() == Granularity.METHOD || !initDone){
            
            Session session = directoryService.open(org.easysoa.registry.types.SlaOrOlaIndicator.DOCTYPE);
           
            try{
                doInitDirectory(session);
                
            } finally {
                // NB. container manages transaction, so no need to commit or rollback
                // (see doc of deprecated session.commit())
                session.close();
            }
            initDone = true;
        }
    }
    

    private void doInitDirectory(Session session) throws Exception {
        Calendar currentDate = new GregorianCalendar();
        
        // Create a first SlaOrOlaIndicator (now)
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("endpointId", ENDPOINT_ID);
        properties.put("slaOrOlaName", INDICATOR_NAME);
        properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
        properties.put("serviceLevelViolation", false);
        Calendar calendar = new GregorianCalendar(); // now
        properties.put("timestamp", calendar);
        session.createEntry(properties);

        // Create a second SlaOrOlaIndicator (still today)  
        properties = new HashMap<String, Object>();
        properties.put("endpointId", ENDPOINT_ID);
        properties.put("slaOrOlaName", ANOTHER_INDICATOR_NAME);
        properties.put("serviceLeveHealth", SERVICE_LEVEL_HEALTH);
        properties.put("serviceLevelViolation", true);
        calendar = new GregorianCalendar();
        calendar.clear();
        calendar.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH), 8, 24, 37);
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
    }

    /**
     * Test the getSlaOrOlaIndicators REST operation with no query params
     */
    @Test
    public void getSlaOrOlaIndicatorsSimpleTest(){
        logTestName(logger);
        
        // Run first test request to get all indicators
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators");
        SlaOrOlaIndicators slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        // Was to avoid to have to reload each time the model. To remove when finished
        //Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        // returns only the current day's indicators :
        Assert.assertEquals(2, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
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
        WebResource discoveryRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators")
                .queryParam("endpointId", ENDPOINT_ID).queryParam("slaOrOlaName", INDICATOR_NAME);
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
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        discoveryRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators")
                .queryParam("endpointId", ENDPOINT_ID)
                .queryParam("periodStart", dateFormater.format(periodStart.getTime()));
                // NB. to mean "now", no "periodEnd" parameter at all is better
                // than dateFormater.format(new GregorianCalendar().getTime())
                // because it'll be initialized on server side therefore later
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(2, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size()); // TODO non-deterministic fail, maybe because filter < same second ?
        indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);        

        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals(SERVICE_LEVEL_HEALTH, indicator.getServiceLevelHealth().toString());
        
    }
    
    /**
     * Test the updateSlaOlaIndicators REST operation
     */
    //@Test // rather called in getSlaOrOlaIndicatorsTestWithDateRangeAndPagination()
    // which expects it else random junit test order on jdk7 may make it fail, see #134
    public void updateSlaOlaIndicatorsTest(){
        logTestName(logger);
        
        // Check original values
        Client client = createAuthenticatedHTTPClient();
        WebResource listRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators")
                .queryParam("endpointId", ENDPOINT_ID).queryParam("slaOrOlaName", INDICATOR_NAME);
        SlaOrOlaIndicators slaOrOlaIndicators = listRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());
        SlaOrOlaIndicator indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);
        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals("gold", indicator.getServiceLevelHealth().toString());
        Assert.assertEquals(false, indicator.isServiceLevelViolation());
        
        // Run update test request
        WebResource createUpdateRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators");
        
        SlaOrOlaIndicators slaOrOlaIndicatorsUpdate = new SlaOrOlaIndicators();
        SlaOrOlaIndicator indicatorUpdate = new SlaOrOlaIndicator();
        indicatorUpdate.setEndpointId(ENDPOINT_ID);
        indicatorUpdate.setSlaOrOlaName(INDICATOR_NAME);
        indicatorUpdate.setServiceLevelViolation(true);
        indicatorUpdate.setServiceLevelHealth(ServiceLevelHealth.bronze);
        indicatorUpdate.setTimestamp(indicator.getTimestamp());
        slaOrOlaIndicatorsUpdate.getSlaOrOlaIndicatorList().add(indicatorUpdate);
        createUpdateRequest.put(slaOrOlaIndicatorsUpdate);

        // Check update
        slaOrOlaIndicators = listRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size()); // nothing new created
        indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);
        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals("bronze", indicator.getServiceLevelHealth().toString());
        Assert.assertEquals(true, indicator.isServiceLevelViolation());
        

        // Run creation test request in an existing indicator but with a new timestamp
        SlaOrOlaIndicators slaOrOlaIndicatorsCreate = new SlaOrOlaIndicators();
        SlaOrOlaIndicator indicatorCreate = new SlaOrOlaIndicator();
        indicatorCreate.setEndpointId(ENDPOINT_ID);
        indicatorCreate.setSlaOrOlaName(INDICATOR_NAME);
        indicatorCreate.setServiceLevelViolation(true);
        indicatorCreate.setServiceLevelHealth(ServiceLevelHealth.silver);
        GregorianCalendar calendar = new GregorianCalendar(); // now
        indicatorCreate.setTimestamp(calendar.getTime());
        slaOrOlaIndicatorsCreate.getSlaOrOlaIndicatorList().add(indicatorCreate);
        createUpdateRequest.post(slaOrOlaIndicatorsCreate);

        // Check creation
        slaOrOlaIndicators = listRequest.get(SlaOrOlaIndicators.class);
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(2, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size()); // something new created  
        
        indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(1);
        Assert.assertEquals(ENDPOINT_ID, indicator.getEndpointId());
        Assert.assertEquals(INDICATOR_NAME, indicator.getSlaOrOlaName());
        Assert.assertEquals("silver", indicator.getServiceLevelHealth().toString());
        Assert.assertEquals(true, indicator.isServiceLevelViolation());
        

        // Run creation test request
        slaOrOlaIndicatorsCreate = new SlaOrOlaIndicators();
        indicatorCreate = new SlaOrOlaIndicator();
        indicatorCreate.setEndpointId("anotherEndpoint");
        indicatorCreate.setSlaOrOlaName("anotherIndicator");
        indicatorCreate.setServiceLevelViolation(true);
        indicatorCreate.setServiceLevelHealth(ServiceLevelHealth.silver);
        calendar = new GregorianCalendar(); // now
        indicatorCreate.setTimestamp(calendar.getTime());
        slaOrOlaIndicatorsCreate.getSlaOrOlaIndicatorList().add(indicatorCreate);
        createUpdateRequest.post(slaOrOlaIndicatorsCreate);        

        // Check creation
        WebResource anotherListRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators")
                .queryParam("endpointId", "anotherEndpoint").queryParam("slaOrOlaName", "anotherIndicator");
        slaOrOlaIndicators = anotherListRequest.get(SlaOrOlaIndicators.class); 
        
        Assert.assertNotNull(slaOrOlaIndicators);
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size()); // something new created       
        
        indicator = slaOrOlaIndicators.getSlaOrOlaIndicatorList().get(0);
        Assert.assertEquals("anotherEndpoint", indicator.getEndpointId());
        Assert.assertEquals("anotherIndicator", indicator.getSlaOrOlaName());
        Assert.assertEquals("silver", indicator.getServiceLevelHealth().toString());
        Assert.assertEquals(true, indicator.isServiceLevelViolation());
    }
    
    @Test
    public void getSlaOrOlaIndicatorsTestWithDateRangeAndPagination(){
        // add some more first
        updateSlaOlaIndicatorsTest();
        
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
        Calendar periodEnd = new GregorianCalendar(); // now
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        WebResource discoveryRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators")
                .queryParam("periodStart", dateFormater.format(periodStart.getTime())).queryParam("periodEnd", dateFormater.format(periodEnd.getTime()));
        SlaOrOlaIndicators slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        // with no pagination, expect to get 5 indicators
        Assert.assertEquals(7, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());

        // Re-Run test request with date range params and pagination params to get the first page
        discoveryRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators")
                .queryParam("periodStart", dateFormater.format(periodStart.getTime())).queryParam("periodEnd", dateFormater.format(periodEnd.getTime())).queryParam("pageSize", "3").queryParam("pageStart", "0");
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);        
        
        Assert.assertNotNull(slaOrOlaIndicators);
        // Get the first page, expect to get three indicators
        Assert.assertEquals(3, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());

        // Re-Run test request with date range params and pagination params to get the second page
        discoveryRequest = client.resource(endpointStateServiceUrl).path("/slaOlaIndicators")
                .queryParam("periodStart", dateFormater.format(periodStart.getTime())).queryParam("periodEnd", dateFormater.format(periodEnd.getTime())).queryParam("pageSize", "3").queryParam("pageStart", "2");
        slaOrOlaIndicators = discoveryRequest.get(SlaOrOlaIndicators.class);
        
        Assert.assertNotNull(slaOrOlaIndicators);
        // Get the first page, expect to get two indicators
        Assert.assertEquals(1, slaOrOlaIndicators.getSlaOrOlaIndicatorList().size());        
        
    }    
    
    
}
