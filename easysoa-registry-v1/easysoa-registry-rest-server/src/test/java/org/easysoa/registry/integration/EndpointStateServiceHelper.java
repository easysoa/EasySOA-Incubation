package org.easysoa.registry.integration;

import org.easysoa.registry.rest.AbstractRestApiTest;

/**
 * 
 * @author jguillemotte
 *
 */
public class EndpointStateServiceHelper {

    private final AbstractRestApiTest test;

    public EndpointStateServiceHelper(AbstractRestApiTest test) {
        this.test = test;
    }
    
    public String getRootURL() {
        return test.getURL(EndpointStateServiceImpl.class);
    }
    
}