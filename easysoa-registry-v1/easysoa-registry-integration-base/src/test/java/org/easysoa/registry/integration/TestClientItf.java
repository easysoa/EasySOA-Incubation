package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.WSDLInformations;

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
    public WSDLInformations testQueryWSDLInterfaces(String search, String subProjectId) throws Exception;
    
    /**
     * 
     * @param search
     * @param subProjectId
     * @return
     * @throws Exception
     */
    public WSDLInformations testQueryEndpoints(String search, String subProjectId) throws Exception;
}