package org.easysoa.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.NuxeoListUtils;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.runtime.api.Framework;

/**
 * Computes...
 * 
 * still TODO (see FIXMEs) :
 * if needed soaMetamodel performances
 * special cases (see FIXMEs)
 * 
 * @author mkalam-alami, mdutoo
 *
 */
public class DiscoveryServiceImpl implements DiscoveryService {

    private static Logger logger = Logger.getLogger(DiscoveryServiceImpl.class);

    public DocumentModel runDiscovery(CoreSession documentManager, SoaNodeId identifier,
            Map<String, Object> properties, List<SoaNodeId> parentDocuments) throws Exception {
    	
        DocumentService documentService = Framework.getService(DocumentService.class);
        ServiceMatchingService serviceMatchingService = Framework.getService(ServiceMatchingService.class);
        EndpointMatchingService endpointMatchingService = Framework.getService(EndpointMatchingService.class);

        if (!documentService.isSoaNode(documentManager, identifier.getType())) {
        	throw new Exception("Can only discover a SoaNode but is " + identifier
        			+ " - " + properties + " - " + parentDocuments);
        }

        // find subproject (or create default one), from SOA node id or properties :
        String subproject = identifier.getSubprojectId();
        if ((subproject == null || subproject.trim().length() == 0) && properties != null) {
            subproject = (String) properties.get(SubprojectNode.XPATH_SUBPROJECT);
            identifier.setSubprojectId(subproject);
        } // else don't create properties, otherwise when called with null to create links
        // (EndpointMatchingService.linkServiceImplementation()), will be saved which triggers loop
        DocumentModel subprojectDocModel = SubprojectServiceImpl
                .getSubprojectOrCreateDefault(documentManager, subproject);
        if (subprojectDocModel == null) {
            throw new Exception("EasySOA DiscoveryService : can't find given subproject "
                    + subproject + " (create it first), aborting discovery of " + identifier);
        }
        
        // Fetch or create document
        DocumentModel newDocumentModel = null;
        DocumentModel foundDocumentModel = null;

        Map<String, Serializable> nuxeoProperties = toNuxeoProperties(properties);
        
        // matching first mode :
        // (for nodes that the probe does't control but wants to refer to, ex. InformationService in source disco)
        boolean matchFirst = identifier.getName() == null || identifier.getName().length() == 0
                || identifier.getName().startsWith("matchFirst:");
        int matchingFirstCandidateNb = 0;
        if (matchFirst) { // TODO extract to API // TODO or == null ??!?? (NOO)
            // try to find an existing one that matches on at least one exact prop

            // first, removing "implicit:" prefix (else newDocumentModel, which may be created if none found, will have it)
            identifier = new SoaNodeId(identifier.getSubprojectId(), identifier.getType(),
                    identifier.getName().substring(11));
            
            // create temporary model as a support to matching :
            newDocumentModel = documentService.newSoaNodeDocument(documentManager, identifier, nuxeoProperties);
            SubprojectServiceImpl.copySubprojectNodeProperties(subprojectDocModel, newDocumentModel);
            
            // finding existing through maching :
            DocumentModelList matchingFirstResults = null;
            String filterComponentId = null; //TODO ??
            if (documentService.isTypeOrSubtype(documentManager, identifier.getType(), InformationService.DOCTYPE)) {
                matchingFirstResults = serviceMatchingService.findInformationServices(documentManager,
                        newDocumentModel, filterComponentId, false, true);
            } else if (documentService.isTypeOrSubtype(documentManager, identifier.getType(), ServiceImplementation.DOCTYPE)) {
                matchingFirstResults = endpointMatchingService.findServiceImplementations(documentManager,
                        newDocumentModel, filterComponentId, false, true);
            }
            
            matchingFirstCandidateNb = matchingFirstResults.size();
            if (matchingFirstCandidateNb == 1) {
                DocumentModel matchingSoaNode = matchingFirstResults.get(0);
                identifier = documentService.createSoaNodeId(matchingSoaNode);
                foundDocumentModel = matchingSoaNode;
            }
        }
        
        if (foundDocumentModel == null) { // not matchFirst or no (or too many) match found
            // finding existing using SOA node ID :
            foundDocumentModel = documentService.findSoanode(documentManager, identifier);
        }
        
        if (foundDocumentModel != null && foundDocumentModel.isVersion()) {
            // exists but is readonly version : do nothing (else triggers SQLDocumentVersion$VersionNotModifiableException)
            // TODO LATER maybe find a way to still provide info, such as adding a "code-level service layer" between iserv & serviceimpl ??
            return foundDocumentModel;
        }
        
        if (matchingFirstCandidateNb > 1) {
        	// don't know which one but at least there are candidates
        	// therefore don't create it, else impl wouldn't know which one to match
        	return null;
        }
        
        DocumentModel documentModel = null;
        
        documentModel = createOrUpdate(documentManager, documentService,
                foundDocumentModel, newDocumentModel, identifier, nuxeoProperties, matchFirst);
        
        linkToParentDocuments(documentManager, documentService, identifier, parentDocuments);

        // TODO also link to other known model elements, especially impl->IS ex. in source disco
        
        return documentModel;
    }

