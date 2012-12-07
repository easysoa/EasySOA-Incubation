/**
 * 
 */
package org.easysoa.registry.rest.integration;

import java.util.ArrayList;
import java.util.List;
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * @author jguillemotte
 *
 */
@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class EndpointInformations {

    @XmlElement
    private List<EndpointInformation> endpointInformations;
    
    /**
     * 
     */
    public EndpointInformations(){
        endpointInformations = new ArrayList<EndpointInformation>();
    }
    
    /**
     * 
     * @param information
     */
    public void addEndpointInformation(EndpointInformation information){
        this.endpointInformations.add(information);
    }

    /**
     * 
     * @return
     */
    public List<EndpointInformation> getEndpointInformationList() {
        return endpointInformations;
    }

    /**
     * 
     * @param wsdlInformationList
     */
    public void setEndpointInformationList(List<EndpointInformation> endpointInformations) {
        if(endpointInformations != null){
            this.endpointInformations = endpointInformations;
        } else {
            this.endpointInformations = new ArrayList<EndpointInformation>();    
        }
    }     
    
}
