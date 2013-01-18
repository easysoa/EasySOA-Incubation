package org.easysoa.registry;

import static org.easysoa.registry.utils.NuxeoListUtils.list;

import java.util.HashMap;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.snapshot.Snapshot;
import org.nuxeo.snapshot.Snapshotable;

/**
 * 
 * @author mdutoo, mkalam-alami
 *
 */
@RepositoryConfig(cleanup = Granularity.CLASS)
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
    
    public static final SoaNodeId COMPONENT_ID = 
    		new SoaNodeId(Component.DOCTYPE, "xxx component");
    
    @Inject
    DocumentService documentService;

    @Inject
    DiscoveryService discoveryService;
    
    @Inject
    SoaMetamodelService soaMetamodelService;
    
    
    //@Test
    public void testDiscoveryAcrossVersionedSubproject() throws Exception {
        
        // SUBPROJECT :
        // creating projects
        DocumentModel projectModel = SubprojectServiceImpl.createProject(documentManager, "MySoaProject");

        DocumentModel otherProjectModel = SubprojectServiceImpl.createProject(documentManager, "MyOtherSoaProject");
        
        // creating subprojects
        DocumentModel specificationsSubprojectModel = SubprojectServiceImpl.createSubproject(
                documentManager, "Specifications", projectModel, null);
        String specificationsSubprojectId = SubprojectServiceImpl.subprojectToId(specificationsSubprojectModel);

        DocumentModel realisationSubprojectModel = SubprojectServiceImpl.createSubproject(
                documentManager, "Realisation", projectModel, list(specificationsSubprojectModel));
        String realisationSubprojectId = SubprojectServiceImpl.subprojectToId(realisationSubprojectModel);

        DocumentModel anotherRealisationSubprojectModel = SubprojectServiceImpl.createSubproject(
                documentManager, "Realisation", projectModel, null);
        
        documentManager.save();
        
        // cross-subproject ids
        SoaNodeId CSP_INFORMATIONSERVICE_ID = new SoaNodeId(specificationsSubprojectId,
                InformationService.DOCTYPE, "nsxxx:namexxx");
        SoaNodeId CSP_COMPONENT_ID = new SoaNodeId(specificationsSubprojectId, Component.DOCTYPE, "xxx component");
        // - SUBPROJECT

        
        // Discover information service
        HashMap<String, Object> isProperties = new HashMap<String, Object>();
        //isProperties.put(SubprojectNode.XPATH_SUBPROJECT, specificationsSubprojectId); // SUBPROJECT
        ///isProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, specificationsSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV)); // SUBPROJECT
        isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
        isProperties.put(InformationService.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
        DocumentModel foundInfoServ = discoveryService.runDiscovery(documentManager, CSP_INFORMATIONSERVICE_ID, isProperties, null);
        
        // Discover component
        HashMap<String, Object> compProperties = new HashMap<String, Object>();
        //compProperties.put(SubprojectNode.XPATH_SUBPROJECT, specificationsSubprojectId); // SUBPROJECT
        ///compProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, specificationsSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV)); // SUBPROJECT
        // requires JAXWS (else would override IS's which would not be matched anymore ; TODO Q otherwise ??) :
        compProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
        compProperties.put(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE, foundInfoServ.getId());
        DocumentModel foundComponent = discoveryService.runDiscovery(documentManager, CSP_COMPONENT_ID, compProperties, null);

        // Create versioned snapshot out of Specifications
        Snapshotable snapshotable = specificationsSubprojectModel.getAdapter(Snapshotable.class);
        Snapshot snapshot = snapshotable.createSnapshot(VersioningOption.MINOR);
        // TODO TODOOOOOOOOOO update IDs in listener of event ABOUT_TO_CREATE_LEAF_VERSION_EVENT
        //DocumentModel specificationsSubprojectV01Model = snapshot.getDocument();
        documentManager.save();
        DocumentModel specificationsSubprojectV01Model = documentManager.getLastDocumentVersion(new IdRef(((String[]) realisationSubprojectModel
                .getPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS))[0])); // TODO updateToVersio
        ///realisationSubprojectModel.setPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS, new String[]{ specificationsSubprojectV01Model.getId() });
        DocumentModel foundInfoServV01 = documentService.find(documentManager, new SoaNodeId(
                SubprojectServiceImpl.subprojectToId(specificationsSubprojectV01Model),
                InformationService.DOCTYPE, "nsxxx:namexxx"));
        Assert.assertNotNull(foundInfoServV01);
        Assert.assertNotSame("Once versioned, InformationService should have different Nuxeo ID",
                foundInfoServ.getId(), foundInfoServV01.getId());
        
        DocumentModel versioningRealisationSubprojectModel = SubprojectServiceImpl.createSubproject(
                documentManager, "Realisation", projectModel, list(specificationsSubprojectV01Model));
        
        // Discover service impl
        String versioningRealisationSubprojectId = SubprojectServiceImpl.subprojectToId(specificationsSubprojectModel);
        SoaNodeId CSP_FIRST_SERVICEIMPL_ID = new SoaNodeId(versioningRealisationSubprojectId,
                ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx");
        HashMap<String, Object> implProperties = new HashMap<String, Object>();
        //implProperties.put(SubprojectNode.XPATH_SUBPROJECT, realisationSubprojectId); // SUBPROJECT
        ///implProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, realisationSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV)); // SUBPROJECT
        implProperties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
        implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
        DocumentModel foundImpl = discoveryService.runDiscovery(documentManager, CSP_FIRST_SERVICEIMPL_ID, implProperties, null);
        
        Assert.assertEquals("Created impl must be linked to existing versioned matching information service", foundInfoServV01.getId(),
                foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
    }
	
    @Test
    public void testSimpleDiscovery() throws Exception {
        // SUBPROJECT :
        // creating projects
        DocumentModel projectModel = SubprojectServiceImpl.createProject(documentManager, "MySoaProject");

        DocumentModel otherProjectModel = SubprojectServiceImpl.createProject(documentManager, "MyOtherSoaProject");
        
        documentManager.save(); // to trigger auto init
        
        // creating subprojects
        // reusing auto created (template) Specifications TODO the same for the others
        //DocumentModel specificationsSubprojectModel = SubprojectServiceImpl.createSubproject(documentManager,
        //        "Specifications", projectModel, null);
        DocumentModel specificationsSubprojectModel = SubprojectServiceImpl.getSubprojectById(documentManager,
                projectModel.getPathAsString() + '/' + Subproject.SPECIFICATIONS_SUBPROJECT_NAME + "_v");
        String specificationsSubprojectId = SubprojectServiceImpl.subprojectToId(specificationsSubprojectModel);

        DocumentModel realisationSubprojectModel = SubprojectServiceImpl.createSubproject(documentManager,
                "Realisation", projectModel, list(specificationsSubprojectModel));
        String realisationSubprojectId = SubprojectServiceImpl.subprojectToId(realisationSubprojectModel);

        DocumentModel anotherRealisationSubprojectModel = SubprojectServiceImpl.createSubproject(documentManager,
                "RealisationPhase2", projectModel, null);
        String anotherRealisationSubprojectId = SubprojectServiceImpl.subprojectToId(anotherRealisationSubprojectModel);
        
        documentManager.save();
        // cross-subproject ids
        SoaNodeId CSP_INFORMATIONSERVICE_ID = new SoaNodeId(specificationsSubprojectId,
                InformationService.DOCTYPE, "nsxxx:namexxx");
        SoaNodeId CSP_FIRST_SERVICEIMPL_ID = new SoaNodeId(realisationSubprojectId,
                ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx");
        SoaNodeId CSP_COMPONENT_ID = new SoaNodeId(specificationsSubprojectId,
                Component.DOCTYPE, "xxx component");
        SoaNodeId CSP_SECOND_SERVICEIMPL_ID = new SoaNodeId(realisationSubprojectId,
                        ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenameyyy");
        SoaNodeId CSP_SECOND_SERVICEIMPL_SEPARATE_SUBPROJECT_ID = new SoaNodeId(anotherRealisationSubprojectId,
                ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenameyyy_separateSubproject");
        // - SUBPROJECT
        
        
        // Discover service impl
    	HashMap<String, Object> implProperties = new HashMap<String, Object>();
        implProperties.put(SubprojectNode.XPATH_SUBPROJECT, realisationSubprojectId); // SUBPROJECT
        ///implProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, realisationSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV)); // SUBPROJECT
    	implProperties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
    	implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
        discoveryService.runDiscovery(documentManager, CSP_FIRST_SERVICEIMPL_ID, implProperties, null);
        
    	// Discover information service
    	HashMap<String, Object> isProperties = new HashMap<String, Object>();
        isProperties.put(SubprojectNode.XPATH_SUBPROJECT, specificationsSubprojectId); // SUBPROJECT
        ///isProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, specificationsSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV)); // SUBPROJECT
    	isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
    	isProperties.put(InformationService.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	DocumentModel foundInfoServ = discoveryService.runDiscovery(documentManager, CSP_INFORMATIONSERVICE_ID, isProperties, null);
    	
    	// Discover component
    	HashMap<String, Object> compProperties = new HashMap<String, Object>();
    	compProperties.put(SubprojectNode.XPATH_SUBPROJECT, specificationsSubprojectId); // SUBPROJECT
    	///compProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, specificationsSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV)); // SUBPROJECT
    	// requires JAXWS (else would override IS's which would not be matched anymore ; TODO Q otherwise ??) :
    	compProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
    	compProperties.put(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE, foundInfoServ.getId());
    	DocumentModel foundComponent = discoveryService.runDiscovery(documentManager, CSP_COMPONENT_ID, compProperties, null);
    	
    	// check
        foundInfoServ = documentService.find(documentManager, CSP_INFORMATIONSERVICE_ID);
        DocumentModel foundImpl = documentService.find(documentManager, CSP_FIRST_SERVICEIMPL_ID);
        Assert.assertEquals("Created information service must be linked to existing matching impl", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
    	
    	// Discover another impl
        implProperties.put(ServiceImplementation.XPATH_ISMOCK, "1");
        discoveryService.runDiscovery(documentManager, CSP_SECOND_SERVICEIMPL_ID, implProperties, null);

        // check
        foundImpl = documentService.find(documentManager, CSP_SECOND_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
        
        // SUBPROJECT :
        // Discover another impl in a separate subproject
        HashMap<String, Object> anotherImplProperties = new HashMap<String, Object>(implProperties);
        //anotherImplProperties.put(SubprojectNode.XPATH_SUBPROJECT, anotherRealisationSubprojectId);
        ///anotherImplProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, anotherRealisationSubprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV));
        discoveryService.runDiscovery(documentManager, CSP_SECOND_SERVICEIMPL_SEPARATE_SUBPROJECT_ID, anotherImplProperties, null);

        // check
        foundImpl = documentService.find(documentManager, CSP_SECOND_SERVICEIMPL_SEPARATE_SUBPROJECT_ID);
        Assert.assertEquals("Separate subproject impl must not be linked to existing matching information service", null,
                foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
        // - SUBPROJECT
    	
    	// Discover a non matching impl ((in another subproject))
    	HashMap<String, Object> impl3Properties = new HashMap<String, Object>();
    	impl3Properties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
    	impl3Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace2}name2");
        discoveryService.runDiscovery(documentManager, THIRD_SERVICEIMPL_ID, impl3Properties, null);
        
        // check
        foundImpl = documentService.find(documentManager, THIRD_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must not be linked to existing matching information service", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));

    	// Rediscover known is then impl
        foundInfoServ  = discoveryService.runDiscovery(documentManager, CSP_INFORMATIONSERVICE_ID, isProperties, null);
        foundImpl = discoveryService.runDiscovery(documentManager, CSP_SECOND_SERVICEIMPL_ID, implProperties, null);
        Assert.assertEquals("Created impl must still be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
    }
    
    @Test
    public void testSimpleDiscoveryWithCriteria() throws Exception {
        // Discover service impl that won't match platform criteria TODO SHOULD STILL MATCH AT IS LEVEL
    	HashMap<String, Object> implN1Properties = new HashMap<String, Object>();
    	implN1Properties.put(ServiceImplementation.XPATH_ISMOCK, "1");
    	implN1Properties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
    	implN1Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	implN1Properties.put(ServiceImplementation.XPATH_IMPL_LANGUAGE, Platform.LANGUAGE_JAVASCRIPT); // differs
    	//implN1Properties.put("impl:build", "Maven");
    	implN1Properties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
    	implN1Properties.put("deltype:nature", "Maven");
    	implN1Properties.put("deltype:repositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx" + "N1KO"), implN1Properties, null);
        
    	// Discover service impl
    	HashMap<String, Object> implProperties = new HashMap<String, Object>();
    	implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	implProperties.put(ServiceImplementation.XPATH_IMPL_LANGUAGE, Platform.LANGUAGE_JAVA);
    	//implProperties.put("impl:build", "Maven");
    	implProperties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
    	implProperties.put("deltype:nature", "Maven");
    	implProperties.put("deltype:repositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
        discoveryService.runDiscovery(documentManager, FIRST_SERVICEIMPL_ID, implProperties, null);
        
    	// Discover service impl that won't match platform criteria
    	HashMap<String, Object> implN2Properties = new HashMap<String, Object>();
    	implN2Properties.put(ServiceImplementation.XPATH_ISMOCK, "1");
    	implN2Properties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
    	implN2Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	implN2Properties.put(ServiceImplementation.XPATH_IMPL_LANGUAGE, Platform.LANGUAGE_JAVA);
    	//implN2Properties.put("impl:build", "Maven");
    	implN2Properties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
    	implN2Properties.put("deltype:nature", "Maven");
    	implN2Properties.put("deltype:repositoryUrl", "http://maven.ow2.org/nexus/content/groups/public"); // differs
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx" + "N2KO"), implN2Properties, null);

    	// Discover information service that won't match platform criteria
    	HashMap<String, Object> isN1Properties = new HashMap<String, Object>();
    	isN1Properties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
    	isN1Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	isN1Properties.put(Platform.XPATH_LANGUAGE, Platform.LANGUAGE_JAVA);
    	isN1Properties.put("platform:build", "Maven");
    	isN1Properties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXRS); // differs
    	isN1Properties.put("platform:deliverableNature", "Maven");
    	isN1Properties.put("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE, "nsxxx:namexxx" + "N1KO"), isN1Properties, null);

    	// Discover information service
    	HashMap<String, Object> isProperties = new HashMap<String, Object>();
    	isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
    	isProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
    	isProperties.put(Platform.XPATH_LANGUAGE, Platform.LANGUAGE_JAVA);
    	isProperties.put("platform:build", "Maven");
    	isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
    	isProperties.put("platform:deliverableNature", "Maven");
    	isProperties.put("platform:deliverableRepositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	DocumentModel foundInfoServ = discoveryService.runDiscovery(documentManager, INFORMATIONSERVICE_ID, isProperties, null);
    	
    	// check
        foundInfoServ = documentService.find(documentManager, INFORMATIONSERVICE_ID);
        DocumentModel foundImpl = documentService.find(documentManager, FIRST_SERVICEIMPL_ID);
        Assert.assertEquals("Created information service must be linked to existing matching impls", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
    	
    	// Discover another impl
        discoveryService.runDiscovery(documentManager, SECOND_SERVICEIMPL_ID, implProperties, null);

        // check
        foundImpl = documentService.find(documentManager, SECOND_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must be linked to existing matching information service", foundInfoServ.getId(),
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
    	
    	// Discover a non matching impl
    	HashMap<String, Object> impl3Properties = new HashMap<String, Object>();
    	impl3Properties.put(ServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS); // differs
    	impl3Properties.put("deltype:nature", "Maven");
    	impl3Properties.put("deltype:repositoryUrl", "http://maven.nuxeo.org/nexus/content/groups/public");
    	impl3Properties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{namespace2}name2");
        discoveryService.runDiscovery(documentManager, THIRD_SERVICEIMPL_ID, impl3Properties, null);
        
        // check
        foundImpl = documentService.find(documentManager, THIRD_SERVICEIMPL_ID);
        Assert.assertEquals("Created impl must not be linked to existing matching information service", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));

    	// Discover another is, then impl that has 2 matches
        foundInfoServ = discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE, "nsxxx:namexxx" + "P1KO"), isProperties, null);
        foundImpl = discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:namexxx=servicenamexxx" + "P1KO"), implProperties, null);
        Assert.assertEquals("Created impl must not be linked because there are too much matching information services", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
    	
        // Discover endpoint that matches no is or impl (use matching dashboard TODO mka)
        HashMap<String, Object> epProperties = new HashMap<String, Object>();
        DocumentModel foundEndpoint = discoveryService.runDiscovery(documentManager, new EndpointId("Production", "staging:http://localhost:8080/cxf/WS1"), epProperties, null);
        Assert.assertEquals("Created endpoint must not be linked to existing matching information service", null,
        		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));

        
        // TODO LATER Discover endpoint that matches is (on url-extracted service name), but no impl LATER
        foundEndpoint = discoveryService.runDiscovery(documentManager, new EndpointId("staging", "http://localhost:8080/cxf/name"), epProperties, null);
        //Assert.assertEquals("Created endpoint must be linked to existing matching information service", foundInfoServ.getId(),
        //		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE));

        // Discover endpoint that matches is (on provided portType & disco'd component), but no impl
        HashMap<String, Object> compProperties = new HashMap<String, Object>();
        // requires JAXWS (else would override IS's which would not be matched anymore ; TODO Q otherwise ??) :
        compProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
        compProperties.put(Component.XPATH_COMP_LINKED_INFORMATION_SERVICE, foundInfoServ.getId());
        DocumentModel foundComponent = discoveryService.runDiscovery(documentManager, COMPONENT_ID, compProperties, null);
        epProperties.put(Endpoint.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS); // TODO better ?!?
        epProperties.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, "{namespace}name");
        epProperties.put(Endpoint.XPATH_COMPONENT_ID, foundComponent.getId());
        foundEndpoint = discoveryService.runDiscovery(documentManager,  new EndpointId("staging", "http://localhost:8080/cxf/WS2"), epProperties, null);
        Assert.assertNotNull("Created endpoint must be linked to existing matching information service",
        		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
        
        // Discover endpoint that matches impl (TODO how, by parent ?? matching ?? component / platform ?)
        foundEndpoint = discoveryService.runDiscovery(documentManager,  new EndpointId("staging", "http://localhost:8080/cxf/WS3"), epProperties, null);
        Assert.assertNotNull("Created endpoint must be linked to existing matching information service",
        		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));

        
    	// Discover a non matching impl because of provided component id and check
        // TODO how is provided component id : in DiscoveryService (but then put matching algo there),
        // or on impl then how to check it (in discoService) and how to handle inconsistencies with metas (validation ?)
        // or on impl as metas with platformUrl as platform id
    	implProperties.put(ServiceImplementation.XPATH_COMPONENT_ID, "id-that-doesnt-exist");
    	foundImpl = discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nszzz:namezzz=servicenamezzz" + "C1KO"), implProperties, null);
        Assert.assertEquals("Created impl must not be linked to existing matching information service because of provided component id", null,
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));

    	// Discover a matching impl because of provided component id and check
        implProperties.put(ServiceImplementation.XPATH_COMPONENT_ID, foundComponent.getId());
    	foundImpl = discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "nszzz:namezzz=servicenamezzz" + "C1OK"), implProperties, null);
        Assert.assertNotNull("Created impl must be linked to existing matching information service because of provided component id",
        		foundImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
        
        // Discover an information service, then an endpoint that matches no impl but matches the IS.
        // A link should be made through a placeholder
        isProperties.clear();
        isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
        isProperties.put(InformationService.XPATH_WSDL_PORTTYPE_NAME, "{www}www");
    	foundInfoServ = discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE, "nswww:namewww"), isProperties, null);
       
        epProperties.clear();
        epProperties.put(Endpoint.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS); // TODO better ?!?
        epProperties.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, "{www}www");
    	foundEndpoint = discoveryService.runDiscovery(documentManager, new EndpointId("Prod", "www.com"), epProperties, null);
        Assert.assertEquals("Created endpoint must be linked to matching information service", foundInfoServ.getId(),
        		foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
        SoaNodeId implId = foundEndpoint.getAdapter(SoaNode.class).getParentOfType(ServiceImplementation.DOCTYPE);
        Assert.assertNotNull("Created endpoint must be linked to an impl", implId);
        foundImpl = documentService.find(documentManager, implId);
        Assert.assertEquals("Created endpoint must be linked to a placeholder impl",
        		true, foundImpl.getPropertyValue(SoaNode.XPATH_ISPLACEHOLDER));
        
        // discover an endpoint matching no IS, then an impl that matches it ; they should be matched
        /*isProperties.clear();
        isProperties.put(Platform.XPATH_SERVICE_LANGUAGE, Platform.SERVICE_LANGUAGE_JAXWS);
        isProperties.put(InformationService.XPATH_WSDL_PORTTYPE_NAME, "{www}noISEndpoint");
        foundInfoServ = discoveryService.runDiscovery(documentManager, new SoaNodeId(InformationService.DOCTYPE, "www:noISEndpoint"), isProperties, null);
        */
        epProperties.clear();
        epProperties.put(Endpoint.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);
        epProperties.put(Endpoint.XPATH_WSDL_PORTTYPE_NAME, "{www}noISEndpoint");
        foundEndpoint = discoveryService.runDiscovery(documentManager, new EndpointId("Prod", "www.a.com/noISEndpoint"), epProperties, null);

        implProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, "{www}noISEndpoint");
        foundImpl = discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "www:noISEndpoint=noISEndpointImplService" + "C1KO"), implProperties, null);
        
        //Assert.assertEquals("Created endpoint must be linked to matching impl", foundImpl.getId(),
        //        foundEndpoint.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE));
        implId = foundEndpoint.getAdapter(SoaNode.class).getParentOfType(ServiceImplementation.DOCTYPE);
        //foundImpl = documentService.find(documentManager, implId);
        Assert.assertEquals("Created endpoint must be linked to matching impl",
                implId.getName(), foundImpl.getPropertyValue(SoaNode.XPATH_SOANAME));
        
        
        // discover endpoint matching guided by component / platform (criteria)
        // ex. in web disco :
        // (0. choose is (impl) directly)
        // 1. choose (env and) component (because you know it fits there), match & link IS in it, find impl HOW ? OPT put impl in it if platform matches, try to find and link to platform
        // (1.bis create "technical component")
        // 2. choose (impl) platform (id, criteria ?) (because you know it's this techno / runs & is dev'd on it), use it to match to impl, then / else match is using this new info (platform matches)
        // 3. both
        // TODO component path actor:cpt, isMock as "test" platform
        discoveryService.runDiscovery(documentManager, new EndpointId("staging", "http://localhost:8080/cxf"), epProperties, null);
        // OK match impl with is on is' component constraints (including platform:deliverableRepositoryUrl that can act as platform id), guided by impl's own metas
        // still TODO set link to Component & isProxy
        // TODO match guided by provided component id : if provided, use it as additional criteria
        // TODO match guided by provided platform id : (can already use platform:deliverableRepositoryUrl as id) : if provided, use it as additional criteria
        // TODO match guided by provided platform criteria ??
        // TODO also match on prop'd url on impl  ???????
    }
    
    @Test
    public void testCheckSoaNode() throws PropertyException, ClientException {
    	DocumentModel soaNodeDoc = documentService.newSoaNodeDocument(documentManager,
    			new SoaNodeId(ServiceImplementation.DOCTYPE, "nsxxx:testCheckSoaNode=testCheckSoaNodeImpl"));
    	soaNodeDoc.setPropertyValue(SoaNode.XPATH_SOANAME, null);
        try {
        	soaMetamodelService.validateIntegrity(soaNodeDoc, false);
			Assert.fail("validateIntegrity should fail on null soan:name");
		} catch (ModelIntegrityException e) {
			Assert.assertTrue("testCheckSoaNode successful",  true);
		}
    }
    
}

