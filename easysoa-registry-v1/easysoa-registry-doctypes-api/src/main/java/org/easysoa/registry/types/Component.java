package org.easysoa.registry.types;

public interface Component extends SoaNode {

	public static final String DOCTYPE = "Component";
	
	public static final String SCHEMA_PREFIX = "acomp";

	public static final String XPATH_LINKED_INFORMATION_SERVICE = SCHEMA_PREFIX + ":linkedInformationService";
	
	public static final String XPATH_LINKED_COMPONENT_CATEGORY = SCHEMA_PREFIX + ":componentCategory";

	public static final String XPATH_PROVIDER_ACTOR = SCHEMA_PREFIX + ":providerActor";
	
}
