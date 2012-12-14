package com.axxx.dps.apv.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.model.Tdr;


/**
 * Fills with default data if database is empty at init
 * 
 * @author mdutoo
 *
 */
@Component
public class ApvInit {

    @Autowired
    private TdrService tdrService;
    
    @Autowired
    private ProjetService projetService;
    
    @PostConstruct
    private void init() {
        Tdr tdr = new Tdr();
        tdr.setNomStructure("V.O.");
        tdrService.create(tdr);

        Projet projet = new Projet();
        projet.setTypeLieu("mer");
        projetService.create(projet);
    }
    
}
