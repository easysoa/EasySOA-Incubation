package com.axxx.dps.apv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.model.TdrTdb;


/**
 * Transacted service that auto fills with default data if database is empty at init.
 * 
 * @author mdutoo
 *
 */
@Service
public class ApvInitServiceImpl implements ApvInitService {

    @Autowired
    private TdrService tdrService;
    
    @Autowired
    private ProjetService projetService;
    
    @Transactional
    public void init() {
        if (tdrService.count() == 0l) {
            Tdr tdr = new Tdr();
            tdr.setNomStructure("V.O.");
            tdr.setTdrTdb(new TdrTdb());
            tdr.getTdrTdb().setPartenaireDepuis(2012);
            tdrService.create(tdr);
    
            Projet projet = new Projet();
            projet.setTdr(tdr);
            projet.setTypeLieu("mer");
            projetService.create(projet);
        }
    }
    
}
