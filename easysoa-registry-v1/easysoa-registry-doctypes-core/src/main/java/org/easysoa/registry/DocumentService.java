package org.easysoa.registry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.query.sql.NXQL;

/**
 *
 * @author mkalam-alami
 *
 */
public interface DocumentService {

    static final String NXQL_SELECT_FROM = "SELECT * FROM ";
    static final String NXQL_WHERE = " WHERE ";
    static final String NXQL_AND = " AND ";
    static final String NXQL_QUOTE = "'";

    static final String NXQL_IS_NOT_DELETED = NXQL.ECM_LIFECYCLESTATE + " != 'deleted'";
    static final String NXQL_IS_NOT_VERSIONED = NXQL.ECM_ISVERSION + " = 0";
    static final String NXQL_IS_VERSIONED = NXQL.ECM_ISVERSION + " = 1";
    static final String NXQL_IS_NO_PROXY = NXQL.ECM_ISPROXY + " = 0";
    static final String NXQL_IS_PROXY = NXQL.ECM_ISPROXY + " = 1";

    /** WARNING harder to use for cross-Phase/subproject, because their Path is relative,
     * so the project can only be known by the subprojectId (which contains the live subproject Path) */
    static final String NXQL_PATH_STARTSWITH = NXQL.ECM_PATH + " STARTSWITH '";

    static final String NXQL_NO_DELETED_DOCUMENTS_CRITERIA = " AND " + NXQL_IS_NOT_DELETED;

    static final String NQXL_NOT_VERSIONED_CRITERIA = " AND " + NXQL_IS_NOT_VERSIONED;

    static final String NQXL_NON_PROXIES_CRITERIA = " AND " + NXQL_IS_NO_PROXY;

    static final String NQXL_PROXIES_CRITERIA = " AND " + NXQL_IS_PROXY;

    static final String NXQL_WHERE_NO_PROXY = NXQL_WHERE + NXQL_IS_NOT_DELETED + NQXL_NON_PROXIES_CRITERIA; // NB. doesn't use NXQL_IS_NOT_VERSIONED because of Phase/subproject
    /** WARNING can't be used in cross-Phase/subproject queries, TODO hard to use with ecm:Path STARTSWITH */
    static final String NXQL_WHERE_PROXY = NXQL_WHERE + NXQL_IS_NOT_DELETED + NQXL_PROXIES_CRITERIA; // NB. doesn't use NXQL_IS_NOT_VERSIONED because of Phase/subproject


    /**
     * EasySOA configuration properties
     * TODO LATER move properties in their own extension point contributions,
     * else move it in its own service
     * @return
     */
    Properties getProperties();

    /**
     * Helper for non-SOA nodes documents (ex. SystemTreeRoot, IntelligentSystemTreeRoot...)
     * Direct use is not recommended for SoaNode types (whose auto reclassification requires soaId).
     * Used to create SOA roots and in tests.
     * @param documentManager
     * @param doctype
     * @param name
     * @param parentPath should be below a Subproject, use DocumentHelper or RepositoryHelper to get such a path
     * @param title
     * @return
     * @throws ClientException
     */
    DocumentModel createDocument(CoreSession documentManager,
            String doctype, String name,
            String parentPath, String title) throws ClientException;


    /**
     * TODO NO triggers documentCreate event but properties have not yet been set !
     * Creates a SoaNode document. If a document of the same identifier
     * exists, returns it instead. If the target path is not the expected path within the repository,
     * a document will be stored in the repository, and proxied at the wanted destination.
     *
     * @throws ClientException
     */
    DocumentModel create(CoreSession documentManager, SoaNodeId identifier, String parentPath) throws ClientException;

