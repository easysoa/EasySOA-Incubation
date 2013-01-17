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
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.EndpointId;
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

		String subprojectId = null; // default
		
		// Platforms
    	p.put(Platform.XPATH_LANGUAGE, "Java");
    	p.put(Platform.XPATH_BUILD, "Maven");
    	p.put(Platform.XPATH_SERVICE_LANGUAGE, "JAXRS");
		SoaNodeId javaPlatformId = new SoaNodeId(subprojectId, Platform.DOCTYPE, "Java Platform");
		postSoaNode(javaPlatformId, p);

		// Create information services
		SoaNodeId serviceId = null;
		for (int i = 0; i < 4; i++) {
			p.put(InformationService.XPATH_TITLE, "Java Service #" + i);
			p.put(InformationService.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType" + i);
			serviceId = new SoaNodeId(subprojectId, InformationService.DOCTYPE, 
					new InformationServiceName(ServiceNameType.WEB_SERVICE, "namespace", "portType" + i).toString());
			postSoaNode(serviceId, p);
		}

        p.put(InformationService.XPATH_TITLE, "Java Service #3 Bis");
        p.put(InformationService.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType3");
        postSoaNode(new SoaNodeId(subprojectId, InformationService.DOCTYPE, 
                new InformationServiceName(ServiceNameType.WEB_SERVICE, "namespace", "portType3bis").toString()), p);
        
		// Components
		p.put(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE, (String) getSoaNode(serviceId).getProperty(SoaNode.XPATH_UUID));
		postSoaNode(new SoaNodeId(subprojectId, Component.DOCTYPE, "Service 2 Component"), p, javaPlatformId);
		
		// Create service impls that matches an IS
        p.put(ServiceImplementation.XPATH_LANGUAGE, "Java");
        p.put(ServiceImplementation.XPATH_BUILD, "Maven");
        p.put(ServiceImplementation.XPATH_SERVICE_LANGUAGE, "JAXRS");
        p.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType2");
        postSoaNode(new SoaNodeId(subprojectId, ServiceImplementation.DOCTYPE, "Java Service Impl 2"), p);
        
        p.put(ServiceImplementation.XPATH_LANGUAGE, "Java");
        p.put(ServiceImplementation.XPATH_BUILD, "Maven");
        p.put(ServiceImplementation.XPATH_SERVICE_LANGUAGE, "JAXRS");
        p.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType2");
        postSoaNode(new SoaNodeId(subprojectId, ServiceImplementation.DOCTYPE, "Java Service Impl 2 Bis"), p);

        // Create impl that matches several IS (no direct link, but suggested through dashboard)
        p.put(ServiceImplementation.XPATH_LANGUAGE, "Java");
        p.put(ServiceImplementation.XPATH_BUILD, "Maven");
        p.put(ServiceImplementation.XPATH_SERVICE_LANGUAGE, "JAXRS");
        p.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType3");
        postSoaNode(new SoaNodeId(subprojectId, ServiceImplementation.DOCTYPE, "Java Service Impl 3"), p);
        
        // Create service impls that doesn't match an IS
        p.put(ServiceImplementation.XPATH_LANGUAGE, "Java");
        p.put(ServiceImplementation.XPATH_BUILD, "Maven");
        p.put(ServiceImplementation.XPATH_SERVICE_LANGUAGE, "JAXRS");
        p.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType4");
        postSoaNode(new SoaNodeId(subprojectId, ServiceImplementation.DOCTYPE, "Java Service Impl 4"), p);
        
        p.put(ServiceImplementation.XPATH_SERVICE_LANGUAGE, "JAXWS");
        p.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portTypeJaxWs");
        p.put(ServiceImplementation.XPATH_WSDL_SERVICE_NAME, "{namespace}serviceJaxWs");
        postSoaNode(new SoaNodeId(subprojectId, ServiceImplementation.DOCTYPE, "Java Service Impl JAXWS"), p);
		
        // Create endpoint that matches an impl
        p.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portTypeJaxWs");
        p.put(Endpoint.XPATH_WSDL_SERVICE_NAME, "{namespace}serviceJaxWs");
        postSoaNode(new EndpointId(subprojectId, "Production", "http://www.endpoint.com/serviceJaxWs"), p);
        
        // Create endpoint that matches no impl but matches an IS (placeholder impl)
        p.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType0");
        p.put(Endpoint.XPATH_WSDL_SERVICE_NAME, "{namespace}service0");
        postSoaNode(new EndpointId(subprojectId, "Production", "http://www.endpoint.com/service0"), p);

        // Create endpoint that matches several impls
        p.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, "{namespace}portType2");
        p.put(Endpoint.XPATH_WSDL_SERVICE_NAME, "{namespace}service2");
        postSoaNode(new EndpointId(subprojectId, "Production", "http://www.endpoint.com/service2"), p);
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
