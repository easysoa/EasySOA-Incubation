package org.easysoa.registry;

import java.util.HashMap;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
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
    	HashMap<String, Object> implProperties = new HashMap<String, Object>();
    	implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	
    	// Discover service impl
        discoveryService.runDiscovery(documentManager, FIRST_SERVICEIMPL_ID, implProperties, null);
        documentManager.save();
        
    	HashMap<String, Object> isProperties = new HashMap<String, Object>();
    	isProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	
    	// Discover information service
    	DocumentModel foundInfoServ = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_ID, isProperties, null);

        foundInfoServ = documentService.find(documentManager, INFORMATIONSERVICE_ID);
        DocumentModel foundImpl = documentService.find(documentManager, FIRST_SERVICEIMPL_ID);
        Assert.assertEquals("Created information service must be linked to existing matching impls", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover another impl
        discoveryService.runDiscovery(documentManager, SECOND_SERVICEIMPL_ID, implProperties, null);
        documentManager.save();

        foundImpl = documentService.find(documentManager, SECOND_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover a non matching impl
    	HashMap<String, Object> impl3Properties = new HashMap<String, Object>();
    	impl3Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace2}name2");
        discoveryService.runDiscovery(documentManager, THIRD_SERVICEIMPL_ID, impl3Properties, null);
        documentManager.save();
        foundImpl = documentService.find(documentManager, THIRD_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must not be linked to existing matching information service", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    }
    
    @Test
    public void testSimpleDiscoveryWithCriteria() throws Exception {
    	HashMap<String, Object> implProperties = new HashMap<String, Object>();
    	implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	implProperties.put("impl:language", "Java");
    	//implProperties.put("impl:build", "Maven");
    	implProperties.put("impl:technology", "JAXWS");
    	implProperties.put("deltype:nature", "Maven");
    	implProperties.put("deltype:repositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	
    	// Discover service impl
        discoveryService.runDiscovery(documentManager, FIRST_SERVICEIMPL_ID, implProperties, null);
        documentManager.save();
        
    	HashMap<String, Object> isProperties = new HashMap<String, Object>();
    	isProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	isProperties.put("platform:language", "Java");
    	isProperties.put("platform:build", "Maven");
    	isProperties.put("platform:serviceLanguage", "JAXWS");
    	isProperties.put("platform:deliverableNature", "Maven");
    	isProperties.put("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	
    	// Discover information service
    	DocumentModel foundInfoServ = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_ID, isProperties, null);
        /*
    	foundInfoServ.addFacet("PlatformData");
        foundInfoServ.setPropertyValue("platform:language", "Java");
        foundInfoServ.setPropertyValue("platform:build", "Maven");
        foundInfoServ.setPropertyValue("platform:serviceLanguage", "JAXWS");
        foundInfoServ.setPropertyValue("platform:deliverableNature", "Maven");
        foundInfoServ.setPropertyValue("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
        //documentManager.save();
        documentManager.saveDocument(foundInfoServ);*/

        foundInfoServ = documentService.find(documentManager, INFORMATIONSERVICE_ID);
        DocumentModel foundImpl = documentService.find(documentManager, FIRST_SERVICEIMPL_ID);
        Assert.assertEquals("Created information service must be linked to existing matching impls", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover another impl
        discoveryService.runDiscovery(documentManager, SECOND_SERVICEIMPL_ID, implProperties, null);
        documentManager.save();

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
        documentManager.save();
        foundImpl = documentService.find(documentManager, THIRD_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must not be linked to existing matching information service", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    }
    
}

