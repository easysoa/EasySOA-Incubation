package org.easysoa.registry.subproject;

import org.apache.log4j.Logger;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;


/**
 * Listens to Snapshotable.ABOUT_TO_CREATE_LEAF_VERSION_EVENT events
 * (aboutToCreateLeafVersionEvent)
 * 
 * @author mdutoo
 * @obsolete replaced by logic before and after snapshotting, see SubprojectServiceImpl.createSubprojectVersion()
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
            ///SubprojectServiceImpl.computeAndSetVisibleSubprojects(documentManager, sourceDocument);
            // TODO increment 
            // TODO also on change ?!?
        } else
        // TODO if if (!sourceDocument.hasFacet("SubprojectNode")) add it ?!!? ex. ITS...
        if (sourceDocument.hasFacet("SubprojectNode")) {
            // TODO recopy from above (if not sp itself) OR get from sp using given id
            String oldSubprojectId = (String) sourceDocument.getPropertyValue(Subproject.XPATH_SUBPROJECT);
            
            // for now only minor increments TODO NUXEO PUT OPTION IN CONTEXT
            String newVersionLabel = getVersion(sourceDocument, "major_version")
                    + "." + (getVersion(sourceDocument, "minor_version") + 1);
            
            String newVersionedSubprojectId = oldSubprojectId + newVersionLabel;
            sourceDocument.setPropertyValue(Subproject.XPATH_SUBPROJECT, newVersionedSubprojectId);
            String[] visibleSubprojects = (String[]) sourceDocument.getPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS);
            StringBuffer visibleSubprojectsCsvBuf = new StringBuffer();
            for (int i = 0; i < visibleSubprojects.length; i++) {
                if (oldSubprojectId.equals(visibleSubprojects[i])) {
                    visibleSubprojects[i] = newVersionedSubprojectId;
                }
                visibleSubprojectsCsvBuf.append('\'');
                visibleSubprojectsCsvBuf.append(visibleSubprojects[i]);
                visibleSubprojectsCsvBuf.append("',");
            }
            visibleSubprojectsCsvBuf.deleteCharAt(visibleSubprojectsCsvBuf.length() - 1); // always at least one visible (itself)
            sourceDocument.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS, visibleSubprojects); // TODO to trigger dirtying ??
            sourceDocument.setPropertyValue(SubprojectNode.XPATH_VISIBLE_SUBPROJECTS_CSV, visibleSubprojectsCsvBuf.toString());
            // TODO or using facet inheritance through spnode:subproject ??
        }
    }


    // from StandardVersioningService TODO NUXEO OPEN IT UP
    protected long getVersion(DocumentModel doc, String prop) throws ClientException {
        Object propVal = doc.getPropertyValue(prop);
        if (propVal == null || !(propVal instanceof Long)) {
            return 0;
        } else {
            return ((Long) propVal).longValue();
        }
    }

}
