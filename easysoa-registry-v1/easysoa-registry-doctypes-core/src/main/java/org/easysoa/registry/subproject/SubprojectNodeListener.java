package org.easysoa.registry.subproject;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;


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
        
        if (sourceDocument.hasSchema("subproject")) {
            // TODO NOO needs to be created first to get id
            //SubprojectServiceImpl.computeAndSetVisibleSubprojects(documentManager, sourceDocument);
            // TODO also on change ?!?
        } else {
            DocumentModel spnodeModel = null;
            DocumentModel parentDocument = documentManager.getDocument(sourceDocument.getParentRef());
            if (parentDocument.hasFacet(SubprojectNode.FACET)) {
                // subproject known through parent => recopy spnode properties from it
                spnodeModel = parentDocument;
                if (!sourceDocument.hasFacet(SubprojectNode.FACET)) {
                    sourceDocument.addFacet(SubprojectNode.FACET);
                }
                if (sourceDocument.getPropertyValue(Subproject.XPATH_SUBPROJECT) == null) { // TODO rm ?! (if spnode:subproject not set anymore in newSoaNodeDocument())
                sourceDocument.setPropertyValue(Subproject.XPATH_SUBPROJECT,
                        parentDocument.getPropertyValue(Subproject.XPATH_SUBPROJECT));
                }
            } else if (sourceDocument.hasFacet(SubprojectNode.FACET)) { // TODO not required ?!
                // subproject should be provided through property => get spnode properties from subproject
                String subprojectId = (String) sourceDocument.getPropertyValue(Subproject.XPATH_SUBPROJECT);
                spnodeModel = documentManager.getDocument(new IdRef(subprojectId));
            } else {
                logger.info("About to create document outside a subproject " + sourceDocument);
            }
            if (spnodeModel != null) {
                sourceDocument.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS, spnodeModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS));
                sourceDocument.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, spnodeModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV));
                // TODO or using facet inheritance through spnode:subproject ?? NOOO documentService.createDocument() ex. ITS...
            }
        }
    }

}
