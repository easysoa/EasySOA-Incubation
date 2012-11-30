package org.easysoa.registry.rest.samples;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.InformationServiceId;
import org.easysoa.registry.types.ids.ServiceIdentifierType;
import org.easysoa.registry.types.ids.ServiceImplementationId;
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
		// Create information services
		Map<String, Serializable> properties = new HashMap<String, Serializable>();
		for (int i = 0; i < 5; i++) {
			properties.clear();
			properties.put(InformationService.XPATH_TITLE, "My Information Service " + i);
			registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(new SoaNodeInformation(new SoaNodeId(InformationService.DOCTYPE, 
					new InformationServiceId(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf" + i).toString()),
					properties, null));
			logger.info("Information service " + i);
		}
		
		// Create service impls
		for (int i = 3; i < 6; i++) {
			properties.clear();
			properties.put(ServiceImplementation.XPATH_TITLE, "My Service Impl " + i);
			registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(new SoaNodeInformation(new SoaNodeId(ServiceImplementation.DOCTYPE, 
					new ServiceImplementationId(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf" + i, "impl" + i).toString()),
					properties, null)); // The informationService will be set automatically
			logger.info("Service impl. " + i);
		}
		
		// Create component
		SoaNodeInformation infoServiceInformation = registryApi.path(InformationService.DOCTYPE)
			.path(new InformationServiceId(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf0").toString())
			.type(MediaType.APPLICATION_JSON_TYPE)
			.get(SoaNodeInformation.class);
		properties.clear();
		properties.put(InformationService.XPATH_TITLE, "My component");
		properties.put(InformationService.XPATH_COMP_LINKED_INFORMATION_SERVICE, infoServiceInformation.getUuid());
		registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(
				new SoaNodeInformation(new SoaNodeId(Component.DOCTYPE, "My component"),
				properties, null));
		logger.info("My component");
		
		// Create Endpoint
		properties.clear();
		properties.put(Endpoint.XPATH_TITLE, "My endpoint");
		registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(
				new SoaNodeInformation(new EndpointId("Production", "http://www.endpoint.com"),
				properties, null));
		logger.info("My endpoint");
		
	}
	
    private Client createAuthenticatedHTTPClient() {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());
        Client client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter("Administrator", "Administrator"));
        return client;
    }
    
	
}
