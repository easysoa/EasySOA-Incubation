package com.axxx.dps.apv;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.2
 * 2012-12-03T10:33:43.094+01:00
 * Generated source version: 2.4.2
 * 
 */
@WebServiceClient(name = "ProjetServiceImpl", 
                  wsdlLocation = "http://localhost:8076/services/ProjetService?wsdl",
                  targetNamespace = "http://www.axxx.com/dps/apv") 
public class ProjetServiceImpl extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://www.axxx.com/dps/apv", "ProjetServiceImpl");
    public final static QName ProjetServicePort = new QName("http://www.axxx.com/dps/apv", "ProjetServicePort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8076/services/ProjetService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ProjetServiceImpl.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:8076/services/ProjetService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ProjetServiceImpl(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ProjetServiceImpl(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ProjetServiceImpl() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns ProjetService
     */
    @WebEndpoint(name = "ProjetServicePort")
    public ProjetService getProjetServicePort() {
        return super.getPort(ProjetServicePort, ProjetService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ProjetService
     */
    @WebEndpoint(name = "ProjetServicePort")
    public ProjetService getProjetServicePort(WebServiceFeature... features) {
        return super.getPort(ProjetServicePort, ProjetService.class, features);
    }

}
