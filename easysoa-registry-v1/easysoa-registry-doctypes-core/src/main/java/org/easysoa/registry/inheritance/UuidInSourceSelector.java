package org.easysoa.registry.inheritance;

import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.runtime.api.Framework;

public class UuidInSourceSelector implements InheritedFacetModelSelector {
	
	public static final String NAME = "uuidInSource";

	public static final String PARAMETER_METADATA = "metadata";
	
	public DocumentModelList findTargets(CoreSession documentManager,
			DocumentModel model, String targetDoctype, Map<String, String> parameters)
			throws Exception {
		String targetUuid = (String) model.getPropertyValue(parameters.get(PARAMETER_METADATA));
		DocumentModelListImpl targetDocuments = new DocumentModelListImpl();
		if (targetUuid != null) {
			DocumentModel targetDocument = documentManager.getDocument(new IdRef(targetUuid));
			targetDocuments.add(targetDocument);
		}
		return targetDocuments;
	}

	public DocumentModel findSource(CoreSession documentManager,
			DocumentModel model, String sourceDoctype, Map<String, String> parameters)
			throws Exception {
		DocumentService documentService = Framework.getService(DocumentService.class);
		DocumentModel workingCopy = documentManager.getSourceDocument(model.getRef());
		String modelId = (workingCopy != null) ? workingCopy.getId() : model.getId();
		String sourceQuery = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE ? = '?'",
				new Object[]{
					sourceDoctype,
					parameters.get(PARAMETER_METADATA),
					modelId
				}, false, true);
		DocumentModelList sourceResult = documentService.query(documentManager, sourceQuery, true, false);
		return !sourceResult.isEmpty() ? sourceResult.get(0) : null;
	}
	
}
