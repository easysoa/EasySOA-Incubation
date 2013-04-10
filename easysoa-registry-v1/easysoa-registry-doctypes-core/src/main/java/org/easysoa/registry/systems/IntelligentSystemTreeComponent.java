package org.easysoa.registry.systems;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 *
 */
public class IntelligentSystemTreeComponent extends DefaultComponent implements IntelligentSystemTreeService {

    public static final String EXTENSION_POINT_CLASSIFIERS = "classifiers";
    
    public static final String EXTENSION_POINT_ISTS = "intelligentSystemTrees";
    
    private static Logger logger = Logger.getLogger(IntelligentSystemTreeComponent.class);
    
    private Map<String, Class<? extends IntelligentSystemTreeClassifier>> classifiers
            = new HashMap<String, Class<? extends IntelligentSystemTreeClassifier>>(); 
    
    private Map<String, IntelligentSystemTreeClassifier> ists = new HashMap<String, IntelligentSystemTreeClassifier>(); 
    
    // TODO Store IST information directly on the document models
    private Map<String, IntelligentSystemTreeDescriptor> istDescriptors = new HashMap<String, IntelligentSystemTreeDescriptor>();
    
    @Override
    public void registerContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) throws Exception {
        if (EXTENSION_POINT_CLASSIFIERS.equals(extensionPoint)) {
        	IntelligentSystemTreeClassifierDescriptor descriptor = null;
            try {
                // Validate descriptor
                descriptor = (IntelligentSystemTreeClassifierDescriptor) contribution;
                if (descriptor.name == null || descriptor.name.isEmpty()) {
                    throw new InvalidParameterException("'name' must not be null");
                }
                
                // Register classifier class (override potiential previous descriptor)
                Class<? extends IntelligentSystemTreeClassifier> classifierClass = Class.forName(descriptor.className.trim()).asSubclass(IntelligentSystemTreeClassifier.class);
                classifiers.put(descriptor.name, classifierClass);
            }
            catch (Exception e) {
                String contribName = (descriptor != null) ? "'" + descriptor.name + "'" : "";
                logger.error("Failed to register contribution " + contribName + " to '" + EXTENSION_POINT_CLASSIFIERS + "'", e);
            }
        }
        else if (EXTENSION_POINT_ISTS.equals(extensionPoint)) {
            IntelligentSystemTreeDescriptor descriptor = null;
            try {
                // Validate descriptor
                descriptor = (IntelligentSystemTreeDescriptor) contribution;
                if (descriptor.getName() == null || descriptor.getName().isEmpty()) {
                    throw new InvalidParameterException("'name' must not be null");
                }
                if (descriptor.getClassifier() == null || descriptor.getClassifier().isEmpty()) {
                    throw new InvalidParameterException("'classifier' must not be null");
                }
                if (!classifiers.containsKey(descriptor.getClassifier())){
                    throw new InvalidParameterException("Classifier '" + descriptor.getClassifier() + "' does not exist");
                }
                
                // Register descriptor
                istDescriptors.put(descriptor.getName(), descriptor);
                
                // Initialize and register IST
                Class<? extends IntelligentSystemTreeClassifier> istClass = classifiers.get(descriptor.getClassifier());
                IntelligentSystemTreeClassifier ist = istClass.newInstance();
                ist.initialize(descriptor.getParameters());
                ists.put(descriptor.getName(), ist);
            }
            catch (Exception e) {
                String contribName = (descriptor != null) ? "'" + descriptor.getName() + "'" : "";
                logger.error("Failed to register contribution " + contribName + " to '" + EXTENSION_POINT_CLASSIFIERS + "'", e);
            }
            
        }
    }
    
    public void handleDocumentModel(CoreSession documentManager, DocumentModel model, boolean force) throws Exception {
        boolean treeChanged = false;
        // Filter documents from other intelligent trees
        String parentType = documentManager.getDocument(model.getParentRef()).getType();
        if (!force && IntelligentSystem.DOCTYPE.equals(parentType) || IntelligentSystemTreeRoot.DOCTYPE.equals(parentType)) {
            return;
        }
        
        // Find the document source & proxies
        DocumentService documentService = Framework.getService(DocumentService.class);
        SoaNodeId soaNodeId = documentService.createSoaNodeId(model);
		DocumentModel sourceModel = documentService.findSoanode(documentManager, soaNodeId);
        if (sourceModel == null) {
            // Can't find source document (can happen when the document is not saved yet)
            return;
        }
        
        // Run the classifiers
        IntelligentSystemTreeApi intelligentSystemTreeApi = new IntelligentSystemTreeApiProxyImpl(documentManager);
        for (Entry<String, IntelligentSystemTreeClassifier> istEntry : ists.entrySet()) {
            IntelligentSystemTreeDescriptor istDescriptor = istDescriptors.get(istEntry.getKey());

            // Filter disabled ISTs
            if (istDescriptor.isEnabled()) {
                // Run classifier, that returns a string to tell where the model must be sorted
                String classification = uniformizeClassificationPath(istEntry.getValue().classify(documentManager, model));

                // Find eventual presence of model in the IST
                String treeName = istDescriptor.getClassifier() + ':' + istDescriptor.getName();

                // Handling when model is accepted
                if (classification != null) {
                    // Fetch or create the IST model
	            	if (!intelligentSystemTreeApi.intelligentSystemTreeExists(soaNodeId.getSubprojectId(), treeName)) {
	            		intelligentSystemTreeApi.createIntelligentSystemTree(soaNodeId.getSubprojectId(), treeName, istDescriptor.getTitle());
	            		treeChanged = true;
	            	}
	            	// TODO TODOOOOOOOOOOOOOOOOOOO case of non-default ITS !!!!!
                    
                    // Check if the model is at its right place
	            	treeChanged = intelligentSystemTreeApi.classifySoaNode(treeName, soaNodeId, classification) || treeChanged;
                }
                
                // Handling when model is rejected
                else if (intelligentSystemTreeApi.intelligentSystemTreeExists(soaNodeId.getSubprojectId(), treeName)) {
                    treeChanged = intelligentSystemTreeApi.deleteSoaNode(treeName, soaNodeId) || treeChanged;
                }
            }
        }
        
        if (treeChanged) {
            documentManager.save();
        }
    }
    
    private String uniformizeClassificationPath(String classification) {
   	 	// Make path uniform
    	if (classification == null) {
    		return null;
    	}
		if (classification.length() > 0 && classification.charAt(0) == '/') {
			classification = classification.substring(1);
		}
		if (classification.length() > 0	&& classification.charAt(classification.length() - 1) == '/') {
			classification = classification.substring(0, classification.length() - 1);
		}
          return classification;
    }
    
}
