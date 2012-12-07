package org.easysoa.registry.types;

import org.easysoa.registry.facets.ArchitectureComponentFacet;
import org.easysoa.registry.facets.PlatformDataFacet;

public interface Component extends SoaNode, ArchitectureComponentFacet, PlatformDataFacet {

	public static final String DOCTYPE = "Component";
	
}
