
package fr.axxx.pivotal.client.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.axxx.pivotal.persistence.GenericEntity;


/**
 * Unique per given Client, Bilan_Libelle & Bilan_Annee
 * 
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
@Entity
@Table(name = "pivotal_informationapv")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifiantClient",
    "bilanLibelle",
    "nombre",
    "bilanAnnee"
})
@XmlRootElement(name = "Information_APV")
public class InformationAPV extends GenericEntity<InformationAPV> {

	private static final long serialVersionUID = 7633271562728451845L;
	
	// List of bilan libelle values
	public static final String BILAN_LIBELLE_JEUNES = "jeunes";
	public static final String BILAN_LIBELLE_SENIORS = "seniors";
	public static final String BILAN_LIBELLE_FAMILLES = "familles";
	public static final String BILAN_LIBELLE_ADULTESISOLES = "adultesisoles"; 

    /** the Client this InformationAPV is about */
	@XmlElement(name = "Identifiant_Client")
    protected String identifiantClient;
    /** the "public" (target) of this InformationAPV, unique per Client & Bilan_Annee (TODO values : jeunes, seniors, familles, adultesisoles) */
    @XmlElement(name = "Bilan_Libelle")
    protected String bilanLibelle;
    /** the year of this InformationAPV, unique per Client & Bilan_Annee */
    @XmlElement(name = "Bilan_Annee")
    protected Integer bilanAnnee;
    @XmlElement(name = "Nombre")
    protected Integer nombre;

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

    /**
     * Check if the value of bilan libelle can be used
     * @param bilanLibelle
     * @return ture if the value can be used, false otherwise
     */
    public static boolean checkBilanLibelleValue(String bilanLibelle) {
        if(BILAN_LIBELLE_ADULTESISOLES.equals(bilanLibelle) || BILAN_LIBELLE_FAMILLES.equals(bilanLibelle) || BILAN_LIBELLE_JEUNES.equals(bilanLibelle) || BILAN_LIBELLE_SENIORS.equals(bilanLibelle)){
            return true;
        } else {
            return false;
        }
    }

}
