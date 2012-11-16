package org.easysoa.registry.rest;

import java.util.HashSet;
import java.util.Set;

import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.dashboard.rest.MatchingDashboard;
import org.easysoa.registry.documentation.rest.ServiceDocumentationController;
import org.easysoa.registry.indicators.rest.IndicatorsController;
import org.easysoa.registry.integration.SimpleRegistryServiceImpl;
import org.nuxeo.ecm.webengine.app.WebEngineModule;

/**
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
                IndicatorsController.class,
                ServiceDocumentationController.class,
                MatchingDashboard.class
                };
    }

}
