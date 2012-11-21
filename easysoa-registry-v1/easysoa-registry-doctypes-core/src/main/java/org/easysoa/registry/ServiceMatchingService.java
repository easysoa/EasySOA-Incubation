package org.easysoa.registry;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public interface ServiceMatchingService {

	/**
	 * @param documentManager
	 * @param impl The service implementation to find matches for
	 * @return List of matching information services
	 * @throws ClientException
	 */
	public abstract DocumentModelList findInformationServices(
			CoreSession documentManager, DocumentModel impl)
			throws ClientException;

	/**
	 * @param documentManager
	 * @param impl The service implementation to find matches for
	 * @param filterComponent All returned information services will have to be part of this component if set (use null for any)
	 * @return List of matching information services
	 * @throws ClientException
	 */
	public abstract DocumentModelList findInformationServices(
			CoreSession documentManager, DocumentModel impl,
			DocumentModel filterComponent) throws ClientException;

	/**
	 * @param documentManager
	 * @param informationService The information service to find matches for
	 * @return List of matching service implementations
	 * @throws ClientException
	 */
	DocumentModelList findServiceImplementations(CoreSession documentManager,
			DocumentModel informationService) throws ClientException;
	
	/**
	 * Links the specified service impl to an information service.
	 * Optionally calls documentManager.saveDocument() + save().
	 * @param documentManager
	 * @param serviceImplModel
	 * @param informationServiceModel
	 * @param save
	 * @throws ClientException
	 */
	public abstract void linkInformationService(CoreSession documentManager,
			DocumentModel serviceImplModel,
			DocumentModel informationServiceModel, boolean save)
			throws ClientException;

}