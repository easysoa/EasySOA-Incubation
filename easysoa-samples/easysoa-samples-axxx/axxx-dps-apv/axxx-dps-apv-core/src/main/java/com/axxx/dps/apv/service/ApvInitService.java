package com.axxx.dps.apv.service;

import org.springframework.transaction.annotation.Transactional;

public interface ApvInitService {

    @Transactional
    public void init();
}
