package org.easysoa.registry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

/**
 * Tests inheritance on DocumentService SOA node operations.
 * 
 * These are consecutive steps of a workflow so test methods must be run
 * in their order of definition and Nuxeo repository state must be kept in between.
 * TODO better : make them less interdependent.
 * 
 * @author mkalam-alami
 *
 */
@RepositoryConfig(cleanup = Granularity.CLASS) // to keep repository state between test methods
public class InheritedDataTest extends AbstractRegistryTest {

	private static final SoaNodeId MYIS_ID = new SoaNodeId(InformationService.DOCTYPE, "myinfoserv");

	private static final SoaNodeId MYIMPL_ID = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl");
	
	private static final SoaNodeId MYIMPL2_ID = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl2");

	private static final SoaNodeId MYENDPOINT_ID = new EndpointId("Production", "MyEndpoint");

	@Inject
	private CoreSession documentManager;
	
	@Inject
	private DocumentService documentService;

	private static DocumentModel myServiceImplModel;

	private static ServiceImplementation myServiceImpl;

	private static DocumentModel myEndpointModel;

	private static ServiceImplementation myEndpointServiceImpl;

	private static DocumentModel endpointProxyModel;

    
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
    public void testAllReordered() throws Exception {
        testInheritanceOnCreation();
        testInheritanceOnUpdate();
        testInheritanceOnMove();
        testQuery();
        testInheritanceOnDeletion();
        testUuidSelectors();
    }
    
    
	//@Test
	public void testInheritanceOnCreation() throws Exception {
		myServiceImplModel = documentService.create(documentManager, MYIMPL_ID);
		myServiceImpl = myServiceImplModel.getAdapter(ServiceImplementation.class);
		myServiceImpl.setTests(Arrays.asList("org.easysoa.Test1"));
		documentManager.saveDocument(myServiceImplModel);
		documentManager.save();
		
		myEndpointModel = documentService.create(documentManager,
				MYENDPOINT_ID, myServiceImplModel.getPathAsString());
		documentManager.save();
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertTrue("Inherited facets' metadata must be copied when child is created",
				myEndpointServiceImpl.getTests() != null &&
				myEndpointServiceImpl.getTests().contains("org.easysoa.Test1"));
	}

	//@Test
	public void testInheritanceOnUpdate() throws Exception {
		// ...when impl is updated...
		myServiceImplModel = documentService.findSoaNode(documentManager, MYIMPL_ID);
		myServiceImpl = myServiceImplModel.getAdapter(ServiceImplementation.class);
		List<String> implTests = myServiceImpl.getTests();
		implTests.add("org.easysoa.Test2");
		myServiceImpl.setTests(implTests);
		documentManager.saveDocument(myServiceImplModel);
		documentManager.save();
		myEndpointModel = documentService.findSoaNode(documentManager, MYENDPOINT_ID);
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertTrue("Inherited facets' metadata must be maintained on updates",
				myEndpointServiceImpl.getTests() != null &&
				myEndpointServiceImpl.getTests().contains("org.easysoa.Test2"));
	}
	

	//@Test
	public void testInheritanceOnMove() throws Exception {
		// ...when endpoint is moved to another impl...
		DocumentModel myServiceImpl2Model = documentService.create(documentManager, MYIMPL2_ID);
		ServiceImplementation myServiceImpl2 = myServiceImpl2Model.getAdapter(ServiceImplementation.class);
		myServiceImpl2.setTests(Arrays.asList("org.easysoa.Test3"));
		documentManager.saveDocument(myServiceImpl2Model);
		endpointProxyModel = documentService.findProxies(documentManager, MYENDPOINT_ID).get(0);
		endpointProxyModel = documentManager.move(endpointProxyModel.getRef(), myServiceImpl2Model.getRef(), endpointProxyModel.getName());
		documentManager.save();

		myEndpointModel = documentService.findSoaNode(documentManager, MYENDPOINT_ID);
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertTrue("Inherited facets' metadata must be updated when child is moved",
				myEndpointServiceImpl.getTests() != null &&
				myEndpointServiceImpl.getTests().contains("org.easysoa.Test3"));
	}

