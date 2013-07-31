package org.easysoa.registry.rest.client;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.RegistryJsonApi;
import org.easysoa.registry.rest.RegistryXmlApi;
import org.easysoa.registry.rest.integration.EndpointStateService;
import org.easysoa.registry.rest.integration.SimpleRegistryService;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ClientBuilder {
    
    public static final String LOCAL_NUXEO_URL = "http://localhost:8080/nuxeo/site/";
    
    private Set<Object> singletons;
    private String nuxeoUrl = LOCAL_NUXEO_URL;
    private String username = "Administrator";
    private String password = "Administrator";

    public ClientBuilder() {
        this.singletons = new HashSet<Object>();
        
        // obsolete, replaced by standard Jackson
        //this.singletons.add(new JsonMessageReader());
        //this.singletons.add(new JsonMessageWriter());

        // Jackson's provider :
        //ObjectMapper mapper = new ObjectMapper(); // allows to configure Jackson
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(/*mapper*/); // from jackson-jaxrs
        singletons.add(jacksonJsonProvider);
        
        // JAXB provider :
        // using Jersey's default one (Jettison I guess ? or Jackson also ??)
    }

    public void addSingleton(Object singleton) {
        this.singletons.add(singleton);
    }

    public void setNuxeoSitesUrl(String nuxeoUrl) {
        this.nuxeoUrl = nuxeoUrl;
    }
    
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public ClientConfig constructClientConfig() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().addAll(this.singletons);
        return clientConfig;
    }
    
    public WebResource constructNuxeoClientBase() {
        return createClient().resource(this.nuxeoUrl);
    }
    
    public RegistryApi constructRegistryApi() {
        return constructRegistryJsonApi();
    }
    
    public RegistryJsonApi constructRegistryJsonApi() {
        WebResource client = constructNuxeoClientBase();
        return WebResourceFactory.newResource(RegistryJsonApi.class, client);
    }
    
    public RegistryXmlApi constructRegistryXmlApi() {
        WebResource client = constructNuxeoClientBase();
        return WebResourceFactory.newResource(RegistryXmlApi.class, client);
    }
    
    public SimpleRegistryService constructSimpleRegistryService() {
        WebResource client = constructNuxeoClientBase();
        return WebResourceFactory.newResource(SimpleRegistryService.class, client);
    }
    
    public EndpointStateService constructEndpointStateService() {
        WebResource client = constructNuxeoClientBase();
        return WebResourceFactory.newResource(EndpointStateService.class, client);
    }
    
    /**
     * Creates an all-purpose, authenticated REST HTTP client to Nuxeo
     * (prefer specific ones if you can)
     * @return
     */
    public Client createClient() {
        Client client = Client.create(constructClientConfig());
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        return client;
    }
    
    /**
     * To be used with createClient()'s all-purpose REST HTTP client
     * @return EasySOA Registry service URLs
     * @param c
     * @return
     */
    public String getURL(Class<?> c) {
        return this.nuxeoUrl + PathExtractor.getPath(c);
    }

    /**
     * To be used with createClient()'s all-purpose REST HTTP client
     * @return EasySOA Registry service URLs
     * @param c
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public String getURL(Class<?> c, String methodName, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException {
        return this.nuxeoUrl + PathExtractor.getPath(c, methodName, parameterTypes);
    }
    
}