    /**
     * 
     * @param documentManager
     * @param documentService
     * @param foundDocumentModel
     * @param newDocumentModel
     * @param identifier
     * @param nuxeoProperties
     * @param dontOverrideProperties
     * @return
     * @throws Exception
     */
    public static DocumentModel createOrUpdate(CoreSession documentManager, DocumentService documentService,
            DocumentModel foundDocumentModel, DocumentModel newDocumentModel, SoaNodeId identifier,
            Map<String, Serializable> nuxeoProperties, boolean dontOverrideProperties) throws Exception {

        if (foundDocumentModel == null) {
            if (newDocumentModel == null) {
                newDocumentModel = documentService.newSoaNodeDocument(documentManager, identifier, nuxeoProperties);
            }
            // only now that props are OK, create or save document
            // (required below for handling parents by creating proxies to it)
            return documentManager.createDocument(newDocumentModel);
        }
        else {
        
            SoaMetamodelService metamodelService = Framework.getService(SoaMetamodelService.class);
            metamodelService.validateWriteRightsOnProperties(foundDocumentModel, nuxeoProperties);

            boolean changed = setNuxeoPropertiesIfChanged(foundDocumentModel, nuxeoProperties, dontOverrideProperties);
            
            if (changed) { // TODO or isDirty ?!
                if (!foundDocumentModel.isDirty()) {
                    logger.error("DiscoveryServiceImpl : SAVING CHANGED BUT NOT DIRTY " + foundDocumentModel);
                }
                // only now that props are OK, create or save document
                // (required below for handling parents by creating proxies to it)
                
                // don't save if properties null, else triggers event loop with matching
                // (findAndMatchServiceImplementation, linkInformationServiceThroughPlaceholder,
                // linkServiceImplementation, runDiscovery)
                foundDocumentModel = documentManager.saveDocument(foundDocumentModel);
            }
            return foundDocumentModel;
        }
    }

    public static boolean setNuxeoPropertiesIfChanged(DocumentModel documentModel,
            Map<String, Serializable> nuxeoProperties,
            boolean dontOverrideProperties) throws PropertyException, ClientException {
        if (nuxeoProperties == null) {
            return false;
        }
        
        boolean changed = false;
        for (Entry<String, Serializable> nuxeoProperty : nuxeoProperties.entrySet()) {
            String propertyKey = nuxeoProperty.getKey();
            Serializable propertyValue = nuxeoProperty.getValue();
            changed = setPropertyIfChanged(documentModel, propertyKey,
                    (Serializable) propertyValue, dontOverrideProperties) || changed; // TODO or isDirty ?!
        }
        return changed;
    }

