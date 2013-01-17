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
 * Listens to Snapshotable.ABOUT_TO_CREATE_LEAF_VERSION_EVENT events
 * 
 * @author mdutoo
 *
 */
public class TreeSnapshotListener implements EventListener {

    private static Logger logger = Logger.getLogger(TreeSnapshotListener.class);
    
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
        
        DocumentModel leafRootModel = (DocumentModel) documentContext.getProperty("leafRootDocument");
        // TODO check matching properties changes, in order to match only if they changed ??
        // TODO compute spnode props if subproject changed ??? NO would require to save (which would trigger event loop)
        
        //match(documentManager, sourceDocument);
        
        if (sourceDocument.hasSchema("subproject")) {
            // TODO NOO needs to be created first to get id
            //SubprojectServiceImpl.computeAndSetVisibleSubprojects(documentManager, sourceDocument);
            // TODO also on change ?!?
        } else
        // TODO if if (!sourceDocument.hasFacet("SubprojectNode")) add it ?!!? ex. ITS...
        if (sourceDocument.hasFacet("SubprojectNode")) {
            // TODO recopy from above (if not sp itself) OR get from sp using given id
            String subprojectId = (String) sourceDocument.getPropertyValue(Subproject.XPATH_SUBPROJECT);
            DocumentModel subprojectModel = documentManager.getDocument(new IdRef(subprojectId));
            ///sourceDocument.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS, subprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS));
            ///sourceDocument.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, subprojectModel.getPropertyValue(Subproject.XPATH_VISIBLE_SUBPROJECTS_CSV));
            // TODO or using facet inheritance through spnode:subproject ??
        }
    }

}
