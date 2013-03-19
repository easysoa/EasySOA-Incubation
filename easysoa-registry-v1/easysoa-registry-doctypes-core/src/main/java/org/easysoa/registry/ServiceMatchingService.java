package org.easysoa.registry;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;

public interface ServiceMatchingService {
	
	/**
	 * @param documentManager
	 * @param impl The service implementation to find matches for
	 * @param filterComponentId All returned information services will have to be part of this component if set (use null for any).
	 * Documents will also be filtered by component if the {@link org.easysoa.registry.facets.ArchitectureComponentFacet.XPATH_COMPONENT_ID}
	 * property is set on the implementation.
     * @param skipPlatformMatching
	 * @param requireAtLeastOneExactCriteria
	 * @return List of matching information services
	 * @throws ClientException
	 */
	public abstract DocumentModelList findInformationServices(CoreSession documentManager,
	        DocumentModel impl, String filterComponentId,
	        boolean skipPlatformMatching, boolean requireAtLeastOneExactCriteria/*, String visibility*/) throws ClientException;

	/**
	 * @param documentManager
	 * @param informationService The information service to find matches for
	 * @return List of matching service implementations
	 * @throws ClientException
	 */
	DocumentModelList findImplementationsCompatibleWithService(CoreSession documentManager,
			DocumentModel informationService) throws ClientException;
	
	/**
	 * Links the specified service impl to an information service,
	 * optionally calls documentManager.saveDocument() + save().
	 * Does nothing if the information service is already linked.
	 * @param documentManager
	 * @param serviceImplModel
	 * @param informationServiceUuid
	 * @param save
	 * @throws ClientException
	 */
	void linkInformationService(CoreSession documentManager,
			DocumentModel serviceImplModel, String informationServiceUuid,
			boolean save) throws ClientException;

	DocumentModelList findPlatforms(CoreSession documentManager,
			DocumentModel modelWithPlatformFacet) throws ClientException;

    boolean isServiceImplementationAlreadyMatched(DocumentModel implDocument)
            throws PropertyException, ClientException;

}