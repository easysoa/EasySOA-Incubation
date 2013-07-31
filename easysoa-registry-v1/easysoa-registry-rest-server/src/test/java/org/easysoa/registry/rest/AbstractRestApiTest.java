package org.easysoa.registry.rest;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.client.PathExtractor;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;


/**
 * To be extended by EasySOA REST API tests.
 * 
 * To test consecutive steps of a workflow, add
 * @RepositoryConfig(cleanup = Granularity.CLASS)
 * in order to keep Nuxeo repository state between test methods.
 * 
 * @author mdutoo
 *
 */
public abstract class AbstractRestApiTest extends AbstractWebEngineTest {

    protected String discoveryApiUrl = EasySOAWebEngineFeature.NUXEO_SITES_URL
            + PathExtractor.getPath(RegistryApi.class);
    
    @Inject
    protected DiscoveryService discoveryService;

    @Inject
    protected DocumentService documentService;

    protected ClientBuilder clientBuilder;
    
    public AbstractRestApiTest() {
        this.clientBuilder = new ClientBuilder();
        this.clientBuilder.setNuxeoSitesUrl(EasySOAWebEngineFeature.NUXEO_SITES_URL);
    }
    
    public Client createAuthenticatedHTTPClient() {
        return this.clientBuilder.createClient();
    }
    
    
}
