package org.easysoa.registry.inheritance;

import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public interface InheritedFacetModelSelector {
	
	DocumentModelList findTargets(CoreSession documentManager, DocumentModel model,
			String targetDoctype, Map<String, String> parameters) throws Exception;

	DocumentModel findSource(CoreSession documentManager, DocumentModel model,
			String sourceDoctype, Map<String, String> parameters) throws Exception;
	
}
