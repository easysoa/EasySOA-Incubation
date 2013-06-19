package org.easysoa.registry.types;

import java.util.List;

import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.types.ids.SoaNodeId;



/**
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceConsumption extends SoaNode, WsdlInfoFacet, RestInfoFacet {
	
    static final String DOCTYPE = "ServiceConsumption";
    
    static final String XPATH_ISTEST = "sc:isTest";
    
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception;
    public boolean getIsTest() throws Exception;
    
}
