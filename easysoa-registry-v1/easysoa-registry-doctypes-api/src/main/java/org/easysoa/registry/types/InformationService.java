package org.easysoa.registry.types;


/**
 * SoaName specs:
 * Either "ws:TARGETNS:NAME"
 * Or "java:PROJECT:INTERFACE"
 * 
 * @author mkalam-alami
 *
 */
public interface InformationService extends SoaNode {

    public static final String DOCTYPE = "InformationService";
    
    public static final String XPATH_PROVIDER_ACTOR = "iserv:providerActor";

    public static final String XPATH_LINKED_BUSINESS_SERVICE = "iserv:linkedBusinessService";
    
    public static final String XPATH_WSDL_PORTTYPE_NAME = "wsdl:wsdlPortTypeName";

	public static final String XPATH_WSDL_SERVICE_NAME = "wsdl:wsdlServiceName";

}
