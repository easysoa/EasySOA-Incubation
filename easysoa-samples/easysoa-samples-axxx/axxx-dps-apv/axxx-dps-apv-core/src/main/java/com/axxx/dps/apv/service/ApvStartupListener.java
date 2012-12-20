package com.axxx.dps.apv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


/**
 * Calls auto fill with default data if database is empty at init.
 * NB. session & transaction are auto setup around calls to service layers.
 * see http://forum.springsource.org/showthread.php?58337-No-transaction-in-transactional-service-called-from-PostConstruct
 * 
 * @author mdutoo
 *
 */
@Component
public class ApvStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ApvInitService apvInitService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        apvInitService.init();
    }
    
}
