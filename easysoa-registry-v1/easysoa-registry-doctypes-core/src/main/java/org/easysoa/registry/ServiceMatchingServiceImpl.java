package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.matching.MatchingHelper;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.utils.EmptyDocumentModelList;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.runtime.api.Framework;

/**
 * TODO matching should be attempted / done if at least one exact (portType, endpointUrl)
 * or provided guide (component, platform (id) of "actual" (??)) info
 * (and not only portType)
 * 
 * @author mdutoo, mkalam-alami
 *
 */
public class ServiceMatchingServiceImpl implements ServiceMatchingService {

    private static Logger logger = Logger.getLogger(ServiceMatchingServiceImpl.class);
	

    public boolean isServiceImplementationAlreadyMatched(DocumentModel implDocument)
            throws PropertyException, ClientException {
        String providedIServId = (String) implDocument.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE);
        return providedIServId != null && providedIServId.length() != 0;
    }
    
    
	/* (non-Javadoc)
	 * @see org.easysoa.registry.ServiceMatchingService#findInformationServices(org.nuxeo.ecm.core.api.CoreSession, org.nuxeo.ecm.core.api.DocumentModel, org.nuxeo.ecm.core.api.DocumentModel, boolean)
	 */
	public DocumentModelList findInformationServices(CoreSession documentManager,
	        DocumentModel impl, String filterComponentId,
	        boolean skipPlatformMatching, boolean requireAtLeastOneExactCriteria) throws ClientException {

        // how should work matching in discovery & dashboard for :
        
    	// endpoint : if has no impl,
    	// find impl : on IS req itf (portType), and whose IS is in provided component if any,
        // and whose impl platform (criteria) match the endpoint's discovered impl platform (criteria) if any ;
    	// if single matched link to it
    	// (if more than one result, use matching dashboard)
    	// if none, create impl and do as for in ServiceMatchingImpl : 1. and fill component, else 2. and link to platform, else 3.
 
		DocumentService documentService = getDocumentService();
        boolean anyExactCriteria = false;
        MatchingQuery query = new MatchingQuery("SELECT * FROM " + InformationService.DOCTYPE);

        // Filter by subproject
        query.addCriteria(SubprojectServiceImpl.buildCriteriaSeenFromSubproject(impl)); // ex. "AXXXSpecifications"; // or in 2 pass & get it from subProject ??
        
    	String implIde = (String) impl.getPropertyValue(ServiceImplementation.XPATH_IMPL_IDE); // OPT only set by ex. FraSCatiStudio, TalendStudio, ScarboModeler
    	String implLanguage = (String) impl.getPropertyValue(ServiceImplementation.XPATH_IMPL_LANGUAGE); // "Java"; // TODO from source disco
    	String implBuild = (String) impl.getPropertyValue(ServiceImplementation.XPATH_IMPL_BUILD); // "Maven" (rather "MavenPom" ?), "Ivy" ; who builds it. TODO Q on top-level DevApp / deliverable only ?!? TODO from source disco or deduced from del:nature if possible
    	String implDeliverableNature = (String) impl.getPropertyValue(Deliverable.XPATH_NATURE); // "Maven" ; MavenArtifact ? copied from Deliverable
    	String implDeliverableRepositoryUrl = (String) impl.getPropertyValue(Deliverable.XPATH_REPOSITORY_URL); // "http://maven.nuxeo.org/nexus/content/groups/public" ; acts as id, copied from Deliverable TODO add in source disco
    	String implServiceLanguage = (String) impl.getPropertyValue(ServiceImplementation.XPATH_TECHNOLOGY); // JAXWS, JAXRS

    	if (!skipPlatformMatching) {
            // TODO rather match platform only if known as "actual" (or linked platform provided ??), rather than ...IfSet
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_IDE, implIde);
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_LANGUAGE, implLanguage);
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_BUILD, implBuild);
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_DELIVERABLE_NATURE, implDeliverableNature);
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_DELIVERABLE_REPOSITORY_URL, implDeliverableRepositoryUrl);
	    	query.addConstraintMatchCriteriaIfSet(Platform.XPATH_SERVICE_LANGUAGE, implServiceLanguage);
    	}
    	
    	// Filter by component
        //String matchingGuideComponentId = (String) impl.getPropertyValue(ServiceImplementation.XPATH_COMPONENT_ID); // in MatchingHelper ; TODO check existence at source disco start
        filterComponentId = MatchingHelper.appendComponentFilterToQuery(documentManager, query, filterComponentId, impl);
        anyExactCriteria = anyExactCriteria || filterComponentId != null;
    	
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
    	
    	if (MatchingHelper.isWsdlInfo(impl)) { // consistency logic
            //query.addCriteria("ecm:mixinType = '" + InformationService.FACET_WSDLINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
    	    //query.addCriteria(Platform.XPATH_SERVICE_LANGUAGE , Platform.SERVICE_LANGUAGE_JAXWS); // TODO required ?
    	        // NO not necessarily JAXWS ! .NET, HTTP mock or no requirement at all would be OK
        	String implPortTypeName = (String) impl.getPropertyValue(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME);
        	query.addCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, implPortTypeName); anyExactCriteria = true;
                // NB. NB. exact match (else useless because too wide), is set since is WS(DL)
        			
    	} else if (MatchingHelper.isRestInfo(impl)) {
            //query.addCriteria("ecm:mixinType = '" + InformationService.FACET_RESTINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
    	    //query.addCriteria(Platform.XPATH_SERVICE_LANGUAGE , Platform.SERVICE_LANGUAGE_JAXRS); // TODO required ?
    	        // NO not necessarily JAXRS ! .NET, HTTP mock or no requirement at all would be OK
        	String implRestPath = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_PATH);
            //OPT String implRestAccepts = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_ACCEPTS); // OPT
            //OPT String implRestContentType = (String) impl.getPropertyValue(ServiceImplementation.XPATH_REST_CONTENT_TYPE); // OPT
        	query.addCriteria(InformationService.XPATH_REST_PATH, implRestPath); anyExactCriteria = true;
                // NB. exact match (else useless because too wide), is set (at least to "") since is REST
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_ACCEPTS, implRestAccepts); // OPT
            //query.addConstraintMatchCriteria(InformationService.XPATH_REST_CONTENT_TYPE, implRestContentType); // OPT        			
    	}

        if (requireAtLeastOneExactCriteria && !anyExactCriteria) {
            return EmptyDocumentModelList.INSTANCE;
        }
        
    	String infoServiceQuery = query.build();
    	DocumentModelList foundInfoServices = documentService.query(documentManager,
    			infoServiceQuery, true, false);
    	return foundInfoServices;
	}

	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.ServiceMatchingService#findServiceImplementations(org.nuxeo.ecm.core.api.CoreSession, org.nuxeo.ecm.core.api.DocumentModel)
	 */
	public DocumentModelList findImplementationsCompatibleWithService(CoreSession documentManager,
			DocumentModel informationService) throws ClientException {

        DocumentService documentService = getDocumentService();

    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + ServiceImplementation.DOCTYPE);

        // Filter by subproject
        query.addCriteria(SubprojectServiceImpl.buildCriteriaSeesSubproject(informationService)); // NB. multivalued prop
    	
    	if (MatchingHelper.isWsdlInfo(informationService)) { // consistency logic
            //query.addCriteria("ecm:mixinType = '" + InformationService.FACET_WSDLINFO + "'"); // NO should be added dynamically but hard to do in DiscoveryServiceImpl
            //query.addConstraintMatchCriteriaIfSet(ServiceImplementation.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXWS); // require to be its "actual" impl ; OPT for other impl platform metas
    	        // NO not necessarily JAWS ! .NET, HTTP mock or no requirement at all would be OK
        	String iservPortTypeName = (String) informationService.getPropertyValue(InformationService.XPATH_WSDL_PORTTYPE_NAME); // if JAXWS
            query.addCriteria(ServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, iservPortTypeName);
                // NB. exact match (else useless because too wide), is set since is WS(DL)
        			
    	} else if (MatchingHelper.isRestInfo(informationService)) {
            //query.addCriteria("ecm:mixinType = '" + ServiceImplementation.FACET_RESTINFO + "'"); // NO should be added dynamically but hard to do in DiscoveryServiceImpl
            //query.addConstraintMatchCriteriaIfSet(ServiceImplementation.XPATH_TECHNOLOGY , Platform.SERVICE_LANGUAGE_JAXRS); // require to be its "actual" impl ; OPT for other impl platform metas
    	        // NO not necessarily JARS ! .NET, HTTP mock or no requirement at all would be OK
            String iservRestPath = (String) informationService.getPropertyValue(InformationService.XPATH_REST_PATH);
            //OPT String iservRestAccepts = (String) informationService.getPropertyValue(InformationService.XPATH_REST_ACCEPTS); // OPT
            //OPT String iservRestContentType = (String) informationService.getPropertyValue(InformationService.XPATH_REST_CONTENT_TYPE); // OPT
            query.addCriteria(ServiceImplementation.XPATH_REST_PATH, iservRestPath);
                // NB. exact match (else useless because too wide), is set (at least to "") since is REST
            //query.addConstraintMatchCriteria(ServiceImplementation.XPATH_REST_ACCEPTS, iservRestAccepts); // OPT
            //query.addConstraintMatchCriteria(ServiceImplementation.XPATH_REST_CONTENT_TYPE, iservRestContentType); // OPT   
    	}
        
        // NB. IS-focused & partial matching, other platform metas are checked in the following call to findServiceImplementations()

        // TODO handle the case of impl-endpoint links that are now wrong i.e. SOA validation
    	
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
        DocumentService documentService = getDocumentService();
		
    	MatchingQuery query = new MatchingQuery("SELECT * FROM " + Platform.DOCTYPE);
    	// TODO subproject ? NO...
		for (String property : MatchingHelper.implPlatformPropsToMatch ) {
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

    private DocumentService getDocumentService() throws ClientException {
        try {
            return Framework.getService(DocumentService.class);
        } catch (Exception e) {
            throw new ClientException("Document service unavailable, aborting");
        }
    }

}
