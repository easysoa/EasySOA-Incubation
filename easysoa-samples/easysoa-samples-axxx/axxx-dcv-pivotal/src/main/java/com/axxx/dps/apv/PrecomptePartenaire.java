
package com.axxx.dps.apv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for precomptePartenaire complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="precomptePartenaire">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identifiantClientPivotal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="typeStructure" type="{http://www.axxx.com/dps/apv}typeStructure"/>
 *         &lt;element name="nomStructure" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="anciennete" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="telephone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adresse" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ville" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="apeNaf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sirenSiret" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "precomptePartenaire", propOrder = {
    "identifiantClientPivotal",
    "typeStructure",
    "nomStructure",
    "anciennete",
    "telephone",
    "email",
    "adresse",
    "ville",
    "cp",
    "apeNaf",
    "sirenSiret"
})
public class PrecomptePartenaire {

    @XmlElement(required = true)
    protected String identifiantClientPivotal;
    @XmlElement(required = true)
    protected TypeStructure typeStructure;
    @XmlElement(required = true)
    protected String nomStructure;
    protected int anciennete;
    @XmlElement(required = true)
    protected String telephone;
    protected String email;
    @XmlElement(required = true)
    protected String adresse;
    @XmlElement(required = true)
    protected String ville;
    @XmlElement(required = true)
    protected String cp;
    @XmlElement(required = true)
    protected String apeNaf;
    @XmlElement(required = true)
    protected String sirenSiret;

    /**
     * Gets the value of the identifiantClientPivotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifiantClientPivotal() {
        return identifiantClientPivotal;
    }

    /**
     * Sets the value of the identifiantClientPivotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifiantClientPivotal(String value) {
        this.identifiantClientPivotal = value;
    }

    /**
     * Gets the value of the typeStructure property.
     * 
     * @return
     *     possible object is
     *     {@link TypeStructure }
     *     
     */
    public TypeStructure getTypeStructure() {
        return typeStructure;
    }

    /**
     * Sets the value of the typeStructure property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeStructure }
     *     
     */
    public void setTypeStructure(TypeStructure value) {
        this.typeStructure = value;
    }

    /**
     * Gets the value of the nomStructure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomStructure() {
        return nomStructure;
    }

    /**
     * Sets the value of the nomStructure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomStructure(String value) {
        this.nomStructure = value;
    }

    /**
     * Gets the value of the anciennete property.
     * 
     */
    public int getAnciennete() {
        return anciennete;
    }

    /**
     * Sets the value of the anciennete property.
     * 
     */
    public void setAnciennete(int value) {
        this.anciennete = value;
    }

    /**
     * Gets the value of the telephone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the value of the telephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephone(String value) {
        this.telephone = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the adresse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdresse() {
        return adresse;
    }

    /**
     * Sets the value of the adresse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdresse(String value) {
        this.adresse = value;
    }

    /**
     * Gets the value of the ville property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVille() {
        return ville;
    }

    /**
     * Sets the value of the ville property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVille(String value) {
        this.ville = value;
    }

    /**
     * Gets the value of the cp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCp() {
        return cp;
    }

    /**
     * Sets the value of the cp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCp(String value) {
        this.cp = value;
    }

    /**
     * Gets the value of the apeNaf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApeNaf() {
        return apeNaf;
    }

    /**
     * Sets the value of the apeNaf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApeNaf(String value) {
        this.apeNaf = value;
    }

    /**
     * Gets the value of the sirenSiret property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSirenSiret() {
        return sirenSiret;
    }

    /**
     * Sets the value of the sirenSiret property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSirenSiret(String value) {
        this.sirenSiret = value;
    }

}
