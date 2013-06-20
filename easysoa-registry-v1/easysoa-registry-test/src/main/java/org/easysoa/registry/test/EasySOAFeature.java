package org.easysoa.registry.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;

/**
 * 
 * @author mkalam-alami
 * 
 */
@Deploy({
    "org.easysoa.registry.test",
    
    // Relations support
    "org.nuxeo.ecm.relations.api",
    "org.nuxeo.ecm.relations",
    "org.nuxeo.ecm.relations.jena",
    
    // Needed by the DocumentModelHelper to fetch doctype UI names
    "org.nuxeo.ecm.platform.types.api",
    "org.nuxeo.ecm.platform.types.core",
    
    // Nuxeo classification add-on core & deps
    "org.nuxeo.ecm.platform.classification.api",
    "org.nuxeo.ecm.platform.classification.core",
    //"org.nuxeo.ecm.platform.audit", // brings exceptions
    
    // Needed to override its MimetypeIconUpdater listener
    "org.nuxeo.ecm.platform.filemanager.core",
    "org.nuxeo.ecm.platform.filemanager.core.listener",
    "org.nuxeo.ecm.platform.mimetype.core",
    
    // Nuxeo tree snapshot add-on & deps
    "org.nuxeo.snapshot",
    "org.nuxeo.ecm.platform.publisher.task",
    "org.nuxeo.ecm.platform.publisher.core",
    
    // Minimal EasySOA requirements
    "org.easysoa.registry.doctypes.api",
    "studio.extensions.easy-soa-open-wide",
    "org.easysoa.registry.doctypes.core",
    
    // Wall requirements
    //"org.nuxeo.ecm.activity",
    //"org.nuxeo.ecm.core.persistence", // brings exceptions
    
    // Diff requirements
    "org.nuxeo.diff.jsf",
    "org.nuxeo.theme.core",
    "org.nuxeo.theme.fragments",
    "org.nuxeo.theme.html",
    "org.nuxeo.theme.jsf",
    
    // Bundles that are required to be deployed by easysoa registry bundles
    "org.nuxeo.ecm.webapp.base", // !!
    "org.nuxeo.ecm.webapp.core", //
    "org.nuxeo.ecm.platform.ui", //
    "org.nuxeo.ecm.platform.web.common" // !!!
})
@LocalDeploy({"org.easysoa.registry.test:directory-config.xml"})
public class EasySOAFeature extends SimpleFeature {

//    private static Logger logger = Logger.getLogger(EasySOAFeature.class);
//
//    @Override
//    public void configure(FeaturesRunner runner, Binder binder) {
//        super.configure(runner, binder);
//        
//        // Check manifest (invalid characters - like tabs? - can cause injection issues & missing contributions) 
//        RuntimeFeature runtimeFeature = runner.getFeature(RuntimeFeature.class);
//        for (String deployment : runtimeFeature.getDeployments()) {
//            if (deployment.contains("easysoa")) {
//                Bundle easysoaBundle = runtimeFeature.getHarness().getOSGiAdapter().getBundle(deployment);
//                if (easysoaBundle != null) {
//                    Object nuxeoComponentField = easysoaBundle.getHeaders().get("Nuxeo-Component");
//                    if (nuxeoComponentField == null) {
//                        logger.warn("No Nuxeo-Component entry has been found in the manifest of '" + deployment  + "'. " +
//                        		"Unless this is intended, there must be some invalid characters in your Manifest.");
//                    }
//                }
//                else {
//                    logger.error("Bundle " + deployment + " has not been deployed");
//                }
//            }
//        }
//    }
    
}
