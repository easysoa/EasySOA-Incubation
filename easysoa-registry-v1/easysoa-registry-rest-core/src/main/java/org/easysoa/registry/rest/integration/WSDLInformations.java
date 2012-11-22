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
 * @author jguillemotte
 *
 */
@XmlRootElement
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class WSDLInformations {

    @XmlElement
    private List<WSDLInformation> wsdlInformations;

    /**
     * 
     */
    public WSDLInformations(){
        wsdlInformations = new ArrayList<WSDLInformation>();
    }
    
    /**
     * 
     * @param information
     */
    public void addWsdlInformation(WSDLInformation information){
        this.wsdlInformations.add(information);
    }

    /**
     * 
     * @return
     */
    public List<WSDLInformation> getWsdlInformationList() {
        return wsdlInformations;
    }

    /**
     * 
     * @param wsdlInformationList
     */
    public void setWsdlInformationList(List<WSDLInformation> wsdlInformations) {
        if(wsdlInformations != null){
            this.wsdlInformations = wsdlInformations;
        } else {
            this.wsdlInformations = new ArrayList<WSDLInformation>();    
        }
    }    
    
}
