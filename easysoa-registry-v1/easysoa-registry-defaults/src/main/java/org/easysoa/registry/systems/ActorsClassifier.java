package org.easysoa.registry.systems;

import java.util.Map;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * XXX Test hierarchy for the upcoming specifications-related additions to the model.
 * 
 * @author mkalam-alami
 *
 */
public class ActorsClassifier implements IntelligentSystemTreeClassifier {

    private static Logger logger = Logger.getLogger(ActorsClassifier.class);
    
    @Override
    public void initialize(Map<String, String> params) {
        // No parameters
    }

    @Override
    public String classify(CoreSession documentManager, DocumentModel model) throws Exception {
        if (!"BusinessService".equals(model.getType())
        		&& !"InformationService".equals(model.getType())
        		&& !"Component".equals(model.getType())) {
            return null;
        }
        
        try {
        	String actorMetadata = null;
        	if ("BusinessService".equals(model.getType())) {
        		actorMetadata = "businessservice:providerActor";
        	}
        	else if ("InformationService".equals(model.getType())) {
        		actorMetadata = "informationservice:providerActor";
        	}
        	else {
        		actorMetadata = "component_schema:providerActor";
        	}
        	String id = (String) model.getPropertyValue(actorMetadata);
        	return documentManager.getDocument(new IdRef(id)).getTitle();
        }
        catch (Exception e) {
            logger.warn("Failed to classify by actor: " + e.getMessage());
            return null;
        }
        
    }

}
