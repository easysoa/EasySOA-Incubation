package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.facets.RestInfoFacet;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mdutoo, mkalam-alami
 *
 */
public class ServiceMatchingServiceImpl implements ServiceMatchingService {

    private static Logger logger = Logger.getLogger(ServiceMatchingServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.ServiceMatchingService#findInformationServices(org.nuxeo.ecm.core.api.CoreSession, org.nuxeo.ecm.core.api.DocumentModel, org.nuxeo.ecm.core.api.DocumentModel, boolean)
	 */
	public DocumentModelList findInformationServices(CoreSession documentManager, DocumentModel impl,
			DocumentModel filterComponent, boolean skipPlatformMatching) throws ClientException {
		
    	// impl :
    	// 1. find IS on IS req itf (portType) in provided component if anyand whose component's platform match the impl's platform (criteria) if any ;
    	// (if more than one result, use matching dashboard)
    	// if single result, link impl to IS and to component (that it fills)
    	// 2. if none, find IS on IS req itf (portType) in provided component (i.e. this impl is not the actual one) ;
    	// (if more than one result, use matching dashboard)
    	// if single result, link impl to IS, then attempt to find platform (single query result) to link to else go to matching dashboard 
    	// 3. if none ("technical" service), create service (?) and attempt to find platform (single query result) to link to else go to matching dashboard
 
		DocumentService documentService;
		try {
			documentService = Framework.getService(DocumentService.class);
		} catch (Exception e) {
			logger.error("Document service unavailable, aborting");
			return null;
		}

    	String implReferredSubProjectIds = "AXXXSpecifications"; // or in 2 pass & get it from subProject ?? 
    	String implSubProjectId = "AXXXRealisation";
    	//String matchingGuideComponentId = (String) impl.getPropertyValue(ServiceImplementation.XPATH_COMPONENT_ID); // TODO check existence at source disco start

    	//String implIDE = "Eclipse"; // OPT
    	String implLanguage = (String) impl.getPropertyValue(ServiceImplementation.XPATH_LANGUAGE); // "Java"; // TODO from source disco
    	String implBuild = "Maven"; // MavenPom ?? or Ivy ; or in top-level deliverable ?? TODO from source disco or deduced from del:nature if possible
    	String implDeliverableNature = (String) impl.getPropertyValue(Deliverable.XPATH_NATURE); // "Maven" ; MavenArtifact ? copied from Deliverable
    	String implDeliverableRepositoryUrl = (String) impl.getPropertyValue(Deliverable.XPATH_REPOSITORY_URL); // "http://maven.nuxeo.org/nexus/content/groups/public" ; acts as id, copied from Deliverable TODO add in source disco
    	String implServiceLanguage = (String) impl.getPropertyValue(ServiceImplementation.XPATH_TECHNOLOGY); // JAXWS, JAXRS

    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + InformationService.DOCTYPE);

    	if (!skipPlatformMatching) {
	    	query.addConstraintMatchCriteriaIfSet("platform:language", implLanguage);
	    	query.addConstraintMatchCriteriaIfSet("platform:build", implBuild);
	    	query.addConstraintMatchCriteriaIfSet("platform:deliverableNature", implDeliverableNature);
	    	query.addConstraintMatchCriteriaIfSet("platform:deliverableRepositoryUrl", implDeliverableRepositoryUrl);
	    	query.addConstraintMatchCriteriaIfSet("platform:serviceLanguage", implServiceLanguage);
    	}
    	
    	// Filter by component
    	DocumentModel filterComponentModel = null;
    	if (filterComponent != null) {
    		filterComponentModel = documentManager.getWorkingCopy(filterComponent.getRef());
    	}
    	else if (impl.getPropertyValue(ServiceImplementation.XPATH_COMPONENT_ID) != null) {
    		try {
	    		filterComponentModel = documentManager.getWorkingCopy(
	    				new IdRef((String) impl.getPropertyValue(ServiceImplementation.XPATH_COMPONENT_ID)));
    		}
    		catch (Exception e) {
    			logger.warn("Component ID stored in impl doesn't exist: " + impl.getPropertyValue(ServiceImplementation.XPATH_COMPONENT_ID));
    		}
    	}
    	if (filterComponentModel != null) {
    		query.addCriteria(Component.XPATH_COMPONENT_ID + " = '" + filterComponentModel.getId() + "'");
    	}
    	
    	/*
    			" WHERE  " +
    			//"soan:subProjectId IN " + implReferredSubProjectIds + "' OR soan:subProjectId='" + implSubProjectId + "'" +
    			//" AND impl:componentId='" + matchingGuideComponentId + "'" + // if any
    			
    			"    platform:language='" + implLanguage + "'" + // if any ; OPT multiple options (consistency handled in logic)
            	" AND platform:build='" + implBuild + "'" + // if any
            	" AND platform:deliverableNature='" + implDeliverableNature + "'" + // if any
            	" AND platform:deliverableRepositoryUrl='" + implDeliverableRepositoryUrl + "'" + // if any
    			" AND platform:serviceLanguage='" + implServiceLanguage + "'"; // if any ; OPT multiple options (consistency handled in logic)
    	*/
    	
    	boolean isWsdl = impl.hasFacet(ServiceImplementation.FACET_WSDLINFO)
    			&& impl.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME) != null;// OPT dynamic if possible when setting props in DiscoveryService ??
    	boolean isRest = impl.hasFacet(ServiceImplementation.FACET_RESTINFO)
    			&& impl.getPropertyValue(RestInfoFacet.XPATH_REST_PATH) != null;// OPT dynamic if possible when setting props in DiscoveryService ??
    	// or platform:serviceDefinition=WSDL|JAXRS ?
    	
