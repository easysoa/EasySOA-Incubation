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
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import static org.easysoa.registry.test.EasySOAWebEngineFeature.NUXEO_URL;

@RunWith(FeaturesRunner.class)
@Features({EasySOAFeature.class, EasySOAWebEngineFeature.class})
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public abstract class AbstractWebEngineTest {

    private static final Logger logger = Logger.getLogger(AbstractWebEngineTest.class);

    @Inject
    protected CoreSession documentManager;

    protected RepositoryLogger repositoryLogger;
    
    protected boolean logRepositoryAfterEachTest = false;

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
            URLConnection connection = new URL(NUXEO_URL).openConnection();
            connection.connect();
        }
        catch (Exception e) {
            String message = "Testing environment issue: cannot reach test WebEngine URL";
            logger.error(message, e);
            Assert.fail(message);
        }
    }
    
    public String getURL(Class<?> c) {
        return NUXEO_URL + PathExtractor.getPath(c);
    }

    public String getURL(Class<?> c, String methodName, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException {
        return NUXEO_URL + PathExtractor.getPath(c, methodName, parameterTypes);
    }
    
    public void logTestName(Logger logger) {
        logger.debug("--------------------");
        logger.debug(name.getMethodName());
        logger.debug("--------------------");
    }
}
