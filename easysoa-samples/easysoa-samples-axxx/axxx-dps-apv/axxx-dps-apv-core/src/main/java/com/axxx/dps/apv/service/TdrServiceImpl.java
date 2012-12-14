package com.axxx.dps.apv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.persistence.GenericEntityServiceImpl;


/**
 * Only add there non-generic business methods.
 * 
 * @author mdutoo
 *
 */
@Service
@Transactional(readOnly=true) // alas tx required to close session only at its end, at least only readOnly by default
public class TdrServiceImpl extends GenericEntityServiceImpl<Tdr> implements TdrService {

    @Autowired
    protected TdrDao tdrDao;

    @Override
    protected TdrDao getGenericDao() {
        return tdrDao;
    }

}
