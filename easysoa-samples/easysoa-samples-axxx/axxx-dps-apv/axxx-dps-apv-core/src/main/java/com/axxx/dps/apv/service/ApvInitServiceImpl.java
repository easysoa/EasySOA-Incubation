package com.axxx.dps.apv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axxx.dps.apv.model.Benefs;
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
            tdr.getTdrTdb().setStatus("approved");
            tdrService.create(tdr);

            Tdr createdTdr = new Tdr();
            createdTdr.setNomStructure("Assovac");
            createdTdr.setTdrTdb(new TdrTdb());
            createdTdr.getTdrTdb().setPartenaireDepuis(2013);
            createdTdr.getTdrTdb().setStatus("created");
            tdrService.create(createdTdr); 
            
            Projet projet = new Projet();
            projet.setTdr(tdr);
            projet.setTypeLieu("mer");
            projet.setPeriode(10);
            projet.setDepartement(69);
            projet.setStatus("created");
            
            projet.setEnfantsBenefs(new Benefs());
            projet.setJeunesBenefs(new Benefs());
            projet.setAdultesIsolesBenefs(new Benefs());
            projet.setSeniorsBenefs(new Benefs());
            projet.getEnfantsBenefs().setMontantApv(500);
            projet.getEnfantsBenefs().setNbBeneficiaires(10);
            projet.getJeunesBenefs().setMontantApv(750);
            projet.getJeunesBenefs().setNbBeneficiaires(6);
            projet.getAdultesIsolesBenefs().setMontantApv(250);
            projet.getAdultesIsolesBenefs().setNbBeneficiaires(2);
            projet.getSeniorsBenefs().setMontantApv(500);
            projet.getSeniorsBenefs().setNbBeneficiaires(2);
            
            projetService.create(projet);
        }
    }
    
}
