package com.axxx.dps.apv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.persistence.GenericEntityServiceImpl;


/**
 * Only add there non-generic business methods.
 * 
 * @author mdutoo
 *
 */
@Service
@Transactional(readOnly=true) // alas tx required to close session only at its end, at least only readOnly by default
public class ProjetServiceImpl extends GenericEntityServiceImpl<Projet> implements ProjetService {

    @Autowired
    protected ProjetDao projetDao;

    @Override
    protected ProjetDao getGenericDao() {
        return projetDao;
    }

    @Override
    public void publish(Projet projet) {
        // TODO check (again) everything is alright ?
        
        // TODO change projet state to "published"
        
        // TODO update tdr infos : tdr.montantDisponible = tdr.montantDisponible - projet.montantapv... 
        
        // TODO send tdr infos to Pivotal
        
    }

}
