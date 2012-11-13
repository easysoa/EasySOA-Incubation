package org.easysoa.registry;

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
        discoveryService.runDiscovery(documentManager, FIRST_SERVICEIMPL_ID, null, null);
        documentManager.save();
    	
    	// Discover information service
        discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_ID, null, null);
        documentManager.save();

        DocumentModel foundInfoServ = documentService.find(documentManager, INFORMATIONSERVICE_ID);
        DocumentModel foundImpl = documentService.find(documentManager, FIRST_SERVICEIMPL_ID);
        Assert.assertEquals("Created information service must be linked to existing matching impls", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
    	
    	// Discover another impl
        discoveryService.runDiscovery(documentManager, SECOND_SERVICEIMPL_ID, null, null);
        documentManager.save();

        foundImpl = documentService.find(documentManager, SECOND_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));
        
    }
    
}

