package org.easysoa.registry.systems;

import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.BusinessService;
import org.easysoa.registry.types.Component;
import org.easysoa.registry.types.InformationService;
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
        	if (BusinessService.DOCTYPE.equals(model.getType())) {
        		actorMetadata = BusinessService.XPATH_PROVIDER_ACTOR;
        	}
        	else if (InformationService.DOCTYPE.equals(model.getType())) {
        		actorMetadata = InformationService.XPATH_PROVIDER_ACTOR;
        	}
        	else {
        		actorMetadata = Component.XPATH_PROVIDER_ACTOR;
        	}
        	String id = (String) model.getPropertyValue(actorMetadata);
        	if (id == null) {
        		return null; // TODO MDU else npex below ?!
        	}
        	return documentManager.getDocument(new IdRef(id)).getTitle();
        }
        catch (Exception e) {
            logger.warn("Failed to classify by actor: " + e.getMessage());
            return null;
        }
        
    }

}
