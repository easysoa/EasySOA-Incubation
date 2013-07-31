package org.easysoa.discovery.code;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.easysoa.discovery.code.handler.JaxRSSourcesHandler;
import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.discovery.code.handler.SourcesHandler;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.test.EasySOAWebEngineFeature;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * 
 * @author mkalam-alami
 *
 */
@Deploy({
    "org.easysoa.registry.doctypes.java.api",
    "org.easysoa.registry.doctypes.java.core",
    "org.easysoa.registry.rest.server"
})
@RepositoryConfig(cleanup = Granularity.CLASS)
public class SourceHandlersTest extends AbstractWebEngineTest {

    private static Logger logger = Logger.getLogger(SourceHandlersTest.class);
    
    private static final Map<String, SourcesHandler> availableHandlers = new HashMap<String, SourcesHandler>();
    
    @BeforeClass
    public static void setUpHandlers() {
        CodeDiscoveryMojo codeDiscovery = new CodeDiscoveryMojo() {{
            this.setMatchInterfacesFirst(true);
            this.setDiscoverInterfaces(true);
            this.setDiscoverImplementations(true);
        }};
        availableHandlers.put("JAX-WS", new JaxWSSourcesHandler(codeDiscovery));
        availableHandlers.put("JAX-RS", new JaxRSSourcesHandler(codeDiscovery));
    }

    @Test
    public void testSourceHandlers() throws Exception {
        String subprojectId = null; // default
        
        // Init registry client
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(EasySOAWebEngineFeature.NUXEO_SITES_URL);
        RegistryApi registryApi = clientBuilder.constructRegistryApi();
        
        // Set sources to explore
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File("../easysoa-samples/easysoa-discovery-code-sample"));
        JavaSource[] sources = builder.getSources();
        
        // Run code discovery
        MavenDeliverableInformation mavenDeliverable = new MavenDeliverableInformation(subprojectId, "TestDeliverable");
        List<SoaNodeInformation> soaNodeResults = runHandlers(sources, mavenDeliverable,
                new CodeDiscoveryRegistryClient(registryApi), new SystemStreamLog());
        
        Assert.assertTrue("SoaNodes must have been found during discovery", soaNodeResults.size() > 0);

        logger.info("Found SoaNodes");
        logger.info("--------------");
        for (SoaNodeInformation soaNode : soaNodeResults) {
            logger.info("   > ID = " + soaNode.getSoaNodeId());
            logger.info("     Properties =  " + soaNode.getProperties());
            logger.info("     Parents =  " + soaNode.getParentDocuments());
        }
    }
    
    private List<SoaNodeInformation> runHandlers(JavaSource[] sources, 
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();
        for (SourcesHandler handler : availableHandlers.values()) {
            // TODO mock instead of null CodeDiscoveryMojo, to test interfaces discovery from deps
            discoveredNodes.addAll(handler.handleSources(sources, mavenDeliverable, registryClient, log));
        }
        return discoveredNodes;
    }

}
