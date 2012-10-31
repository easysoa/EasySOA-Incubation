package org.easysoa.registry.types;


/**
 * SoaName specs:
 * Either "ws-TARGETNS:NAME"
 * Or "itf-INTERFACE"
 * 
 * @author mkalam-alami
 *
 */
public interface InformationService extends SoaNode {

	// SoaName specs:
	// Either "ws-TARGETNS:NAME=SERVICENAME"
	// Or "itf-INTERFACE=NAME"
	
    public static final String DOCTYPE = "InformationService";
    
    public static final String XPATH_PROVIDER_ACTOR = "iserv:providerActor";

    public static final String XPATH_LINKED_BUSINESS_SERVICE = "iserv:linkedBusinessService";

}
