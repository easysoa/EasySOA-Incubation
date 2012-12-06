
package fr.axxx.pivotal.client.model;

import java.math.BigDecimal;
import java.util.logging.Level;

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
 *         &lt;element name="Raison_Sociale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Type_Structure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Statut_Client" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NPAI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Complement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Num_et_voie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Code_Postal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Ville" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pays" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Tel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RIB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Forme_Juridique" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SIREN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dot_Glob_APV_N" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dont_Reliquat_N_1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dont_Dot_N" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Nb_Benef_Prev_N" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Montant_Utilise_N" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Nb_Benef_N" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Entity
@Table(name = "pivotal_client")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifiantClient",
    "raisonSociale",
    "anciennete",
    "typeStructure",
    "numEtVoie",
    "email",
    "codePostal",
    "ville",
    "pays",
    "tel",
    "rib",
    "formeJuridique",
    "siren",
    "dotGlobAPVN",
    "dontReliquatN1",
    "dontDotN",
    "nbBenefPrevN",
    "montantUtiliseN",
    "nbBenefN",
    "creerPrecompteDone"
})
@XmlRootElement(name = "Client")
public class Client extends GenericEntity<Client> {

	private static final long serialVersionUID = 8085798108335446115L;
	
	/** same as id, don't modify */
	@XmlElement(name = "Identifiant_Client")
    protected String identifiantClient = "";
    @XmlElement(name = "Raison_Sociale")
    protected String raisonSociale = "";
    @XmlElement(name = "Anciennete")
    protected Integer anciennete;
	@XmlElement(name = "Type_Structure")
    protected String typeStructure = "";
    @XmlElement(name = "Num_et_voie")
    protected String numEtVoie = "";
    @XmlElement(name = "Email")
    protected String email = "";
    @XmlElement(name = "Code_Postal")
    protected String codePostal = "";
    @XmlElement(name = "Ville")
    protected String ville = "";
    @XmlElement(name = "Pays")
    protected String pays = "";
    @XmlElement(name = "Tel")
    protected String tel = "";
    @XmlElement(name = "RIB")
    protected String rib = "";
    @XmlElement(name = "Forme_Juridique")
    protected String formeJuridique = "";
    @XmlElement(name = "SIREN")
    protected String siren = "";
	/** stat, allow to set at init but don't modify */
    @XmlElement(name = "Dot_Glob_APV_N")
    protected BigDecimal dotGlobAPVN;
	/** stat, allow to set at init but don't modify */
    @XmlElement(name = "Dont_Reliquat_N_1")
    protected BigDecimal dontReliquatN1;
	/** stat, allow to set at init but don't modify */
    @XmlElement(name = "Dont_Dot_N")
    protected BigDecimal dontDotN;
	/** stat, allow to set at init but don't modify */
    @XmlElement(name = "Nb_Benef_Prev_N")
    protected BigDecimal nbBenefPrevN;
	/** stat, allow to set at init but don't modify */
    @XmlElement(name = "Montant_Utilise_N")
    protected BigDecimal montantUtiliseN;
	/** stat, allow to set at init but don't modify */
    @XmlElement(name = "Nb_Benef_N")
    protected BigDecimal nbBenefN;
    @XmlElement(name = "creerPrecompteDone")
    protected Boolean creerPrecompteDone;

    /**
     * Default constructor
     */
    public Client() {
    }

    /**
     * 
     * @param identifiantClient
     * @param raisonSociale
     * @param siren
     * @param email
     */
    public Client(String identifiantClient, String raisonSociale, String siren, String email) {
        this();
        this.identifiantClient = identifiantClient;
        this.setRaisonSociale(raisonSociale);
        this.setSIREN(siren);
        this.setEmail(email);
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
    	if (this.identifiantClient == null && this.getId() != null) {
    		// identifiantClient is actually id
    		this.setIdentifiantClient(this.getId().toString());
    	}
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
		// identifiantClient is actually id
        if (value == null || value.isEmpty()) {
        	this.setId(null);
        } else {
	        try {
	        	this.setId(Long.parseLong(value));
	        } catch (Exception e) {
	        	LOG.log(Level.SEVERE, "identifiantClient is not a long integer", e);
	        }
        }
    }
    
    @Override
    public void setId(Long id) {
    	super.setId(id);
    	this.identifiantClient = (id == null) ? null : id.toString();
    }

