package com.axxx.dps.apv.service;

import org.springframework.stereotype.Repository;

import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.persistence.GenericEntityDaoImpl;

/**
 * Only add there non-generic DAO methods
 * 
 * @author mdutoo
 *
 */
@Repository // access sessionFactory, instead of "session in view pattern"
public class TdrDaoImpl extends GenericEntityDaoImpl<Tdr> implements TdrDao {
    
}
