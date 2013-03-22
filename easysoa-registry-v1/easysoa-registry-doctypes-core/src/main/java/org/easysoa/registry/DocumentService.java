package org.easysoa.registry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    static final String NXQL_PATH_STARTSWITH = NXQL.ECM_PATH + " STARTSWITH '";
    
    static final String NO_DELETED_DOCUMENTS_CRITERIA = " AND " + NXQL_IS_NOT_DELETED;

    static final String NOT_VERSIONED_CRITERIA = " AND " + NXQL_IS_NOT_VERSIONED;
    
    static final String PROXIES_CRITERIA = " AND " + NXQL_IS_NO_PROXY;
    
    static final String NON_PROXIES_CRITERIA = " AND " + NXQL_IS_PROXY;

    static final String NXQL_WHERE_NO_PROXY = NXQL_WHERE + NXQL_IS_NOT_DELETED
            + NXQL_AND + NXQL_IS_NOT_VERSIONED + NXQL_AND + NXQL_IS_NO_PROXY;
    static final String NXQL_WHERE_PROXY = NXQL_WHERE + NXQL_IS_NOT_DELETED
            + NXQL_AND + NXQL_IS_NOT_VERSIONED + NXQL_AND + NXQL_IS_PROXY;

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
     * Finds any document given its type and name
     * If a SoaNode, returns the source (non-proxy) from the repository 
     * @param documentManager
     * @param identifier
     * @return The document, or null if it doesn't exist
     * @throws ClientException
     */
    DocumentModel find(CoreSession documentManager, SoaNodeId identifier)
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

	DocumentModelList query(CoreSession documentManager, String query,
			boolean filterProxies, boolean filterNonProxies)
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
     * Proxy-compliant alternative to CoreSession.getChildren():
     * If ref is a proxy, gets the children of its actual target.
     * @param session
     * @param ref
     * @param doctype Can be null
     * @return
     * @throws ClientException
     */
    DocumentModelList getChildren(CoreSession session, DocumentRef ref,
            String doctype) throws ClientException;


    boolean isSoaNode(CoreSession documentManager, String doctype) throws ClientException;
    
    boolean isTypeOrSubtype(CoreSession documentManager, String doctypeToTest, String expectedDoctype) throws ClientException;


    /**
     * TODO MDU hack
     * @param documentManager
     * @param identifier
     * @return
     */
	DocumentModel findEndpoint(CoreSession documentManager,
			SoaNodeId identifier, Map<String, Object> properties,
			List<SoaNodeId> suggestedParentIds /*NOT USED*/, List<SoaNodeId> knownComponentIds)
					throws ClientException;

}