package org.easysoa.registry.types.ids;

import org.easysoa.registry.types.Deliverable;

public class DeliverableId extends SoaNodeId {
	
	private final String nature;

	public DeliverableId(String nature, String name) {
		super(Deliverable.DOCTYPE, nature + ":" + name);
		this.nature = nature;
	}
	
	public String getNature() {
		return nature;
	}
	
}
