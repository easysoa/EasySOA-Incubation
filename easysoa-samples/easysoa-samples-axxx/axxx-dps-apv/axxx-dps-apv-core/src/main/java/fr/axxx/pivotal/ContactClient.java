
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
 *         &lt;element name="Identifiant_Client" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Nom_Contact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Prenom_Contact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Fonction_Contact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Telephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Num_et_voie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Code_postal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Ville" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pays" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "identifiantClient",
    "nomContact",
    "prenomContact",
    "fonctionContact",
    "telephone",
    "email",
    "numEtVoie",
    "codePostal",
    "ville",
    "pays"
})
@XmlRootElement(name = "Contact_Client")
public class ContactClient {

    @XmlElement(name = "Identifiant_Client")
    protected String identifiantClient;
    @XmlElement(name = "Nom_Contact")
    protected String nomContact;
    @XmlElement(name = "Prenom_Contact")
    protected String prenomContact;
    @XmlElement(name = "Fonction_Contact")
    protected String fonctionContact;
    @XmlElement(name = "Telephone")
    protected String telephone;
    @XmlElement(name = "Email")
    protected String email;
    @XmlElement(name = "Num_et_voie")
    protected String numEtVoie;
    @XmlElement(name = "Code_postal")
    protected String codePostal;
    @XmlElement(name = "Ville")
    protected String ville;
    @XmlElement(name = "Pays")
    protected String pays;

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
     * Gets the value of the nomContact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomContact() {
        return nomContact;
    }

    /**
     * Sets the value of the nomContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomContact(String value) {
        this.nomContact = value;
    }

    /**
     * Gets the value of the prenomContact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrenomContact() {
        return prenomContact;
    }

    /**
     * Sets the value of the prenomContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrenomContact(String value) {
        this.prenomContact = value;
    }

    /**
     * Gets the value of the fonctionContact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFonctionContact() {
        return fonctionContact;
    }

    /**
     * Sets the value of the fonctionContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFonctionContact(String value) {
        this.fonctionContact = value;
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
     * Gets the value of the numEtVoie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumEtVoie() {
        return numEtVoie;
    }

    /**
     * Sets the value of the numEtVoie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumEtVoie(String value) {
        this.numEtVoie = value;
    }

    /**
     * Gets the value of the codePostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodePostal() {
        return codePostal;
    }

    /**
     * Sets the value of the codePostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodePostal(String value) {
        this.codePostal = value;
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
     * Gets the value of the pays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPays() {
        return pays;
    }

    /**
     * Sets the value of the pays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPays(String value) {
        this.pays = value;
    }

}
