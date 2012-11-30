package org.easysoa.registry;

import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public interface EndpointMatchingService {

	/**
	 * 
	 * @param documentManager
	 * @param endpoint The endpoint to find matches for
	 * @param filterComponent All returned service implementations will have to be part of this component if set (use null for any).
	 * Documents will also be filtered by component if the {@link org.easysoa.registry.facets.ArchitectureComponentFacet.XPATH_COMPONENT_ID}
	 * property is set on the implementation.
	 * @param skipPlatformMatching
	 * @returnList of matching service implementations
	 * @throws ClientException
	 */
	DocumentModelList findServiceImpls(CoreSession documentManager,
			DocumentModel endpoint, DocumentModel filterComponent,
			boolean skipPlatformMatching) throws ClientException;

	void linkServiceImplementation(CoreSession documentManager,
			SoaNodeId endpointId, SoaNodeId implId,
			boolean save) throws Exception;

}
