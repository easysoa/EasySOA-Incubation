/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.WSDLInformations;
import org.osoa.sca.annotations.Reference;

/**
 * @author jguillemotte
 *
 */
public class TestClient implements TestClientItf {

    /** Reference to the Simple registry service */
    @Reference
    private SimpleRegistryService service;
    
    /**
     * 
     * @return
     * @throws Exception
     */
    public WSDLInformations testQueryWSDLInterfaces(String search, String subProjectId) throws Exception {
        return service.queryWSDLInterfaces(search, subProjectId);
    }
    
    /**
     * 
     * @return
     */
    public WSDLInformations testQueryEndpoints(String search, String subProjectId) throws Exception {
        return service.queryEndpoints(search, subProjectId);        
    }
    
}
