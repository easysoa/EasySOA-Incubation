package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class RegistryApiTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(RegistryApiTest.class);

    private RegistryApiHelper discoveryApi = new RegistryApiHelper(this);

    @Inject
    DiscoveryService discoveryService;

    @Inject
    DocumentService documentService;

    private final int SERVICE_COUNT = 5;

    private SoaNodeId deliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa:deliverable");

    @Test
    public void getOne() throws Exception {
        logTestName(logger);

        // Fill repository for all tests
        for (int i = 0; i < SERVICE_COUNT; i++) {
            discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE,
                    "MyService" + i), null, null);
        }
        documentManager.save();

        // Fetch one service
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(discoveryApi.getRootURL())
                .path(InformationService.DOCTYPE).path("MyService0");
        SoaNodeInformation soaNodeInformation = discoveryRequest.get(SoaNodeInformation.class);

        // Check result
        Assert.assertEquals("Returned SoaNode must have the expected ID", 
        		new SoaNodeId(InformationService.DOCTYPE, "MyService0").toString(),
                soaNodeInformation.getSoaNodeId().toString());
        Map<String, Serializable> properties = soaNodeInformation.getProperties();
        Assert.assertTrue("Properties must be provided for the document", properties != null
                && !properties.isEmpty());
        Assert.assertEquals("Valid properties must be returned", "MyService0",
                properties.get("dc:title"));
    }

    @Test
    public void getList() throws Exception {
        logTestName(logger);

        // Run request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(discoveryApi.getRootURL()).path(InformationService.DOCTYPE);
        SoaNodeInformation[] soaNodes = discoveryRequest.get(SoaNodeInformation[].class);

        // Check result
        Assert.assertEquals("All registered services must be found", SERVICE_COUNT, soaNodes.length);
        SoaNodeId firstSoaNodeId = soaNodes[0].getSoaNodeId();
        Assert.assertEquals("'type' property must be provided for each document", InformationService.DOCTYPE,
                firstSoaNodeId.getType());
        Assert.assertEquals("'name' property must be provided for each document", "MyService0",
                firstSoaNodeId.getName());
    }

    @Test
    public void create() throws Exception {
        logTestName(logger);

        // Create service
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(Deliverable.XPATH_TITLE, "My Deliverable");
        properties.put(Deliverable.XPATH_APPLICATION, "myapp");
        SoaNodeInformation soaNodeInfo = new SoaNodeInformation(deliverableId, properties, null);

        // Run request
        Client client = createAuthenticatedHTTPClient();
        Builder discoveryRequest = client.resource(discoveryApi.getRootURL()).type(MediaType.APPLICATION_JSON);
        OperationResult result = discoveryRequest.post(OperationResult.class, soaNodeInfo);

        // Check result
        Assert.assertTrue("Creation request must be successful", result.isSuccessful());
        SoaNodeInformation resultSoaNodeInfo = client.resource(discoveryApi.getRootURL())
                    .path(deliverableId.getType())
                    .path(deliverableId.getName())
                    .get(SoaNodeInformation.class);
        SoaNodeId id = resultSoaNodeInfo.getSoaNodeId();
        Assert.assertEquals("'type' property must be provided for the document",
                deliverableId.getType(), id.getType());
        Assert.assertEquals("'name' property must be provided for the document",
                deliverableId.getName(), id.getName());
        Map<String, Serializable> resultProperties = resultSoaNodeInfo.getProperties();
        Assert.assertTrue("Properties must be provided for the document", properties != null && !properties.isEmpty());
        Assert.assertEquals("Valid properties must be returned", properties.get(Deliverable.XPATH_TITLE),
                resultProperties.get(Deliverable.XPATH_TITLE));
        Assert.assertEquals("Valid properties must be returned", properties.get(Deliverable.XPATH_APPLICATION),
                resultProperties.get(Deliverable.XPATH_APPLICATION));
    }

    @Test
    public void delete() throws Exception {
        logTestName(logger);

        // Run discovery to make a proxy to delete
        SoaNodeId endpointId = new EndpointId("Production", "MyEndpoint");
        discoveryService.runDiscovery(documentManager, endpointId, null, Arrays.asList(deliverableId));

        // Delete only proxy (TODO test as array)
        Client client = createAuthenticatedHTTPClient();
        Builder discoveryRequest = client.resource(discoveryApi.getRootURL())
                .path(deliverableId.getType()).path(deliverableId.getName())
                .path(endpointId.getType()).path(endpointId.getName())
                .type(MediaType.APPLICATION_JSON);
        OperationResult deletionResult = discoveryRequest.delete(OperationResult.class);

        // Check result
        Assert.assertTrue("Deletion must be marked as successful", deletionResult.isSuccessful());
        Assert.assertTrue("Proxy must not be available after deletion",
                documentService.findProxies(documentManager, deliverableId).isEmpty());
    }

    @Test
    public void query() throws Exception {
        logTestName(logger);
        
        EndpointId endpointToQuery = new EndpointId("Production", "EndpointToQuery");
        documentService.create(documentManager, endpointToQuery);
        
        Client client = createAuthenticatedHTTPClient();
        Builder discoveryRequest = client.resource(discoveryApi.getRootURL())
                .path("query")
                .type(MediaType.TEXT_PLAIN);
       SoaNodeInformation[] foundEndpoints = discoveryRequest.post(SoaNodeInformation[].class, "SELECT * FROM Endpoint WHERE endp:url = 'EndpointToQuery'");
       Assert.assertTrue(foundEndpoints.length == 1);
       Assert.assertEquals(endpointToQuery.getName(), foundEndpoints[0].getProperties().get(Endpoint.XPATH_TITLE));
        
    }
}
