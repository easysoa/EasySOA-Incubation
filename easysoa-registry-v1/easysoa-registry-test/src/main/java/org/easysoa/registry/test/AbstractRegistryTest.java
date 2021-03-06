package org.easysoa.registry.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;


/**
 * To be extended by Nuxeo local tests.
 * 
 * To test consecutive steps of a workflow, add
 * @RepositoryConfig(cleanup = Granularity.CLASS)
 * in order to keep Nuxeo repository state between test methods.
 * 
 * @author mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features({EasySOAFeature.class, PlatformFeature.class})
@RepositoryConfig(cleanup = Granularity.METHOD) // truly unitary tests :
//don't keep Nuxeo repository state between test methods
public class AbstractRegistryTest {
    
    @Inject
    protected CoreSession documentManager;

    protected RepositoryLogger repositoryLogger;
    
    protected boolean logRepositoryAfterEachTest = true;

    protected String defaultSubprojectId = null;
    
    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        if (documentManager == null) {
            System.err.println("documentManager not inited");
            throw new RuntimeException("documentManager not inited");
        }
        repositoryLogger = new RepositoryLogger(documentManager);
    }
    
    @After
    public void logRepository() throws ClientException {
        if (logRepositoryAfterEachTest) {
        	documentManager.save();
            repositoryLogger.setTitle(name.getMethodName());
            repositoryLogger.logAllRepository();
        }
    }
    
    public void setLogRepositoryAfterEachTest(boolean logRepositoryAfterEachTest) {
        this.logRepositoryAfterEachTest = logRepositoryAfterEachTest;
    }
    
}
