package org.easysoa.registry;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.easysoa.registry.utils.RepositoryHelper;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

import com.google.inject.Inject;

/**
 * Tests SOA node behaviours i.e. RepositoryManagementListener.
 * 
 * These are consecutive steps of a workflow so test methods must be run
 * in their order of definition and Nuxeo repository state must be kept in between.
 * TODO better : make them less interdependent.
 * 
 * @author mkalam-alami
 *
 */
@RepositoryConfig(cleanup = Granularity.CLASS) // to keep repository state between test methods
public class SoaNodeRepositoryTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SoaNodeRepositoryTest.class);

    @Inject
    DocumentService documentService;

    private static DocumentModel strModel;

    private static DocumentModel systemModel;

    private static DocumentModel deliverableModel;
    
    @Test
    public void testRepositoryCreation() throws ClientException {
        // Check that the repository document doesn't exist
        Assume.assumeTrue(!documentManager.exists(new PathRef(RepositoryHelper
                .getRepositoryPath(documentManager, defaultSubprojectId))));

        DocumentModel repositoryInstance = RepositoryHelper.getRepository(documentManager, defaultSubprojectId);
        Assert.assertNotNull("Repository must be created on first access", repositoryInstance);
    }
    
    /**
     * Reorders tests, so they can run on jdk7 (workaround for #134)
     * 
     * This is a workaround required because Class.getDeclaredMethods() returns random ordered
     * results since jdk7 : http://sourceforge.net/p/jumble/bugs/10/
     * Long term fix is to upgrade to junit 4.11 (to be done by Nuxeo first) and use @FixMethodOrder :
     * http://stackoverflow.com/questions/3693626/how-to-run-test-methods-in-spec-order-in-junit4
     * @throws ClientException
     * @throws IOException
     */
    @Test
    public void testAllReordered() throws ClientException {
        testDocumentRelocation();
        testDuplicatesHandling();
        testProxyCopy();
        testSourceCopy();
    }

    //@Test
    public void testDocumentRelocation() throws ClientException {
        // Create SystemTreeRoot
        strModel = documentService.createDocument(documentManager,
                SystemTreeRoot.DOCTYPE, "MyRoot", DocumentModelHelper
                .getWorkspacesPath(documentManager, defaultSubprojectId), "MyRoot");

        // Create System in it
        systemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                strModel.getPathAsString());

        documentManager.save();

        // Make sure that there are 2 instances of the system
        DocumentModelList systemInstances = documentService.findAllInstances(documentManager,
                systemModel);
        Assert.assertEquals("There must be 2 instances of the system", 2, systemInstances.size());
        boolean hasSystemTreeRootAsParent = false, hasRepositoryAsParent = false;
        for (DocumentModel systemInstance : systemInstances) {
            if (systemInstance.getParentRef().equals(strModel.getRef())) {
                Assert.assertTrue("Created system must be a proxy of the one from the repository",
                        systemInstance.isProxy());
                hasSystemTreeRootAsParent = true;
            } else if (systemInstance.getPathAsString().startsWith(
                    RepositoryHelper.getRepositoryPath(documentManager, defaultSubprojectId))) {
                hasRepositoryAsParent = true;
            }
        }
        Assert.assertTrue("System must be stored under the system tree root",
                hasSystemTreeRootAsParent);
        Assert.assertTrue("System must be stored in the repository", hasRepositoryAsParent);

        // Make sure that the instance in the system tree root is a proxy
        DocumentModel strChild = documentManager.getChild(strModel.getRef(), systemModel.getName());
        Assert.assertTrue("System tree root child must be a proxy", strChild.isProxy());
        
        // Make sure that the system tree root is not in the repository (not a SoaNode)
        DocumentModelList strInstances = documentManager.query("SELECT * FROM " + strModel.getType() + " WHERE dc:title = 'MyRoot'");
        Assert.assertEquals("The system tree root must not have a proxy", 1, strInstances.size());
    }

    //@Test
    public void testDuplicatesHandling() throws ClientException {
        // Create already created system
        DocumentModel duplicateModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                strModel.getPathAsString());
        
        // Make sure the system created twice still have only one source
        boolean sourceFound = false;
        DocumentModelList allInstances = documentService.findAllInstances(documentManager, duplicateModel);
        for (DocumentModel instance : allInstances) {
            if (instance.getPathAsString().startsWith(
                    RepositoryHelper.getRepositoryPath(documentManager, defaultSubprojectId))) {
                Assert.assertFalse("System created twice should still have only one source", sourceFound);
                sourceFound = true;
            }
        }
    }
    
    //@Test
    public void testProxyCopy() throws ClientException {
        // Create new system
        DocumentModel newSystemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem2"),
                strModel.getPathAsString());

        // Create deliverable to put in both systems
        deliverableModel = documentService.create(documentManager,
                new SoaNodeId(Deliverable.DOCTYPE, "org.company:mydeliverable"),
                systemModel.getPathAsString());

        documentManager.save();

        // Copy
        documentService.copy(documentManager, deliverableModel, newSystemModel.getRef());
        
        documentManager.save();

        // Make sure that there are 2 proxies of the document
        DocumentModelList proxies = documentService.findProxies(documentManager, deliverableModel);
        Assert.assertEquals("The deliverable must now have 2 proxies", 2, proxies.size());
        assertAllProxiesAreSetOnTheSources(proxies);
    }

    //@Test
    public void testSourceCopy() throws ClientException {
        // Create a third system
        DocumentModel thirdSystemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem3"),
                strModel.getPathAsString());

        // Copy the deployable source into it
        DocumentModel sourceDeployableModel = documentManager.getSourceDocument(deliverableModel.getRef());
        documentService.copy(documentManager, sourceDeployableModel, thirdSystemModel.getRef());
        documentManager.save();

        // Make sure that there are 3 proxies of the document
        DocumentModelList proxies = documentService.findProxies(documentManager, deliverableModel);
        Assert.assertEquals("The deliverable must now have 3 proxies", 3, proxies.size());
        assertAllProxiesAreSetOnTheSources(proxies);
    }

    private void assertAllProxiesAreSetOnTheSources(DocumentModelList proxies) throws ClientException {
        for (DocumentModel proxy : proxies) {
            Assert.assertTrue("All proxies must be set on the source SoaNodes",
                    proxy.getPathAsString().startsWith(
                            RepositoryHelper.getRepositoryPath(documentManager, defaultSubprojectId)));
        }
    }
}