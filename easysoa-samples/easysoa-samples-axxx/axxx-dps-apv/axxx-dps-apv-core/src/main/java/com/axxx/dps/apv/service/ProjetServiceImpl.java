package com.axxx.dps.apv.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.persistence.GenericEntityServiceImpl;


/**
 * Only add there non-generic business methods.
 * 
 * @author mdutoo
 *
 */
@Service
public class ProjetServiceImpl extends GenericEntityServiceImpl<Projet> implements ProjetService {

    @Autowired
    protected ProjetDao projetDao;

    @Override
    protected ProjetDao getGenericDao() {
        return projetDao;
    }
    
    @Override
    public Tdr getTdr(Projet projet) {
        return projet.getTdr(); // can be done because inside session here
    }

    @Override
    public void approve(Projet projet) {
        // TODO check (again) everything is alright ?
        
        // TODO change projet state to "published"
        
        // TODO update tdr infos : tdr.montantDisponible = tdr.montantDisponible - projet.montantapv... 
        
        // TODO send tdr infos to Pivotal
        
    }

    @Override
    public List<Projet> listByTdr(long tdrId) {
        Map<String, Long> properties = new HashMap<String, Long>();
        properties.put("tdr.id", tdrId);
        Criterion filter = Restrictions.allEq(properties);
        Order order = Order.asc("id");
        return projetDao.list(Projet.class, filter, order, null, null);        
    }

}
