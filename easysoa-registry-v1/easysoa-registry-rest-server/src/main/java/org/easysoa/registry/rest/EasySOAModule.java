package org.easysoa.registry.rest;

import java.util.HashSet;
import java.util.Set;

import org.easysoa.registry.context.rest.ContextController;
import org.easysoa.registry.dashboard.rest.MatchingDashboard;
import org.easysoa.registry.dbb.rest.ServiceFinderRest;
import org.easysoa.registry.documentation.rest.ServiceDocumentationController;
import org.easysoa.registry.index.rest.IndexController;
import org.easysoa.registry.indicators.rest.IndicatorsController;
import org.easysoa.registry.integration.EndpointStateServiceImpl;
import org.easysoa.registry.integration.SimpleRegistryServiceImpl;
import org.easysoa.registry.monitoring.rest.EndpointStateController;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
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
        singletons.add(new JsonMessageReader());
        singletons.add(new JsonMessageWriter());
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
                EndpointStateController.class,
                ContextController.class,
                IndexController.class,
                };
    }
    
}
