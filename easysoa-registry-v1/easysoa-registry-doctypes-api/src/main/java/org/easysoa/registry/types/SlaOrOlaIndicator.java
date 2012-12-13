/**
 * 
 */
package org.easysoa.registry.types;

import org.easysoa.registry.facets.ArchitectureComponentFacet;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;

/**
 * @author jguillemotte
 *
 */
public interface SlaOrOlaIndicator extends SoaNode, WsdlInfoFacet, RestInfoFacet, ArchitectureComponentFacet  {

    static final String DOCTYPE = "slaOrOlaIndicator";
    
    static final String XPATH_ENDPOINT_ID  = "ind:endpointId";

    static final String XPATH_SLA_OR_OLA_NAME = "ind:slaOrOlaName";    

    static final String XPATH_TIMESTAMP = "ind:timestamp";
    
    static final String XPATH_SERVICE_LEVEL_HEALTH = "ind:serviceLeveHealth";
    
    static final String XPATH_SERVICE_LEVEL_VIOLATION = "ind:serviceLevelViolation";
    
}
