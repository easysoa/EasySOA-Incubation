package org.easysoa.registry.integration;

import org.easysoa.registry.rest.integration.WSDLInformations;

public interface TestClientItf {

    public WSDLInformations testQueryWSDLInterfaces(String search, String subProjectId) throws Exception;
    
    public WSDLInformations testQueryEndpoints(String search, String subProjectId) throws Exception;
}