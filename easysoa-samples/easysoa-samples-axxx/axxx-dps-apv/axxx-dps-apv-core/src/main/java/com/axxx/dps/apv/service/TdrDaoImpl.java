package com.axxx.dps.apv.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.axxx.dps.apv.model.Tdr;

@Repository // access sessionFactory, instead of "session in view pattern"
public class TdrDaoImpl implements TdrDao {

    @Autowired
    private SessionFactory sessionFactory;
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Tdr> listTdrs() {
        return sessionFactory.getCurrentSession().createQuery("from Tdr").list();
    }

}
