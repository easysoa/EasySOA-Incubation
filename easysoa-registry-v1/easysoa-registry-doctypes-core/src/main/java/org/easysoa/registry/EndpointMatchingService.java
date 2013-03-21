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
     * @param requireAtLeastOneExactCriteria
	 * @returnList of matching service implementations
	 * @throws ClientException
	 */
	DocumentModelList findServiceImplementations(CoreSession documentManager,
			DocumentModel endpoint, String filterComponentId,
			boolean skipPlatformMatching, boolean requireAtLeastOneExactCriteria) throws ClientException;

	DocumentModelList findInformationServices(CoreSession documentManager,
			DocumentModel endpoint, String filterComponentId,
			boolean requireAtLeastOneExactCriteria) throws ClientException;

    /**
     * @param documentManager
     * @param serviceImpl The service implementation to find matches for
     * @return List of matching service implementations
     * @throws ClientException
     */
    DocumentModelList findEndpointsCompatibleWithImplementation(CoreSession documentManager,
            DocumentModel serviceImpl) throws ClientException;

	void linkServiceImplementation(CoreSession documentManager,
			SoaNodeId endpointId, SoaNodeId implId,
			boolean save) throws Exception;

	void linkInformationServiceThroughPlaceholder(CoreSession documentManager,
			DocumentModel endpoint, DocumentModel informationService,
			boolean save) throws ClientException, Exception;

	boolean isEndpointAlreadyMatched(DocumentModel endpointDocument,
            CoreSession documentManager) throws ClientException;

}
