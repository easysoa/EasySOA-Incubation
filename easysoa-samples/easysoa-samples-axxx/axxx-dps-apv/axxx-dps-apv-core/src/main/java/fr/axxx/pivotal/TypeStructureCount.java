
package fr.axxx.pivotal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeStructureCount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TypeStructureCount">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="typeStructure" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="clientCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TypeStructureCount", propOrder = {
    "typeStructure",
    "clientCount"
})
public class TypeStructureCount {

    @XmlElement(required = true)
    protected String typeStructure;
    protected long clientCount;

    /**
     * Gets the value of the typeStructure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeStructure() {
        return typeStructure;
    }

    /**
     * Sets the value of the typeStructure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeStructure(String value) {
        this.typeStructure = value;
    }

    /**
     * Gets the value of the clientCount property.
     * 
     */
    public long getClientCount() {
        return clientCount;
    }

    /**
     * Sets the value of the clientCount property.
     * 
     */
    public void setClientCount(long value) {
        this.clientCount = value;
    }

}
