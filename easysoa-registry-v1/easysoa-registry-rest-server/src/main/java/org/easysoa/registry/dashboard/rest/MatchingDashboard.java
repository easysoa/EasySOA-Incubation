package org.easysoa.registry.dashboard.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.EndpointMatchingService;
import org.easysoa.registry.ServiceMatchingService;
import org.easysoa.registry.SoaMetamodelService;
import org.easysoa.registry.rest.EasysoaModuleRoot;
import org.easysoa.registry.rest.samples.DashboardMatchingSamples;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.utils.ContextData;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
@WebObject(type = "dashboard")
@Path("easysoa/dashboard")
public class MatchingDashboard extends EasysoaModuleRoot {
	
    private static Logger logger = Logger.getLogger(MatchingDashboard.class);
    
	@GET
	public Template viewDashboard(@QueryParam("subprojectId") String subprojectId,
	        @QueryParam("visibility") String visibility) {
        CoreSession session = SessionFactory.getSession(request);
		try {
			DocumentService docService = Framework.getService(DocumentService.class);
			Template view = getView("index");
			
			String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, subprojectId, visibility);
                        
			// All information services
			List<DocumentModel> allInfoServices = docService.getInformationServicesInCriteria(session, subprojectCriteria);
			Map<String, DocumentModel> infoServicesById = new HashMap<String, DocumentModel>();
			for (DocumentModel infoService : allInfoServices) {
				infoServicesById.put(infoService.getId(), infoService);
			}
            view.arg("infoServicesById", infoServicesById);
 
			// Find matched impls & their infoservice
			DocumentModelList matchedImpls = docService.query(session, DocumentService.NXQL_SELECT_FROM
					+ ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
					+ ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + " IS NOT NULL ",
					 true, false);
			view.arg("matchedImpls", matchedImpls);

			// List unimplemented services
			Map<String, DocumentModel> unimplementedServsMap = new HashMap<String, DocumentModel>(infoServicesById);
			for (DocumentModel matchedImpl : matchedImpls) {
				unimplementedServsMap.remove(
						matchedImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE)
				);
			}
			view.arg("unimplementedServs", unimplementedServsMap.values());

            // Find matched endpoints & their impls
            DocumentModelList matchedEndpoints = docService.query(session,
                    DocumentService.NXQL_SELECT_FROM + Endpoint.DOCTYPE + subprojectCriteria
                    + DocumentService.NXQL_AND + Endpoint.XPATH_PARENTSIDS + " LIKE '%"
                    + ServiceImplementation.DOCTYPE + ":%' ", true, false);
            view.arg("matchedEndpoints", matchedEndpoints);

			// List endpoints without impls
			DocumentModelList unmatchedEndpoints = docService.query(session,
					DocumentService.NXQL_SELECT_FROM + Endpoint.DOCTYPE + subprojectCriteria
					+ DocumentService.NXQL_AND + Endpoint.XPATH_PARENTSIDS + " NOT LIKE '%"
					+ ServiceImplementation.DOCTYPE + ":%' ", true, false); // TODO not updated on delete endpoint proxy ?!!
			view.arg("endpointWithoutImpl", unmatchedEndpoints);
			
			// List impls without infoservice
			DocumentModelList servWithoutSpecs = docService.query(session, 
					 DocumentService.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE + subprojectCriteria
					 + DocumentService.NXQL_AND + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE
					 + " IS NULL ", true, false);
			
			view.arg("servWithoutSpecs", servWithoutSpecs)
			
                .arg("new_f", new freemarker.template.utility.ObjectConstructor())
                
