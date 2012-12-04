package org.easysoa.registry;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Component;
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
import org.nuxeo.runtime.api.Framework;

public class EndpointMatchingServiceImpl implements EndpointMatchingService {

    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(EndpointMatchingServiceImpl.class);
    
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

    	// Match platform properties
    	// TODO IF A LINKED PLATFORM HAS BEEN PROVIDED FOR THE ENDPOINT BY THE PROBE EX. WEB DISCO
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
    	}
    	
    	// TODO ELSE MATCH DISCOVERED PLATFORM CRITERIA OF THE ENDPOINT AGAINST THE IMPL'S SERVICE'S PLATFORM
    	
    	// Match endpoint properties
    	boolean isWsdl = endpoint.hasFacet(Endpoint.FACET_WSDLINFO) && endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME) != null;
    	boolean isRest = endpoint.hasFacet(Endpoint.FACET_RESTINFO) && endpoint.getPropertyValue(Endpoint.XPATH_REST_PATH) != null;    	
    	if (isWsdl) {
        	String endpointPortTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME); // if JAXWS
        	query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_WSDLINFO + "'");
        	query.addConstraintMatchCriteria(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, endpointPortTypeName);
    	} else if (isRest) {
        	query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_RESTINFO + "'");
        	//String endpointRestPath = (String) endpoint.getPropertyValue(RestInfoFacet.XPATH_REST_PATH); // if JAXRS
        	//OPT String endpointMediaType = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_MEDIA_TYPE); // if JAXRS
    		//infoServiceQueryString +=
					//" AND ecm:mixinType = 'RestInfo' AND " +
        			//" AND " ServiceImplementation.XPATH_REST_PATH + "='" + endpointRestPath + "'" +
        			//OPT " AND " ServiceImplementation.XPATH_REST_MEDIA_TYPE + "='" + endpointMediaType + "'" +
    	}

    	/*
    	//////////////////////////////////////////////////////////
		// only for discovery in web or message monitoring :
    	String endpointServiceTransport = "HTTP"; // if WS extracted from WSDL binding/transport=="http://schemas.xmlsoap.org/soap/http"
    	// NB. endpointServiceProtocol=WS|REST is implied by platform:serviceDefinition=WSDL|JAXRS
    	
		infoServiceQueryString +=
				" AND platform:serviceTransport='" + endpointServiceTransport + "'"; // if any
		boolean isHttp = "HTTP".equals(endpointServiceTransport);
		if (isHttp) { // consistency logic
        	String endpointServiceTransportHttpContentType = "application/json+nxautomation";
        	String endpointServiceTransportHttpContentKind = "XML"; // deduced from ContentType if possible, else from message monitoring
    		infoServiceQueryString +=
    				" AND platform:serviceTransport='" + endpointServiceTransportHttpContentType + "'" +
    				" AND platform:serviceTransport='" + endpointServiceTransportHttpContentKind + "'"; // if any
		}
		
		// OPT String endpointServiceRuntime = "CXF";
		// OPT appServer=Apache Tomcat|Jetty
		// NB. endpointProtocolServer (Apache Tomcat, Jetty) is different but not as interesting
		*/

    	// Filter by component
    	appendComponentFilterToQuery(documentManager, query, filterComponent, endpoint);
    	
    	// Run query
    	query.addConstraintMatchCriteria(ServiceImplementation.XPATH_ISMOCK, 0);
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
    	}
    	
    	boolean isWsdl = endpoint.hasFacet(Endpoint.FACET_WSDLINFO) && endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME) != null;
    	boolean isRest = endpoint.hasFacet(Endpoint.FACET_RESTINFO) && endpoint.getPropertyValue(Endpoint.XPATH_REST_PATH) != null;    	
    	if (isWsdl) {
        	String endpointPortTypeName = (String) endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME); // if JAXWS
        	query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_WSDLINFO + "'");
        	query.addConstraintMatchCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, endpointPortTypeName);
    	} else if (isRest) {
        	query.addCriteria("ecm:mixinType = '" + Endpoint.FACET_RESTINFO + "'");
        	//String endpointRestPath = (String) endpoint.getPropertyValue(RestInfoFacet.XPATH_REST_PATH); // if JAXRS
        	//OPT String endpointMediaType = (String) endpoint.getPropertyValue(Endpoint.XPATH_REST_MEDIA_TYPE); // if JAXRS
    		//infoServiceQueryString +=
					//" AND ecm:mixinType = 'RestInfo' AND " +
        			//" AND " ServiceImplementation.XPATH_REST_PATH + "='" + endpointRestPath + "'" +
        			//OPT " AND " ServiceImplementation.XPATH_REST_MEDIA_TYPE + "='" + endpointMediaType + "'" +
    	}
    	
    	// Filter by component
    	appendComponentFilterToQuery(documentManager, query, filterComponent, endpoint);
    	
    	// Run query
    	String isQuery = query.build();
    	DocumentModelList foundImpls = documentService.query(documentManager, isQuery, true, false);
    	return foundImpls;
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
