package org.easysoa.registry.systems;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.types.java.MavenDeliverable;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@Features(EasySOADefaultsFeature.class)
@RepositoryConfig(cleanup = Granularity.CLASS)
public class MavenHierarchyTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(MavenHierarchyTest.class);

    @Inject
    DocumentService documentService;

    @Test
    public void testClassification() throws ClientException {
        // Create manual SystemTreeRoot
        DocumentModel strModel = documentService.createDocument(documentManager,
                SystemTreeRoot.DOCTYPE, "MyRoot",
                DocumentModelHelper.WORKSPACEROOT_REF.toString(), "MyRoot");

        // Create System in it
        DocumentModel systemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                strModel.getPathAsString());

        // Create Deliverable in it
        DocumentModel deliverableModel = documentService.create(documentManager,
                new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa.registry:myartifact"),
                systemModel.getPathAsString());
        deliverableModel.setPropertyValue(Deliverable.XPATH_NATURE, MavenDeliverable.NATURE);
        documentManager.saveDocument(deliverableModel);
        
        documentManager.save();
        
        // Make sure that the deliverable is now in the Maven hierarchy
        
        DocumentModel istrModel = documentService.findDocument(documentManager,
                IntelligentSystemTreeRoot.DOCTYPE, "mavenHierarchy:mavenHierarchy");
        Assert.assertNotNull("A Maven hierarchy intelligent system tree root must have been created",
                istrModel);
        
        // (getChild() throws exceptions when the children are not found)
        DocumentModel firstChild = documentManager.getChild(istrModel.getRef(), "org.easysoa");
        DocumentModel secondChild = documentManager.getChild(firstChild.getRef(), "org.easysoa.registry");
        documentManager.getChild(secondChild.getRef(), "org.easysoa.registry:myartifact");
    }
    
}
