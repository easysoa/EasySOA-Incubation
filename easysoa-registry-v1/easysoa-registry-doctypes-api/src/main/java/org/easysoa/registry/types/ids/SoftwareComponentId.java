package org.easysoa.registry.types.ids;

import org.easysoa.registry.types.SoftwareComponent;

public class SoftwareComponentId extends SoaNodeId {

	public SoftwareComponentId(String name) {
		super(SoftwareComponent.DOCTYPE, name);
	}
	
}