    public static boolean setPropertiesIfChanged(DocumentModel documentModel, Map<String, Object> properties,
            boolean dontOverrideProperties) throws PropertyException, ClientException {
        if (properties == null) {
            return false;
        }
        
        boolean changed = false;
        for (Entry<String, Object> property : properties.entrySet()) {
            String propertyKey = property.getKey();
            Object propertyValue = property.getValue();
            // FIXME Non-serializable error handling
            changed = setPropertyIfChanged(documentModel, propertyKey,
                    (Serializable) propertyValue, dontOverrideProperties) || changed; // TODO or isDirty ?!
        }
        return changed;
    }

    private static boolean setPropertyIfChanged(DocumentModel documentModel,
            String propertyKey, Serializable propertyValue, boolean dontOverrideProperties)
                    throws PropertyException, ClientException {
        Serializable docPropertyValue = documentModel.getPropertyValue(propertyKey);
        if (dontOverrideProperties && docPropertyValue != null
            // in dontOverrideProperties (ex.matchFirst mode), only set if doesn't exist yet
            || propertyEquals(docPropertyValue, propertyValue)) { // don't set if the same
            return false;
        }
        documentModel.setPropertyValue(propertyKey, (Serializable) propertyValue);
        return true;
    }

    /**
     * WARNING may be costly !
     * Both must be in their Nuxeo better form (ex. String[] and not List...)
     * TODO better handle all cases of complex properties : types, depth, lists...
     * @param docPropertyValue
     * @param propertyValue
     * @return
     */
    private static boolean propertyEquals(Serializable docPropertyValue, Object propertyValue) {
        if (docPropertyValue == null && propertyValue == null) {
            return true;
        }
        if (docPropertyValue != null) {
            if (docPropertyValue instanceof String[]) {
                // FIXME better handle all cases of complex properties : types, depth, lists, maps (for operations)...
                if (propertyValue instanceof String[]) {
                    return Arrays.deepEquals((String[]) docPropertyValue, (String[]) propertyValue);
                } else if (propertyValue instanceof List) {
                    return Arrays.deepEquals((String[]) docPropertyValue, ((List<?>) propertyValue).toArray(NuxeoListUtils.EMPTY_STRING_ARRAY));
                } else {
                    return false;//TODO
                }
            } else {//TODO
                return docPropertyValue.equals(propertyValue);
            }
        }
        return false;
    }

    /**
     * Converts (so values stored in Nuxeo will be well typed) :
     * * Boolean to String
     * * List to String[]
     * @param properties
     * @return
     */
    private Map<String, Serializable> toNuxeoProperties(Map<String, Object> properties) {
        if (properties == null) {
            return null;
        }
        Map<String, Serializable> nuxeoProperties = new HashMap<String, Serializable>(properties.size());
        for (Entry<String, Object> property : properties.entrySet()) {
            // FIXME Non-serializable error handling
            Object propertyValue = property.getValue();
            if (propertyValue instanceof Boolean) {
                propertyValue = propertyValue.toString();
            }/* else if (propertyValue instanceof List) {
                propertyValue = ((List<?>) propertyValue).toArray(NuxeoListUtils.EMPTY_STRING_ARRAY);
                // TODO also non-String properties
            }*///TODO NOO rather array to list comparison in propertyEquals()
            nuxeoProperties.put(property.getKey(), (Serializable) propertyValue);
        }
        return nuxeoProperties;
    }

