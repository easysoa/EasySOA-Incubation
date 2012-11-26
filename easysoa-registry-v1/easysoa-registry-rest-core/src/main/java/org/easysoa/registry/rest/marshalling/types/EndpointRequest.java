package org.easysoa.registry.rest.marshalling.types;

import org.easysoa.registry.rest.marshalling.SoaNodeRequest;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;

public class EndpointRequest extends SoaNodeRequest implements Endpoint {

	private EndpointId id;

	public EndpointRequest(String environment, String url) throws Exception {
		super(Endpoint.DOCTYPE);
		this.id = new EndpointId(environment, url);
		this.setProperty(XPATH_ENVIRONMENT, environment);
		this.setProperty(XPATH_URL, url);
	}
	
	@Override
	public SoaNodeId getSoaNodeId() throws Exception {
		return this.id;
	}

	@Override
	public String getSoaName() throws Exception {
		return this.id.getName();
	}

	@Override
	public String getEnvironment() throws Exception {
		return this.id.getEnvironment();
	}

}
