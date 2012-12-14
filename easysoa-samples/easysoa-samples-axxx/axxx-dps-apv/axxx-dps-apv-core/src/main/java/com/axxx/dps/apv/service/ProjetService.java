package com.axxx.dps.apv.service;

import com.axxx.dps.apv.model.Projet;
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
public interface ProjetService extends GenericEntityService<Projet> {
    
    public void publish(Projet projet);
    
}
