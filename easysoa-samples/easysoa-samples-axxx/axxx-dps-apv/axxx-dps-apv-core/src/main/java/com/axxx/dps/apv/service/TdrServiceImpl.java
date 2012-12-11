package com.axxx.dps.apv.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axxx.dps.apv.model.Tdr;

@Service
public class TdrServiceImpl implements TdrService {
    
    @Transactional
    public List<Tdr> listTdrs() {
        return null;
    }

}
