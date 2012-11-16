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
	public Object get() {
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
	@Path("suggest/{serviceImplUuid}")
	public List<DocumentModel> getSuggestions(@PathParam("serviceImplUuid") String serviceImplUuid) throws Exception {
		return getSuggestions(serviceImplUuid, null);
	}

	@GET
	@Path("suggest/{serviceImplUuid}/{componentUuid}")
	public List<DocumentModel> getSuggestions(@PathParam("serviceImplUuid") String serviceImplUuid,
			@PathParam("componentUuid") String componentUuid) throws Exception {
		if (serviceImplUuid != null) {
	        CoreSession session = SessionFactory.getSession(request);
			DocumentService docService = Framework.getService(DocumentService.class);
			//DocumentModel serviceImpl = session.getDocument(new IdRef(serviceImplUuid));
			
			// TODO Find matching information services only
			String informationServiceQuery = "SELECT * FROM " + InformationService.DOCTYPE;
			if (componentUuid != null) {
				// TODO Add filter to query
				informationServiceQuery += "";
			}
			return docService.query(session, informationServiceQuery, true, false);
		}
		else {
			return new ArrayList<DocumentModel>();
		}
	}

	@POST
	public Object post(@FormParam("infoServiceId") String infoServiceId,
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
				return get();
        	}
        	else {
        		throw new Exception("InfoService and/or ServiceImpl is not selected");
        	}
		} catch (Exception e) {
			return getView("error").arg("error", e);
		}
	}
}
