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
 * 2012-12-03T11:13:37.967+01:00
 * Generated source version: 2.4.2
 * 
 */
/*
@WebServiceClient(name = "PrecomptePartenaireServiceImpl", 
                  wsdlLocation = "http://localhost:8076/services/PrecomptePartenaireService?wsdl",
                  targetNamespace = "http://www.axxx.com/dps/apv")
*/ 
public class PrecomptePartenaireServiceImpl extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://www.axxx.com/dps/apv", "PrecomptePartenaireServiceImpl");
    public final static QName PrecomptePartenairePort = new QName("http://www.axxx.com/dps/apv", "PrecomptePartenairePort");
    static {
        URL url = null;
        try {
            //url = new URL("http://localhost:8076/services/PrecomptePartenaireService?wsdl");
            //url = new URL("file:///src/main/resources/api/PrecomptePartenaireService.wsdl");
            url = new URL(new URL("file:"), "src/main/resources/api/PrecomptePartenaireService.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(PrecomptePartenaireServiceImpl.class.getName())
                //.log(java.util.logging.Level.INFO, "Can not initialize the default wsdl from {0}", "http://localhost:8076/services/PrecomptePartenaireService?wsdl");
                .log(java.util.logging.Level.INFO, "Can not initialize the default wsdl from {0}", "file:///src/main/resources/api/PrecomptePartenaireService.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public PrecomptePartenaireServiceImpl(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public PrecomptePartenaireServiceImpl(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public PrecomptePartenaireServiceImpl() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns PrecomptePartenaireService
     */
    @WebEndpoint(name = "PrecomptePartenairePort")
    public PrecomptePartenaireService getPrecomptePartenairePort() {
        return super.getPort(PrecomptePartenairePort, PrecomptePartenaireService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns PrecomptePartenaireService
     */
    @WebEndpoint(name = "PrecomptePartenairePort")
    public PrecomptePartenaireService getPrecomptePartenairePort(WebServiceFeature... features) {
        return super.getPort(PrecomptePartenairePort, PrecomptePartenaireService.class, features);
    }

}
