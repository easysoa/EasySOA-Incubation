/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.marshalling.WSDLInformation;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.WsdlInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Tests for Simple registry service
 * 
 * @author jguillemotte
 *
 */
@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class SimpleRegistryServiceTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(SimpleRegistryServiceTest.class);

    private SimpleRegistryServiceHelper simpleRegistryService = new SimpleRegistryServiceHelper(this);
    
    @Inject
    DocumentService documentService;
    
    @Inject
    DiscoveryService discoveryService;    
    
    private final static int SERVICE_COUNT = 5;

    public static final SoaNodeId INFORMATIONSERVICE_TEST_WITHOUT_POPRTTYPE_ID = 
            new SoaNodeId(InformationService.DOCTYPE, "ns:testWithoutWsdl");

    public static final SoaNodeId INFORMATIONSERVICE_TEST_ID = 
            new SoaNodeId(InformationService.DOCTYPE, "ns:test");

    public static final SoaNodeId INFORMATIONSERVICE_TEST_WITH_PLATFORM_METAS_ID = 
            new SoaNodeId(InformationService.DOCTYPE, "ns:testWithPlatformMetas");

    public static final SoaNodeId ENDPOINT_TEST = 
            new SoaNodeId(Endpoint.DOCTYPE, "ns:endpointTest");
    
    public static final String anotherTitle = "anotherTitle";
    public static final String anotherName = "anotherName";
    public static final String anotherDescription = "anotherDescription";
    
    public static boolean initDone = false;
    
    /**
     * Init the tests
     * @throws Exception
     */
    @Before
    public void init() throws Exception {
        // Fill repository for all tests
        if(!initDone){
            // Add information service
            /*for (int i = 0; i < SERVICE_COUNT; i++) {
                discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE,
                        "MyService" + i), null, null);
            }*/
    
            HashMap<String, Object> isProperties = new HashMap<String, Object>();
            
            discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_TEST_WITHOUT_POPRTTYPE_ID, isProperties, null);
            
            isProperties.put(WsdlInfo.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType");
            discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_TEST_ID, isProperties, null);
            
            //isProperties.put(WsdlInfo.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType");
            isProperties.put("platform:language", "Java");
            isProperties.put("platform:build", "Maven");
            isProperties.put("platform:serviceLanguage", "JAXWS");
            isProperties.put("platform:deliverableNature", "Maven");
            isProperties.put("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
            //OPT FraSCAtiStudio platform
            DocumentModel infoService = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_TEST_WITH_PLATFORM_METAS_ID, isProperties, null);
            infoService.setPropertyValue(SoaNode.XPATH_SOANAME, anotherName);
            infoService.setPropertyValue("dc:title", anotherTitle);
            infoService.setPropertyValue("dc:description", anotherDescription);
            // Add a associated wsdl file
            /*StringBlob blob = new StringBlob("test blob content");
            blob.setFilename("testFile.wsdl");
            infoService.setPropertyValue("file", blob);*/
            documentManager.saveDocument(infoService);
            
            // Add endpoint
            isProperties = new HashMap<String, Object>();
            isProperties.put("env:environment", "Test");
            isProperties.put("endp:url", "http://localhost:8659/Test");
            discoveryService.runDiscovery(documentManager, ENDPOINT_TEST, isProperties, null);
            
            documentManager.save();
            initDone = true;            
        }
    }
    
    /**
     * Test the queryWSDLInterfaces REST operation
     */
    @Test
    public void queryWSDLInterfacesTest(){
        logTestName(logger);

        // Run first test request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryWSDLInterfaces").queryParam("search", "test").queryParam("subProjectId", "test");
        WSDLInformation[] wsdlInformations = discoveryRequest.get(WSDLInformation[].class);
        // Check a result is returned
        Assert.assertNotNull(wsdlInformations);

        // Check result
        WSDLInformation firstWSDLInformation = wsdlInformations[0];
        Assert.assertEquals("ns:testWithoutWsdl", firstWSDLInformation.getSoaName());
       
        WSDLInformation secondWSDLInformation = wsdlInformations[1];
        Assert.assertEquals("ns:test", secondWSDLInformation.getSoaName());

        // Run second test request
        discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryWSDLInterfaces").queryParam("search", "another");
        wsdlInformations = discoveryRequest.get(WSDLInformation[].class);
        // Check a result is returned
        Assert.assertNotNull(wsdlInformations);        

        // Check result
        firstWSDLInformation = wsdlInformations[0];
        Assert.assertEquals("anotherName", firstWSDLInformation.getSoaName());
        Assert.assertEquals("anotherDescription", firstWSDLInformation.getDescription());
        //Assert.assertTrue(firstWSDLInformation.getWsdlDownloadUrl() != null && firstWSDLInformation.getWsdlDownloadUrl().contains("testFile.wsdl"));
        
        // Run third test request
        discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryWSDLInterfaces");
        wsdlInformations = discoveryRequest.get(WSDLInformation[].class);
        
        Assert.assertNotNull(wsdlInformations);
        Assert.assertEquals(3, wsdlInformations.length); // 3 Informations services should be returned
    }
    
    /**
     * Test the queryEndpoints REST operation
     */
    @Test
    public void queryEndpointsTest(){
        logTestName(logger);

        // Run request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryEndpoints");
        WSDLInformation[] wsdlInformations = discoveryRequest.get(WSDLInformation[].class);
        
        Assert.assertNotNull(wsdlInformations);

        WSDLInformation firstWSDLInformation = wsdlInformations[0];
        Assert.assertEquals("ns:endpointTest", firstWSDLInformation.getName());
        Assert.assertEquals("Test", firstWSDLInformation.getEnvironment());
        Assert.assertEquals("http://localhost:8659/Test", firstWSDLInformation.getEndpointUrl());
        // TODO : got a marshalling problem when not result is returned
        // See JSONMessageReader.readFrom method => if(jsonNode.get(0)!=null && jsonNode.get(0).has("projectID")){ (line 40)
        
        //WebResource discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryEndpoints").queryParam("search", "test").queryParam("subProjectId", "test");
        //WSDLInformation[] wsdlInformations = discoveryRequest.get(WSDLInformation[].class);
    }    
    
}
