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

}