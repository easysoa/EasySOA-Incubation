package org.easysoa.samples.axxx.dps;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace="http://www.pureairflowers.com/services/")
public interface TdrService {

    /**
     * With arguments, without annotations
     * @return
     */
    public void updateTdr(Tdr tdr);
    
    /**
     * With arguments and annotations
     * @return
     */
    @WebMethod(operationName="updateTdrOperation")
    public @WebResult(name="TdrReturn") Tdr updateAndReturnTdr(@WebParam(name="tdr") Tdr tdr);

    /**
     * Without arguments
     * @return
     */
    public Tdr[] getTdr();
    
}
