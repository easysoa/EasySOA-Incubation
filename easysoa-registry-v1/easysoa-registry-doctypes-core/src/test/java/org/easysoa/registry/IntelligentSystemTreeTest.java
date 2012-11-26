package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@LocalDeploy({ "org.easysoa.registry.doctypes.core:OSGI-INF/sample-ist-contrib.xml" })
public class IntelligentSystemTreeTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(IntelligentSystemTreeTest.class);

    @Inject
    DocumentService documentService;

    @Test
    public void testEnvironmentTrees() throws ClientException {
        // Create manual SystemTreeRoot
        DocumentModel strModel = documentService.createDocument(documentManager,
                SystemTreeRoot.DOCTYPE, "MyRoot",
                DocumentModelHelper.WORKSPACEROOT_REF.toString(), "MyRoot");

        // Create System in it
        DocumentModel systemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                strModel.getPathAsString());

        // Create Endpoint in it
        DocumentModel endpointModel  = documentService.create(documentManager,
                new EndpointId("Production", "MyEndpoint"),
                systemModel.getPathAsString());
        documentManager.saveDocument(endpointModel);
        
        documentManager.save();
        
        repositoryLogger.logAllRepository();
        
        // Make sure that there are now 3 proxies of the endpoint,
        // one in the manual tree, the others in the intelligent trees
        
        // By environment
        
        DocumentModel istrModel = documentService.findDocument(documentManager,
                IntelligentSystemTreeRoot.DOCTYPE, "environment:environment");
        Assert.assertNotNull("A By Environment intelligent system tree root must have been created",
                istrModel);
        Assert.assertEquals("The By Environment STR must contain 1 system", 1, documentManager.getChildren(istrModel.getRef()).size());
        
        DocumentModel productionSystem = documentService.findDocument(documentManager,
                IntelligentSystem.DOCTYPE, "Production");
        Assert.assertNotNull("A 'Production' system must have been created", productionSystem);

        DocumentModelList productionSystemChildren = documentManager.getChildren(productionSystem.getRef());
        Assert.assertEquals("The 'Production' system must have a child", 1, productionSystemChildren.size());
        
        DocumentModel childModel = productionSystemChildren.get(0);
        Assert.assertTrue("The document in the 'Production' system must be the expected endpoint",
                Endpoint.DOCTYPE.equals(childModel.getType()) && 
                "MyEndpoint".equals(childModel.getPropertyValue(Endpoint.XPATH_URL)));
        
        // By alphabetical order
        
        endpointModel = documentManager.getDocument(new PathRef("/default-domain/workspaces/alphabeticalOrder:first2Letters/P/R/Production:MyEndpoint"));
        Assert.assertNotNull("The endpoint must be classified by alphabetical order " +
        		"(= in a multiple-levels hierarchy defined with parameters)", endpointModel);
        
        // Flat documents
        
        endpointModel = documentManager.getDocument(new PathRef("/default-domain/workspaces/everythingFlat:everythingFlat/Production:MyEndpoint"));
        Assert.assertNotNull("The endpoint must be added to the flat document list", endpointModel);
       
    }
    
}
