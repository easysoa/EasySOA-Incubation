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

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.EndpointMatchingService;
import org.easysoa.registry.ServiceMatchingService;
import org.easysoa.registry.rest.samples.DashboardMatchingSamples;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
@WebObject(type = "dashboard")
@Path("easysoa/dashboard")
public class MatchingDashboard extends ModuleRoot {
	
	@GET
	public Template viewDashboard() {
        CoreSession session = SessionFactory.getSession(request);
		try {
			DocumentService docService = Framework.getService(DocumentService.class);
			Template view = getView("index");
			
			// All information services
			DocumentModelList allInfoServices = docService.query(session, "SELECT * FROM "
					+ InformationService.DOCTYPE, true, false);
			Map<String, DocumentModel> infoServicesById = new HashMap<String, DocumentModel>();
			for (DocumentModel infoService : allInfoServices) {
				infoServicesById.put(infoService.getId(), infoService);
			}
 
			// Find matched impls & their infoservice
			DocumentModelList matchedImpls = docService.query(session, "SELECT * FROM " + ServiceImplementation.DOCTYPE + 
					 " WHERE " + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + " IS NOT NULL",
					 true, false);
			view.arg("matchedImpls", matchedImpls);
			view.arg("infoServicesById", infoServicesById);

			// List unimplemented services
			Map<String, DocumentModel> unimplementedServsMap = new HashMap<String, DocumentModel>(infoServicesById);
			for (DocumentModel matchedImpl : matchedImpls) {
				unimplementedServsMap.remove(
						matchedImpl.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE)
				);
			}
			view.arg("unimplementedServs", unimplementedServsMap.values());

			// List endpoints without impls
			DocumentModelList unmatchedEndpoints = docService.query(session,
					"SELECT * FROM " + Endpoint.DOCTYPE + " WHERE " +
					Endpoint.XPATH_PARENTSIDS + " NOT LIKE '" + ServiceImplementation.DOCTYPE + "%'", true, false);
			view.arg("endpointWithoutImpl", unmatchedEndpoints);
			
			// List impls without infoservice
			DocumentModelList servWithoutSpecs = docService.query(session, 
					 "SELECT * FROM " + ServiceImplementation.DOCTYPE + 
					 " WHERE " + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + " IS NULL",
					 true, false);
			view.arg("servWithoutSpecs", servWithoutSpecs);
			
			return view;
		} catch (Exception e) {
			return getView("error").arg("error", e);
		}
	}

	@GET
	@Path("components/{uuid}")
	public Template suggestComponents(@PathParam("uuid") String uuid) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
		Template view = viewDashboard();
		view.arg("components", fetchComponents(session));
		view.arg("selectedModel", uuid);
		return view;
	}
	
	@GET
	@Path("suggest/{uuid}")
	public Template suggestServices(@PathParam("uuid") String uuid) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel model = session.getDocument(new IdRef(uuid));
		Template view = viewDashboard();

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
		view.arg("selectedModel", uuid);
		
		return view;
	}

	@GET
	@Path("suggest/{uuid}/{componentUuid}")
	public Template suggestServicesFromComponent(@PathParam("uuid") String uuid,
			@PathParam("componentUuid") String componentUuid) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel model = session.getDocument(new IdRef(uuid));
		Template view = viewDashboard();

		// Args: components
		List<DocumentModel> components = fetchComponents(session);
		view.arg("components", fetchComponents(session));
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
			String infoServicesQuery = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE ? = '?'",
					new Object[] {
						targetDoctype,
						Component.XPATH_COMPONENT_ID,
						componentUuid
					}, false, true);
			DocumentService docService = Framework.getService(DocumentService.class);
			view.arg("allFromComponent", docService.query(session, infoServicesQuery, true, false));
		}
		
		// Arg: selected document
		view.arg("selectedModel", uuid);
		
		return view;
	}
	
	@POST
	public Object submit(@FormParam("unmatchedModelId") String unmatchedModelId,
			@FormParam("targetId") String targetId) {
	    try {
	    	if (unmatchedModelId != null && !unmatchedModelId.isEmpty()) {
	    		// Fetch impl
	            CoreSession session = SessionFactory.getSession(request);
				DocumentModel model = session.getDocument(new IdRef(unmatchedModelId));
				String doctype = model.getType();
				
				// Compute new link value
	            String newTargetId; 
	    		if (targetId != null && !targetId.isEmpty()) {
	    			newTargetId = targetId; // Create link
	    		}
	    		else {
					if (ServiceImplementation.DOCTYPE.equals(doctype)
							&& model.getPropertyValue(ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE) == null) {
			    		throw new Exception("Information Service not selected");
					}
					else if (Endpoint.DOCTYPE.equals(doctype)) {
						Endpoint endpointAdapter = model.getAdapter(Endpoint.class);
						if (endpointAdapter.getParentOfType(ServiceImplementation.DOCTYPE) != null) {
				    		throw new Exception("Service Implementation not selected");
						}
					}
					newTargetId = null; // Destroy link
	    		}
	    		
	    		// Apply on document
				if (ServiceImplementation.DOCTYPE.equals(doctype)) {
		    		ServiceMatchingService matchingService = Framework.getService(ServiceMatchingService.class);
					matchingService.linkInformationService(session, model, newTargetId, true);
				}
				else {
					DocumentService docService = Framework.getService(DocumentService.class);
		    		EndpointMatchingService matchingService = Framework.getService(EndpointMatchingService.class);
					matchingService.linkServiceImplementation(session,
							docService.createSoaNodeId(model),
							((newTargetId != null) ? docService.createSoaNodeId(session.getDocument(new IdRef(newTargetId))) : null),
							true);
				}
				return viewDashboard();
	    	}
	    	else {
	    		throw new Exception("Service Implementation not selected");
	    	}
		} catch (Exception e) {
			return getView("error").arg("error", e);
		}
	}

	@POST
	@Path("samples")
	public Object submit() throws Exception {
		new DashboardMatchingSamples("http://localhost:8080").run();
		return viewDashboard();
	}
	
	private List<DocumentModel> fetchComponents(CoreSession session) throws Exception {
		DocumentService docService = Framework.getService(DocumentService.class);
		return docService.query(session, "SELECT * FROM Component", true, false);
	}
	
	private List<DocumentModel> fetchSuggestions(CoreSession session, DocumentModel model, String componentUuid,
			boolean skipPlatformMatching) throws Exception {
		if (model != null) {
			// Fetch component
			DocumentModel componentModel = null;
			if (componentUuid != null) {
				componentModel = session.getDocument(new IdRef(componentUuid));
			}
			
			// Run matching service according to doctype
			if (Endpoint.DOCTYPE.equals(model.getType())) {
				EndpointMatchingService matchingService = Framework.getService(EndpointMatchingService.class);
				return matchingService.findServiceImplementations(session,
				        model, componentModel.getId(), skipPlatformMatching, false);
			}
			else {
				ServiceMatchingService matchingService = Framework.getService(ServiceMatchingService.class);
				return matchingService.findInformationServices(session,
				        model, componentModel.getId(), skipPlatformMatching, false);
			}
		}
		else {
			return new ArrayList<DocumentModel>();
		}
	}
}
