package com.axxx.dps.apv.service;

import org.springframework.stereotype.Repository;

import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.persistence.GenericEntityDaoImpl;

/**
 * Only add there non-generic DAO methods
 * 
 * @author mdutoo
 *
 */
@Repository // access sessionFactory, instead of "session in view pattern"
public class ProjetDaoImpl extends GenericEntityDaoImpl<Projet> implements ProjetDao {
    
}
