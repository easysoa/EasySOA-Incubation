package org.easysoa.registry.rest.client;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

@Deploy("org.easysoa.registry.rest.server")
public class RestClientTest extends AbstractWebEngineTest {

    @Inject
    DiscoveryService discoveryService; // only for initDefaultSubprojectId()
    
    private RegistryApi registryApi;
    
    public RestClientTest() {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(EasySOAWebEngineFeature.NUXEO_URL);
        registryApi = clientBuilder.constructRegistryApi();
        
        this.clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());
    }

    
    // COPIED FROM AbstractRestApiTest
    // BEWARE this code can't be shared in easysoa-registry-rest-core tests else maven build pbs
    protected String discoveryApiUrl = EasySOAWebEngineFeature.NUXEO_URL + "easysoa/registry";
    
    private final ClientConfig clientConfig;
    
    public Client createAuthenticatedHTTPClient() {
        return createAuthenticatedHTTPClient("Administrator", "Administrator");
    }
    
    public Client createAuthenticatedHTTPClient(String username, String password) {
        Client client = Client.create(this.clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        return client;
    }
    // - COPIED FROM AbstractRestApiTest
    

    @Before
    public void initDefaultSubprojectId() throws Exception {
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Component.DOCTYPE,
                "ComponentForDefaultSubprojectIdInit"), null, null);

        // Fetch disco'd Component
        Client client = createAuthenticatedHTTPClient();
        WebResource discoveryRequest = client.resource(this.discoveryApiUrl)
                .path(Component.DOCTYPE).path("ComponentForDefaultSubprojectIdInit");
        SoaNodeInformation soaNodeInformation = discoveryRequest.get(SoaNodeInformation.class);
        
        defaultSubprojectId = soaNodeInformation.getSubprojectId();
    }
    
    @Test
    public void testClientCreation() throws Exception {
        Assert.assertNotNull("RegistryApi instanciation must be successful", registryApi);
        
        // Create some document
        SoaNodeId myServiceId = new SoaNodeId(defaultSubprojectId, InformationService.DOCTYPE, "MyService");
        OperationResult result = registryApi.post(new SoaNodeInformation(myServiceId, null, null));
        Assert.assertTrue("Creation must be successful", result.isSuccessful());
        
        // Fetch it
        SoaNodeInformation foundSoaNode = registryApi.get(myServiceId.getSubprojectId(),
                myServiceId.getType(), myServiceId.getName());
        Assert.assertNotNull("Created SoaNode must have been found by the client", foundSoaNode);
        Assert.assertEquals("Found document must be the expected Service", myServiceId, foundSoaNode.getSoaNodeId());
    }

}
