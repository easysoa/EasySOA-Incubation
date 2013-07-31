package org.easysoa.registry.test;

import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * To be extended by WebEngine tests.
 * 
 * To test consecutive steps of a workflow, add
 * @RepositoryConfig(cleanup = Granularity.CLASS)
 * in order to keep Nuxeo repository state between test methods.
 * 
 * @author mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features({EasySOAFeature.class, EasySOAWebEngineFeature.class})
@RepositoryConfig(cleanup = Granularity.METHOD) // truly unitary tests :
// don't keep Nuxeo repository state between test methods
public abstract class AbstractWebEngineTest {

    private static final Logger logger = Logger.getLogger(AbstractWebEngineTest.class);

    @Inject
    protected CoreSession documentManager;

    protected RepositoryLogger repositoryLogger;
    
    protected boolean logRepositoryAfterEachTest = false;
    
    protected static String defaultSubprojectId = "/default-domain"
            + "/MyProject" + "/Realisation" + "_v"; // static because initialized once (if any)

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
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

    @Before
    public void testAvailability() {
        try {
            URLConnection connection = new URL(EasySOAWebEngineFeature.NUXEO_SITES_URL).openConnection();
            connection.connect();
        }
        catch (Exception e) {
            String message = "Testing environment issue: cannot reach test WebEngine URL";
            logger.error(message, e);
            Assert.fail(message);
        }
    }
    
    public String getURL(Class<?> c) {
        return EasySOAWebEngineFeature.NUXEO_SITES_URL + PathExtractor.getPath(c);
    }

    public String getURL(Class<?> c, String methodName, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException {
        return EasySOAWebEngineFeature.NUXEO_SITES_URL + PathExtractor.getPath(c, methodName, parameterTypes);
    }
    
    public void logTestName(Logger logger) {
        logger.debug("--------------------");
        logger.debug(name.getMethodName());
        logger.debug("--------------------");
    }
}
