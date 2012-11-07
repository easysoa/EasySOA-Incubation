package org.easysoa.registry.types;

public interface Endpoint extends SoaNode {

    static final String DOCTYPE = "Endpoint";
    
    static final String XPATH_ENVIRONMENT = "env:environment";
    
    static final String XPATH_URL = "endp:url";

    static final String XPATH_WSDL_PORTTYPE_NAME = "wsdl:wsdlPortTypeName";

    static final String XPATH_WSDL_SERVICE_NAME = "wsdl:wsdlServiceName";
    
    String getEnvironment() throws Exception;

}