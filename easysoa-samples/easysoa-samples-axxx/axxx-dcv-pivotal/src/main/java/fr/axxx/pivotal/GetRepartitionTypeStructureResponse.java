
package fr.axxx.pivotal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getClientResult" type="{http://pivotal.axxx.fr/}ArrayOfTypeStructureCount" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getClientResult"
})
@XmlRootElement(name = "getRepartitionTypeStructureResponse")
public class GetRepartitionTypeStructureResponse {

    protected ArrayOfTypeStructureCount getClientResult;

    /**
     * Gets the value of the getClientResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTypeStructureCount }
     *     
     */
    public ArrayOfTypeStructureCount getGetClientResult() {
        return getClientResult;
    }

    /**
     * Sets the value of the getClientResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTypeStructureCount }
     *     
     */
    public void setGetClientResult(ArrayOfTypeStructureCount value) {
        this.getClientResult = value;
    }

}
