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

        // how should work matching in discovery & dashboard for :
        
    	// endpoint : if has no impl,
    	// find impl : on IS req itf (portType), and whose IS is in provided component if any,
        // and whose impl platform (criteria) match the endpoint's discovered impl platform (criteria) if any ;
    	// if single matched link to it
    	// (if more than one result, use matching dashboard)
    	// if none, create impl and do as for in ServiceMatchingImpl : 1. and fill component, else 2. and link to platform, else 3.
 
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

    	String implIde = (String) impl.getPropertyValue(ServiceImplementation.XPATH_IMPL_IDE); // OPT only set by ex. FraSCatiStudio, TalendStudio, ScarboModeler
    	String implLanguage = (String) impl.getPropertyValue(ServiceImplementation.XPATH_IMPL_LANGUAGE); // "Java"; // TODO from source disco
    	String implBuild = (String) impl.getPropertyValue(ServiceImplementation.XPATH_IMPL_BUILD); // "Maven" (rather "MavenPom" ?), "Ivy" ; who builds it. TODO Q on top-level DevApp / deliverable only ?!? TODO from source disco or deduced from del:nature if possible
    	String implDeliverableNature = (String) impl.getPropertyValue(Deliverable.XPATH_NATURE); // "Maven" ; MavenArtifact ? copied from Deliverable
    	String implDeliverableRepositoryUrl = (String) impl.getPropertyValue(Deliverable.XPATH_REPOSITORY_URL); // "http://maven.nuxeo.org/nexus/content/groups/public" ; acts as id, copied from Deliverable TODO add in source disco
    	String implServiceLanguage = (String) impl.getPropertyValue(ServiceImplementation.XPATH_TECHNOLOGY); // JAXWS, JAXRS

    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + InformationService.DOCTYPE);

    	if (!skipPlatformMatching) {
	    	query.addConstraintMatchCriteriaIfSet("platform:ide", implIde);
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
    	
    	boolean isWsdl = impl.hasFacet(ServiceImplementation.FACET_WSDLINFO) // for now static facet so always
    	        && "JAX-WS".equals(impl.getPropertyValue(ServiceImplementation.XPATH_TECHNOLOGY))
    			&& impl.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME) != null;// OPT dynamic if possible when setting props in DiscoveryService ??
    	boolean isRest = impl.hasFacet(ServiceImplementation.FACET_RESTINFO) // for now static facet so always
    	        && "JAX-RS".equals(impl.getPropertyValue(ServiceImplementation.XPATH_TECHNOLOGY))
    			&& impl.getPropertyValue(RestInfoFacet.XPATH_REST_PATH) != null;// OPT dynamic if possible when setting props in DiscoveryService ??
    	
    	if (isWsdl) { // consistency logic
            //query.addCriteria("ecm:mixinType = '" + ServiceImplementation.FACET_WSDLINFO + "'"); // NO should be added dynamically but hard to do in DiscoveryServiceImpl
    	    query.addCriteria(ServiceImplementation.XPATH_TECHNOLOGY , "JAX-WS");
        	String implPortTypeName = (String) impl.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME);
        	query.addConstraintMatchCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, implPortTypeName);
        			
    	} else if (isRest) {
            //query.addCriteria("ecm:mixinType = '" + ServiceImplementation.FACET_RESTINFO + "'"); // NO should be added dynamically but hard to do in DiscoveryServiceImpl
            query.addCriteria(ServiceImplementation.XPATH_TECHNOLOGY , "JAX-RS");
        	String implRestPath = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_PATH);
            //OPT String implRestAccepts = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_ACCEPTS); // OPT
            //OPT String implRestContentType = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_CONTENT_TYPE); // OPT
        	query.addConstraintMatchCriteria(InformationService.XPATH_REST_PATH, implRestPath);
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_ACCEPTS, implRestAccepts); // OPT
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_CONTENT_TYPE, implRestContentType); // OPT        			
    	}
    	
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
