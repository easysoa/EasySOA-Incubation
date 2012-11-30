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

    private static Logger logger = Logger.getLogger(EndpointMatchingServiceImpl.class);
    
	public DocumentModelList findServiceImpls(CoreSession documentManager,
			DocumentModel endpoint, DocumentModel filterComponent,
			boolean skipPlatformMatching) throws ClientException {
		
		// Init
		DocumentService documentService;
		try {
			documentService = Framework.getService(DocumentService.class);
		} catch (Exception e) {
			logger.error("Document service unavailable, aborting");
			return null;
		}

    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE);

    	// Match platform properties
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
    	
    	// Match endpoint properties
    	boolean isWsdl = endpoint.hasFacet(Endpoint.FACET_WSDLINFO) && endpoint.getPropertyValue(Endpoint.XPATH_WSDL_PORTTYPE_NAME) != null;
    	boolean isRest = endpoint.hasFacet(Endpoint.FACET_RESTINFO) && endpoint.getPropertyValue(Endpoint.XPATH_REST_PATH) != null;    	
    	if (isWsdl) {
        	String endpointPortTypeName = (String) endpoint.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME); // if JAXWS
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
    	
    	// Run query
    	query.addConstraintMatchCriteria(ServiceImplementation.XPATH_ISMOCK, 0);
    	String implQuery = query.build();
    	DocumentModelList foundImpls = documentService.query(documentManager,
    			implQuery, true, false);
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

}
