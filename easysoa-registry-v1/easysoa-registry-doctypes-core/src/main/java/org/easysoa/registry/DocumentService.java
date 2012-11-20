package org.easysoa.registry;

import java.util.List;
import java.util.Map;

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
    
    static final String DELETED_DOCUMENTS_QUERY_FILTER = " AND " + NXQL.ECM_LIFECYCLESTATE + " != 'deleted'";

    static final String VERSIONS_QUERY_FILTER = " AND " + NXQL.ECM_ISVERSION + " = 0";
    
    static final String PROXIES_QUERY_FILTER = " AND " + NXQL.ECM_ISPROXY + " = 0";
    
    static final String NON_PROXIES_QUERY_FILTER = " AND " + NXQL.ECM_ISPROXY + " = 1";

    /**
     * Explodes if soan:name is null.
     * Use it (in event listeners...) to helps maintaining consistency
     * across events & lookup methods.
     * @param soaNodeDoc
     * @throws ClientException
     */
	public void checkSoaName(DocumentModel soaNodeDoc) throws ClientException;
	
    /**
     * Helper for general document creation.
     * Direct use is not recommended for SoaNode types (whose auto reclassification requires soaId).
     * Used to create SOA roots and in tests.
     * 
     * @return
     * @throws ClientException
     */
    DocumentModel createDocument(CoreSession documentManager, String doctype, String name,
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
     * TODO NO triggers documentCreate event but properties have not yet been set !
     * Creates a document and puts it in the repository. If a document of the same identifier
     * exists, returns it instead.
     * Works only with SoaNode types (returns null otherwise).
     * 
     * @throws ClientException
     */
    DocumentModel create(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;
    /**
     * Doesn't create (have to save afterwards), to be preferred to createDocument
     * because doesn't trigger documentCreated event
     * @param documentManager
     * @param identifier
     * @return
     * @throws ClientException nuxeo error, or if already exists - check it before using :
     * coreSession.exists(new PathRef(getSourcePath(identifier)))
     * or
     * documentService.find(documentManager, identifier)
     */
    DocumentModel newDocument(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;
    
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
    
    DocumentModel findDocument(CoreSession documentManager, String type, String name)
    throws ClientException;

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

    String getSourceFolderPath(String doctype);

    void ensureSourceFolderExists(CoreSession documentManager, String doctype)
            throws ClientException;

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