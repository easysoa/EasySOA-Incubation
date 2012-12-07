/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.integration.EndpointInformation;
import org.easysoa.registry.rest.integration.EndpointInformations;
import org.easysoa.registry.rest.integration.ServiceInformation;
import org.easysoa.registry.rest.integration.ServiceInformations;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
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

    public static final SoaNodeId INFORMATIONSERVICE_TEST_WITHOUT_POPRTTYPE_ID = 
            new SoaNodeId(InformationService.DOCTYPE, "ns:testWithoutWsdl");

    public static final SoaNodeId INFORMATIONSERVICE_TEST_ID = 
            new SoaNodeId(InformationService.DOCTYPE, "ns:test");

    public static final SoaNodeId INFORMATIONSERVICE_TEST_WITH_PLATFORM_METAS_ID = 
            new SoaNodeId(InformationService.DOCTYPE, "ns:testWithPlatformMetas");

    public static final SoaNodeId ENDPOINT_TEST = 
            new EndpointId("Test", "http://localhost:8659/Test");
    
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
            HashMap<String, Object> isProperties = new HashMap<String, Object>();
            
            discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_TEST_WITHOUT_POPRTTYPE_ID, isProperties, null);
            
            isProperties.put(WsdlInfoFacet.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType");
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
            infoService = documentManager.saveDocument(infoService);
            
            // Add endpoint
            isProperties = new HashMap<String, Object>();
            isProperties.put("dc:title", "ns:endpointTest");
            // Associate endpoint with information service
            isProperties.put("impl:providedInformationService", infoService.getId());
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
        ServiceInformations serviceInformations = discoveryRequest.get(ServiceInformations.class);
        // Check a result is returned
        Assert.assertNotNull(serviceInformations);

        // Check result
        ServiceInformation firstServiceInformation = serviceInformations.getServiceInformationList().get(0);
        Assert.assertEquals("ns:testWithoutWsdl", firstServiceInformation.getSoaName());
       
        ServiceInformation secondServiceInformation = serviceInformations.getServiceInformationList().get(1);
        Assert.assertEquals("ns:test", secondServiceInformation.getSoaName());

        // Run second test request
        discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryWSDLInterfaces").queryParam("search", "another");
        serviceInformations = discoveryRequest.get(ServiceInformations.class);
        // Check a result is returned
        Assert.assertNotNull(serviceInformations);        

        // Check result
        firstServiceInformation = serviceInformations.getServiceInformationList().get(0);
        Assert.assertEquals("anotherName", firstServiceInformation.getSoaName());
        Assert.assertEquals("anotherDescription", firstServiceInformation.getDescription());
        
        // Run third test request
        discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryWSDLInterfaces");
        serviceInformations = discoveryRequest.get(ServiceInformations.class);
        
        Assert.assertNotNull(serviceInformations);
        Assert.assertEquals(3, serviceInformations.getServiceInformationList().size()); // 3 Informations services should be returned
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
        EndpointInformations endpointInformations = discoveryRequest.get(EndpointInformations.class);
        
        Assert.assertNotNull(endpointInformations);

        EndpointInformation firstEndpointInformation = endpointInformations.getEndpointInformationList().get(0);
        Assert.assertEquals("ns:endpointTest", firstEndpointInformation.getName());
        Assert.assertEquals("Test", firstEndpointInformation.getEnvironment());
        Assert.assertEquals("http://localhost:8659/Test", firstEndpointInformation.getEndpointUrl());
    }
    
    @Test
    public void queryServicesWithEndpointTest(){
        logTestName(logger);
        
        // Run request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(simpleRegistryService.getRootURL()).path("/queryServicesWithEndpoints");
        ServiceInformations serviceInformations = discoveryRequest.get(ServiceInformations.class);
        
        Assert.assertNotNull(serviceInformations);
        
        ServiceInformation firstServiceInformation = serviceInformations.getServiceInformationList().get(2);
        Assert.assertEquals("anotherTitle", firstServiceInformation.getName());
       
        EndpointInformations endpointInformations = firstServiceInformation.getEndpoints();
        Assert.assertNotNull(endpointInformations);
        EndpointInformation firstEndpointInformation = endpointInformations.getEndpointInformationList().get(0);
        Assert.assertEquals("ns:endpointTest", firstEndpointInformation.getName());
        
    }
    
}
