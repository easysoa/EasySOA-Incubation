package org.easysoa.registry.types;

import org.easysoa.registry.facets.ArchitectureComponentFacet;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;


/**
 * SoaName specs:
 * Either "ws:TARGETNS:NAME"
 * Or "java:PROJECT:INTERFACE"
 * 
 * @author mkalam-alami
 *
 */
public interface InformationService extends SoaNode, WsdlInfoFacet, RestInfoFacet, ArchitectureComponentFacet {

    static final String DOCTYPE = "InformationService";
    
    static final String XPATH_PROVIDER_ACTOR = "iserv:providerActor";

    static final String XPATH_LINKED_BUSINESS_SERVICE = "iserv:linkedBusinessService";
    
}