    	if (isWsdl) { // consistency logic
        	String implPortTypeName = (String) impl.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME); // if JAXWS
        	query.addCriteria("ecm:mixinType = 'WsdlInfo'");
        	query.addConstraintMatchCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, implPortTypeName);
        			
    	} else if (isRest) {
        	String implRestPath = (String) impl.getPropertyValue(RestInfoFacet.XPATH_REST_PATH); // if JAXRS
        	//OPT String implMediaType = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_MEDIA_TYPE); // if JAXRS
    		//infoServiceQueryString +=
					//" AND ecm:mixinType = 'RestInfo' AND " +
        			//" AND " InformationService.XPATH_REST_PATH + "='" + implRestPath + "'" +
        			//OPT " AND " InformationService.XPATH_REST_MEDIA_TYPE + "='" + implMediaType + "'" +
        			
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
    	
    	String infoServiceQuery = query.build();
    	DocumentModelList foundInfoServices = documentService.query(documentManager,
    			infoServiceQuery, true, false);
    	return foundInfoServices;
	}

	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.ServiceMatchingService#findServiceImplementations(org.nuxeo.ecm.core.api.CoreSession, org.nuxeo.ecm.core.api.DocumentModel)
	 */
	public DocumentModelList findServiceImplementations(CoreSession documentManager,
			DocumentModel informationService) throws ClientException {
		
		DocumentService documentService;
		try {
			documentService = Framework.getService(DocumentService.class);
		} catch (Exception e) {
			logger.error("Document service unavailable, aborting");
			return null;
		}
		
		boolean isWsdl = informationService.hasFacet("WsdlInfo"); // TODO impl.hasFacet("WsdlInfo");
    	boolean isRest = informationService.hasFacet("RestInfo");// OPT dynamic
    	// or platform:serviceDefinition=WSDL|JAXRS ?
    	
    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE);
    	
    	if (isWsdl) { // consistency logic
        	String implPortTypeName = (String) informationService.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME); // if JAXWS
        	query.addCriteria("ecm:mixinType = 'WsdlInfo'");
        	query.addConstraintMatchCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, implPortTypeName);
        			
    	} else if (isRest) {
        	String implRestPath = (String) informationService.getPropertyValue(RestInfoFacet.XPATH_REST_PATH); // if JAXRS
        	//OPT String implMediaType = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_MEDIA_TYPE); // if JAXRS
    		//infoServiceQueryString +=
					//" AND ecm:mixinType = 'RestInfo' AND " +
        			//" AND " InformationService.XPATH_REST_PATH + "='" + implRestPath + "'" +
        			//OPT " AND " InformationService.XPATH_REST_MEDIA_TYPE + "='" + implMediaType + "'" +
        			
    	}

    	// TODO handle the case of IS-impl links that are now wrong i.e. SOA validation
    	
    	/*String serviceImplQuery = NXQLQueryBuilder.getQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE + 
    			" WHERE " + ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME + " = '?'",
    			new Object[] { serviceModel.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME) },
    			false, true);*/
    	String serviceImplQuery = query.build();
    	return documentService.query(documentManager, serviceImplQuery, true, false);
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.ServiceMatchingService#findPlatforms(org.nuxeo.ecm.core.api.CoreSession, org.nuxeo.ecm.core.api.DocumentModel)
	 */
	public DocumentModelList findPlatforms(CoreSession documentManager, DocumentModel modelWithPlatformFacet) throws ClientException {
		DocumentService documentService;
		try {
			documentService = Framework.getService(DocumentService.class);
		} catch (Exception e) {
			logger.error("Document service unavailable, aborting");
			return null;
		}
		
    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + Platform.DOCTYPE);
		String[] platformPropsToMatch = new String[] {
				Platform.XPATH_LANGUAGE, Platform.XPATH_BUILD, Platform.XPATH_SERVICE_LANGUAGE,
				Platform.XPATH_DELIVERABLE_NATURE, Platform.XPATH_DELIVERABLE_REPOSITORY_URL};
		for (String property : platformPropsToMatch ) {
	    	query.addConstraintMatchCriteriaIfSet(property,
	    			modelWithPlatformFacet.getPropertyValue(property));
		}
    	String serviceImplQuery = query.build();
    	return documentService.query(documentManager, serviceImplQuery, true, false);
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.ServiceMatchingService#linkInformationService(org.nuxeo.ecm.core.api.CoreSession, org.nuxeo.ecm.core.api.DocumentModel, java.lang.String, boolean)
	 */
	public void linkInformationService(CoreSession documentManager, DocumentModel serviceImplModel,
			String informationServiceUuid, boolean save) throws ClientException {
		Object previousLinkValue = serviceImplModel.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE);
		if (informationServiceUuid == null && previousLinkValue != null
				|| informationServiceUuid != null && !informationServiceUuid.equals(previousLinkValue)) {
	    	serviceImplModel.setPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE, informationServiceUuid);
			if (save) {
				documentManager.saveDocument(serviceImplModel);
				documentManager.save();
			}
		}
	}

}
