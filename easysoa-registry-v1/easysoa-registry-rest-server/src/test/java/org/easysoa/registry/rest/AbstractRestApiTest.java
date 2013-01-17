package org.easysoa.registry.rest;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public abstract class AbstractRestApiTest extends AbstractWebEngineTest {

    protected String discoveryApiUrl = EasySOAWebEngineFeature.NUXEO_URL + "easysoa/registry";
    
    @Inject
    protected DiscoveryService discoveryService;

    @Inject
    protected DocumentService documentService;
    
    private final ClientConfig clientConfig;
    
    public AbstractRestApiTest() {
        this.clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());
    }
    
    public Client createAuthenticatedHTTPClient() {
        return createAuthenticatedHTTPClient("Administrator", "Administrator");
    }
    
    public Client createAuthenticatedHTTPClient(String username, String password) {
        Client client = Client.create(this.clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        return client;
    }
    
    
}
