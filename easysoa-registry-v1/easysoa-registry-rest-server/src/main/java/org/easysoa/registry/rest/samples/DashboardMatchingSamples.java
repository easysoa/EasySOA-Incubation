package org.easysoa.registry.rest.samples;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.InformationServiceName;
import org.easysoa.registry.types.ids.ServiceNameType;
import org.easysoa.registry.types.ids.SoaNodeId;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * Creates some sample documents to fill the repository enough
 * to make the matching dashboard interesting.
 * 
 * @author mkalam-alami
 *
 */
public class DashboardMatchingSamples {

	private static final Logger logger = Logger.getLogger(DashboardMatchingSamples.class);
    
	public static void main(String[] args) throws Exception {
		new DashboardMatchingSamples("http://localhost:8080").run();
	}

	private WebResource registryApi;
	
	public DashboardMatchingSamples(String nuxeoUrl) {
        Client client = createAuthenticatedHTTPClient();
		registryApi = client.resource(nuxeoUrl).path("nuxeo/site/easysoa/registry");
	}

	public void run() throws Exception {
		Map<String, Serializable> p = new HashMap<String, Serializable>(); // will be cleared after each post

		// Platforms
    	p.put(Platform.XPATH_LANGUAGE, "Java");
    	p.put(Platform.XPATH_BUILD, "Maven");
    	p.put(Platform.XPATH_SERVICE_LANGUAGE, "JAXRS");
    	p.put(Platform.XPATH_DELIVERABLE_NATURE, "Maven");
		SoaNodeId javaPlatformId = new SoaNodeId(Platform.DOCTYPE, "Java Platform");
		postSoaNode(javaPlatformId, p);

		// Create information services
		SoaNodeId serviceId = null;
		for (int i = 0; i < 3; i++) {
			p.put(InformationService.XPATH_TITLE, "Java Service #" + i);
			serviceId = new SoaNodeId(InformationService.DOCTYPE, 
					new InformationServiceName(ServiceNameType.WEB_SERVICE, "namespace", "itf" + i).toString());
			postSoaNode(serviceId, p);
		}
		
		// Components
		p.put(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE, (String) getSoaNode(serviceId).getProperty(SoaNode.XPATH_UUID));
		postSoaNode(new SoaNodeId(Component.DOCTYPE, "Service 1 Component"), p, javaPlatformId);
		
		// Create service impl that matches an IS
		// TODO

        // Create service impl that doesn't match an IS
        // TODO
		
        // Create endpoint that matches an impl
        // TODO

        // Create endpoint that matches no impl but matches an IS (placeholder impl)
        // TODO

        // Create endpoint that matches nothing
        // TODO
		
	}
	
	public Client createAuthenticatedHTTPClient() {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());
        Client client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter("Administrator", "Administrator"));
        return client;
    }

	public SoaNodeInformation getSoaNode(SoaNodeId id) {
		return registryApi.path(id.getType())
			.path(id.getName())
			.type(MediaType.APPLICATION_JSON_TYPE)
			.get(SoaNodeInformation.class);
	}

	public void postSoaNode(SoaNodeId id, Map<String, Serializable> properties, SoaNodeId... parents) {
		registryApi
			.type(MediaType.APPLICATION_JSON_TYPE)
			.post(new SoaNodeInformation(id, properties, Arrays.asList(parents)));
		logger.info("> " + id.toString());
		properties.clear();
	}
    
	
}
