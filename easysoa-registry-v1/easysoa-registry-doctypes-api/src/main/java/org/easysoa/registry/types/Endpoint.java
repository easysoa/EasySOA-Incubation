package org.easysoa.registry.types;

public interface Endpoint extends SoaNode, WsdlInfo {

    static final String DOCTYPE = "Endpoint";
    
    static final String XPATH_ENVIRONMENT = "env:environment";
    
    static final String XPATH_URL = "endp:url";
    
    String getEnvironment() throws Exception;

}