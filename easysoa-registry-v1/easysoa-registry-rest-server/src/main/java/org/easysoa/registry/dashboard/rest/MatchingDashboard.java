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
import org.easysoa.registry.ServiceMatchingService;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
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
			DocumentService service = Framework.getService(DocumentService.class);
			Template view = getView("index");
			
			// All information services
			DocumentModelList allInfoServices = service.query(session, "SELECT * FROM "
					+ InformationService.DOCTYPE, true, false);
			Map<String, DocumentModel> infoServicesById = new HashMap<String, DocumentModel>();
			for (DocumentModel infoService : allInfoServices) {
				infoServicesById.put(infoService.getId(), infoService);
			}
 
			// Find matched impls & their infoservice
			DocumentModelList matchedImpls = service.query(session, "SELECT * FROM " + ServiceImplementation.DOCTYPE + 
					 " WHERE " + ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE + " IS NOT NULL",
					 true, false);
			view.arg("matchedImpls", matchedImpls);
			view.arg("infoServicesById", infoServicesById);

			// List unimplemented services
			Map<String, DocumentModel> unimplementedServsMap = new HashMap<String, DocumentModel>(infoServicesById);
			for (DocumentModel matchedImpl : matchedImpls) {
				unimplementedServsMap.remove(
						matchedImpl.getPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE)
				);
			}
			view.arg("unimplementedServs", unimplementedServsMap.values());

			// List impls without infoservice
			DocumentModelList servWithoutSpecs = service.query(session, 
					 "SELECT * FROM " + ServiceImplementation.DOCTYPE + 
					 " WHERE " + ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE + " IS NULL",
					 true, false);
			view.arg("servWithoutSpecs", servWithoutSpecs);
			
			return view;
		} catch (Exception e) {
			return getView("error").arg("error", e);
		}
	}

	@GET
	@Path("components/{serviceImplUuid}")
	public Template suggestComponents(@PathParam("serviceImplUuid") String serviceImplUuid) throws Exception {
		Template view = viewDashboard();
		view.arg("components", fetchComponents());
		view.arg("selectedServiceImpl", serviceImplUuid);
		return view;
	}

	@GET
	@Path("suggest/{serviceImplUuid}")
	public Template suggestServices(@PathParam("serviceImplUuid") String serviceImplUuid) throws Exception {
		Template view = viewDashboard();
		view.arg("suggestions", fetchSuggestions(serviceImplUuid, null));
		view.arg("selectedServiceImpl", serviceImplUuid);
		return view;
	}
	
	@GET
	@Path("suggest/{serviceImplUuid}/{componentUuid}")
	public Template suggestServicesFromComponent(@PathParam("serviceImplUuid") String serviceImplUuid,
			@PathParam("componentUuid") String componentUuid) throws Exception {
		Template view = viewDashboard();
		List<DocumentModel> components = fetchComponents();
		view.arg("components", fetchComponents());
		view.arg("suggestions", fetchSuggestions(serviceImplUuid, componentUuid));
		view.arg("selectedServiceImpl", serviceImplUuid);
		for (DocumentModel component : components) {
			if (component.getId().equals(componentUuid)) {
				view.arg("selectedComponentTitle", component.getTitle());
				break;
			}
		}
		return view;
	}
	@POST
	public Object submit(@FormParam("infoServiceId") String infoServiceId,
			@FormParam("serviceImplId") String serviceImplId) {
	    try {
	    	if (serviceImplId != null && !serviceImplId.isEmpty()) {
	            String newInfoServiceId; 
	    		if (infoServiceId != null && !infoServiceId.isEmpty()) {
	    			newInfoServiceId = infoServiceId; // Create link
	    		}
	    		else {
	    			newInfoServiceId = null; // Destroy link
	    		}
	    		
	    		// Apply on document
	            CoreSession session = SessionFactory.getSession(request);
				DocumentModel serviceImpl = session.getDocument(new IdRef(serviceImplId));
				serviceImpl.setPropertyValue(ServiceImplementation.XPATH_LINKED_INFORMATION_SERVICE, newInfoServiceId);
				session.saveDocument(serviceImpl);
				session.save();
				return viewDashboard();
	    	}
	    	else {
	    		throw new Exception("InfoService and/or ServiceImpl is not selected");
	    	}
		} catch (Exception e) {
			return getView("error").arg("error", e);
		}
	}

	private List<DocumentModel> fetchComponents() throws Exception {
        CoreSession session = SessionFactory.getSession(request);
		DocumentService docService = Framework.getService(DocumentService.class);
		return docService.query(session, "SELECT * FROM Component", true, false);
	}
	
	private List<DocumentModel> fetchSuggestions(String serviceImplUuid, String componentUuid) throws Exception {
		if (serviceImplUuid != null) {
	        CoreSession session = SessionFactory.getSession(request);
			ServiceMatchingService matchingService = Framework.getService(ServiceMatchingService.class);
			
			DocumentModel serviceImplModel = session.getDocument(new IdRef(serviceImplUuid));
			DocumentModel componentModel = null;
			if (componentUuid != null) {
				componentModel = session.getDocument(new IdRef(componentUuid));
			}
			
			return matchingService.findInformationServices(session, serviceImplModel, componentModel);
		}
		else {
			return new ArrayList<DocumentModel>();
		}
	}
}
