/**
 * 
 */
package org.easysoa.registry.integration;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
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

    private String simpleRegistryServiceUrl = this.getURL(SimpleRegistryServiceImpl.class);
    
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

    public static final EndpointId ENDPOINT_TEST_WITHOUT_POPRTTYPE_ID = 
            new EndpointId("Test", "http://localhost:8658/Test");

    public static final EndpointId ENDPOINT_TEST = 
            new EndpointId("Test", "http://localhost:8659/Test");

    public static final EndpointId ENDPOINT_INTEGRATION = 
            new EndpointId("Integration", "http://192.168.0.1:8659/Test");

    public static final EndpointId ENDPOINT_PRODUCTION = 
            new EndpointId("Production", "http://vmtest:8659/Test");

    public static final EndpointId ENDPOINT_TEST_WITH_PLATFORM_METAS_ID = 
            new EndpointId("Test", "http://localhost:8660/Test");
    
    public static final String anotherTitle = "anotherTitle";
    public static final String anotherName = "anotherName";
    public static final String anotherDescription = "anotherDescription";

    private static final String TEST_PORT_TYPE = "{namespace}portType";
    private static final String TEST_PORT_TYPE_WITH_PLATFORM_METAS = "{namespace}portTypeWithPlatformMetas";
    
    public static boolean initDone = false;
    
    /**
     * Init the tests
     * @throws Exception
     */
    @Before
    public void init() throws Exception {
        // Fill repository for all tests
        if(!initDone){
            HashMap<String, Object> isProperties = new HashMap<String, Object>();

            // Add information service without wsdl
            DocumentModel infoServiceWithoutWsdl = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_TEST_WITHOUT_POPRTTYPE_ID, isProperties, null);

            // Add information service with wsdl
            isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
            isProperties.put(WsdlInfoFacet.XPATH_WSDL_PORTTYPE_NAME, TEST_PORT_TYPE);
            DocumentModel infoService = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_TEST_ID, isProperties, null);

            // Add information service with wsdl & file, platform & common metas
            isProperties.put(WsdlInfoFacet.XPATH_WSDL_PORTTYPE_NAME, TEST_PORT_TYPE_WITH_PLATFORM_METAS);
            isProperties.put(Platform.XPATH_LANGUAGE, Platform.LANGUAGE_JAVA);
            isProperties.put("platform:build", "Maven"); // TODO
            //isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
            isProperties.put("platform:deliverableNature", "Maven");
            isProperties.put("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
            //OPT FraSCAtiStudio platform
            // Add information service with wsdl & file, platform & common metas
            DocumentModel infoServiceWithMetas = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_TEST_WITH_PLATFORM_METAS_ID, isProperties, null);
            infoServiceWithMetas.setPropertyValue("dc:title", anotherTitle);
            infoServiceWithMetas.setPropertyValue("dc:description", anotherDescription);
            // Add a associated wsdl file
            StringBlob blob = new StringBlob("test blob content");
            blob.setFilename("testFile.wsdl");
            ArrayList<Serializable> fileMapList = new ArrayList<Serializable>();
            HashMap<String, Blob> fileMap = new HashMap<String, Blob>();
            fileMap.put("file", blob);
            fileMapList.add(fileMap);
            infoServiceWithMetas.setPropertyValue("files", fileMapList);
            infoServiceWithMetas = documentManager.saveDocument(infoServiceWithMetas);
            
            // Add endpoints
            HashMap<String, Object> epProperties = new HashMap<String, Object>();
            //isProperties.put("dc:title", "ns:endpointTest"); // TODO
            epProperties.put("dc:description", "this is an endpoint to be used...");
            epProperties.put(Endpoint.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS); // TODO better ?!?
            // Associate endpoint with information service
            //isProperties.put("impl:providedInformationService", infoServiceWithMetas.getId()); // TODO matching should be done
            discoveryService.runDiscovery(documentManager, ENDPOINT_TEST_WITHOUT_POPRTTYPE_ID, epProperties, null); // TODO or by providing parent IS ??
            epProperties.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, TEST_PORT_TYPE);
            discoveryService.runDiscovery(documentManager, ENDPOINT_TEST, epProperties, null);
            discoveryService.runDiscovery(documentManager, ENDPOINT_INTEGRATION, epProperties, null);
            discoveryService.runDiscovery(documentManager, ENDPOINT_PRODUCTION, epProperties, null);
            epProperties.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, TEST_PORT_TYPE_WITH_PLATFORM_METAS);
            discoveryService.runDiscovery(documentManager, ENDPOINT_TEST_WITH_PLATFORM_METAS_ID, epProperties, null);
            
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
        WebResource discoveryRequest = client.resource(simpleRegistryServiceUrl).path("/queryWSDLInterfaces").queryParam("search", "test").queryParam("subProjectId", Subproject.DEFAULT_SUBPROJECT_PATH + "_v");
        ServiceInformations serviceInformations = discoveryRequest.get(ServiceInformations.class);
        // Check a result is returned
        Assert.assertNotNull(serviceInformations);

        // Check result
        ServiceInformation firstServiceInformation = serviceInformations.getServiceInformationList().get(0);
        Assert.assertEquals(INFORMATIONSERVICE_TEST_WITHOUT_POPRTTYPE_ID.getName(), firstServiceInformation.getSoaName());
        Assert.assertEquals(null, firstServiceInformation.getWsdlPortType());
        Assert.assertEquals(null, firstServiceInformation.getWsdlServiceName());
       
        ServiceInformation secondServiceInformation = serviceInformations.getServiceInformationList().get(1);
        Assert.assertEquals(INFORMATIONSERVICE_TEST_ID.getName(), secondServiceInformation.getSoaName());
        Assert.assertEquals(TEST_PORT_TYPE, secondServiceInformation.getWsdlPortType());
        Assert.assertEquals(null, secondServiceInformation.getWsdlServiceName());

        // Run second test request
        discoveryRequest = client.resource(simpleRegistryServiceUrl).path("/queryWSDLInterfaces").queryParam("search", "another");
        serviceInformations = discoveryRequest.get(ServiceInformations.class);
        // Check a result is returned
        Assert.assertNotNull(serviceInformations);        

        // Check result
        firstServiceInformation = serviceInformations.getServiceInformationList().get(0);
        Assert.assertEquals(INFORMATIONSERVICE_TEST_WITH_PLATFORM_METAS_ID.getName(), firstServiceInformation.getSoaName());
        Assert.assertEquals("anotherDescription", firstServiceInformation.getDescription());
        
        // Run third test request
        discoveryRequest = client.resource(simpleRegistryServiceUrl).path("/queryWSDLInterfaces");
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
        WebResource discoveryRequest = client.resource(simpleRegistryServiceUrl).path("/queryEndpoints");
        EndpointInformations endpointInformations = discoveryRequest.get(EndpointInformations.class);
        
        Assert.assertNotNull(endpointInformations);

        EndpointInformation firstEndpointInformation = endpointInformations.getEndpointInformationList().get(0);
        Assert.assertEquals(ENDPOINT_TEST_WITHOUT_POPRTTYPE_ID.getName(), firstEndpointInformation.getEnvironment() + ":" + firstEndpointInformation.getName());
        Assert.assertEquals("Test", firstEndpointInformation.getEnvironment());
        Assert.assertEquals(ENDPOINT_TEST_WITHOUT_POPRTTYPE_ID.getUrl(), firstEndpointInformation.getEndpointUrl());
    }
    
    @Test
    public void queryServicesWithEndpointTest(){
        logTestName(logger);
        
        // Run request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(simpleRegistryServiceUrl).path("/queryServicesWithEndpoints");
        ServiceInformations serviceInformations = discoveryRequest.get(ServiceInformations.class);
        
        Assert.assertNotNull(serviceInformations);
        
        ServiceInformation firstServiceInformation = serviceInformations.getServiceInformationList().get(2);
        Assert.assertEquals("anotherTitle", firstServiceInformation.getName());
       
        EndpointInformations endpointInformations = firstServiceInformation.getEndpoints();
        Assert.assertNotNull(endpointInformations);
        EndpointInformation firstEndpointInformation = endpointInformations.getEndpointInformationList().get(0);
        Assert.assertEquals(ENDPOINT_TEST_WITH_PLATFORM_METAS_ID.getName(), firstEndpointInformation.getEnvironment() + ":" + firstEndpointInformation.getName());
        
    }
    
}
