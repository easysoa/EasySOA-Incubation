package org.easysoa.registry.rest.samples;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.names.InformationServiceName;
import org.easysoa.registry.types.names.ServiceIdentifierType;
import org.easysoa.registry.types.names.ServiceImplementationName;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Creates some sample documents to fill the repository enough
 * to make the matching dashboard interesting.
 * 
 * @author mkalam-alami
 *
 */
public class DashboardMatchingSamples extends AbstractRestApiTest {

	private static final Logger logger = Logger.getLogger(DashboardMatchingSamples.class);
	
	public static void main(String[] args) {
		new DashboardMatchingSamples("http://localhost:8080").run();
	}

	private WebResource registryApi;
	
	public DashboardMatchingSamples(String nuxeoUrl) {
        Client client = createAuthenticatedHTTPClient();
		registryApi = client.resource(nuxeoUrl).path("nuxeo/site/easysoa/registry");
	}
	
	public void run() {
		// Create information services
		Map<String, Serializable> properties = new HashMap<String, Serializable>();
		for (int i = 0; i < 5; i++) {
			properties.put("dc:title", "My Information Service " + i);
			registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(new SoaNodeInformation(new SoaNodeId(InformationService.DOCTYPE, 
					new InformationServiceName(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf" + i).toString()),
					properties, null));
			logger.info("Information service " + i);
			properties.clear();
		}
		
		// Create service impls
		for (int i = 3; i < 6; i++) {
			properties.put("dc:title", "My Service Impl " + i);
			registryApi.type(MediaType.APPLICATION_JSON_TYPE).post(new SoaNodeInformation(new SoaNodeId(ServiceImplementation.DOCTYPE, 
					new ServiceImplementationName(ServiceIdentifierType.WEB_SERVICE, "namespace", "itf" + i, "impl" + i).toString()),
					properties, null)); // The informationService will be set automatically
			logger.info("Service impl. " + i);
			properties.clear();
		}
	}
	
}
