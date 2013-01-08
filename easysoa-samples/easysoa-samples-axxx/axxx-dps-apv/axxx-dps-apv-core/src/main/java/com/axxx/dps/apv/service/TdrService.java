package com.axxx.dps.apv.service;

import java.util.List;

import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.persistence.GenericEntityService;

/**
 * To be used by the web layer.
 * This is the business layer, that marks transactional boundaries.
 * 
 * Only add there non-generic business methods.
 * 
 * @author mdutoo
 *
 */
public interface TdrService extends GenericEntityService<Tdr> {

    //public List<Projet> getProjets(Tdr tdr);
    
    public void approve(Tdr tdr);

    public void computeTdb(Tdr tdr);

    public void publish(Tdr tdr);
    
    /**
     * Returns only Tdr's with approved status
     * @return
     */
    public List<Tdr> getTdrs();
    
    /**
     * Returns only Tdr's with created status
     * @return
     */
    public List<Tdr> getTdrPrecomptes();
}
