package org.easysoa.registry.types.ids;

import org.easysoa.registry.types.Endpoint;

public class EndpointId extends SoaNodeId {
	
	private final String environment;
	
	private final String url;

	public EndpointId(String environment, String url) {
		super(Endpoint.DOCTYPE, environment + ":" + url);
		this.environment = environment;
		this.url = url;
	}

	public String getEnvironment() {
		return this.environment;
	}
	
	public String getUrl() {
		return url;
	}
	
}