    /**
     * Gets the value of the raisonSociale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRaisonSociale() {
        if(this.raisonSociale != null){
            return this.raisonSociale;
        } else {
            return "";
        }
    }

    /**
     * Sets the value of the raisonSociale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRaisonSociale(String value) {
        if(value != null){
            this.raisonSociale = value;
        } else {
            this.raisonSociale = "";
        }
    }

    /**
     * Gets the value of the typeStructure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeStructure() {
        if(this.typeStructure != null){
            return this.typeStructure;
        } else {
            return "";
        }
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
        if(value != null){
            this.typeStructure = value;
        } else {
            this.typeStructure = "";
        }        
    }

    /**
     * Gets the value of the anciennete property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAnciennete() {
        if(this.anciennete != null){
            return this.anciennete;
        } else {
            return 0;
        }
    }

    /**
     * Sets the value of the anciennete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAnciennete(Integer value) {
        if(value != null){
            this.anciennete = value;
        } else {
            this.anciennete = 0;
        }        
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
        if(this.numEtVoie != null){
            return this.numEtVoie;
        } else {
            return "";
        }
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
        if(value != null){
            this.numEtVoie = value;
        } else {
            this.numEtVoie = "";
        }        
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
        if(this.email != null){
            return this.email;
        } else {
            return "";
        }
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
        if(value != null){
            this.email = value;
        } else {
            this.email = "";
        }        
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
        if(this.codePostal != null){
            return this.codePostal;
        } else {
            return "";
        }
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
        if(value != null){
            this.codePostal = value;
        } else {
            this.codePostal = "";
        }        
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
        if(this.ville != null){
            return this.ville;
        } else {
            return "";
        }
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
        if(value != null){
            this.ville = value;
        } else {
            this.ville = "";
        }        
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
        if(this.pays != null){
            return this.pays;
        } else {
            return "";
        }
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
        if(value != null){
            this.pays = value;
        } else {
            this.pays = "";
        }        
    }

    /**
     * Gets the value of the tel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTel() {
        if(this.tel != null){
            return this.tel;
        } else {
            return "";
        }
    }

    /**
     * Sets the value of the tel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTel(String value) {
        if(value != null){
            this.tel = value;
        } else {
            this.tel = "";
        }        
    }

    /**
     * Gets the value of the rib property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRIB() {
        if(this.rib != null){
            return this.rib;
        } else {
            return "";
        }
    }

    /**
     * Sets the value of the rib property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRIB(String value) {
        if(value != null){
            this.rib = value;
        } else {
            this.rib = "";
        }
    }

    /**
     * Gets the value of the formeJuridique property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormeJuridique() {
        if(this.formeJuridique != null){
            return this.formeJuridique;
        } else {
            return "";
        }
    }

    /**
     * Sets the value of the formeJuridique property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormeJuridique(String value) {
        if(value != null){
            this.formeJuridique = value;
        } else {
            this.formeJuridique = "";
        }
    }

    /**
     * Gets the value of the siren property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSIREN() {
        if(this.siren != null){
            return this.siren;
        } else {
            return "";
        }
    }

    /**
     * Sets the value of the siren property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSIREN(String value) {
        if(value != null){
            this.siren = value;
        } else {
            this.siren = "";
        }
    }

    /**
     * Gets the value of the dotGlobAPVN property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDotGlobAPVN() {
        return dotGlobAPVN;
    }

    /**
     * Sets the value of the dotGlobAPVN property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDotGlobAPVN(BigDecimal value) {
        this.dotGlobAPVN = value;
    }

    /**
     * Gets the value of the dontReliquatN1 property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDontReliquatN1() {
        return dontReliquatN1;
    }

    /**
     * Sets the value of the dontReliquatN1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDontReliquatN1(BigDecimal value) {
        this.dontReliquatN1 = value;
    }

    /**
     * Gets the value of the dontDotN property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDontDotN() {
        return dontDotN;
    }

    /**
     * Sets the value of the dontDotN property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDontDotN(BigDecimal value) {
        this.dontDotN = value;
    }

    /**
     * Gets the value of the nbBenefPrevN property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNbBenefPrevN() {
        return nbBenefPrevN;
    }

    /**
     * Sets the value of the nbBenefPrevN property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNbBenefPrevN(BigDecimal value) {
        this.nbBenefPrevN = value;
    }

    /**
     * Gets the value of the montantUtiliseN property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMontantUtiliseN() {
        return montantUtiliseN;
    }

    /**
     * Sets the value of the montantUtiliseN property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMontantUtiliseN(BigDecimal value) {
        this.montantUtiliseN = value;
    }

    /**
     * Gets the value of the nbBenefN property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNbBenefN() {
        return nbBenefN;
    }

    /**
     * Sets the value of the nbBenefN property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNbBenefN(BigDecimal value) {
        this.nbBenefN = value;
    }

    /**
     * @return the creerPrecompteDone
     */
    public Boolean isCreerPrecompteDone() {
        if(creerPrecompteDone != null){
            return creerPrecompteDone;
        } else {
            return new Boolean(false);
        }
    }

    /**
     * @param creerPrecompteDone the creerPrecompteDone to set
     */
    public void setCreerPrecompteDone(Boolean creerPrecompteDone) {
        this.creerPrecompteDone = creerPrecompteDone;
    }
    
}