			    .arg("subprojectId", subprojectId)
                .arg("visibility", visibility)
                .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
			return view;
		} catch (Exception e) {
			logger.debug(e);
			return getView("error").arg("error", e);
		}
	}

	@GET
	@Path("components/{uuid}")
	public Template suggestComponents(@PathParam("uuid") String uuid,
			@QueryParam("subprojectId") String subprojectId,
			@QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
		Template view = viewDashboard(subprojectId, visibility);
		DocumentModel modelElement = session.getDocument(new IdRef(uuid));
		List<DocumentModel> suggestedComponents = fetchComponents(session, modelElement);
		
		view.arg("components", suggestedComponents)
		    .arg("selectedModel", uuid)
		    
            .arg("new_f", new freemarker.template.utility.ObjectConstructor())
                
		    .arg("subprojectId", subprojectId)
            .arg("visibility", visibility)
            .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
		return view;
	}
	
	@GET
	@Path("suggest/{uuid}")
	public Template suggestServices(@PathParam("uuid") String uuid,
			@QueryParam("subprojectId") String subprojectId,
			@QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel model = session.getDocument(new IdRef(uuid));
		Template view = viewDashboard(subprojectId, visibility);

		// Args: suggestions
		List<DocumentModel> suggestions = fetchSuggestions(session, model, null, false);
		view.arg("suggestions", suggestions);
		List<DocumentModel> anyPlatformSuggestions = fetchSuggestions(session, model, null, true);
		if (suggestions.size() == 0) {
			view.arg("anyPlatformSuggestions", anyPlatformSuggestions);
		}
		else {
			view.arg("anyPlatformSuggestionsCount", anyPlatformSuggestions.size() - suggestions.size());
		}
		
		// Arg: selected document
		view.arg("selectedModel", uuid)
		
            .arg("new_f", new freemarker.template.utility.ObjectConstructor())
		
		    .arg("subprojectId", subprojectId)
            .arg("visibility", visibility)
            .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
		return view;
	}

	@GET
	@Path("suggest/{uuid}/{componentUuid}")
	public Template suggestServicesFromComponent(@PathParam("uuid") String uuid,
			@PathParam("componentUuid") String componentUuid, 
                        @QueryParam("subprojectId") String subprojectId,
                        @QueryParam("visibility") String visibility) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel model = session.getDocument(new IdRef(uuid));
		Template view = viewDashboard(subprojectId, visibility);

		// Args: components
		List<DocumentModel> components = fetchComponents(session, model);
		view.arg("components", fetchComponents(session, model));
		for (DocumentModel component : components) {
			if (component.getId().equals(componentUuid)) {
				view.arg("selectedComponentTitle", component.getTitle());
				break;
			}
		}
		
		// Args: suggestions
		List<DocumentModel> suggestions = fetchSuggestions(session, model, componentUuid, false);
		view.arg("suggestions", suggestions);
		List<DocumentModel> anyPlatformSuggestions = fetchSuggestions(session, model, componentUuid, true);
		if (suggestions.size() == 0) {
			view.arg("anyPlatformSuggestions", anyPlatformSuggestions);
		}
		else {
			view.arg("anyPlatformSuggestionsCount", anyPlatformSuggestions.size() - suggestions.size());
		}
		if (anyPlatformSuggestions.size() == 0) {
			String targetDoctype = Endpoint.DOCTYPE.equals(model.getType()) ?
					ServiceImplementation.DOCTYPE : 
					InformationService.DOCTYPE;
			String modelElementSubprojectId = (String) model.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
			String subprojectCriteria = " AND " + NXQLQueryHelper.buildSubprojectCriteria(session, modelElementSubprojectId, true);
			// TODO or according to model element type ??
			// TODO or both subproject criteria intersected ?
			//		NXQLQueryHelper.buildSubprojectPathCriteria(session, subprojectId, visibility));
			String infoServicesQuery = NXQLQueryBuilder.getQuery(DocumentService.NXQL_SELECT_FROM
					+ "?" + subprojectCriteria + DocumentService.NXQL_AND + "? = '?'",
					new Object[] {
						targetDoctype,
						Component.XPATH_COMPONENT_ID,
						componentUuid
					}, false, true);
			DocumentService docService = Framework.getService(DocumentService.class);
			view.arg("allFromComponent", docService.query(session, infoServicesQuery, true, false));
		}
		
		// Arg: selected document
		view.arg("selectedModel", uuid)
		
            .arg("new_f", new freemarker.template.utility.ObjectConstructor())
            
		    .arg("subprojectId", subprojectId)
            .arg("visibility", visibility)
            .arg("contextInfo", ContextData.getVersionData(session, subprojectId));
		return view;
	}
	
	@POST
	public Object submit(@FormParam("unmatchedModelId") String unmatchedModelId,
			@FormParam("targetId") String targetId, 
                        @QueryParam("subprojectId") String subprojectId,
                        @QueryParam("visibility") String visibility) {
	    try {
	    	if (unmatchedModelId != null && !unmatchedModelId.isEmpty()) {
	    		// Fetch impl
	            CoreSession session = SessionFactory.getSession(request);
				DocumentModel model = session.getDocument(new IdRef(unmatchedModelId));
				String doctype = model.getType();

				DocumentService docService = Framework.getService(DocumentService.class);
	    		SoaMetamodelService soaMetamodelService = Framework.getService(SoaMetamodelService.class);
	    		boolean isServiceImplementation = soaMetamodelService.isAssignable(doctype, ServiceImplementation.DOCTYPE);
				boolean isEndpoint = soaMetamodelService.isAssignable(doctype, Endpoint.DOCTYPE);
	    		
				// Compute new link value
	            String newTargetId; 
	    		if (targetId != null && !targetId.isEmpty()) {
	    			newTargetId = targetId; // Create link
	    		}
	    		else {
	    			// No targetId, meaning it's rather a "delete link" command.
	    			
	    			// Checking that there is a parent SOA node to unlink from :
					if (isServiceImplementation && docService.getParentInformationService(model) == null) {
						// no parent iserv, meaning it's not a "delete link command" but a UI bug
			    		throw new Exception("Information Service not selected");
					}
					else if (isEndpoint && docService.getParentServiceImplementation(model) == null) {
						// no parent serviceimpl, meaning it's not a "delete link command" but a UI bug
			    		throw new Exception("Service Implementation not selected");
					}
					newTargetId = null; // Destroy link
	    		}
	    		
	    		// Apply on document
				if (isServiceImplementation) {
		    		ServiceMatchingService matchingService = Framework.getService(ServiceMatchingService.class);
					matchingService.linkInformationService(session, model, newTargetId, true);
				}
				else { // isEndpoint
		    		EndpointMatchingService matchingService = Framework.getService(EndpointMatchingService.class);
					DocumentModel targetModel = (newTargetId != null) ? session.getDocument(new IdRef(targetId)) : null;
		    		if (targetModel != null && soaMetamodelService.isAssignable(targetModel.getType(), ServiceImplementation.DOCTYPE)) {
						matchingService.linkServiceImplementation(session, docService.createSoaNodeId(model),
								((newTargetId != null) ? docService.createSoaNodeId(session.getDocument(new IdRef(newTargetId))) : null),
								true/*, "strict"*/);
		    		} else { // InformationService target through placeholder, or delete
						matchingService.linkInformationServiceThroughPlaceholder(session, model, targetModel, true);
		    		}
				}
				return viewDashboard(subprojectId, visibility);
	    	}
	    	else {
	    		// error Service Implementation not selected, merely redisplay the page
				return viewDashboard(subprojectId, visibility);
	    	}
		} catch (Exception e) {
			logger.debug(e);
			return getView("error").arg("error", e);
		}
	}

	@POST
	@Path("samples")
	public Object submit(@QueryParam("subprojectId") String subprojectId,
			@QueryParam("visibility") String visibility) throws Exception {
		new DashboardMatchingSamples(this.getContext().getBaseURL()).run();
		return viewDashboard(subprojectId, visibility);
	}
	
	/**
	 * TODO refactor as ComponentMatchingService.findComponents() ?
	 * @param session whose subproject will be used as point of view / perspective
	 * to find components
	 * @param modelElement
	 * @return
	 * @throws Exception
	 */
	private List<DocumentModel> fetchComponents(CoreSession session,
			DocumentModel modelElement) throws Exception {
		String modelElementSubprojectId = (String) modelElement.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
		String subprojectCriteria = NXQLQueryHelper.buildSubprojectCriteria(session, modelElementSubprojectId, true);
		// TODO or according to model element type ??
		// TODO or both subproject criteria intersected ?
		//		NXQLQueryHelper.buildSubprojectPathCriteria(session, subprojectId, visibility));
		DocumentService docService = Framework.getService(DocumentService.class);
		return docService.getComponentsInCriteria(session, subprojectCriteria);
	}
	
	private List<DocumentModel> fetchSuggestions(CoreSession session, DocumentModel model, String componentUuid,
			boolean skipPlatformMatching) throws Exception {
		if (model != null) {
			// Run matching service according to doctype
			if (Endpoint.DOCTYPE.equals(model.getType())) {
				EndpointMatchingService matchingService = Framework.getService(EndpointMatchingService.class);
				List<DocumentModel> matchingImpls = matchingService.findServiceImplementations(session,
				        model, componentUuid, skipPlatformMatching, false);
				ServiceMatchingService serviceMatchingService = Framework.getService(ServiceMatchingService.class);
				List<DocumentModel> matchingServices = serviceMatchingService.findInformationServices(session,
				        model, componentUuid, skipPlatformMatching, false);
				List<DocumentModel> res = new ArrayList<DocumentModel>(matchingImpls.size() + matchingServices.size());
				res.addAll(matchingImpls);
				res.addAll(matchingServices);
				return res;
			}
			else { // ServiceImplementation
				ServiceMatchingService matchingService = Framework.getService(ServiceMatchingService.class);
				return matchingService.findInformationServices(session,
				        model, componentUuid, skipPlatformMatching, false);
			}
		}
		else {
			return new ArrayList<DocumentModel>();
		}
	}
}
