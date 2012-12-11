package org.easysoa.registry;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.runtime.api.Framework;

public class EndpointMatchingServiceImpl implements EndpointMatchingService {

    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(EndpointMatchingServiceImpl.class);
    

    public boolean isEndpointAlreadyMatched(DocumentModel endpointDocument,
            CoreSession documentManager) throws ClientException {
        return endpointDocument.isProxy() // the proxy, possibly still being created,
                // used to link endpoint under impl (else loops)
                || !documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
                + " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
                + NXQL.ECM_UUID + "='" + endpointDocument.getId() + "' AND "
                // NB. %ServiceImplementation to also handle JavaServiceImplementations
                + Endpoint.XPATH_PARENTSIDS + "/* LIKE '%ServiceImplementation%'").isEmpty();
    }

    /** TODO share elsewhere */
    public static boolean isWsdlInfo(DocumentModel wsdlInfo) throws PropertyException, ClientException {
        return wsdlInfo.hasFacet(WsdlInfoFacet.FACET_WSDLINFO) // for now static facet so always
                //&& Platform.SERVICE_LANGUAGE_JAXWS.equals(informationService.getPropertyValue(Platform.XPATH_SERVICE_LANGUAGE))
                && wsdlInfo.getPropertyValue(WsdlInfoFacet.XPATH_WSDL_PORTTYPE_NAME) != null; // OPT dynamic if possible when setting props in DiscoveryService ??
    }

    /** TODO share elsewhere */
    public static boolean isRestInfo(DocumentModel restInfo) throws PropertyException, ClientException {
        return restInfo.hasFacet(RestInfoFacet.FACET_RESTINFO) // for now static facet so always
                //&& Platform.SERVICE_LANGUAGE_JAXRS.equals(informationService.getPropertyValue(Platform.XPATH_SERVICE_LANGUAGE))
                && restInfo.getPropertyValue(RestInfoFacet.XPATH_REST_PATH) != null; // OPT dynamic if possible when setting props in DiscoveryService ??
    }
    
    
	public DocumentModelList findServiceImpls(CoreSession documentManager,
			DocumentModel endpoint, DocumentModel filterComponent,
			boolean skipPlatformMatching) throws ClientException {

        // how should work matching in discovery & dashboard for :
        
    	// endpoint : if has no impl,
    	// find impl : on IS req itf (portType), and whose IS is in provided component if any,
        // and whose impl platform (criteria) match the endpoint's discovered impl platform (criteria) if any ;
    	// if single matched link to it
    	// (if more than one result, use matching dashboard)
    	// if none, create impl and do as for in ServiceMatchingImpl : 1. and fill component, else 2. and link to platform, else 3. 
		
		// Init
		DocumentService documentService = getDocumentService();
    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE);

    	// Match platform properties :
    	
    	// 1. IF A LINKED PLATFORM HAS BEEN PROVIDED FOR THE ENDPOINT BY THE PROBE EX. WEB DISCO
    	if (!skipPlatformMatching && endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM) != null) {
    		DocumentModel platformDocument = documentManager.getDocument(
    				new IdRef((String) endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM)));
    		
    		String[] platformPropsToMatch = new String[] {
    				Platform.XPATH_LANGUAGE, Platform.XPATH_BUILD, Platform.XPATH_SERVICE_LANGUAGE,
    				Platform.XPATH_DELIVERABLE_NATURE, Platform.XPATH_DELIVERABLE_REPOSITORY_URL};
			for (String property : platformPropsToMatch ) {
		    	query.addConstraintMatchCriteriaIfSet(property,
		    			platformDocument.getPropertyValue(property));
    		}
			
			// TODO rather match platform ID (if any) than criteria
	    	//query.addConstraintMatchCriteriaWithAltIfSet("iserv:linkedPlatform", "impl:platform",
	    	//		endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM));
    	}
    	
    	// TODO ELSE MATCH DISCOVERED PLATFORM CRITERIA OF THE ENDPOINT
    	// AGAINST THE IMPL'S SERVICE'S PLATFORM IF ANY ELSE AGAINST THE IMPL'S DISCOVERED PLATFORM CRITERIA
    	// TODO make it possible in disco (?) & model (yes)
    	else if (!skipPlatformMatching) {
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
    	
    	// TODO IF FOUND PLATFORM LINK TO IT ?????
    	
    	// Match endpoint properties
    	if (isWsdlInfo(endpoint)) { // TODO rather specific to Endpoint !!!!!!!!!!!!!!!
            //query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_WSDLINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
            query.addCriteria(ServiceImplementation.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXWS);
        	String endpointPortTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME);
        	query.addConstraintMatchCriteria(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, endpointPortTypeName);
    	} else if (isRestInfo(endpoint)) { // TODO rather specific to Endpoint !!!!!!!!!!!!!!!
            //query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_RESTINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
            query.addCriteria(ServiceImplementation.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXRS);
        	String endpointRestPath = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_PATH);
        	//String endpointRestContentType = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_MEDIA_TYPE); // OPT
        	//OPT String endpointRestAccepts = (String) impl.getPropertyValue(Endpoint.XPATH_REST_ACCEPTS); // OPT
            query.addConstraintMatchCriteria(ServiceImplementation.XPATH_REST_PATH, endpointRestPath);
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
    	appendComponentFilterToQuery(documentManager, query, filterComponent, endpoint);

        // TODO filter mock ? a priori not...
        //query.addConstraintMatchCriteria(ServiceImplementation.XPATH_ISMOCK, "0");
    	
    	// Run query
    	String implQuery = query.build();
    	DocumentModelList foundImpls = documentService.query(documentManager,
    			implQuery, true, false);
    	return foundImpls;

	}

    @Override
	public DocumentModelList findInformationServices(
			CoreSession documentManager, DocumentModel endpoint,
			DocumentModel filterComponent) throws ClientException {
		// Init
		DocumentService documentService = getDocumentService();
    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + InformationService.DOCTYPE);

    	// Match platform properties
    	if (endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM) != null) {
    		DocumentModel platformDocument = documentManager.getDocument(
    				new IdRef((String) endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM)));
    		
    		String[] platformPropsToMatch = new String[] {
    				Platform.XPATH_LANGUAGE, Platform.XPATH_BUILD, Platform.XPATH_SERVICE_LANGUAGE,
    				Platform.XPATH_DELIVERABLE_NATURE, Platform.XPATH_DELIVERABLE_REPOSITORY_URL};
			for (String property : platformPropsToMatch ) {
		    	query.addConstraintMatchCriteriaIfSet(property,
		    			platformDocument.getPropertyValue(property));
    		}
			
			// TODO rather match platform ID (if any) than criteria
	    	//query.addConstraintMatchCriteriaIfSet("iserv:linkedPlatform",
	    	//		endpoint.getPropertyValue(Endpoint.XPATH_LINKED_PLATFORM));
    	}
    	 	
    	if (isWsdlInfo(endpoint)) { // is endpoint WSDL (JAXWS) ?
            //query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_WSDLINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
    	    query.addCriteria(Platform.XPATH_SERVICE_LANGUAGE , Platform.SERVICE_LANGUAGE_JAXWS); // TODO better, not impl !!!!!!!!
        	String endpointPortTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME);
        	query.addConstraintMatchCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, endpointPortTypeName);
        	
    	} else if (isRestInfo(endpoint)) { // is endpoint REST (JAXRS) ?
        	//query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_RESTINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
    	    query.addCriteria(Platform.XPATH_SERVICE_LANGUAGE , Platform.SERVICE_LANGUAGE_JAXRS); // TODO better, not impl !!!!!!!!
        	String endpointRestPath = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_PATH); // if JAXRS
            //OPT String endpointRestAccepts = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_ACCEPTS); // OPT
            //OPT String endpointRestContentType = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_CONTENT_TYPE); // OPT
            query.addConstraintMatchCriteria(InformationService.XPATH_REST_PATH, endpointRestPath);
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_ACCEPTS, implRestAccepts); // OPT
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_CONTENT_TYPE, implRestContentType); // OPT
    	}
    	
    	// Filter by component
    	appendComponentFilterToQuery(documentManager, query, filterComponent, endpoint);
    	
    	// Run query
    	String isQuery = query.build();
    	DocumentModelList foundIs = documentService.query(documentManager, isQuery, true, false);
    	return foundIs;
	}

	@Override
	public void linkServiceImplementation(CoreSession documentManager,
			SoaNodeId endpointId, SoaNodeId implId, boolean save)
			throws Exception {
		if (implId != null) {
			// Create link
			DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);
			discoveryService.runDiscovery(documentManager, endpointId, null, Arrays.asList(implId));
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
		
		// Create placeholder impl
		String portTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME);
		DocumentModel implModel = docService.create(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, portTypeName));
		implModel.setPropertyValue(ServiceImplementation.XPATH_ISPLACEHOLDER, true);
        implModel.setPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, portTypeName);
		documentManager.saveDocument(implModel);
		
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

	private void appendComponentFilterToQuery(CoreSession documentManager,
			MatchingQuery query, DocumentModel filterComponent, DocumentModel endpoint) throws ClientException {
		DocumentModel filterComponentModel = null;
		if (filterComponent != null) {
			filterComponentModel = documentManager.getWorkingCopy(filterComponent.getRef());
		}
		else if (endpoint.getPropertyValue(Endpoint.XPATH_COMPONENT_ID) != null) {
			filterComponentModel = documentManager.getWorkingCopy(
					new IdRef((String) endpoint.getPropertyValue(Endpoint.XPATH_COMPONENT_ID)));
		}
		if (filterComponentModel != null) {
			query.addCriteria(Component.XPATH_COMPONENT_ID + " = '" + filterComponentModel.getId() + "'");
		}
	}

}
