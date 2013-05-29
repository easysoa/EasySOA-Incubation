package org.easysoa.registry;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.easysoa.registry.facets.ServiceImplementationDataFacet;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.easysoa.registry.utils.NXQLQueryHelper;
import org.easysoa.registry.utils.RepositoryHelper;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

public class DocumentServiceImpl extends DefaultComponent implements DocumentService {

	private Properties properties;
	
    @Override
    public void activate(ComponentContext context) throws Exception {
        super.activate(context);
        
        // Loading configuration
        // NB. accesses the file in config/ using bundle resource (like SchedulerImpl)
        // rather than using Environment.getDefault().getConfig() (like Composer.createMailer())
        // TODO LATER maybe rather as an extension point / contribution ?? else in its own service
    	properties = new Properties();
        URL cfg = context.getRuntimeContext().getResource("config/easysoa.properties");
        if (cfg != null) {
            InputStream cfgIn = cfg.openStream();
            try {
            	properties.load(cfgIn);
            } finally {
            	cfgIn.close();
            }
        } //  will have to use default config (unit tests...)
    }
        
	public Properties getProperties() {
		return properties;
	}
	
    /* (non-Javadoc)
     * @see org.easysoa.registry.DocumentService#createDocument(org.nuxeo.ecm.core.api.CoreSession, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public DocumentModel createDocument(CoreSession documentManager,
            String doctype, String name, String parentPath, String title) throws ClientException {
        // TODO if doctype belongs to SoaNode subtypes, throw new Exception("createDocument() doesn't work for SoaNode types, rather use create()")
        DocumentModel documentModel = documentManager.createDocumentModel(doctype);
        documentModel.setPathInfo(parentPath, safeName(name));
        documentModel.setPropertyValue(SoaNode.XPATH_TITLE, title);
        // TODO fully recursive spnode copy, to ease setting spnode props (in SubprojectNode event listener) ??
        //if (!documentModel.hasFacet("SubprojectNode")) {
        //    documentModel.addFacet("SubprojectNode");
        //    }
        //documentModel.setPropertyValue(SubprojectNode.XPATH_SUBPROJECT, subprojectId);
        // TODO or only if facet ??
        documentModel = documentManager.createDocument(documentModel);
        return documentModel;
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.registry.DocumentService#create(org.nuxeo.ecm.core.api.CoreSession, org.easysoa.registry.types.ids.SoaNodeId, java.lang.String)
     */
    public DocumentModel create(CoreSession documentManager, SoaNodeId identifier, String parentPath) throws ClientException {
        String doctype = identifier.getType();
 
        if (isSoaNode(documentManager, doctype)) {
            
            // Create or fetch source document
            DocumentModel documentModel = findSoaNode(documentManager, identifier);
        	if (documentModel == null) {
                documentModel = newSoaNodeDocument(documentManager, identifier);
                documentModel = documentManager.createDocument(documentModel);
        	}
            
            // Create proxy if needed (but make sure the parent is the instance of the repository,
            // otherwise the child proxy will only be visible in the context of the parent proxy)
            boolean createProxy = !parentPath.equals(getSourceFolderPath(documentManager, identifier)); // XXX Redundant with RepositoryManagementListener?
            if (createProxy) {
                PathRef parentRef = new PathRef(parentPath);
                DocumentModel parentModel = documentManager.getDocument(parentRef);
                if (parentModel != null) {
                    if (parentModel.isProxy()) {
                        parentModel = findSoaNode(documentManager, createSoaNodeId(parentModel));
                    }
                }
                else {
                    throw new ClientException("Invalid parent path: " + parentPath);
                }
                
                DocumentModel existingProxy = null;
            	DocumentModelList foundProxies = findProxies(documentManager, identifier);
            	for (DocumentModel foundProxy : foundProxies) {
            		if (foundProxy.getParentRef().equals(parentModel.getRef())) {
            			existingProxy = foundProxy;
            			break;
            		}
            	}
            	
            	if (existingProxy == null) {
            		return documentManager.createProxy(documentModel.getRef(), parentModel.getRef());
            	}
            	else {
            		return existingProxy;
            	}
            }
            else {
                return documentModel;
            }
        }
        else {
            return null;
        }
    }

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
    public DocumentModel create(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        DocumentModel documentModel = null;
        
        if (isSoaNode(documentManager, identifier.getType())) {
        	documentModel = findSoaNode(documentManager, identifier);
        	if (documentModel == null) {
                documentModel = newSoaNodeDocument(documentManager, identifier);
                documentModel = documentManager.createDocument(documentModel);
        	}
        }
        
        return documentModel;
    }

