
package com.axxx.dps.apv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for creerPrecompte complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="creerPrecompte">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="precomptePartenaire" type="{http://www.axxx.com/dps/apv}precomptePartenaire" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "creerPrecompte", propOrder = {
    "precomptePartenaire"
})
public class CreerPrecompte {

    protected PrecomptePartenaire precomptePartenaire;

    /**
     * Gets the value of the precomptePartenaire property.
     * 
     * @return
     *     possible object is
     *     {@link PrecomptePartenaire }
     *     
     */
    public PrecomptePartenaire getPrecomptePartenaire() {
        return precomptePartenaire;
    }

    /**
     * Sets the value of the precomptePartenaire property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrecomptePartenaire }
     *     
     */
    public void setPrecomptePartenaire(PrecomptePartenaire value) {
        this.precomptePartenaire = value;
    }

}
