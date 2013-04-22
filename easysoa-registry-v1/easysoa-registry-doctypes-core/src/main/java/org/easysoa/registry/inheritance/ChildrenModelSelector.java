package org.easysoa.registry.inheritance;

import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaMetamodelService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.api.Framework;

public class ChildrenModelSelector implements InheritedFacetModelSelector {
	
	public static final String NAME = "children";
	
	public DocumentModelList findTargets(CoreSession documentManager, DocumentModel model,
			String targetDoctype, Map<String, String> parameters) throws Exception {
		DocumentService documentService = Framework.getService(DocumentService.class);
		DocumentModelList children = documentService.getChildren(documentManager, model.getRef(), targetDoctype);
		DocumentModelList childrenWithExpectedFacet = new DocumentModelListImpl();
		for (DocumentModel child : children) {
			if (child.getType().equals(targetDoctype)) {
				childrenWithExpectedFacet.add(child);
			}
		}
		return childrenWithExpectedFacet;
	}

	public DocumentModel findSource(CoreSession documentManager, DocumentModel model,
			String sourceDoctype, Map<String, String> parameters) throws Exception {
		DocumentService documentService = Framework.getService(DocumentService.class);
		SoaMetamodelService soaMetamodelService = Framework.getService(SoaMetamodelService.class);
		DocumentModelList parents = documentService.findAllParents(documentManager, model);
		for (DocumentModel parent : parents) {
			if (soaMetamodelService.isAssignable(parent.getType(), sourceDoctype)) {
				// ex. isAssignable("JavaServiceImplementation", "ServiceImplementation")
				// XXX What if several parents have the same inherited facet?
				return parent;
			}
		}
		return null;
	}
}
