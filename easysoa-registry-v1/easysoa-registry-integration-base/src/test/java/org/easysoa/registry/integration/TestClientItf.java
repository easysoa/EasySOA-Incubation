package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.EndpointInformations;
import org.easysoa.registry.rest.integration.ServiceInformations;

/**
 * Simple registry service test client interface
 * 
 * @author jguillemotte
 *
 */
public interface TestClientItf {

    /**
     * 
     * @param search
     * @param subProjectId
     * @return
     * @throws Exception
     */
    public ServiceInformations testQueryWSDLInterfaces(String search, String subProjectId) throws Exception;
    
    /**
     * 
     * @param search
     * @param subProjectId
     * @return
     * @throws Exception
     */
    public EndpointInformations testQueryEndpoints(String search, String subProjectId) throws Exception;
    
    /**
     * 
     * @param search
     * @param subProjectId
     * @return
     * @throws Exception
     */
    public ServiceInformations testQueryServicesWithEndpoints(String search, String subProjectId) throws Exception;    
}