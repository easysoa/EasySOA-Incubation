package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class EndpointRegistryApiTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(EndpointRegistryApiTest.class);

    private RegistryApiHelper discoveryApi = new RegistryApiHelper(this);

    @Inject
    DiscoveryService discoveryService;

    @Inject
    DocumentService documentService;

    private final int SERVICE_COUNT = 5;

    private SoaNodeId deliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa:deliverable");


    @Test
    public void create() throws Exception {
        logTestName(logger);

        // Create endpoint
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
		List<SoaNodeId> parents = new ArrayList<SoaNodeId>();
        
        // runtime props :
        String env = "dev"; // known from discovery probe (proxy) conf
        properties.put("env:environment", env);
        String endpUrl = "http://localhost:8076/services/PrecomptePartenaireService";
        properties.put("endp:url", endpUrl);
        // TODO WSDL content
        // WSDL extracted metas : 
        properties.put("endp:wsdl_portType_name", "{http://www.axxx.com/dps/apv}PrecomptePartenaireService -->");
        //properties.put("endp:wsdl_service_port_binding_type_name", ""); // alternative for one soa model element (ex. endpoint) per service
        properties.put("endp:wsdl_service_name", "{http://www.axxx.com/dps/apv}PrecomptePartenaireServiceImpl");
        
        // OPT impl props - to be merged on server side (TODO or alert if not compatible)
        ArrayList<Object> operations = new ArrayList<Object>();
        Map<String, Object> operation1 = new HashMap<String, Object>();
        operation1.put("operationParameters", null);
        operation1.put("operationName", "creerPrecompte");
        operation1.put("operationDocumentation", "Method: GET, Path: \"/orders/{clientName}\", Description: Returns the orders number for the specified client name.");
        operations.add(operation1);
        properties.put(ServiceImplementation.XPATH_OPERATIONS, operations);
        
        // known model to attach to, provided in discovery probe (proxy) conf :
        // parents :
        parents.add(new SoaNodeId(InformationService.DOCTYPE, "PrecomptePartenaireService")); // specified service
        //parents.add(new SoaNodeId(ServiceImplementation.DOCTYPE, "PrecomptePartenaireServiceImpl")); // OPT known impl
        parents.add(new SoaNodeId("Component", "APVWeb")); // specified component
        parents.add(new SoaNodeId("Component", "FraSCAti Studio for AXXX DPS DCV Integration")); // technical component
        // OPT (component or service(impl(endpoint))) platform :
        //properties.put("endp:runtime_platform", "FraSCAti Studio");

        // deduced props :
        // rather done on server side, once possible matching has been done 
        //properties.put("dc:title", "CreerPrecompteService " + env + " endpoint");
        
		SoaNodeInformation soaNodeInfo = new SoaNodeInformation(new SoaNodeId(
        		Endpoint.DOCTYPE, env + ":" + endpUrl), properties, parents );

        // Run request
        Client client = createAuthenticatedHTTPClient();
        Builder discoveryRequest = client.resource(discoveryApi.getRootURL()).type(MediaType.APPLICATION_JSON);
        OperationResult result = discoveryRequest.post(OperationResult.class, soaNodeInfo);

        // on server side, before creating DiscoveryService first attempts to match existing Endpoint
        // or other model elements referred to, while restricting its queries to the context of
        // info provided (endpoint metas, parent service(impls) & components)
        // TODO
        
        // Check result
        
        
    }


    //@Test
    public void query() throws Exception {
        logTestName(logger);
        
    }
}
