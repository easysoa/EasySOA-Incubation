package org.easysoa.registry.systems;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface IntelligentSystemTreeService {

    /**
     * May Classify (but doesn't change the document in itself) or Delete (?) in ITS.
     * May save session if tree changed.
     * @param documentManager
     * @param model
     * @param force
     * @throws Exception
     */
    void handleDocumentModel(CoreSession documentManager, DocumentModel model, boolean force) throws Exception;

}
