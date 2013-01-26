package org.easysoa.registry.subproject;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;


/**
 * Listens to aboutToCreate events
 * TODO elsewhere handle documentCreatedByCopy, aboutToMove...
 * 
 * @author mdutoo
 *
 */
public class SubprojectNodeListener implements EventListener {

    private static Logger logger = Logger.getLogger(SubprojectNodeListener.class);
    
    @Override
    public void handleEvent(Event event) throws ClientException {
    	
        // Ensure event nature
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        CoreSession documentManager = documentContext.getCoreSession();
        
        // TODO check isSoaNode() ??
        
        ///DocumentModel previousDocumentModel = (DocumentModel) documentContext.getProperty("previousDocumentModel");
        // TODO check matching properties changes, in order to match only if they changed ??
        // TODO compute spnode props if subproject changed ??? NO would require to save (which would trigger event loop)
        
        //match(documentManager, sourceDocument);
        
        if (sourceDocument.isProxy()) {
            logger.debug("SubprojectNodeListener : skipping because proxy " + sourceDocument);
            return;
        }
        
        if (sourceDocument.hasSchema(Subproject.SCHEMA)) {
            // TODO NOO needs to be created first to get id
            if (DocumentEventTypes.ABOUT_TO_CREATE.equals(event.getName())) {
                SubprojectServiceImpl.onSubprojectAboutToCreate(documentManager, sourceDocument);
                
            } else { // DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName())
                DocumentModel subproject = sourceDocument;
                DocumentModel previousSubproject = (DocumentModel) context.getProperty("previousDocumentModel");
                
                String subprojectId = (String) subproject.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
                String previousSubprojectId = (String) previousSubproject.getPropertyValue(SubprojectNode.XPATH_SUBPROJECT);
                boolean subprojectIdChanged = !subprojectId.equals(previousSubprojectId);
                if (subprojectIdChanged) {
                    // case 1 : changing subprojectId before or after tree snapshot versioning
                    // case 2 : TODO check if rename changes path
                    logger.warn("Changing subprojectId from  " + previousSubprojectId
                            + " to " + subprojectId + " on " + previousSubproject);
                }
                
                String[] parentSubprojects = (String[]) subproject.getPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS);
                String[] previousParentSubprojects = (String[]) previousSubproject.getPropertyValue(Subproject.XPATH_PARENT_SUBPROJECTS);
                
                boolean parentSubprojectsChanged = parentSubprojects == null && previousParentSubprojects != null
                        || parentSubprojects != null && previousParentSubprojects == null
                        || parentSubprojects != null && previousParentSubprojects != null
                        && !Arrays.asList(previousParentSubprojects).equals(Arrays.asList(previousParentSubprojects));
                if (subprojectIdChanged || parentSubprojectsChanged) {
                    // parentSubprojects changed
                    // update computed metas :
                    SubprojectServiceImpl.computeAndSetVisibleSubprojects(documentManager, sourceDocument);
                    // recursively set them on subnodes :
                    SubprojectServiceImpl.copySubprojectNodePropertiesOnChildrenRecursive(subproject);
                }
            }
            
        } else if (DocumentEventTypes.ABOUT_TO_CREATE.equals(event.getName())) {
            // anything that is under a subproject
            
            DocumentModel parentDocument = documentManager.getDocument(sourceDocument.getParentRef());
            
            SubprojectServiceImpl.copySubprojectNodeProperties(parentDocument, sourceDocument);
        }
    }

}
