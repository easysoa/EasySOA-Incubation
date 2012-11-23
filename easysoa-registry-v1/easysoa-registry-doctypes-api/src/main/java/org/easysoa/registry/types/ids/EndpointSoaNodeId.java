package org.easysoa.registry.types.ids;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.Endpoint;

public class EndpointSoaNodeId extends SoaNodeId {
	
	public EndpointSoaNodeId(String environment, String url) {
		super(Endpoint.DOCTYPE, environment + ":" + url);
	}
	
}
