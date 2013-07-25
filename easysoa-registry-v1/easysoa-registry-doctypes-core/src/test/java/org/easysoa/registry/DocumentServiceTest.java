package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.RepositoryHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

import com.google.inject.Inject;

/**
 * Tests DocumentService CRUD operations.
 * 
 * These are consecutive steps of a workflow so test methods must be run
 * in their order of definition and Nuxeo repository state must be kept in between.
 * 
 * @author mkalam-alami
 *
 */
public class DocumentServiceTest extends AbstractRegistryTest {

    private static final SoaNodeId MYSYSTEM_ID = new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem");
    
    private static final SoaNodeId MYOTHERSYSTEM_ID = new SoaNodeId(TaggingFolder.DOCTYPE, "MyOtherSystem");
    
    private static IdRef mySystemIdRef = null;

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DocumentServiceTest.class);

    @Inject
    DocumentService documentService;
    
    @Test
    public void testModelCreation() throws ClientException {
        DocumentModel systemModel = documentService.create(documentManager, MYSYSTEM_ID);
        ///documentManager.saveDocument(systemModel);try{Thread.currentThread().sleep(5000);} catch (Exception e) {}
        documentManager.save();
        
        mySystemIdRef = new IdRef(systemModel.getId());
        // TODO without this line or the next one, sometimes won't be found by SOA ID in testModelQuery()
        // or even only in testModelDeletion() !?!! see #134 NOOOO WORKS AGAIN
        ///Assert.assertTrue("Created system must be found by ref", documentManager.exists(mySystemIdRef));
        ////Assert.assertTrue("Created system must be found by uuid query", !documentManager.query("SELECT * FROM TaggingFolder WHERE ecm:uuid = '" + mySystemIdRef.value + "'").isEmpty());
        
        systemModel = documentService.findSoaNode(documentManager, MYSYSTEM_ID);
        Assert.assertNotNull("Created system must be found by name", systemModel);
        Assert.assertNotNull(systemModel);
        Assert.assertEquals(MYSYSTEM_ID.getName(), systemModel.getName());
        Assert.assertEquals(MYSYSTEM_ID.getName(), systemModel.getTitle());
    }

    @Test
    public void testModelQuery() throws ClientException {
        // creating first
        testModelCreation();
        
        DocumentModel systemModel = documentService.findSoaNode(documentManager, MYSYSTEM_ID);
        Assert.assertNotNull("Created system must be found by name", systemModel);
        Assert.assertEquals(MYSYSTEM_ID.getName(), systemModel.getTitle());
        Assert.assertTrue("Returned document must be in the repository, in the System folder",
                systemModel.getPathAsString().startsWith(RepositoryHelper
                        .getRepositoryPath(documentManager, MYSYSTEM_ID.getSubprojectId())));
    }

    @Test
    public void testModelDeletion() throws ClientException {
        // creating first
        testModelCreation();
        
        // TODO without this line or the next one, sometimes won't be found
        // by SOA ID !?!! see #134 NOOOO WORKS AGAIN
        ///Assert.assertTrue("Created system must be found by ref", documentManager.exists(mySystemIdRef));
        ////Assert.assertTrue("Created system must be found by uuid query", !documentManager.query("SELECT * FROM TaggingFolder WHERE ecm:uuid = '" + mySystemIdRef.value + "'").isEmpty());
        
        DocumentModel systemModel = documentService.findSoaNode(documentManager, MYSYSTEM_ID);
        Assert.assertNotNull("Created system must be found by name", systemModel);
        boolean success = documentService.delete(documentManager, MYSYSTEM_ID);
        Assert.assertTrue("Document deletion must be successful", success);
        systemModel = documentService.findSoaNode(documentManager, MYSYSTEM_ID);
        Assert.assertNull("Deleted system must not be available after deletion", systemModel);
        documentManager.save();
        
        // Re-create some documents
        systemModel = documentService.create(documentManager, MYSYSTEM_ID);
        Assert.assertNotNull("It must be possible to re-create a deleted document", systemModel);
        DocumentModel otherSystemModel = documentService.create(documentManager, MYOTHERSYSTEM_ID,
                systemModel.getPathAsString());
        Assert.assertNotNull("A child system must have been succesfully created", otherSystemModel);
        documentManager.save();
        
        // Delete the proxy only
        documentService.deleteProxy(documentManager, MYOTHERSYSTEM_ID, systemModel.getPathAsString());
        DocumentModelList allOtherSystemInstances = documentService.findAllInstances(documentManager, MYOTHERSYSTEM_ID);
        Assert.assertEquals("Only the repository instance of the deleted document should remain",
                1, allOtherSystemInstances.size());
        Assert.assertFalse("Only the repository instance of the deleted document should remain",
                allOtherSystemInstances.get(0).isProxy());
        
    }
}
