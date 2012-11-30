package org.easysoa.registry.types;

import org.easysoa.registry.facets.ArchitectureComponentFacet;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.ServiceImplementationDataFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;

public interface Endpoint extends SoaNode, RestInfoFacet, WsdlInfoFacet,
		ArchitectureComponentFacet, ServiceImplementationDataFacet {

    static final String DOCTYPE = "Endpoint";
    
    static final String XPATH_ENVIRONMENT = "env:environment";
    
    static final String XPATH_URL = "endp:url";
    
    static final String XPATH_LINKED_PLATFORM = "endp:linkedPlatform";
    
    String getEnvironment() throws Exception;

}