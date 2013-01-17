package org.easysoa.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
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

    public DocumentModel runDiscovery(CoreSession documentManager, SoaNodeId identifier,
            Map<String, Object> properties, List<SoaNodeId> parentDocuments) throws Exception {
    	
        DocumentService documentService = Framework.getService(DocumentService.class);
        ServiceMatchingService serviceMatchingService = Framework.getService(ServiceMatchingService.class);
        EndpointMatchingService endpointMatchingService = Framework.getService(EndpointMatchingService.class);

        if (!documentService.isSoaNode(documentManager, identifier.getType())) {
        	throw new Exception("Can only discover a SoaNode but is " + identifier
        			+ " - " + properties + " - " + parentDocuments);
        }

        // SUBPROJECT :
        // find subproject (or create default one), from SOA node id or properties :
        String subproject = null;
        if ((subproject == null || subproject.length() == 0) && properties != null) {
            subproject = (String) properties.get(SubprojectNode.XPATH_SUBPROJECT);
            identifier.setSubprojectId(subproject);
        } // else don't create properties, otherwise when called with null to create links
        // (EndpointMatchingService.linkServiceImplementation()), will be saved which triggers loop
        DocumentModel subprojectDocModel = SubprojectServiceImpl
                .getSubprojectOrCreateDefault(documentManager, subproject);
        //SubprojectServiceImpl.getSubprojectIdOrCreateDefault(documentManager, identifier.getSubprojectId())// TODO rather
        
        // complete disco'd SOA node by subproject infos
        ///properties.put(SubprojectNode.XPATH_PARENT_SUBPROJECTS, subprojectDocModel.getPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS));
        //properties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS, subprojectDocModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS)); // TODO or recompute ??
        /*DocumentModel tmpDocumentModel = documentManager.createDocumentModel(identifier.getType()); NOO
        tmpDocumentModel.addFacet(SubprojectNode.FACET);
        tmpDocumentModel.setProperties("soanode", properties); // TODO
        tmpDocumentModel.setProperties("dublincore", properties);
        tmpDocumentModel.setProperties("informationservice", properties);
        tmpDocumentModel.setProperties("platform", properties);
        tmpDocumentModel.setProperties("architecturecomponent", properties);
        tmpDocumentModel.setProperties("wsdlinfo", properties);*/
        // TODO compute visibleSubprojectIds, elsewhere
        // - SUBPROJECT
        
        // Fetch or create document
        DocumentModel documentModel = null;
        
        // matching first mode :
        // (for nodes that the probe does't control but wants to refer to, ex. InformationService in source disco)
        boolean matchFirst = identifier.getName() == null || identifier.getName().length() == 0
                || identifier.getName().startsWith("matchFirst:");
        if (matchFirst) { // TODO extract to API // TODO or == null ??!?? (NOO)
            // try to find an existing one that matches on at least one exact prop
            DocumentModel matchingSoaNode = findMatching(documentManager, documentService,
                    serviceMatchingService, endpointMatchingService, identifier, properties);
            // FIXME merge with *MatchingService
            /*String filterComponentId = null;
            DocumentModelList matchingFirstResults = serviceMatchingService.findInformationServices(documentManager,
                    tmpDocumentModel, filterComponentId, false, true);
            DocumentModel matchingSoaNode = null;
            if (matchingFirstResults.size() == 1) {
                matchingSoaNode = matchingFirstResults.get(0);
            }*/
            
            if (matchingSoaNode != null) { // TODO documentService ?
                identifier = documentService.createSoaNodeId(matchingSoaNode);
                documentModel = matchingSoaNode;
            } else {
                // removing "implicit:" prefix
                identifier = new SoaNodeId(identifier.getSubprojectId(), identifier.getType(),
                        identifier.getName().substring(11));
            }
        }

        Map<String, Serializable> nuxeoProperties = toNuxeoProperties(properties);
        
        // SUBPROJECT :
        if (nuxeoProperties == null) {
            // for subproject props
            // TODO may trigger event loop, try better for setting subproject props:
            // only if incomplete or change, in event listener...
            nuxeoProperties =  new HashMap<String, Serializable>(3);
        }
        nuxeoProperties.put(SubprojectNode.XPATH_SUBPROJECT, subprojectDocModel.getPropertyValue(Subproject.XPATH_SUBPROJECT));
        nuxeoProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS, subprojectDocModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS)); // TODO or recompute ??
        nuxeoProperties.put(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, subprojectDocModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV)); // TODO or recompute ??
        // - SUBPROJECT
        
        documentModel = createOrUpdate(documentManager, documentService,
                documentModel, identifier, nuxeoProperties, matchFirst);
        
        linkToParentDocuments(documentManager, documentService, identifier, parentDocuments);

        // TODO also link to other known model elements, especially impl->IS ex. in source disco
        
        return documentModel;
    }

    public static DocumentModel createOrUpdate(CoreSession documentManager, DocumentService documentService,
            DocumentModel documentModel, SoaNodeId identifier,
            Map<String, Serializable> nuxeoProperties, boolean dontOverrideProperties) throws Exception {
        if (documentModel == null) {
            documentModel = documentService.find(documentManager, identifier);
        }
        
        // TODO subproject: new param ? prefix soaname ? map to subproject strategies ?? SubprojectDocumentService ?
        // TODO subproject: computeVisibleSubprojects here ? in listener ?
        boolean shouldCreate = false;
        if (documentModel == null) {
            shouldCreate = true;
            documentModel = documentService.newSoaNodeDocument(documentManager, identifier);
        }
        else {
            SoaMetamodelService metamodelService = Framework.getService(SoaMetamodelService.class);
            metamodelService.validateWriteRightsOnProperties(documentModel, nuxeoProperties);
        }
        
        // Set properties
        boolean changed = false;
        if (nuxeoProperties != null) {
            for (Entry<String, Serializable> nuxeoProperty : nuxeoProperties.entrySet()) {
                // FIXME Non-serializable error handling
                String propertyKey = nuxeoProperty.getKey();
                Serializable propertyValue = nuxeoProperty.getValue();
                Serializable docPropertyValue = documentModel.getPropertyValue(propertyKey);
                if (dontOverrideProperties && docPropertyValue != null
                    // in dontOverrideProperties (ex.matchFirst mode), only set if doesn't exist yet
                    || propertyEquals(docPropertyValue, propertyValue)) { // don't set if the same
                    continue;
                }
                documentModel.setPropertyValue(propertyKey, propertyValue);
                changed = true;
            }
        }
        
        // only now that props are OK, create or save document
        // (required below for handling parents by creating proxies to it)
        if (shouldCreate) {
            documentModel = documentManager.createDocument(documentModel);
        } else if (changed) {
            // don't save if properties null, else triggers event loop with matching
            // (findAndMatchServiceImplementation, linkInformationServiceThroughPlaceholder,
            // linkServiceImplementation, runDiscovery)
            documentModel = documentManager.saveDocument(documentModel);
        }
        return documentModel;
    }

    /**
     * WARNING may be costly !
     * TODO better handle all cases of complex properties : types, depth, lists...
     * @param docPropertyValue
     * @param propertyValue
     * @return
     */
    private static boolean propertyEquals(Serializable docPropertyValue, Serializable propertyValue) {
        if (docPropertyValue == null && propertyValue == null) {
            return true;
        }
        if (docPropertyValue != null) {
            if (docPropertyValue instanceof String[]) {
                // FIXME better handle all cases of complex properties : types, depth, lists...
                return Arrays.deepEquals((String[]) docPropertyValue, (String[]) propertyValue);
            } else {
                return docPropertyValue.equals(propertyValue);
            }
        }
        return false;
    }

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
            }
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

    private String createOrReuseIntermediateDocuments(CoreSession documentManager, DocumentService documentService,
	        SoaNodeId parentDocumentId, List<String> pathBelowParent,
	        List<SoaNodeId> parentDocuments) throws ClientException {
        DocumentModel parentDocument = documentService.find(documentManager, parentDocumentId);
        
        // Create parent if necessary
        if (parentDocument == null) {
            parentDocument = documentService.create(documentManager, parentDocumentId);
            // NB. don't do a documentService.create() here, else triggers documentCreated event, even though
            // properties have not been set yet
            //parentDocument = documentService.newSoaNodeDocument(documentManager, parentDocumentId);
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
     * @return
     * @throws ClientException
     */
    private DocumentModel findMatching(CoreSession documentManager, DocumentService documentService,
            ServiceMatchingService serviceMatchingService, EndpointMatchingService endpointMatchingService,
            SoaNodeId identifier, Map<String, Object> properties) throws ClientException {
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

        // SUBPROJECT :        
        // implReferredSubProjectIds = "AXXXSpecifications"; // or in 2 pass & get it from subProject ?? 
        //String implSubProjectId = "AXXXRealisation";
        String implVisibleSubprojectIds = (String) properties.get(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV); // "AXXXSpecifications"; // or in 2 pass & get it from subProject ?? 
        //String implSubProjectId = // "AXXXRealisation";

        // Filter by subproject
        if (properties.get(SubprojectNode.XPATH_SUBPROJECT) != null) { // TODO remove ; only to allow still to work as usual
            if (implVisibleSubprojectIds == null) {
                throw new ClientException("visibleSubprojects should not be null on " + identifier
                        + " " + properties);
            }
            query.addCriteria(SubprojectNode.XPATH_SUBPROJECT + " IN (" + implVisibleSubprojectIds + ")");
        }
        // - SUBPROJECT

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
