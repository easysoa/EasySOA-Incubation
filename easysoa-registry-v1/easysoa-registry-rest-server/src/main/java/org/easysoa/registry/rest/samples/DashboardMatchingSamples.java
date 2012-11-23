package org.easysoa.registry.rest.samples;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.ids.InformationServiceSoaNodeId;
import org.easysoa.registry.types.ids.ServiceIdentifierType;
import org.easysoa.registry.types.ids.ServiceImplementationSoaNodeId;

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
		Map<String, Serializable> properties = new HashMap<String, Serializable>();
		
		// Create platforms
		properties.clear();
		properties.put(Platform.XPATH_PLATFORM_LANGAGE, "java");
		properties.put(Platform.XPATH_TITLE, "Platform 1");
		registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(
				new SoaNodeInformation(new SoaNodeId(Platform.DOCTYPE, "Platform 1"),
				properties, null));
		logger.info("Platform 1");
		
		properties.clear();
		properties.put(Platform.XPATH_PLATFORM_LANGAGE, "js");
		properties.put(Platform.XPATH_TITLE, "Platform 2");
		registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(
				new SoaNodeInformation(new SoaNodeId(Platform.DOCTYPE, "Platform 2"),
				properties, null));
		logger.info("Platform 2");
		
		// Create information services
		for (int i = 0; i < 5; i++) {
			properties.clear();
			properties.put(InformationService.XPATH_TITLE, "My Information Service " + i);
			registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(new SoaNodeInformation(new SoaNodeId(InformationService.DOCTYPE, 
					new InformationServiceSoaNodeId(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf" + i).toString()),
					properties, Arrays.asList(new SoaNodeId(Platform.DOCTYPE, "Platform 1"))));
			logger.info("Information service " + i);
		}
		
		// Create deliverable
		properties.clear();
		properties.put(Deliverable.XPATH_TITLE, "My Deliverable");
		properties.put(Deliverable.XPATH_NATURE, "maven");
		registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(new SoaNodeInformation(
				new SoaNodeId(Deliverable.DOCTYPE, "deliv"), properties, null));
		logger.info("My Deliverable");
		
		// Create service impls
		for (int i = 3; i < 6; i++) {
			properties.clear();
			properties.put(ServiceImplementation.XPATH_TITLE, "My Service Impl " + i);
			properties.put(ServiceImplementation.XPATH_LANGUAGE, "java");
			registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(new SoaNodeInformation(new SoaNodeId(ServiceImplementation.DOCTYPE, 
					new ServiceImplementationSoaNodeId(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf" + i, "impl" + i).toString()),
					properties, null));
			logger.info("Service impl. " + i);
		}
		
		// Create component
		properties.clear();
		properties.put(Component.XPATH_TITLE, "My component");
		properties.put(Component.XPATH_LINKED_INFORMATION_SERVICE, getUuid(
				InformationService.DOCTYPE,
				new InformationServiceSoaNodeId(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf0").toString())
			);
		registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(
				new SoaNodeInformation(new SoaNodeId(Component.DOCTYPE, "My component"),
				properties, null));
		logger.info("My component");
	}
	
	private String getUuid(String doctype, String soaName) throws Exception {
		SoaNodeInformation soaNodeInformation = registryApi.path(InformationService.DOCTYPE)
			.path(doctype)
			.path(soaName)
			.type(MediaType.APPLICATION_JSON_TYPE)
			.get(SoaNodeInformation.class);
		return soaNodeInformation.getUuid();
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
