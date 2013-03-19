package org.easysoa.registry;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.easysoa.registry.facets.ServiceImplementationDataFacet;
import org.easysoa.registry.matching.MatchingHelper;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.EmptyDocumentModelList;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.runtime.api.Framework;


/**
 * TODO matching should be attempted / done if at least one exact (portType, endpointUrl)
 * or provided guide (component, platform (id) of "actual" (??)) info
 * (and not only portType)
 * 
 * @author mdutoo
 *
 */
public class EndpointMatchingServiceImpl implements EndpointMatchingService {

	private static Logger logger = Logger.getLogger(EndpointMatchingServiceImpl.class);
    
    public boolean isEndpointAlreadyMatched(DocumentModel endpointDocument,
            CoreSession documentManager) throws ClientException {
        return endpointDocument.isProxy() // the proxy, possibly still being created,
                // used to link endpoint under impl (else loops)
                || !documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
                + " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
                + NXQL.ECM_UUID + "='" + endpointDocument.getId() + "' AND "
                // NB. %ServiceImplementation to also handle JavaServiceImplementations
                + Endpoint.XPATH_PARENTSIDS + "/* LIKE '%ServiceImplementation:%'").isEmpty();
    }
    
    
	public DocumentModelList findServiceImplementations(CoreSession documentManager,
			DocumentModel endpoint, String filterComponentId,
			boolean skipPlatformMatching, boolean requireAtLeastOneExactCriteria/*, String visibility*/) throws ClientException { // TODO impl vs runtime platform matching ?

        // how should work matching in discovery & dashboard for :
        
    	// endpoint : if has no impl,
    	// find impl : on IS req itf (portType), and whose IS is in provided component if any,
        // and whose impl platform (criteria) match the endpoint's discovered impl platform (criteria) if any ;
    	// if single matched link to it
    	// (if more than one result, use matching dashboard)
    	// if none, create impl and do as for in ServiceMatchingImpl : 1. and fill component, else 2. and link to platform, else 3. 
		
		// Init
		DocumentService documentService = getDocumentService();
    	boolean anyExactCriteria = false;
		MatchingQuery query = new MatchingQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE);

        // Filter by subproject
        query.addCriteria(SubprojectServiceImpl.buildCriteriaSeenFromSubproject(endpoint)/*, visibility*/);

    	// Match platform properties :
    	
    	// 1. IF A LINKED PLATFORM HAS BEEN PROVIDED FOR THE ENDPOINT BY THE PROBE EX. WEB DISCO
    	if (!skipPlatformMatching) {
    	    if (endpoint.hasFacet(Endpoint.SCHEMA_ENDPOINT)
    	            && endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM) != null) {
    	        
    		DocumentModel platformDocument = documentManager.getDocument(
    				new IdRef((String) endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM)));
    		
			for (String property : MatchingHelper.implPlatformPropsToMatch ) {
		    	query.addConstraintMatchCriteriaIfSet(property,
		    			platformDocument.getPropertyValue(property));
    		}
            // TODO up to runtime platform constraints
			
			// TODO rather match platform ID (if any) than criteria
	    	//query.addConstraintMatchCriteriaWithAltIfSet("iserv:linkedPlatform", "impl:platform",
	    	//		endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM));

            }
    	
    	// TODO ELSE MATCH DISCOVERED PLATFORM CRITERIA OF THE ENDPOINT
    	// AGAINST THE IMPL'S SERVICE'S PLATFORM IF ANY ELSE AGAINST THE IMPL'S DISCOVERED PLATFORM CRITERIA
    	// TODO make it possible in disco (?) & model (yes)
    	    else if (endpoint.hasFacet(ServiceImplementationDataFacet.FACET_SERVICEIMPLEMENTATIONDATA)) {
    	    // TODO rather match platform only if known as "actual" (or linked platform provided ??), rather than ...IfSet
	    	query.addConstraintMatchCriteriaWithAltIfSet(Platform.XPATH_IDE,
	    			ServiceImplementation.XPATH_IMPL_IDE,
	    			endpoint.getPropertyValue(ServiceImplementation.XPATH_IMPL_IDE)); // TODO Q rather Endpoint.xx ?!?
	    	query.addConstraintMatchCriteriaWithAltIfSet(Platform.XPATH_LANGUAGE,
	    			ServiceImplementation.XPATH_IMPL_LANGUAGE,
	    			endpoint.getPropertyValue(ServiceImplementation.XPATH_IMPL_LANGUAGE)); // TODO Q rather Endpoint.xx ?!?
	    	query.addConstraintMatchCriteriaWithAltIfSet(Platform.XPATH_BUILD,
	    			ServiceImplementation.XPATH_IMPL_BUILD,
	    			endpoint.getPropertyValue(ServiceImplementation.XPATH_IMPL_BUILD)); // TODO Q rather Endpoint.xx ?!?
	    	query.addConstraintMatchCriteriaWithAltIfSet(Platform.XPATH_DELIVERABLE_NATURE,
	    			Deliverable.XPATH_NATURE,
	    			endpoint.getPropertyValue(Deliverable.XPATH_NATURE)); // TODO Q rather Endpoint.xx ?!?
	    	query.addConstraintMatchCriteriaWithAltIfSet(Platform.XPATH_DELIVERABLE_REPOSITORY_URL,
	    			Deliverable.XPATH_REPOSITORY_URL,
	    			endpoint.getPropertyValue(Deliverable.XPATH_REPOSITORY_URL)); // TODO Q rather Endpoint.xx ?!?
    		
	    	if (endpoint.hasFacet(Endpoint.SCHEMA_ENDPOINT)) {
    		// endpoint platform criteria : match those provided on the endpoint against required platform
    		String endpointServiceProtocol = (String) endpoint.getPropertyValue(Endpoint.XPATH_ENDP_SERVICE_PROTOCOL);
    		String endpointTansportProtocol = (String) endpoint.getPropertyValue(Endpoint.XPATH_ENDP_TRANSPORT_PROTOCOL);
    		String endpointServiceRuntime = (String) endpoint.getPropertyValue(Endpoint.XPATH_ENDP_SERVICE_RUNTIME);
    		String endpointAppServerRuntime = (String) endpoint.getPropertyValue(Endpoint.XPATH_ENDP_APP_SERVER_RUNTIME);
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_SERVICE_PROTOCOL, endpointServiceProtocol); // SOAP, XML or JSON (or AtomPub...) for REST...
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_TRANSPORT_PROTOCOL, endpointTansportProtocol); // HTTP (HTTPS ?)...
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_SERVICE_RUNTIME, endpointServiceRuntime); // CXF (, Axis2...)
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_APP_SERVER_RUNTIME, endpointAppServerRuntime); // ApacheTomcat, Jetty...
    	    }
	    	
    	    }
    	} // else find impl for "matchingFirst" impl (see DiscoveryService)
    	
    	// TODO IF FOUND PLATFORM LINK TO IT ?????
    	
    	// Match endpoint properties
    	if (MatchingHelper.isWsdlInfo(endpoint)) { // TODO rather specific to Endpoint !!!!!!!!!!!!!!!
            //query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_WSDLINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
            query.addCriteria(ServiceImplementation.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXWS);
        	String endpointPortTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME);
        	query.addCriteria(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, endpointPortTypeName); anyExactCriteria = true;
                // NB. exact match (else useless because too wide), is set since is WS(DL)
        	
    	} else if (MatchingHelper.isRestInfo(endpoint)) { // TODO rather specific to Endpoint !!!!!!!!!!!!!!!
            //query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_RESTINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
            query.addCriteria(ServiceImplementation.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXRS);
        	String endpointRestPath = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_PATH); anyExactCriteria = true;
        	//String endpointRestContentType = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_MEDIA_TYPE); // OPT
        	//OPT String endpointRestAccepts = (String) impl.getPropertyValue(Endpoint.XPATH_REST_ACCEPTS); // OPT
            query.addCriteria(ServiceImplementation.XPATH_REST_PATH, endpointRestPath); anyExactCriteria = true;
                // NB. exact match (else useless because too wide), is set (at least to "") since is REST
            //query.addConstraintMatchCriteria(ServiceImplementation.XPATH_REST_ACCEPTS, endpointRestAccepts); // OPT
            //query.addConstraintMatchCriteria(ServiceImplementation.XPATH_REST_MEDIA_TYPE, endpointRestContentType); // OPT
    	}

    	// TODO UPDATE
    	/*String endpointServiceProtocol= "SOAP"; // REST/XML
    	String endpointTransportProtocol = "HTTP"; // if WS extracted from WSDL binding/transport=="http://schemas.xmlsoap.org/soap/http"
    	// NB. endpointServiceProtocol=WS|REST is implied by platform:serviceDefinition=WSDL|JAXRS, though SOAP spec includes also ex. HTTP binding
    	
		infoServiceQueryString +=
				" AND platform:transportProtocol='" + endpointTransportProtocol + "'"; // if any
		boolean isHttp = "HTTP".equals(endpointTransportProtocol);
		if (isHttp) { // consistency logic
        	String endpointServiceTransportHttpContentType = "application/json+nxautomation";
        	String endpointServiceTransportHttpContentKind = "XML"; // deduced from ContentType if possible, else from message monitoring
    		infoServiceQueryString +=
    				" AND platform:transportProtocol='" + endpointServiceTransportHttpContentType + "'" +
    				" AND platform:transportProtocol='" + endpointServiceTransportHttpContentKind + "'"; // if any
		}*/

    	// Filter by component
    	filterComponentId = MatchingHelper.appendComponentFilterToQuery(documentManager, query, filterComponentId, endpoint);
    	anyExactCriteria = anyExactCriteria || filterComponentId != null;

        // TODO filter mock ? a priori not...
        //query.addConstraintMatchCriteria(ServiceImplementation.XPATH_ISMOCK, "0");
    	
    	if (requireAtLeastOneExactCriteria && !anyExactCriteria) {
    	    return EmptyDocumentModelList.INSTANCE;
    	}
    	
    	// Run query
    	String implQuery = query.build();
    	DocumentModelList foundImpls = documentService.query(documentManager,
    			implQuery, true, false);
    	return foundImpls;

	}

    @Override
	public DocumentModelList findInformationServices(CoreSession documentManager,
	        DocumentModel endpoint, String filterComponentId,
	        boolean requireAtLeastOneExactCriteria/*, String visibility*/) throws ClientException { // TODO impl vs runtime platform matching ?
		// Init
		DocumentService documentService = getDocumentService();
        boolean anyExactCriteria = false;
        
    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + InformationService.DOCTYPE);

        // Filter by subproject
        query.addCriteria(SubprojectServiceImpl.buildCriteriaSeenFromSubproject(endpoint)/*, visibility*/);

        // 1. IF A LINKED PLATFORM HAS BEEN PROVIDED FOR THE ENDPOINT BY THE PROBE EX. WEB DISCO
        if (endpoint.hasFacet(Endpoint.SCHEMA_ENDPOINT)
                && endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM) != null) {
    		DocumentModel platformDocument = documentManager.getDocument(
    				new IdRef((String) endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM)));
    		
			for (String property : MatchingHelper.allPlatformPropsToMatch ) {
		    	query.addConstraintMatchCriteriaIfSet(property,
		    			platformDocument.getPropertyValue(property));
    		}
			
			// TODO rather match platform ID (if any) than criteria
	    	//query.addConstraintMatchCriteriaIfSet("iserv:linkedPlatform",
	    	//		endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM));
    	}
    	
    	// 2. NB. NO PLATFORM MATCHING WHEN FINDING COMPATIBLE IS
    	// else couldn't match SOAPUI mock endpoint with JAXWS expected IS + Component
    	// (but do match it with impl)
        // TODO rather match platform only if known as "actual" (or linked platform provided ??), rather than ...IfSet
    	 	
    	if (MatchingHelper.isWsdlInfo(endpoint)) { // is endpoint WSDL (JAXWS) ?
            //query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_WSDLINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
    	    //query.addConstraintMatchCriteriaIfSet(Platform.XPATH_SERVICE_LANGUAGE , endpoint.getPropertyValue(Endpoint.XPATH_TECHNOLOGY));
                // NO not necessarily JAWS ! .NET, HTTP mock or no requirement at all would be OK
        	String endpointPortTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME);
        	query.addCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, endpointPortTypeName); anyExactCriteria = true;
        	    // NB. exact match (else useless because too wide), is set since is WS(DL)
        	
    	} else if (MatchingHelper.isRestInfo(endpoint)) { // is endpoint REST (JAXRS) ?
        	//query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_RESTINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
    	    //query.addConstraintMatchCriteriaIfSet(Platform.XPATH_SERVICE_LANGUAGE, endpoint.getPropertyValue(Endpoint.XPATH_TECHNOLOGY));
    	        // NO not necessarily JAXRS ! .NET, HTTP mock or no requirement at all would be OK
        	String endpointRestPath = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_PATH);
            //OPT String endpointRestAccepts = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_ACCEPTS); // OPT
            //OPT String endpointRestContentType = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_CONTENT_TYPE); // OPT
            query.addCriteria(InformationService.XPATH_REST_PATH, endpointRestPath); anyExactCriteria = true;
                // NB. exact match (else useless because too wide), is set (at least to "") since is REST
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_ACCEPTS, implRestAccepts); // OPT
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_CONTENT_TYPE, implRestContentType); // OPT
    	}
    	
    	// Filter by component
        filterComponentId = MatchingHelper.appendComponentFilterToQuery(documentManager, query, filterComponentId, endpoint);
        anyExactCriteria = anyExactCriteria || filterComponentId != null;
    	
    	// Run query
    	String isQuery = query.build();
    	DocumentModelList foundIs = documentService.query(documentManager, isQuery, true, false);
    	return foundIs;
	}
    

    @Override
    public DocumentModelList findEndpointsCompatibleWithImplementation(CoreSession documentManager,
            DocumentModel serviceImpl) throws ClientException {
        
        DocumentService documentService = getDocumentService();

        MatchingQuery query = new MatchingQuery("SELECT * FROM " + Endpoint.DOCTYPE);

        // Filter by subproject
        query.addCriteria(SubprojectServiceImpl.buildCriteriaSeesSubproject(serviceImpl)); // NB. multivalued prop
        
        if (MatchingHelper.isWsdlInfo(serviceImpl)) { // consistency logic
            //query.addCriteria("ecm:mixinType = '" + InformationService.FACET_WSDLINFO + "'"); // NO should be added dynamically but hard to do in DiscoveryServiceImpl
            query.addConstraintMatchCriteria(Endpoint.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXWS); // require to be its "actual" impl ; OPT for other impl platform metas
            String implPortTypeName = (String) serviceImpl.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME); // if JAXWS
            query.addConstraintMatchCriteria(Endpoint.XPATH_WSDL_PORTTYPE_NAME, implPortTypeName);
                // NB. exact match (else useless because too wide), is set since is WS(DL)
                    
        } else if (MatchingHelper.isRestInfo(serviceImpl)) {
            //query.addCriteria("ecm:mixinType = '" + ServiceImplementation.FACET_RESTINFO + "'"); // NO should be added dynamically but hard to do in DiscoveryServiceImpl
            query.addConstraintMatchCriteria(Endpoint.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXRS); // require to be its "actual" impl ; OPT for other impl platform metas
            String implRestPath = (String) serviceImpl.getPropertyValue(ServiceImplementation.XPATH_REST_PATH);
            //OPT String implRestAccepts = (String) serviceImpl.getPropertyValue(ServiceImplementation.XPATH_REST_ACCEPTS); // OPT
            //OPT String implRestContentType = (String) serviceImpl.getPropertyValue(ServiceImplementation.XPATH_REST_CONTENT_TYPE); // OPT
            query.addConstraintMatchCriteria(Endpoint.XPATH_REST_PATH, implRestPath);
                // NB. exact match (else useless because too wide), is set (at least to "") since is REST
            //query.addConstraintMatchCriteria(Endpoint.XPATH_REST_ACCEPTS, implRestAccepts); // OPT
            //query.addConstraintMatchCriteria(Endpoint.XPATH_REST_CONTENT_TYPE, implRestContentType); // OPT   
        }
        
        // NB. IS-focused & partial matching, other platform metas are checked in the following call to findServiceImplementations()

        // TODO handle the case of impl-endpoint links that are now wrong i.e. SOA validation
        
        String serviceImplQuery = query.build();
        return documentService.query(documentManager, serviceImplQuery, true, false);
    }

    
	@Override
	public void linkServiceImplementation(CoreSession documentManager,
			SoaNodeId endpointId, SoaNodeId implId, boolean save)
			throws Exception {
		if (implId != null) {
			// Create link
			DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);
			discoveryService.runDiscovery(documentManager, endpointId, null, Arrays.asList(implId)); // null props, else saved which triggers loop
			// TODO or explicitly ??
			// i.e. impl.SOA_NAME (= implId.getName()) == ep.getParentOfType(ServiceImplementation.DOCTYPE).getName()

	        // TODO if unmatched impl, enrich impl with impl-level platform metas to help it match to IS ?? 
		}
		else {
			// Remove link
			DocumentService docService = Framework.getService(DocumentService.class);
			DocumentModelList endpointProxies = docService.findProxies(documentManager, endpointId);
			for (DocumentModel endpointProxy : endpointProxies) {
				DocumentModel proxyParent = documentManager.getParentDocument(endpointProxy.getRef());
				if (ServiceImplementation.DOCTYPE.equals(proxyParent.getType())) {
					documentManager.removeDocument(endpointProxy.getRef());
				}
			}
		}
		if (save) {
			documentManager.save();
		}
	}

	@Override
	public void linkInformationServiceThroughPlaceholder(
			CoreSession documentManager, DocumentModel endpoint,
			DocumentModel informationService, boolean save) throws Exception {
		DocumentService docService = getDocumentService();
		
		// Create placeholder impl (in endpoint subproject) :
		String portTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME);
		String subprojectId = (String) endpoint.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
		SoaNodeId implId = new SoaNodeId(subprojectId, ServiceImplementation.DOCTYPE, portTypeName);
		
		HashMap<String, Serializable> nuxeoProperties = new HashMap<String, Serializable>(2);
		nuxeoProperties.put(ServiceImplementation.XPATH_ISPLACEHOLDER, true);
		nuxeoProperties.put(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, portTypeName);
        // TODO also copy other impl-level platform metas. To be used when merging placeholder ? NOO rather delete placeholder and rematch endpoint
		
        // NB. don't do a documentManager.create() here, else triggers documentCreated event
        // (and from there event loop) , even though properties have not been set yet
        DocumentModel implModel = DiscoveryServiceImpl.createOrUpdate(documentManager, Framework.getService(DocumentService.class),
                null, null, implId, nuxeoProperties, false);
		
		// Attach placeholder impl to information service
		try {
			ServiceMatchingService serviceMatchingService = Framework.getService(ServiceMatchingService.class);
			serviceMatchingService.linkInformationService(documentManager, implModel, informationService.getId(), true);
		} catch (Exception e) {
			documentManager.removeDocument(implModel.getRef());
			throw new ClientException("Service matching service unavailable, aborting");
		}
		
		// Attach endpoint to placeholder impl
		linkServiceImplementation(documentManager,
				docService.createSoaNodeId(endpoint), 
				docService.createSoaNodeId(implModel),
				false);

		if (save) {
			documentManager.save();
		}
	}

	private DocumentService getDocumentService() throws ClientException {
		try {
			return Framework.getService(DocumentService.class);
		} catch (Exception e) {
			throw new ClientException("Document service unavailable, aborting");
		}
	}

}