    private void linkToParentDocuments(CoreSession documentManager, DocumentService documentService,
            SoaNodeId identifier, List<SoaNodeId> parentDocuments) throws Exception {
        // FIXME cache / build model of soaMetamodelService responses to speed it up
        if (parentDocuments != null && !parentDocuments.isEmpty()) {
            String type = identifier.getType();
            SoaMetamodelService soaMetamodelService = Framework.getService(SoaMetamodelService.class);
            for (SoaNodeId parentDocumentId : parentDocuments) {
                if (parentDocumentId.getSubprojectId() == null) {
                    // if no subproject, put in same as child
                    parentDocumentId.setSubprojectId(identifier.getSubprojectId());
                } // else TODO soaMetamodelService supporting across subprojects
                List<String> pathBelowParent = soaMetamodelService.getPath(parentDocumentId.getType(), type);
                String parentPathAsString = null;
                
                if (parentDocumentId.getType() != null) {
                    // Make sure parent is valid
                    if (pathBelowParent == null) {
                        documentManager.cancel();
                        throw new Exception("No possible valid path from "
                                + parentDocumentId.getType() + " (" + parentDocumentId.toString()
                                + ") to " + type + " (" + identifier.getName() + ")");
                    }
                    else {
                        parentPathAsString = createOrReuseIntermediateDocuments(documentManager,
                                documentService, parentDocumentId, pathBelowParent, parentDocuments);
                        if (parentPathAsString == null) {
                            // parent document is version, so skip (because can't create anything in it)
                            // TODO LATER redo parents through a reverse link allowing this case
                        }
                    }
                
                }
                else { // TODO when does it occur ??
                    parentPathAsString = parentDocumentId.getName();
                }
                
                // Create target document below parent if necessary
                if (documentService.findProxy(documentManager, identifier, parentPathAsString) == null) {
                    documentService.create(documentManager, identifier, parentPathAsString);
                }
            }
        }
    }

    
    /**
     * 
     * @param documentManager
     * @param documentService
     * @param parentDocumentId
     * @param pathBelowParent
     * @param parentDocuments
     * @return null if parent document (with parentDocumentId) is a version (because then can't create anything below)
     * @throws ClientException
     */
    private String createOrReuseIntermediateDocuments(CoreSession documentManager, DocumentService documentService,
	        SoaNodeId parentDocumentId, List<String> pathBelowParent,
	        List<SoaNodeId> parentDocuments) throws ClientException {
        DocumentModel parentDocument = documentService.findSoanode(documentManager, parentDocumentId);
        
        // Create parent if necessary
        if (parentDocument == null) {
            parentDocument = documentService.create(documentManager, parentDocumentId);
            // NB. don't do a documentService.create() here, else triggers documentCreated event, even though
            // properties have not been set yet
            //parentDocument = documentService.newSoaNodeDocument(documentManager, parentDocumentId);
        }
        
        if (parentDocument.isVersion()) {
            // parent is readonly version : can't create path folders or tagging proxy, abort
            return null;
        }
        
        // Link the intermediate documents 
        // If we have unknown documents between the two, create placeholders
        for (String pathStepType : pathBelowParent.subList(0, pathBelowParent.size() - 1)) {
            // Before creating a placeholder, check if the intermediate type
            // is not already listed in the parent documents
            boolean placeholderNeeded = true;
            for (SoaNodeId placeholderReplacementCandidate : parentDocuments) {
                if (pathStepType.equals(placeholderReplacementCandidate.getType())) {
                    parentDocument = documentService.create(documentManager,
                            placeholderReplacementCandidate, parentDocument.getPathAsString());
                    placeholderNeeded = false;
                    break;
                }
            }
            
            if (placeholderNeeded) {
                parentDocument = documentService.create(documentManager,
                        new SoaNodeId(parentDocumentId.getSubprojectId(), pathStepType, IdUtils.generateStringId()),
                        parentDocument.getPathAsString());
                parentDocument.setPropertyValue(SoaNode.XPATH_TITLE, "(Placeholder)");
                parentDocument.setPropertyValue(SoaNode.XPATH_ISPLACEHOLDER, "1");
            }
        }
        return parentDocument.getPathAsString();
    }
    

