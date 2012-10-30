package org.easysoa.registry.systems;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class EverythingFlatClassifier implements IntelligentSystemTreeClassifier {
    
    @Override
    public void initialize(Map<String, String> params) {
        // Nothing
    }

    @Override
    public String classify(CoreSession documentManager, DocumentModel model) throws ClientException {
        return "";
    }

}
