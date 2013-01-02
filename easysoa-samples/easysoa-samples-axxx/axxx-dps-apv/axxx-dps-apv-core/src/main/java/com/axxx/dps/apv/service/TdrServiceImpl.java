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
public class TdrServiceImpl extends GenericEntityServiceImpl<Tdr> implements TdrService {

    @Autowired
    protected TdrDao tdrDao;

    @Override
    protected TdrDao getGenericDao() {
        return tdrDao;
    }

    @Override
    public List<Projet> getProjets(Tdr tdr) {
        return tdr.getProjets(); // can be done because inside session here
    }

    @Override
    public List<Tdr> getTdrPrecomptes(){
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("tdrTdb.status", "created");
        Criterion filter = Restrictions.allEq(properties);
        Order order = Order.asc("nomStructure");
        return tdrDao.list(Tdr.class, filter, order, null, null);
    }
    
    @Override
    public void approve(Tdr tdr) {
        // TODO
    }

    @Override
    public void computeTdb(Tdr tdr) {
        // TODO
    }

    @Override
    public void publish(Tdr tdr) {
        // TODO
    }

}
