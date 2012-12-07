
package fr.axxx.pivotal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="Identifiant_Client" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Bilan_Libelle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Nombre" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Bilan_Annee" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "id",
    "identifiantClient",
    "bilanLibelle",
    "nombre",
    "bilanAnnee"
})
@XmlRootElement(name = "Information_APV")
public class InformationAPV {

    @XmlElement(name = "Id")
    protected Long id;
    @XmlElement(name = "Identifiant_Client")
    protected String identifiantClient;
    @XmlElement(name = "Bilan_Libelle")
    protected String bilanLibelle;
    @XmlElement(name = "Nombre")
    protected Integer nombre;
    @XmlElement(name = "Bilan_Annee")
    protected Integer bilanAnnee;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the identifiantClient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifiantClient() {
        return identifiantClient;
    }

    /**
     * Sets the value of the identifiantClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifiantClient(String value) {
        this.identifiantClient = value;
    }

    /**
     * Gets the value of the bilanLibelle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBilanLibelle() {
        return bilanLibelle;
    }

    /**
     * Sets the value of the bilanLibelle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBilanLibelle(String value) {
        this.bilanLibelle = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNombre(Integer value) {
        this.nombre = value;
    }

    /**
     * Gets the value of the bilanAnnee property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBilanAnnee() {
        return bilanAnnee;
    }

    /**
     * Sets the value of the bilanAnnee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBilanAnnee(Integer value) {
        this.bilanAnnee = value;
    }

}
