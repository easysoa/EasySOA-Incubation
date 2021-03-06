package fr.axxx.pivotal;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.2
 * 2012-12-11T18:54:45.414+01:00
 * Generated source version: 2.4.2
 * 
 */
@WebServiceClient(name = "ContactSvc", 
                  wsdlLocation = "file:/home/jguillemotte/EasySOA-Workspace/EasySOA-Incubation/EasySOA-Incubation/easysoa-samples/easysoa-samples-axxx/axxx-dps-apv/axxx-dps-apv-core/src/main/resources/axxx-dps-pivotal/ContactSvc.asmx.wsdl",
                  targetNamespace = "http://pivotal.axxx.fr/") 
public class ContactSvc extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://pivotal.axxx.fr/", "ContactSvc");
    public final static QName ContactSvcSoap12 = new QName("http://pivotal.axxx.fr/", "ContactSvcSoap12");
    public final static QName ContactSvcSoap = new QName("http://pivotal.axxx.fr/", "ContactSvcSoap");
    static {
        URL url = null;
        try {
            url = new URL("file:/home/jguillemotte/EasySOA-Workspace/EasySOA-Incubation/EasySOA-Incubation/easysoa-samples/easysoa-samples-axxx/axxx-dps-apv/axxx-dps-apv-core/src/main/resources/axxx-dps-pivotal/ContactSvc.asmx.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ContactSvc.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/home/jguillemotte/EasySOA-Workspace/EasySOA-Incubation/EasySOA-Incubation/easysoa-samples/easysoa-samples-axxx/axxx-dps-apv/axxx-dps-apv-core/src/main/resources/axxx-dps-pivotal/ContactSvc.asmx.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ContactSvc(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ContactSvc(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ContactSvc() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns ContactSvcSoap
     */
    @WebEndpoint(name = "ContactSvcSoap12")
    public ContactSvcSoap getContactSvcSoap12() {
        return super.getPort(ContactSvcSoap12, ContactSvcSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ContactSvcSoap
     */
    @WebEndpoint(name = "ContactSvcSoap12")
    public ContactSvcSoap getContactSvcSoap12(WebServiceFeature... features) {
        return super.getPort(ContactSvcSoap12, ContactSvcSoap.class, features);
    }
    /**
     *
     * @return
     *     returns ContactSvcSoap
     */
    @WebEndpoint(name = "ContactSvcSoap")
    public ContactSvcSoap getContactSvcSoap() {
        return super.getPort(ContactSvcSoap, ContactSvcSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ContactSvcSoap
     */
    @WebEndpoint(name = "ContactSvcSoap")
    public ContactSvcSoap getContactSvcSoap(WebServiceFeature... features) {
        return super.getPort(ContactSvcSoap, ContactSvcSoap.class, features);
    }

}
