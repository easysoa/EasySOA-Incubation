package org.easysoa.registry.types;

import org.easysoa.registry.facets.ArchitectureComponentFacet;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.ServiceImplementationDataFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;

public interface Endpoint extends SoaNode, RestInfoFacet, WsdlInfoFacet,
		ArchitectureComponentFacet, ServiceImplementationDataFacet {

    static final String DOCTYPE = "Endpoint";
    
    static final String XPATH_ENDP_ENVIRONMENT = "env:environment";
    
    static final String XPATH_URL = "endp:url";
    
    static final String XPATH_LINKED_PLATFORM = "endp:linkedPlatform";

    static final String XPATH_ENDP_SERVICE_PROTOCOL = "endp:serviceProtocol";
    
    static final String XPATH_ENDP_TRANSPORT_PROTOCOL = "endp:transportProtocol";

    static final String XPATH_ENDP_SERVICE_RUNTIME = "endp:serviceRuntime";

    static final String XPATH_ENDP_APP_SERVER_RUNTIME = "endp:appServerRuntime";
    
    String getEnvironment() throws Exception;

}