    public DocumentModel newSoaNodeDocument(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        return newSoaNodeDocument(documentManager, identifier, null);
    }
    /* (non-Javadoc)
     * @see org.easysoa.registry.DocumentService#newSoaNodeDocument(org.nuxeo.ecm.core.api.CoreSession, org.easysoa.registry.SoaNodeId)
     */
    public DocumentModel newSoaNodeDocument(CoreSession documentManager, SoaNodeId identifier,
            Map<String, Serializable> nuxeoProperties) throws ClientException {
        String subprojectId = SubprojectServiceImpl.setDefaultSubprojectIfNone(documentManager, identifier);
        String doctype = identifier.getType();
        String name = identifier.getName();
        DocumentModel sourceFolder = getSourceFolder(documentManager, subprojectId, doctype); // ensuring it exists at the same time
        DocumentModel documentModel = documentManager.createDocumentModel(doctype);
        documentModel.setPathInfo(sourceFolder.getPathAsString(), safeName(name));
        
        // setting default props from SOA node id
        documentModel.setPropertyValue(SoaNode.XPATH_TITLE, name);

        // other default properties : (user ex. for Endpoint)
        for (Entry<String, Serializable> defaultPropertyValue : identifier.getDefaultPropertyValues().entrySet()) {
            documentModel.setPropertyValue(defaultPropertyValue.getKey(), defaultPropertyValue.getValue());
        }
        
        if (nuxeoProperties != null) {
            // user-provided properties
            for (Entry<String, Serializable> nuxeoProperty : nuxeoProperties.entrySet()) {
                String propertyKey = nuxeoProperty.getKey();
                Serializable propertyValue = nuxeoProperty.getValue();
                documentModel.setPropertyValue(propertyKey, propertyValue);
            }
        }
        
        // setting props than must not be overriden by user-provided props
        documentModel.setPropertyValue(SoaNode.XPATH_SOANAME, name);
        documentModel.setPropertyValue(SubprojectNode.XPATH_SUBPROJECT, subprojectId);
        
        // TODO copy spnode metas, or in listener, or using facet inheritance ??
        return documentModel;
    }

	/**
     * Transforms the parameter into a string suitable for use as Nuxeo's name.
     */
    private String safeName(String name) {
		return name.replace('/', '|');
	}

    /**
     * TODO across subproject
     */
	@Override
    public DocumentModel copy(CoreSession documentManager, DocumentModel sourceModel, DocumentRef ref) throws ClientException {
        if (sourceModel.isProxy()) {
            return documentManager.copy(sourceModel.getRef(), ref, sourceModel.getName());
        }
        else {
            return documentManager.createProxy(sourceModel.getRef(), ref);
        }
    }

