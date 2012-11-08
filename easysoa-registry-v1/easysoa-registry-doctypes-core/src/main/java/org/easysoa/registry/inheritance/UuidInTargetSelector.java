package org.easysoa.registry.inheritance;

import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.runtime.api.Framework;

public class UuidInTargetSelector implements InheritedFacetModelSelector {
	
	public static final String NAME = "uuidInTarget";

	public static final String PARAMETER_METADATA = "metadata";
	
	public DocumentModelList findTargets(CoreSession documentManager,
			DocumentModel model, String targetDoctype, Map<String, String> parameters)
			throws Exception {
		DocumentService documentService = Framework.getService(DocumentService.class);
		DocumentModel workingCopy = documentManager.getWorkingCopy(model.getRef());
		String modelId = (workingCopy != null) ? workingCopy.getId() : model.getId();
		String sourceQuery = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE AND ? = '?'",
				new Object[]{
					targetDoctype,
					parameters.get(PARAMETER_METADATA),
					modelId
				}, false, true);
		return documentService.query(documentManager, sourceQuery, true, false);
	}

	public DocumentModel findSource(CoreSession documentManager,
			DocumentModel model, String sourceDoctype, Map<String, String> parameters)
			throws Exception {
		String sourceUuid = (String) model.getPropertyValue(parameters.get(PARAMETER_METADATA));
		return documentManager.getDocument(new IdRef(sourceUuid));
	}
	
}
