/**
 * 
 */
package org.easysoa.registry.rest.integration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * WSDLInformation list container, used for JSON conversion
 * 
 * @author jguillemotte
 *
 */
@XmlRootElement
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class ServiceInformations {
    
    @XmlElement
    private List<ServiceInformation> serviceInformations;

    /**
     * 
     */
    public ServiceInformations(){
        serviceInformations = new ArrayList<ServiceInformation>();
    }
    
    /**
     * 
     * @param information
     */
    public void addServiceInformation(ServiceInformation information){
        this.serviceInformations.add(information);
    }

    /**
     * 
     * @return
     */
    public List<ServiceInformation> getServiceInformationList() {
        return serviceInformations;
    }

    /**
     * 
     * @param wsdlInformationList
     */
    public void setServiceInformationList(List<ServiceInformation> wsdlInformations) {
        if(wsdlInformations != null){
            this.serviceInformations = wsdlInformations;
        } else {
            this.serviceInformations = new ArrayList<ServiceInformation>();    
        }
    }    
    
}