    /**
     * Code copied from ServiceMatchingServiceImpl etc.
     * TODO merge with (Service/Endpoint)MatchingServiceImpl code
     * 
     * @param documentManager
     * @param documentService
     * @param serviceMatchingService
     * @param endpointMatchingService
     * @param identifier
     * @param properties
     * @param subprojectDocModel 
     * @return
     * @throws ClientException
     */
    private DocumentModel findMatching(CoreSession documentManager, DocumentService documentService,
            ServiceMatchingService serviceMatchingService, EndpointMatchingService endpointMatchingService,
            SoaNodeId identifier, Map<String, Object> properties, DocumentModel subprojectDocModel) throws ClientException {
        boolean anyExactCriteria = false;
        MatchingQuery query = null;
        
        if (documentService.isTypeOrSubtype(documentManager, identifier.getType(), InformationService.DOCTYPE)) {
            query = new MatchingQuery("SELECT * FROM " + InformationService.DOCTYPE);
            
            if (properties.get(WsdlInfoFacet.XPATH_WSDL_PORTTYPE_NAME) != null) { // MatchingHelper.isWsdlInfo(documentModel)
                //query.addCriteria("ecm:mixinType = '" + InformationService.FACET_WSDLINFO + "'"); // not required unless added dynamically but hard to do in DiscoveryServiceImpl
                //query.addCriteria(Platform.XPATH_SERVICE_LANGUAGE , Platform.SERVICE_LANGUAGE_JAXWS); // TODO required ?
                    // NO not necessarily JAXWS ! .NET, HTTP mock or no requirement at all would be OK
                String implPortTypeName = (String) properties.get(WsdlInfoFacet.XPATH_WSDL_PORTTYPE_NAME);
                query.addCriteria(InformationService.XPATH_WSDL_PORTTYPE_NAME, implPortTypeName); anyExactCriteria = true;
                    // NB. NB. exact match (else useless because too wide), is set since is WS(DL)
            }
            // else TODO if REST...
            
            // TODO also platform criteria...
        }
        
        // if impl, endpoint...
        
        if (query == null) {
            return null;
        }
        // Filter by subproject
        query.addCriteria(SubprojectServiceImpl.buildCriteriaSeesSubproject(subprojectDocModel)); // NB. multivalued prop

        // Filter by component
        // TODO component also as parent ?!?!??
        //filterComponentId = MatchingHelper.appendComponentFilterToQuery(documentManager, query, null, impl);
        String filterComponentId = (String) properties.get(Endpoint.XPATH_COMPONENT_ID);
        if (filterComponentId != null) {
            query.addCriteria(Component.XPATH_COMPONENT_ID + " = '" + filterComponentId + "'");
            anyExactCriteria = anyExactCriteria || filterComponentId != null;
        }

        if (/*requireAtLeastOneExactCriteria && */!anyExactCriteria) {
            return null;//return EmptyDocumentModelList.INSTANCE;
        }
        
        String infoServiceQuery = query.build();
        DocumentModelList foundInfoServices = documentService.query(documentManager,
                infoServiceQuery, true, false);
        //return foundInfoServices;
        if (foundInfoServices.size() == 1) {
            return foundInfoServices.get(0);
        }
        return null;
    }

	
    /**
     * @obsolete
     * @param documentManager
     * @param identifier
     * @param properties
     * @param parentDocuments
     * @return
     * @throws Exception
     */
	private DocumentModel discoverEndpoint(CoreSession documentManager,
			SoaNodeId identifier, Map<String, Object> properties,
			List<SoaNodeId> parentDocuments) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);

		// TODO differ between :
        List<SoaNodeId> suggestedParentIds = new ArrayList<SoaNodeId>(); // better : those that don't exist
        List<SoaNodeId> knownComponentIds = parentDocuments; // better : those that exist
        // TODO check that all component ids exist
        
        // Fetch or create document
        DocumentModel documentModel = documentService.findEndpoint(documentManager, identifier,
        		properties, suggestedParentIds, knownComponentIds);

        if (documentModel == null) {
            documentModel = documentService.create(documentManager, identifier);
            // TODO create suggestedParentIds if don't exist
            // TODO link with suggestedParentIds
            
        } else {
        	// TODO create assumedParentIds if don't exist
        }
        
        return documentModel;
	}

}
