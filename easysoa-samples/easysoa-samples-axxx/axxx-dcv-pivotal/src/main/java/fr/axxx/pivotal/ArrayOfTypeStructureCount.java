
package fr.axxx.pivotal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfTypeStructureCount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTypeStructureCount">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="typeStructureCount" type="{http://pivotal.axxx.fr/}TypeStructureCount" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTypeStructureCount", propOrder = {
    "typeStructureCount"
})
public class ArrayOfTypeStructureCount {

    @XmlElement(nillable = true)
    protected List<TypeStructureCount> typeStructureCount;

    /**
     * Gets the value of the typeStructureCount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the typeStructureCount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypeStructureCount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeStructureCount }
     * 
     * 
     */
    public List<TypeStructureCount> getTypeStructureCount() {
        if (typeStructureCount == null) {
            typeStructureCount = new ArrayList<TypeStructureCount>();
        }
        return this.typeStructureCount;
    }

}
