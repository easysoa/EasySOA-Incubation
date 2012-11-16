package org.easysoa.registry.integration;

import org.easysoa.registry.rest.AbstractRestApiTest;

/**
 * 
 * @author jguillemotte
 *
 */
public class SimpleRegistryServiceHelper {

    private final AbstractRestApiTest test;

    public SimpleRegistryServiceHelper(AbstractRestApiTest test) {
        this.test = test;
    }
    
    public String getRootURL() {
        return test.getURL(SimpleRegistryServiceImpl.class);
    }
    
    
}