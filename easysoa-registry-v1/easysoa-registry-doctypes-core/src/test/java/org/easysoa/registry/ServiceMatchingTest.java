package org.easysoa.registry;

import java.util.HashMap;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class ServiceMatchingTest extends AbstractRegistryTest {

	@SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DiscoveryServiceTest.class);

    public static final SoaNodeId FIRST_SERVICEIMPL_ID = 
    		new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx");
    
    public static final SoaNodeId SECOND_SERVICEIMPL_ID = 
    		new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenameyyy");
    
    public static final SoaNodeId THIRD_SERVICEIMPL_ID = 
    		new SoaNodeId(ServiceImplementation.DOCTYPE, "nszzz:namezzz=servicenamezzz");

    public static final SoaNodeId INFORMATIONSERVICE_ID = 
    		new SoaNodeId(InformationService.DOCTYPE, "nsxxx:namexxx");
    
    @Inject
    DocumentService documentService;

    @Inject
    DiscoveryService discoveryService;
    
    @Inject
    SoaMetamodelService soaMetamodelService;
    
    @Test
    public void testSimpleDiscovery() throws Exception {
    	// Discover service impl
    	HashMap<String, Object> implProperties = new HashMap<String, Object>();
    	implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
        discoveryService.runDiscovery(documentManager, FIRST_SERVICEIMPL_ID, implProperties, null);
        
    	// Discover information service
    	HashMap<String, Object> isProperties = new HashMap<String, Object>();
    	isProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	DocumentModel foundInfoServ = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_ID, isProperties, null);

    	// check
        foundInfoServ = documentService.find(documentManager, INFORMATIONSERVICE_ID);
        DocumentModel foundImpl = documentService.find(documentManager, FIRST_SERVICEIMPL_ID);
        Assert.assertEquals("Created information service must be linked to existing matching impls", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover another impl
        discoveryService.runDiscovery(documentManager, SECOND_SERVICEIMPL_ID, implProperties, null);

        // check
        foundImpl = documentService.find(documentManager, SECOND_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover a non matching impl
    	HashMap<String, Object> impl3Properties = new HashMap<String, Object>();
    	impl3Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace2}name2");
        discoveryService.runDiscovery(documentManager, THIRD_SERVICEIMPL_ID, impl3Properties, null);
        
        // check
        foundImpl = documentService.find(documentManager, THIRD_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must not be linked to existing matching information service", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));

    	// Rediscover known is then impl
        foundInfoServ  = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_ID, isProperties, null);
        foundImpl = discoveryService.runDiscovery(documentManager, SECOND_SERVICEIMPL_ID, implProperties, null);
        Assert.assertEquals("Created impl must still be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    }
    
    @Test
    public void testSimpleDiscoveryWithCriteria() throws Exception {
        // Discover service impl that won't match platform criteria TODO SHOULD STILL MATCH AT IS LEVEL
    	HashMap<String, Object> implN1Properties = new HashMap<String, Object>();
    	implN1Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	implN1Properties.put("impl:language", "Javascript"); // differs
    	//implN1Properties.put("impl:build", "Maven");
    	implN1Properties.put("impl:technology", "JAXWS");
    	implN1Properties.put("deltype:nature", "Maven");
    	implN1Properties.put("deltype:repositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx" + "N1KO"), implN1Properties, null);
        
    	// Discover service impl
    	HashMap<String, Object> implProperties = new HashMap<String, Object>();
    	implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	implProperties.put("impl:language", "Java");
    	//implProperties.put("impl:build", "Maven");
    	implProperties.put("impl:technology", "JAXWS");
    	implProperties.put("deltype:nature", "Maven");
    	implProperties.put("deltype:repositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
        discoveryService.runDiscovery(documentManager, FIRST_SERVICEIMPL_ID, implProperties, null);
        
    	// Discover service impl that won't match platform criteria
    	HashMap<String, Object> implN2Properties = new HashMap<String, Object>();
    	implN2Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	implN2Properties.put("impl:language", "Java");
    	//implN2Properties.put("impl:build", "Maven");
    	implN2Properties.put("impl:technology", "JAXWS");
    	implN2Properties.put("deltype:nature", "Maven");
    	implN2Properties.put("deltype:repositoryUrl", "http://maven.ow2.org/nexus/content/groups/public"); // differs
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx" + "N2KO"), implN2Properties, null);

    	// Discover information service that won't match platform criteria
    	HashMap<String, Object> isN1Properties = new HashMap<String, Object>();
    	isN1Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	isN1Properties.put("platform:language", "Java");
    	isN1Properties.put("platform:build", "Maven");
    	isN1Properties.put("platform:serviceLanguage", "JAXRS"); // differs
    	isN1Properties.put("platform:deliverableNature", "Maven");
    	isN1Properties.put("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE, "nsxxx:namexxx" + "N1KO"), isN1Properties, null);

    	// Discover information service
    	HashMap<String, Object> isProperties = new HashMap<String, Object>();
    	isProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	isProperties.put("platform:language", "Java");
    	isProperties.put("platform:build", "Maven");
    	isProperties.put("platform:serviceLanguage", "JAXWS");
    	isProperties.put("platform:deliverableNature", "Maven");
    	isProperties.put("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	DocumentModel foundInfoServ = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_ID, isProperties, null);
    	
    	// check
        foundInfoServ = documentService.find(documentManager, INFORMATIONSERVICE_ID);
        DocumentModel foundImpl = documentService.find(documentManager, FIRST_SERVICEIMPL_ID);
        Assert.assertEquals("Created information service must be linked to existing matching impls", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover another impl
        discoveryService.runDiscovery(documentManager, SECOND_SERVICEIMPL_ID, implProperties, null);

        // check
        foundImpl = documentService.find(documentManager, SECOND_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover a non matching impl
    	HashMap<String, Object> impl3Properties = new HashMap<String, Object>();
    	impl3Properties.put("impl:technology", "JAXWS"); // differs
    	impl3Properties.put("deltype:nature", "Maven");
    	impl3Properties.put("deltype:repositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	impl3Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace2}name2");
        discoveryService.runDiscovery(documentManager, THIRD_SERVICEIMPL_ID, impl3Properties, null);
        
        // check
        foundImpl = documentService.find(documentManager, THIRD_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must not be linked to existing matching information service", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));

    	// Discover another is, then impl that has 2 matches
        foundInfoServ = discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE, "nsxxx:namexxx" + "P1KO"), isProperties, null);
        foundImpl = discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx" + "P1KO"), implProperties, null);
        Assert.assertEquals("Created impl must not be linked because there is too much matching information services", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
        // Discover endpoint that matches no is or impl (use matching dashboard TODO mka)
        HashMap<String, Object> epProperties = new HashMap<String, Object>();
        DocumentModel foundEndpoint = discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "staging:http://localhost:8080/cxf/WS1"), epProperties, null);
        Assert.assertEquals("Created endpoint must not be linked to existing matching information service", null,
        		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));

        /*
        // TODO LATER Discover endpoint that matches is (on url-extracted service name), but no impl (or too much ???)
        foundEndpoint = discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "staging:http://localhost:8080/cxf/name"), epProperties, null);
        //Assert.assertEquals("Created endpoint must be linked to existing matching information service", foundInfoServ.getId(),
        //		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));

        // Discover endpoint that matches is (on provided portType), but no impl (or too much ???)
        epProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
        foundEndpoint = discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "staging:http://localhost:8080/cxf/WS2"), epProperties, null);
        Assert.assertEquals("Created endpoint must be linked to existing matching information service", foundInfoServ.getId(),
        		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
        
        // Discover endpoint that matches impl (TODO how, by parent ?? matching ?? component / platform ?)
        foundEndpoint = discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "staging:http://localhost:8080/cxf/WS3"), epProperties, null);
        Assert.assertEquals("Created endpoint must be linked to existing matching information service", foundInfoServ.getId(),
        		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));

        
    	// Discover a non matching impl because of provided component id and check
        // TODO how is provided component id : in DiscoveryService (but then put matching algo there),
        // or on impl then how to check it (in discoService) and how to handle inconsistencies with metas (validation ?)
        // or on impl as metas with platformUrl as platform id
    	implProperties.put("impl's(candidate)componentId", "zz");
    	foundImpl = discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nszzz:namezzz=servicenamezzz" + "C1KO"), implProperties, null);
        Assert.assertEquals("Created impl must not be linked to existing matching information service because of provided component id", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));

    	// Discover a matching impl because of provided component id and check
        implProperties.put("impl's(candidate)componentId", "zz");
    	foundImpl = discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nszzz:namezzz=servicenamezzz" + "C1OK"), implProperties, null);
        Assert.assertEquals("Created impl must be linked to existing matching information service because of provided component id", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
        
        // discover endpoint matching guided by component / platform (criteria)
        // ex. in web disco :
        // (0. choose is (impl) directly)
        // 1. choose (env and) component (because you know it fits there), match & link IS in it, find impl HOW ? OPT put impl in it if platform matches, try to find and link to platform
        // (1.bis create "technical component")
        // 2. choose (impl) platform (id, criteria ?) (because you know it's this techno / runs & is dev'd on it), use it to match to impl, then / else match is using this new info (platform matches)
        // 3. both
        // TODO component path actor:cpt, isMock as "test" platform
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "staging:http://localhost:8080/cxf"), epProperties, null);
        // OK match impl with is on is' component constraints (including platform:deliverableRepositoryUrl that can act as platform id), guided by impl's own metas
        // still TODO set link to Component & isProxy
        // TODO match guided by provided component id : if provided, use it as additional criteria
        // TODO match guided by provided platform id : (can already use platform:deliverableRepositoryUrl as id) : if provided, use it as additional criteria
        // TODO match guided by provided platform criteria ??
        // TODO also match on prop'd url on impl  ???????
        
        // how should work matching in discovery & dashboard for :
        
    	// impl :
    	// 1. find IS on IS req itf (portType) in provided component and whose component's platform match the impl's platform (criteria) ;
    	// (if more than one result, use matching dashboard)
    	// if single result, link impl to IS and to component (that it fills)
    	// 2. if none, find IS on IS req itf (portType) only ;
    	// (if more than one result, use matching dashboard)
    	// if single result, link impl to IS, then attempt to find platform (single query result) to link to else go to matching dashboard 
    	// 3. if none ("technical" service), create service (?) and attempt to find platform (single query result) to link to else go to matching dashboard
    	
    	// endpoint :
    	// find impl on IS req itf (portType) in provided component and whose impl platform match the endpoint's platform (criteria) ;
    	// if single matched link to it
    	// (if more than one result, use matching dashboard)
    	// if none, createe impl and do as above : 1. and fill component, else 2. and link to platform, else 3. 
    	
         */
    }
    
    @Test
    public void testCheckSoaNode() throws PropertyException, ClientException {
    	DocumentModel soaNodeDoc = documentService.newSoaNodeDocument(documentManager,
    			new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:testCheckSoaNode=testCheckSoaNodeImpl"));
    	soaNodeDoc.setPropertyValue(SoaNode.XPATH_SOANAME, null);
        try {
			documentService.checkSoaName(soaNodeDoc);
			Assert.fail("checkSoaName should fail on null soan:name");
		} catch (ClientException e) {
			Assert.assertTrue("testCheckSoaNode successful",  true);
		}
    }
    
}

