package org.easysoa.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.matching.MatchingHelper;
import org.easysoa.registry.matching.MatchingQuery;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.EmptyDocumentModelList;
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

    	// FIXME
//        if (Endpoint.DOCTYPE.equals(identifier.getType())) { // TODO MDU hack
//        	return discoverEndpoint(documentManager, identifier, properties, parentDocuments);
//        }
    	
    	SoaMetamodelService metamodelService = Framework.getService(SoaMetamodelService.class);
        DocumentService documentService = Framework.getService(DocumentService.class);
        ServiceMatchingService serviceMatchingService = Framework.getService(ServiceMatchingService.class);
        EndpointMatchingService endpointMatchingService = Framework.getService(EndpointMatchingService.class);

        if (!documentService.isSoaNode(documentManager, identifier.getType())) {
        	throw new Exception("Can only discover a SoaNode but is " + identifier
        			+ " - " + properties + " - " + parentDocuments);
        }
        
        // Fetch or create document
        boolean shouldCreate = false;
        DocumentModel documentModel = null;
        boolean matchFirst = identifier.getName() == null || identifier.getName().length() == 0
                || identifier.getName().startsWith("matchFirst:");
        if (matchFirst) { // TODO extract to API // TODO or == null ??!?? (NOO)
            // try to find an existing one that matches on at least one exact prop
            DocumentModel matchingSoaNode = findMatching(documentManager, documentService,
                    serviceMatchingService, endpointMatchingService, identifier, properties);
            if (matchingSoaNode != null) { // TODO documentService ?
                identifier = documentService.createSoaNodeId(matchingSoaNode);
                documentModel = matchingSoaNode;
            } else {
                // removing "implicit:"
                identifier = new SoaNodeId(identifier.getType(), identifier.getName().substring(11));
            }
        }
        if (documentModel == null) {
            documentModel = documentService.find(documentManager, identifier);
        }
        // TODO subproject: new param ? prefix soaname ? map to subproject strategies ?? SubprojectDocumentService ?
        // TODO subproject: computeVisibleSubprojects here ? in listener ?
        if (documentModel == null) {
            shouldCreate = true;
            documentModel = documentService.newSoaNodeDocument(documentManager, identifier);
        }
        else {
            metamodelService.validateWriteRightsOnProperties(documentModel, properties);
        }
        
        // Set properties
        if (properties != null) {
            for (Entry<String, Object> property : properties.entrySet()) {
                // FIXME Non-serializable error handling
            	Object propertyValue = property.getValue();
            	if (propertyValue instanceof Boolean) {
            		propertyValue = propertyValue.toString();
            	}
            	String key = property.getKey();
            	if (!matchFirst || documentModel.getPropertyValue(key) == null
            	        || documentModel.getPropertyValue(key) == null) { // in matchFirst mode, only set if doesn't exist yet
            	    documentModel.setPropertyValue(key, (Serializable) propertyValue);
            	}
            }
            //documentManager.saveDocument(documentModel); // TODO mdu ?!
        }
        
        // only now that props are OK, create or save document
        // (required below for handling parents by creating proxies to it)
        if (shouldCreate) {
        	documentModel = documentManager.createDocument(documentModel);
        } else if (properties != null && !properties.isEmpty()) {
        	documentModel = documentManager.saveDocument(documentModel);
        }
        
        // Link to parent documents
        // FIXME cache / build model of soaMetamodelService responses to speed it up
        if (parentDocuments != null && !parentDocuments.isEmpty()) {
            String type = documentModel.getType();
            SoaMetamodelService soaMetamodelService = Framework.getService(SoaMetamodelService.class);
            for (SoaNodeId parentDocumentId : parentDocuments) {
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
        
        return documentModel;
    }

    private String createOrReuseIntermediateDocuments(CoreSession documentManager, DocumentService documentService,
	        SoaNodeId parentDocumentId, List<String> pathBelowParent,
	        List<SoaNodeId> parentDocuments) throws ClientException {
        DocumentModel parentDocument = documentService.find(documentManager, parentDocumentId);
        
        // Create parent if necessary
        if (parentDocument == null) {
            parentDocument = documentService.create(documentManager, parentDocumentId);
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
                        new SoaNodeId(pathStepType, IdUtils.generateStringId()), parentDocument.getPathAsString());
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
        String implVisibleSubprojectIds = (String) properties.get(SoaNode.XPATH_VISIBLE_SUBPROJECTS); // "AXXXSpecifications"; // or in 2 pass & get it from subProject ?? 
        //String implSubProjectId = // "AXXXRealisation";

        // Filter by subproject
        if (properties.get(SoaNode.XPATH_SUBPROJECT) != null) { // TODO remove ; only to allow still to work as usual
            if (implVisibleSubprojectIds == null) {
                throw new ClientException("visibleSubprojects should not be null on " + identifier
                        + " " + properties);
            }
            query.addCriteria(SoaNode.XPATH_SUBPROJECT + " IN (" + implVisibleSubprojectIds + ")");
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
