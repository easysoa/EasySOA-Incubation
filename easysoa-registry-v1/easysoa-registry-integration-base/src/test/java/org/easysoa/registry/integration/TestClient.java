/**
 * 
 */
package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.EndpointInformations;
import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.ServiceInformations;
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
     * @see
     */
    public ServiceInformations testQueryWSDLInterfaces(String search, String subProjectId) throws Exception {
        return service.queryWSDLInterfaces(search, subProjectId, "strict");
    }
    
    /**
     * @see 
     */
    public EndpointInformations testQueryEndpoints(String search, String subProjectId) throws Exception {
        return service.queryEndpoints(search, subProjectId, "strict");
    }
    
    /**
     * @see
     */
    public ServiceInformations testQueryServicesWithEndpoints(String search, String subProjectId) throws Exception {
        return service.queryServicesWithEndpoints(search, subProjectId, "strict");
    }
    
}
