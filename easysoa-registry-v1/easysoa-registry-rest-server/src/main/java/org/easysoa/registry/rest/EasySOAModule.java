package org.easysoa.registry.rest;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.easysoa.registry.cartography.rest.CartographyController;
import org.easysoa.registry.context.rest.ContextController;
import org.easysoa.registry.dashboard.rest.MatchingDashboard;
import org.easysoa.registry.dbb.rest.ServiceFinderRest;
import org.easysoa.registry.documentation.rest.ServiceDocumentationController;
import org.easysoa.registry.governance.rest.GovernanceController;
import org.easysoa.registry.index.rest.IndexController;
import org.easysoa.registry.indicators.rest.IndicatorsController;
import org.easysoa.registry.integration.EndpointStateServiceImpl;
import org.easysoa.registry.integration.SimpleRegistryServiceImpl;
import org.easysoa.registry.monitoring.rest.MonitoringController;
import org.easysoa.registry.rest.jackson.JsonMessageReader;
import org.easysoa.registry.rest.jackson.JsonMessageWriter;
import org.nuxeo.ecm.webengine.app.WebEngineModule;

/**
 * EasySOA Module.
 *
 * Used only for webengine configuration : REST (JSON reader/writer) & controllers
 * (this is NOT the module that is provided to Freemarker templates).
 *
 * @author mkalam-alami
 *
 */
public class EasySOAModule extends WebEngineModule {

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<Object>();
        
        // custom JAXRS provider :
        //singletons.add(new JsonMessageReader());
        //singletons.add(new JsonMessageWriter());
        
        // Jersey's Jackson provider ;
        //singletons.add(new JacksonProviderProxy()); // however its configuration would be Jersey's
        // see http://www.mkyong.com/webservices/jax-rs/json-example-with-jersey-jackson/
        // http://grepcode.com/file/repo1.maven.org/maven2/com.sun.jersey/jersey-bundle/1.11/com/sun/jersey/json/impl/provider/entity/JacksonProviderProxy.java?av=f

        // Jackson's provider :
        //ObjectMapper mapper = new ObjectMapper(); // allows to configure Jackson
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(/*mapper*/); // from jackson-jaxrs
        singletons.add(jacksonJsonProvider);
        
        // JAXB provider :
        // using Jersey's default one (Jettison I guess ? or Jackson also ??)
        
        return singletons;
    }

    @Override
    public Class<?>[] getWebTypes() {
        return new Class<?>[] {
            RegistryApiImpl.class,
            SimpleRegistryServiceImpl.class,
            EndpointStateServiceImpl.class,
            IndicatorsController.class,
            ServiceDocumentationController.class,
            MatchingDashboard.class,
            ServiceFinderRest.class,
            MonitoringController.class,
            ContextController.class,
            IndexController.class,
            GovernanceController.class,
            CartographyController.class,
            SoapUIConfRest.class
        };
    }

}