    /**
     * If a document of the same identifier exists, returns it. Else creates a new document
     * and puts it in the repository.
     * BEWARE : it triggers documentCreate event (so don't use it if properties have still to be set
     * ex. in DiscoveryServiceImpl or EndpointMatchingServiceImpl.linkInformationServiceThroughPlaceholder()
     * but rather use find() then newSoaNodeDocument())
     * Works only with SoaNode types (returns null otherwise).
     *
     * @throws ClientException
     */
    DocumentModel create(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;
    /**
     * Creates a new SoaNode document but doesn't save it (therefore have to save
     * afterwards using ex. coreSession.saveDocument()). To be preferred to createDocument
     * because doesn't trigger documentCreated event.
     * Calls newSoaNodeDocument(CoreSession, SoaNodeId, null).
     * @param documentManager nuxeo core session
     * @param identifier must be a valid SoaNodeId (check it with isSoaNode())
     * @param nuxeoProperties
     * @throws ClientException nuxeo error, or if already exists - check it before using
     * documentService.find(documentManager, identifier)
     */
    DocumentModel newSoaNodeDocument(CoreSession documentManager, SoaNodeId identifier) throws ClientException;

    /**
     * Creates a new SoaNode document and ses computed properties AFTER setting
     * argument-provided properties so that they don't break anything.
     * To be used from DiscoveryService.
     * @param documentManager
     * @param identifier
     * @param nuxeoProperties
     * @return
     * @throws ClientException
     */
    DocumentModel newSoaNodeDocument(CoreSession documentManager, SoaNodeId identifier,
            Map<String, Serializable> nuxeoProperties) throws ClientException;

    /**
     * Copies a document at the target destination.
     * Recommended for SoaNodes as it handles proxies correctly.
     */
    DocumentModel copy(CoreSession documentManager, DocumentModel sourceModel, DocumentRef destRef)
            throws ClientException;

    /**
     * Deletes the specified SoaNode from the repository, including all proxies.
     * @return true if the document existed and was succesfully deleted
     */
    boolean delete(CoreSession documentManager, SoaNodeId soaNodeId) throws ClientException;


    /**
     * Deletes the specified SoaNode proxy from a specific location.
     * @return true if the document existed and was succesfully deleted
     */
    boolean deleteProxy(CoreSession documentManager, SoaNodeId soaNodeId, String parentPath) throws ClientException;

    /**
     * To be used for non-SOA node documents (ex. SystemTreeRoot, IntelligentSystemTreeRoot...)
     * @param documentManager
     * @param subprojectId
     * @param type
     * @param name
     * @return
     * @throws ClientException
     */
    DocumentModel findDocument(CoreSession documentManager, String subprojectId, String type, String name) throws ClientException;

    /**
     *
     * @param documentManager
     * @param subprojectId
     * @param type
     * @param name
     * @param deepSearch
     * @return
     * @throws ClientException
     */
    DocumentModel findDocument(CoreSession documentManager, String subprojectId, String type, String name, boolean deepSearch) throws ClientException;

    /**
     * Same as findSoaNode(documentManager, SoaNodeId identifier, false)
     * @param documentManager
     * @param identifier
     * @return The document, or null if it doesn't exist
     * @throws ClientException
     */
    DocumentModel findSoaNode(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;

    /**
     * Finds any SOA document given its id (subproject, type and name)
     * If a SoaNode, returns the source (non-proxy) from the repository
     * NB. doesn't check that it's below the Repository (so check it in RepositoryManagementListener when putting it there)
     * @param documentManager
     * @param identifier
     * @param deepSearch TODO true not supported in discovery for now, else allowing
     * several SOA IDs (in different subproject / Phase) for a single node ;
     * at worse it should create an "inheriting" one
     * @return
     * @throws ClientException
     */
    DocumentModel findSoaNode(CoreSession documentManager, SoaNodeId identifier, boolean deepSearch)
            throws ClientException;

    /**
     * Find a proxy at a specific location
     */
    DocumentModel findProxy(CoreSession documentManager, SoaNodeId identifier, String parentPath)
            throws ClientException;

    /**
     * Find all proxies for a document given its type and name
     */
    DocumentModelList findProxies(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;

    /**
     * Find all proxies for a document given a model (either one proxy or the source)
     */
    DocumentModelList findProxies(CoreSession documentManager, DocumentModel model)
            throws ClientException;

    /**
     * Find all proxies and the repository source, given a type and name
     */
    DocumentModelList findAllInstances(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;

    /**
     * Find all proxies and the repository source, given a model (either one proxy or the source)
     */
    DocumentModelList findAllInstances(CoreSession documentManager, DocumentModel model)
            throws ClientException;

    /**
     * Find all documents that contain either the specified model, or any other instance of it.
     */
    DocumentModelList findAllParents(CoreSession documentManager, DocumentModel documentModel)
            throws Exception;

    /**
     * Does model.getAdapter(Endpoint.class).getParentOfType(type)
	 * (which is for now, as for all proxy parent classified SOA nodes, the fastest way)
     * then resolves SOA id to DocumentModel
     * @param soaNodeModel
     * @param type
     * @return
     * @throws ClientException
     */
	DocumentModel getSoaNodeParent(DocumentModel soaNodeModel, String type)
			throws ClientException;

	/**
	 * Gets actual (non-proxy) model then does getChildren(type)
	 * @param soaNodeModel
	 * @param type
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> getSoaNodeChildren(DocumentModel soaNodeModel, String type)
			throws ClientException;


    /**
     * NB. doesn't filter out versions because of Phase/subproject
     * @param documentManager
     * @param query
     * @param nonProxiesCriteria
     * @param proxiesCriteria
     * @return
     * @throws ClientException
     */
	DocumentModelList query(CoreSession documentManager, String query,
	        boolean nonProxiesCriteria, boolean proxiesCriteria)
			throws ClientException;

    boolean hasChild(CoreSession documentManager, DocumentModel document, SoaNodeId childId)
            throws ClientException;


    /**
     * Calls getSourceFolderPath(String subprojectId, String doctype)
     * @param soaNodeDocument
     * @return
     * @throws ClientException
     * @throws PropertyException
     */
    String getSourceFolderPath(CoreSession documentManager, SoaNodeId soaNodeId) throws PropertyException, ClientException;

    /**
     * Uses docType & spnode:subproject
     * calls getSourceFolderPath(String subprojectId, String doctype)
     * @param soaNodeDocument
     * @return
     * @throws ClientException
     * @throws PropertyException
     */
    String getSourceFolderPath(CoreSession documentManager, DocumentModel spNode) throws PropertyException, ClientException;

    /**
     * Base version of getSourceFolderPath()
     * @param subprojectId
     * @param doctype
     * @return
     * @throws ClientException
     * @throws PropertyException
     */
    String getSourceFolderPath(CoreSession documentManager, String subprojectId, String doctype) throws PropertyException, ClientException;

    /**
     * (helper in case RepositoryHelper.getRepositoryPath() has already been called)
     * @param subprojectRepositoryPath
     * @param doctype
     * @return
     * @throws ClientException
     */
    String getSourceFolderPathBelowSubprojectRepository(
    		String subprojectRepositoryPath, String doctype) throws ClientException;

    /**
     * Get or create source folder for type in subproject
     * Calls ensureSourceFolderExists(CoreSession documentManager, String subprojectId, String doctype)
     * @param documentManager
     * @param spNode
     * @return
     * @throws ClientException
     */
    DocumentModel getSourceFolder(CoreSession documentManager,
            DocumentModel spNode) throws ClientException;

    /**
     * Get or create source folder for type in subproject
     * Base version of getSourceFolder()
     * @param documentManager
     * @param subprojectId
     * @param doctype
     * @return
     * @throws ClientException
     */
    DocumentModel getSourceFolder(CoreSession documentManager,
            String subprojectId, String doctype) throws ClientException;

    SoaNodeId createSoaNodeId(DocumentModel model) throws PropertyException, ClientException;

    List<SoaNodeId> createSoaNodeIds(DocumentModel... models) throws PropertyException, ClientException;

    /**
     * Adds proxy compliance to CoreSession.getChildren():
     * If ref is a proxy, gets the children of its actual target.
     * @param session
     * @param ref
     * @param doctype Can be null
     * @return
     * @throws ClientException
     */
    DocumentModelList getChildren(CoreSession session, DocumentRef ref,
            String doctype) throws ClientException;
    /**
     * Proxy AND SUBTYPE-compliant alternative to CoreSession.getChildren() USING NXQL:
     * If ref is a proxy, gets the children of its actual target.
     * @param docModel
     * @param type
     * @return
     * @throws ClientException
     */
    DocumentModelList getChildren(DocumentModel docModel, String type) throws ClientException;


    boolean isSoaNode(CoreSession documentManager, String doctype) throws ClientException;

    boolean isTypeOrSubtype(CoreSession documentManager, String doctypeToTest, String expectedDoctype) throws ClientException;


	List<DocumentModel> getInformationServices(CoreSession session, String subprojectId) throws ClientException;
	List<DocumentModel> getInformationServicesInCriteria(CoreSession session, String subprojectCriteria) throws ClientException;

	List<DocumentModel> getServiceImplementations(CoreSession session, String subprojectId) throws ClientException;
	List<DocumentModel> getServiceImplementationsInCriteria(CoreSession session, String subprojectCriteria) throws ClientException;

	List<DocumentModel> getEndpoints(CoreSession session, String subprojectId) throws ClientException;
	List<DocumentModel> getEndpointsInCriteria(CoreSession session, String subprojectCriteria) throws ClientException;

	List<DocumentModel> getByType(CoreSession session, String type, String subprojectId) throws ClientException;
	List<DocumentModel> getByTypeInCriteria(CoreSession session, String type, String subprojectCriteria) throws ClientException;

	DocumentModel getServiceImplementationFromEndpoint(DocumentModel endpointModel) throws ClientException;

	List<DocumentModel> getEndpointsOfService(DocumentModel service, String subprojectId) throws ClientException;
	List<DocumentModel> getEndpointsOfServiceInCriteria(DocumentModel service, String subprojectCriteria) throws ClientException;

	DocumentModel getEndpointOfService(DocumentModel service, String environment, String subprojectId) throws ClientException;
	DocumentModel getEndpointOfServiceInCriteria(DocumentModel service, String environment, String subprojectCriteria) throws ClientException;

	List<DocumentModel> getEndpointsOfImplementation(DocumentModel serviceImpl, String subprojectId) throws ClientException;
	List<DocumentModel> getEndpointsOfImplementationInCriteria(DocumentModel serviceImpl, String subprojectCriteria) throws ClientException;

    DocumentModel getEndpointOfImplementation(DocumentModel serviceImpl, String environment, String subprojectId) throws ClientException;
    DocumentModel getEndpointOfImplementationInCriteria(DocumentModel serviceImpl, String environment, String subprojectCriteria) throws ClientException;

	List<String> getEnvironments(CoreSession session, String subprojectId) throws ClientException;
	List<String> getEnvironmentsInCriteria(CoreSession session, String subprojectCriteria) throws ClientException;

	List<DocumentModel> getComponents(CoreSession session, String subprojectId) throws ClientException;
	List<DocumentModel> getComponentsInCriteria(CoreSession session, String subprojectCriteria) throws ClientException;

	DocumentRef getParentInformationService(DocumentModel model) throws ClientException;

	/**
	 * For now uses getSoaNodeParent()
	 * @param model endpoint
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getParentServiceImplementation(DocumentModel model)throws ClientException;

}