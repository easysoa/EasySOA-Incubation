package org.easysoa.registry.rest;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.runtime.test.runner.Deploy;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Deploy("org.easysoa.registry.rest.server")
public class RegistryApiTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(RegistryApiTest.class);

    private final int SERVICE_COUNT = 5;

    private SoaNodeId deliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa:deliverable");

    
    @Before
    public void initDefaultSubprojectId() throws Exception {
        logTestName(logger);
        defaultSubprojectId = null;
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Component.DOCTYPE,
                "ComponentForDefaultSubprojectIdInit"), null, null);

        // Fetch disco'd Component
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(discoveryApiUrl)
                .path(Component.DOCTYPE).path("ComponentForDefaultSubprojectIdInit");
        SoaNodeInformation soaNodeInformation = discoveryRequest.get(SoaNodeInformation.class);

        defaultSubprojectId = soaNodeInformation.getSubprojectId();
        Assert.assertNotNull(defaultSubprojectId, "defaultSubprojectId should not be null");

        // Fill repository for all tests
        for (int i = 0; i < SERVICE_COUNT; i++) {
            discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE,
                    "MyService" + i), null, null);
        }
        documentManager.save();
    }

    @Test
    public void getOne() throws Exception {
        logTestName(logger);

        // Fetch one service
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(discoveryApiUrl)
                .path(InformationService.DOCTYPE).path("MyService0");
        SoaNodeInformation soaNodeInformation = discoveryRequest.get(SoaNodeInformation.class);

        // Check result
        Assert.assertEquals("Returned SoaNode must have the expected ID",
        		new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyService0").toString(),
                soaNodeInformation.getSoaNodeId().toString());
        Map<String, Serializable> properties = soaNodeInformation.getProperties();
        Assert.assertTrue("Properties must be provided for the document", properties != null
                && !properties.isEmpty());
        Assert.assertEquals("Valid properties must be returned", "MyService0",
                properties.get("dc:title"));
    }

    @Test
    public void testJacksonApiOnActualModel() throws Exception {
        logTestName(logger);

        // Fetch one service
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(discoveryApiUrl)
                .path(InformationService.DOCTYPE).path("MyService0");
        SoaNodeInformation soaNodeInformation = discoveryRequest.get(SoaNodeInformation.class);

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter prettyPrintWriter = mapper.defaultPrettyPrintingWriter();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        prettyPrintWriter.writeValue(bos, soaNodeInformation);
        String serialized = bos.toString();
        logger.info(serialized);
        Assert.assertTrue(serialized != null && !serialized.trim().isEmpty());
    }

    @Test
    public void testJaxbApiOnActualModel() throws Exception {
        logTestName(logger);

        // Fetch one service
        RegistryApi xmlRegistryApi = clientBuilder.constructRegistryXmlApi();
        SoaNodeInformation soaNodeInformation = xmlRegistryApi.get(null, InformationService.DOCTYPE, "MyService0");

        JAXBContext jc = JAXBContext.newInstance(SoaNodeInformation.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(soaNodeInformation, bos);
        String marshalled = bos.toString();
        logger.info(marshalled);
        Assert.assertTrue(marshalled != null && !marshalled.trim().isEmpty());
    }

    @Test
    public void getList() throws Exception {
        logTestName(logger);

        // Run request
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(discoveryApiUrl).path(InformationService.DOCTYPE);
        SoaNodeInformations soaNodes = discoveryRequest.get(SoaNodeInformations.class);

        // Check result
        Assert.assertEquals("All registered services must be found", SERVICE_COUNT, soaNodes.getSoaNodeInformationList().size());
        SoaNodeId firstSoaNodeId = soaNodes.getSoaNodeInformationList().get(0).getSoaNodeId();
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
        Builder discoveryRequest = client.resource(discoveryApiUrl).type(MediaType.APPLICATION_JSON);
        OperationResult result = discoveryRequest.post(OperationResult.class, soaNodeInfo);

        // Check result
        Assert.assertTrue("Creation request must be successful", result.isSuccessful());
        SoaNodeInformation resultSoaNodeInfo = client.resource(discoveryApiUrl)
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
        Builder discoveryRequest = client.resource(discoveryApiUrl)
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
        Builder discoveryRequest = client.resource(discoveryApiUrl)
                .path("query")
                .type(MediaType.TEXT_PLAIN);
       SoaNodeInformations foundEndpoints = discoveryRequest.post(SoaNodeInformations.class,
    		   "SELECT * FROM Endpoint WHERE " + Endpoint.XPATH_URL +" = 'EndpointToQuery'");
       Assert.assertEquals(1, foundEndpoints.getSoaNodeInformationList().size());
       Assert.assertEquals(endpointToQuery.getEnvironment(), foundEndpoints.getSoaNodeInformationList().get(0).getProperties().get(Endpoint.XPATH_ENDP_ENVIRONMENT));

    }
}
