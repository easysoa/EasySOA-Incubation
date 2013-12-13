package org.easysoa.samples.axxx.dps;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;

import org.easysoa.samples.axxx.dcv.ClientService;


@WebService
public class TdrServiceImpl implements TdrService {
    
    @Inject ClientService clientService;

    @Inject public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @Inject
    ClientServiceClient clientServiceClient;

    @WebServiceRef
    ClientService jaxwsInjectedClientService;

    @Inject
    ClientServiceProvider clientServiceProvider;

    @Override
    public void updateTdr(Tdr tdr) {

    }

    @Override
    public Tdr updateAndReturnTdr(Tdr tdr) {
        return tdr;
    }

    @Override
    public Tdr[] getTdr() {
        return new Tdr[0];
    }

}