    @Override
    public boolean delete(CoreSession documentManager, SoaNodeId soaNodeId) throws ClientException {
        DocumentModel sourceDocument = findSoaNode(documentManager, soaNodeId);
        if (sourceDocument != null) {
            documentManager.removeDocument(sourceDocument.getRef());
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean deleteProxy(CoreSession documentManager, SoaNodeId soaNodeId, String parentPath)
            throws ClientException {
        DocumentModelList instances = findAllInstances(documentManager, soaNodeId);
        for (DocumentModel instance : instances) {
            if (instance.getPath().removeLastSegments(1).equals(new Path(parentPath))) {
                documentManager.removeDocument(instance.getRef());
                return true;
            }
        }   
        return false;
    }

    @Override
    public DocumentModel findDocument(CoreSession documentManager,
            String subprojectId, String type, String name) throws ClientException {
        return findDocument(documentManager, subprojectId, type, name, false); ///TODO ??!!
    }
    
    @Override
    public DocumentModel findDocument(CoreSession documentManager,
            String subprojectId, String type, String name, boolean deepSearch) throws ClientException {
        
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT * FROM ? WHERE ");
        queryString.append(NXQL.ECM_NAME);
        queryString.append(" = '?' ");
        queryString.append(NXQLQueryHelper.buildSubprojectCriteria(documentManager,
        		SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, subprojectId), deepSearch));
        
        String query = NXQLQueryBuilder.getQuery(queryString.toString(),
                new Object[] { type, safeName(name) },
                false, true);
        DocumentModelList results = this.query(documentManager, query, true, false);
        return results.size() > 0 ? results.get(0) : null;
    }
    
    @Override
    public DocumentModel findSoaNode(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        return findSoaNode(documentManager, identifier, false); // TODO true not supported in discovery
        // for now, else allowing several SOA IDs (in different subproject / Phase) for a single node ;
        // at worse it should create an "inheriting" one 
    }
    
    @Override
    public DocumentModel findSoaNode(CoreSession documentManager, SoaNodeId identifier, boolean deepSearch) throws ClientException {
        
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT * FROM ? WHERE ");
        
        // checking SOA name :
        queryString.append(SoaNode.XPATH_SOANAME);
        queryString.append(" = '?' ");
        
        // checking subproject :
        queryString.append(NXQLQueryHelper.buildSubprojectCriteria(documentManager,
        		SubprojectServiceImpl.setDefaultSubprojectIfNone(documentManager, identifier), deepSearch));
        // NB. not using subproject path because doesn't work on versions
        
        // NB. not checking that it's below the Repository (in ex. REPOSITORY_PATH = /default-domain/MyProject/Default/Repository)
        // because costlier, so check it in RepositoryManagementListener when still putting it there !
        /// if (!resDoc.isVersion()) if (!resDoc.startsWith(RepositoryHelper.getRepositoryPath(documentManager, subprojectId)) return null;
        
        String query = NXQLQueryBuilder.getQuery(queryString.toString(),
                // TODO must be 
                new Object[] { identifier.getType()/*, Repository.REPOSITORY_PATH*/, identifier.getName() },
                false, true);
        DocumentModelList results = query(documentManager, query, true, false);
        return results.size() > 0 ? results.get(0) : null;
    }

    @Override
    public DocumentModelList findProxies(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + SoaNode.XPATH_SOANAME
                + " = '?' AND " + SubprojectNode.XPATH_SUBPROJECT + " = '?'",
                new Object[] { identifier.getType(), identifier.getName(),
                SubprojectServiceImpl.setDefaultSubprojectIfNone(documentManager, identifier) },
                false, true);
        return query(documentManager, query, false, true);
    }

    public DocumentModelList findProxies(CoreSession documentManager, DocumentModel model) throws ClientException {
        if (!model.isProxy()) {
            return documentManager.getProxies(model.getRef(), null);
        }
        else {
            return findProxies(documentManager, createSoaNodeId(model));
        }
    }
   
    @Override
    public DocumentModel findProxy(CoreSession documentManager, SoaNodeId identifier,
            String parentPath) throws ClientException {
        // Check parent
        PathRef parentRef = new PathRef(parentPath);
        DocumentModel parentModel = documentManager.getDocument(parentRef);
        if (parentModel.isProxy()) {
            parentModel = findSoaNode(documentManager, createSoaNodeId(parentModel));
        }
        
        // Find proxy among children
        DocumentModelList childrenModels = documentManager.getChildren(parentModel.getRef());
        for (DocumentModel childModel : childrenModels) {
            if (this.isSoaNode(documentManager, childModel.getType()) // else case of .doc in biz archi along BS
                    && createSoaNodeId(childModel).equals(identifier)) {
                return childModel;
            }
        }
        return null;
    }