	//@Test
	public void testQuery() throws Exception {
		// Intermission: take advantage of this feature,
		// by looking for "the endpoints that are covered by a certain test"
		DocumentModelList results = documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
				+ " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
				+ ServiceImplementation.XPATH_TESTS + "/* = 'org.easysoa.Test3'");
		Assert.assertEquals("Query of ServiceImpl metadata on Endpoints must work", 1, results.size());
		
		// Check parents IDs storage feature...
		myEndpointModel = results.get(0);
		Endpoint myEndpoint = myEndpointModel.getAdapter(Endpoint.class);
		List<SoaNodeId> endpointParentIds = myEndpoint.getParentIds();
		Assert.assertEquals("Endpoint must store its parents' ids", endpointParentIds.size(), 1);
		Assert.assertEquals("Endpoint must store its correct parents' ids", endpointParentIds.get(0), MYIMPL2_ID);
		
		// ...Before testing it with a query (look for "the endpoints that expose a certain serviceimpl")
		results = documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
				+ " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
				+ Endpoint.XPATH_PARENTSIDS + "/* = '" + MYIMPL2_ID + "'");
		Assert.assertEquals("Query of parent IDs on Endpoints must work", 1, results.size());
		
		// Look for an endpoint that is classified under any impl
		results = documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
				+ " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
				+ Endpoint.XPATH_PARENTSIDS + "/* LIKE '%" + ServiceImplementation.DOCTYPE + ":%'");
		Assert.assertEquals("Query of parent IDs on Endpoints must work (pt. 2)", 1, results.size());
	}
	
	//@Test
	public void testInheritanceOnDeletion() throws Exception {
		// ...and when endpoint is removed from impl

        DocumentModelList results = documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
                + " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
                + Endpoint.XPATH_PARENTSIDS + "/* = '" + MYIMPL2_ID + "'");
        Assert.assertEquals("Checking state before removal...", 1, results.size());
        
		documentManager.removeDocument(endpointProxyModel.getRef());
		documentManager.save();
		myEndpointModel = documentService.findSoaNode(documentManager, MYENDPOINT_ID);
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertEquals("Inherited facets' metadata must be reset when child is deleted",
				0, myEndpointServiceImpl.getTests().size());
        
        // ...Before testing it with a query (look for "the endpoints that expose a certain serviceimpl")
		results = documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
                + " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
                + Endpoint.XPATH_PARENTSIDS + "/* = '" + MYIMPL2_ID + "'");
        Assert.assertEquals("Query of parent IDs on Endpoints must work after removal", 0, results.size());
	}
	
	//@Test
	public void testUuidSelectors() throws ClientException {
		// Link comp > infoservice < serviceimpl by UUIDs
		DocumentModel infoServModel = documentService.create(documentManager, MYIS_ID);
		myServiceImplModel.setPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE, infoServModel.getId());
		documentManager.saveDocument(myServiceImplModel);
		DocumentModel componentModel = documentService.create(documentManager, new SoaNodeId(Component.DOCTYPE, "mycomp"));
		componentModel.setPropertyValue(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE, infoServModel.getId());
		documentManager.saveDocument(componentModel);
		documentManager.save();
		
		// Make sure that comp's metadata has been transfered to infoservice then to serviceimpl
		DocumentModel myInfoServiceModel = documentService.findSoaNode(documentManager, MYIS_ID);
		Assert.assertEquals("Facet inheritance through UUID metadata must work",
				infoServModel.getId(),
				myInfoServiceModel.getPropertyValue(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE));
		myServiceImplModel = documentService.findSoaNode(documentManager, MYIMPL_ID);
		Assert.assertEquals("Facet inheritance through UUID metadata must work",
				infoServModel.getId(),
				myServiceImplModel.getPropertyValue(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE));
	}
}
