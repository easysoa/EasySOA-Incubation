package org.easysoa.samples.axxx.dps;

import javax.jws.WebService;

@WebService(targetNamespace="http://www.pureairflowers.com/services/")
public interface TdrService {

    public void updateTdr(Tdr tdr);
    
}