    public DocumentModelList findAllInstances(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
    	if (identifier == null) {
    		return new DocumentModelListImpl();
    	}
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + SoaNode.XPATH_SOANAME
                + " = '?' AND " + SubprojectNode.XPATH_SUBPROJECT + " = '?'",
                new Object[] { identifier.getType(), identifier.getName(),
                SubprojectServiceImpl.setDefaultSubprojectIfNone(documentManager, identifier) },
                false, true);
        return query(documentManager, query, false, false);
    }
    
    public DocumentModelList query(CoreSession documentManager, String query,
    		boolean nonProxiesCriteria, boolean proxiesCriteria) throws ClientException {
    	String filteredQuery = query +
        		((nonProxiesCriteria) ? NQXL_NON_PROXIES_CRITERIA : "") + 
                ((proxiesCriteria) ? NQXL_PROXIES_CRITERIA : "") + 
        		NXQL_NO_DELETED_DOCUMENTS_CRITERIA;
    	if (!filteredQuery.contains("WHERE")) {
    		filteredQuery = filteredQuery.replaceFirst("AND", "WHERE");
    	}
        return documentManager.query(filteredQuery);
    }
    
    public DocumentModelList findAllInstances(CoreSession documentManager, DocumentModel model) throws ClientException {
		return findAllInstances(documentManager, createSoaNodeId(model));
    }
    
    public DocumentModelList findAllParents(CoreSession documentManager, DocumentModel documentModel) throws Exception {
        // Find working copy of document
        DocumentModel sourceDocument;
        if (documentModel.isProxy()) {
        	sourceDocument = documentManager.getSourceDocument(documentModel.getRef());
        }
        else {
        	sourceDocument = documentModel;
        }
    
        // Build proxies list
        DocumentModelList modelInstances = documentManager.getProxies(documentModel.getRef(), null);
        modelInstances.add(sourceDocument);
        
        // Fetch parents
        DocumentModelList parents = new DocumentModelListImpl();
        for (DocumentModel modelInstance : modelInstances) {
            DocumentModel parentDocument = documentManager.getParentDocument(modelInstance.getRef());
            if (parentDocument != null // happens if modelInstance is a version
            		&& !parents.contains(parentDocument)) {
                parents.add(parentDocument);
            }
        }
        return parents;
    }

    @Override
    public boolean hasChild(CoreSession documentManager, DocumentModel document,
            SoaNodeId childId) throws ClientException {
        if (document != null && childId != null) {
            DocumentModelList children = documentManager.getChildren(document.getRef(), childId.getType());
            for (DocumentModel child : children) {
                if (createSoaNodeId(child).equals(childId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSourceFolderPath(CoreSession documentManager,
            SoaNodeId soaNodeId) throws PropertyException, ClientException {
        return getSourceFolderPath(documentManager, soaNodeId.getSubprojectId(), soaNodeId.getType());
    }
    
    public String getSourceFolderPath(CoreSession documentManager,
            DocumentModel spNode) throws PropertyException, ClientException {
        String subprojectId = (String) spNode.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        String doctype = spNode.getType();
        return getSourceFolderPath(documentManager, subprojectId, doctype);
    }

    public String getSourceFolderPath(CoreSession documentManager,
            String subprojectId, String doctype) throws ClientException {
        String repositoryPath = RepositoryHelper.getRepositoryPath(documentManager, subprojectId);
        return getSourceFolderPathBelowSubprojectRepository(repositoryPath, doctype);
    }

    public String getSourceFolderPathBelowSubprojectRepository(
    		String subprojectRepositoryPath, String doctype) throws ClientException {
        return subprojectRepositoryPath + '/' + doctype;
    }
    
    public DocumentModelList getChildren(CoreSession session, DocumentRef parentRef, String type) throws ClientException {
        parentRef = session.getSourceDocument(parentRef).getRef(); // making sure it's not a proxy
        return session.getChildren(parentRef, type);
    }

    public DocumentModel getSourceFolder(CoreSession documentManager,
            DocumentModel spNode) throws ClientException {
        String subprojectId = (String) spNode.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
        return getSourceFolder(documentManager, subprojectId, spNode.getType());
    }

    public DocumentModel getSourceFolder(CoreSession documentManager,
            String subprojectId, String doctype) throws ClientException {
        PathRef sourceFolderRef = new PathRef(getSourceFolderPath(documentManager, subprojectId, doctype));
        // TODO not using PathRef ??
        if (documentManager.exists(sourceFolderRef)) {
            return documentManager.getDocument(sourceFolderRef);
        } else {
            DocumentModel subprojectRepository = RepositoryHelper.getRepository(documentManager, subprojectId);
            DocumentModel sourceFolderModel = documentManager.createDocumentModel(
                    subprojectRepository.getPathAsString(), doctype, IntelligentSystem.DOCTYPE);
            sourceFolderModel.setPropertyValue("dc:title", DocumentModelHelper.getDocumentTypeLabel(doctype) + "s");
            sourceFolderModel = documentManager.createDocument(sourceFolderModel);
            return sourceFolderModel;
        }
    }

    @Override
    public SoaNodeId createSoaNodeId(DocumentModel model) throws ClientException {
        try {
            return new SoaNodeId((String) model.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT),
                    model.getType(), (String) model.getPropertyValue(SoaNode.XPATH_SOANAME));
        }
        catch (PropertyNotFoundException e) {
            throw new ClientException("Invalid document type (" + model.getType() + "), an SoaNode is expected");
        }
    }

    @Override
    public List<SoaNodeId> createSoaNodeIds(DocumentModel... models) throws PropertyException, ClientException {
        List<SoaNodeId> soaNodeIds = new ArrayList<SoaNodeId>(models.length);
        for (DocumentModel model : models) {
            soaNodeIds.add(createSoaNodeId(model));
        }
        return soaNodeIds;
    }
    
    public boolean isSoaNode(CoreSession documentManager, String doctype) throws ClientException {
        return documentManager.getDocumentType(doctype).hasSchema(SoaNode.SCHEMA);
    }

	@Override
	public boolean isTypeOrSubtype(CoreSession documentManager,
			String doctypeToTest, String expectedDoctype)
			throws ClientException {
		/*if (doctypeToTest == null || expectedDoctype == null) {
			return false;
		}
		if (doctypeToTest.equals(expectedDoctype)) {
			return true;
		}
		DocumentType documentType = documentManager.getDocumentType(doctypeToTest);
		for (Type parentType : documentType.getTypeHierarchy()) {
			if (parentType.getName().equals(expectedDoctype)) {
				return true;
			}
		}
		return false;*/
		SoaMetamodelService soaMetamodelService;
		try {
			soaMetamodelService = Framework.getService(SoaMetamodelService.class);
		} catch (Exception e) {
			throw new ClientException("Unable to get SoaMetamodelService", e);
		}
		return soaMetamodelService.isAssignable(doctypeToTest, expectedDoctype);
	}

	/**
     * @obsolete actual code is in Service/EndpointMatchingService
	 * TODO test component-scoped request in actual scenario
	 * TODO impl multi component-scoped request 
	 */
	public DocumentModel findEndpoint(CoreSession documentManager,
			SoaNodeId identifier, Map<String, Object> properties,
			List<SoaNodeId> suggestedParentIds /*NOT USED*/, List<SoaNodeId> knownComponentIds)
					throws ClientException {
        
        ArrayList<Object> params = new ArrayList<Object>(5);
        params.add(identifier.getType());
        params.add(identifier.getName()); // TODO MDU and not safeName(identifier.getName()) !?
        StringBuffer querySbuf = new StringBuffer("SELECT * FROM ? WHERE "
        		+ SoaNode.XPATH_SOANAME + " = '?'"); // environment:url);
        
        // query context restricted to known components :
        if (knownComponentIds != null) {
	        for (SoaNodeId knownComponentId : knownComponentIds) {
	        	querySbuf.append(" AND ? IN soan:parentIds");
	        	params.add(knownComponentId);
	        }
        }
        
        // match extracted metas to service'as :
        Object portTypeName = properties.get(Endpoint.XPATH_WSDL_PORTTYPE_NAME);
        Object serviceName = properties.get(Endpoint.XPATH_WSDL_SERVICE_NAME);
        querySbuf.append(" AND " + InformationService.XPATH_WSDL_PORTTYPE_NAME + " = '?'");
		params.add(portTypeName != null ? portTypeName : "");
        querySbuf.append(" AND " + InformationService.XPATH_WSDL_SERVICE_NAME + " = '?'");
        params.add(serviceName != null ? serviceName : "");
        
		String query = NXQLQueryBuilder.getQuery(querySbuf.toString(), params.toArray(), false, true);
        DocumentModelList results = this.query(documentManager, query, true, false);
        DocumentModel documentModel = results.size() > 0 ? results.get(0) : null;
		return documentModel;
	}
	
	

	@Override
	public List<DocumentModel> getByType(CoreSession session, String type, String subprojectCriteria) throws ClientException {
        String query = DocumentService.NXQL_SELECT_FROM + type + subprojectCriteria;
        DocumentModelList services = this.query(session, query, true, false);
        return services;
	}
	@Override
	public DocumentModel getSoaNodeParent(DocumentModel soaNodeModel, String type) throws ClientException {
		Endpoint endpointAdapter = soaNodeModel.getAdapter(Endpoint.class);
		try {
			SoaNodeId parentSoaNodeId = endpointAdapter.getParentOfType(type);
			if (parentSoaNodeId != null) {
				return this.findSoaNode(soaNodeModel.getCoreSession(), parentSoaNodeId);
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
		return null;
	}
	@Override
	public List<DocumentModel> getSoaNodeChildren(DocumentModel soaNodeModel, String type) throws ClientException {
		if (soaNodeModel.isProxy()) {
			soaNodeModel = findSoaNode(soaNodeModel.getCoreSession(), this.createSoaNodeId(soaNodeModel));
		}
		return soaNodeModel.getCoreSession().getChildren(soaNodeModel.getRef(), type);
	}


	@Override
	public List<DocumentModel> getInformationServices(CoreSession session, String subprojectCriteria) throws ClientException {
        return getByType(session, InformationService.DOCTYPE, subprojectCriteria);
	}
	@Override
	public List<DocumentModel> getServiceImplementations(CoreSession session, String subprojectCriteria) throws ClientException {
        return getByType(session, ServiceImplementation.DOCTYPE, subprojectCriteria);
	}
	@Override
	public List<DocumentModel> getEndpoints(CoreSession session, String subprojectCriteria) throws ClientException {
        return getByType(session, Endpoint.DOCTYPE, subprojectCriteria);
	}

	@Override
	public DocumentModel getServiceImplementationFromEndpoint(DocumentModel endpointModel) throws ClientException {
		// TODO rather using implId on endpoint
        //List<DocumentModel> productionImplRes = docService.query(session, DocumentService.NXQL_SELECT_FROM
        //		+ ServiceImplementation.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND, false, true);
    	/*SoaMetamodelService soaMetamodelService;
		try {
			soaMetamodelService = Framework.getService(SoaMetamodelService.class);
			SoaNode productionEndpointNode = endpointModel.getAdapter(SoaNode.class);
	    	for (SoaNodeId productionEndpointParentNode : productionEndpointNode.getParentIds()) {
	    		if (soaMetamodelService.isAssignable(productionEndpointParentNode.getType(), ServiceImplementation.DOCTYPE) ) {
	    			return this.findSoaNode(endpointModel.getCoreSession(), productionEndpointParentNode);
	    		}
	    	}
		} catch (Exception e) {
			throw new RuntimeException("Can't get SoaMetamodelService", e);
		}*/
		return getSoaNodeParent(endpointModel, ServiceImplementation.DOCTYPE);
	}

	@Override
	public List<DocumentModel> getEndpointsOfService(DocumentModel service, String subprojectCriteria) throws ClientException {
		return this.query(service.getCoreSession(), DocumentService.NXQL_SELECT_FROM
            		+ Endpoint.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
                    + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + "='" + service.getId() + "'", true, false);
	}

	@Override
	public DocumentModel getEndpointOfService(DocumentModel service, String environment, String subprojectCriteria) throws ClientException {
        List<DocumentModel> endpoints = this.query(service.getCoreSession(), DocumentService.NXQL_SELECT_FROM
        		+ Endpoint.DOCTYPE + subprojectCriteria + DocumentService.NXQL_AND
                + ServiceImplementation.XPATH_PROVIDED_INFORMATION_SERVICE + "='" + service.getId() + "'"
                + DocumentService.NXQL_AND + Endpoint.XPATH_ENDP_ENVIRONMENT + "='Production'", true, false);
        if (!endpoints.isEmpty()) {
        	return endpoints.get(0);
        }
        return null;
	}

	@Override
	public List<String> getEnvironments(CoreSession session, String subprojectCriteria) throws ClientException {
        List<String> envs = new ArrayList<String>();
        
        // Get the environments
        DocumentModelList environments = this.query(session, "SELECT DISTINCT " + Endpoint.XPATH_ENDP_ENVIRONMENT
        		+ " FROM " + Endpoint.DOCTYPE + subprojectCriteria, true, false);
        // Fill the envs list
        for(DocumentModel model : environments){
            envs.add((String) model.getPropertyValue(Endpoint.XPATH_ENDP_ENVIRONMENT));
        }
        return envs;
	}

	@Override
	public List<DocumentModel> getComponents(CoreSession session, String subprojectCriteria) throws ClientException {
		return this.query(session, DocumentService.NXQL_SELECT_FROM
				+ Component.DOCTYPE + subprojectCriteria, true, false);
	}

	@Override
	public DocumentRef getParentInformationService(DocumentModel model) throws ClientException {
		// is it itself an iserv ?
    	/*SoaMetamodelService soaMetamodelService;
		try {
			soaMetamodelService = Framework.getService(SoaMetamodelService.class);
			if (soaMetamodelService.isAssignable(model.getType(), InformationService.DOCTYPE)) {
				return model.getRef();
			}
		} catch (Exception e) {
			throw new RuntimeException("Can't get SoaMetamodelService", e);
		}*/
		String iservId;
		try {
			// NB. this prop is defined in ServiceImplementationDataFacet on serviceimpl or endpoint
			iservId = (String) model.getPropertyValue(ServiceImplementationDataFacet.XPATH_PROVIDED_INFORMATION_SERVICE);
			if (iservId != null && iservId.length() != 0) {
				return new IdRef(iservId);	
			}
		} catch (PropertyException e) {
			// not a serviceimpl or endpoint
		}
    	return null;
	}

	@Override
	public DocumentModel getParentServiceImplementation(DocumentModel model) throws ClientException {
		// is it itself a serviceimpl ?
    	/*SoaMetamodelService soaMetamodelService;
		try {
			soaMetamodelService = Framework.getService(SoaMetamodelService.class);
			if (soaMetamodelService.isAssignable(model.getType(), ServiceImplementation.DOCTYPE)) {
				return model.getRef();
			}
		} catch (Exception e) {
			throw new RuntimeException("Can't get SoaMetamodelService", e);
		}*/
		return getSoaNodeParent(model, ServiceImplementation.DOCTYPE);
	}

}
