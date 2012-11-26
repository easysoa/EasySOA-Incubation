package org.easysoa.registry.types.ids;

import java.io.Serializable;
import java.util.Map;

import org.easysoa.registry.types.Endpoint;

public class EndpointId extends SoaNodeId {

	private final String environment;
	private final String url;

	public EndpointId(String environment, String url) {
		super(Endpoint.DOCTYPE, buildName(environment, url));
		this.environment = environment;
		this.url = url;
	}
	
	private static String buildName(String environment, String url) {
		return environment + ":" + url;
	}
	
	@Override
	public Map<String, Serializable> getDefaultPropertyValues() {
		Map<String, Serializable> properties = super.getDefaultPropertyValues();
		properties.put(Endpoint.XPATH_ENVIRONMENT, environment);
		properties.put(Endpoint.XPATH_URL, url);
		properties.put(Endpoint.XPATH_TITLE, url);
		return properties;
	}

	public Object getEnvironment() {
		return environment;
	}
	
	public String getUrl() {
		return url;
	}
	
}
