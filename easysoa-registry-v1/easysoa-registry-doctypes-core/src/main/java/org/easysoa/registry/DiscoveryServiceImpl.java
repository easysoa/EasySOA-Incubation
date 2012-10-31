package org.easysoa.registry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easysoa.registry.types.SoaNode;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Computes...
 * 
 * still TODO (see FIXMEs) :
 * if needed soaMetamodel performances
 * special cases (see FIXMEs)
 * 
 * @author mkalam-alami
 *
 */
public class DiscoveryServiceImpl implements DiscoveryService {

    public DocumentModel runDiscovery(CoreSession documentManager, SoaNodeId identifier,
            Map<String, Object> properties, List<SoaNodeId> parentDocuments) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        // Fetch or create document
        DocumentModel documentModel = documentService.find(documentManager, identifier);
        if (documentModel == null) {
            documentModel = documentService.create(documentManager, identifier);
        }
        
        // Set properties
        if (properties != null) {
            for (Entry<String, Object> property : properties.entrySet()) {
                // FIXME Non-serializable error handling
            	Object propertyValue = property.getValue();
            	if (propertyValue instanceof Boolean) {
            		propertyValue = propertyValue.toString();
            	}
                documentModel.setPropertyValue(property.getKey(), (Serializable) propertyValue);
            }
            documentManager.saveDocument(documentModel);
        }
        
        // Link to parent documents
        // FIXME cache / build model of soaMetamodelService responses to speed it up
        if (parentDocuments != null && !parentDocuments.isEmpty()) {
            String type = documentModel.getType();
            SoaMetamodelService soaMetamodelService = Framework.getService(SoaMetamodelService.class);
            for (SoaNodeId parentDocumentId : parentDocuments) {
                List<String> path = soaMetamodelService.getPath(parentDocumentId.getType(), type);
                String parentPathAsString = null;
                
                if (parentDocumentId.getType() != null) {
	                // Make sure parent is valid
	                if (path == null) {
	                    documentManager.cancel();
	                    throw new Exception("No possible valid path from "
	                            + parentDocumentId.getType() + " (" + parentDocumentId.toString()
	                            + ") to " + type + " (" + identifier.getName() + ")");
	                }
	                else {
		                DocumentModel parentDocument = documentService.find(documentManager, parentDocumentId);
		                
	                    // Create parent if necessary
	                    if (parentDocument == null) {
	                        parentDocument = documentService.create(documentManager, parentDocumentId);
	                    }
		                parentPathAsString = parentDocument.getPathAsString();
	                    
	                    // Link the intermediate documents 
	                    // If we have unknown documents between the two, create placeholders
	                    for (String pathStepType : path.subList(0, path.size() - 1)) {
	                        // Before creating a placeholder, check if the intermediate type
	                        // is not already listed in the parent documents
	                        boolean placeholderNeeded = true;
	                        for (SoaNodeId placeholderReplacementCandidate : parentDocuments) {
	                            if (pathStepType.equals(placeholderReplacementCandidate.getType())) {
	                                parentDocument = documentService.create(documentManager,
	                                        placeholderReplacementCandidate, parentPathAsString);
	        		                parentPathAsString = parentDocument.getPathAsString();
	                                placeholderNeeded = false;
	                                break;
	                            }
	                        }
	                        
	                        if (placeholderNeeded) {
	                            parentDocument = documentService.create(documentManager,
	                                    new SoaNodeId(pathStepType, IdUtils.generateStringId()), parentPathAsString);
	                            parentDocument.setPropertyValue(SoaNode.XPATH_TITLE, "(Placeholder)");
	                            parentDocument.setPropertyValue(SoaNode.XPATH_ISPLACEHOLDER, true);
	                        }
	                    }
	                }
                
                }
                else {
                	parentPathAsString = parentDocumentId.getName();
                }
                
                // Create target document if necessary
                if (documentService.findProxy(documentManager, identifier, parentPathAsString) == null) {
                    documentService.create(documentManager, identifier, parentPathAsString);
                }
            } 
        }
        
        return documentModel;
    }

}
