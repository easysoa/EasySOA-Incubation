package com.axxx.dps.apv.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.axxx.dps.apv.persistence.GenericEntity;


@Entity
@Table(name = "apv_tdr")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tdr extends GenericEntity<Tdr> {

    private static final long serialVersionUID = 8153363774843169652L;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="tdr", orphanRemoval=true, fetch=FetchType.LAZY)
    ///@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Projet> projets;

    // NB. to add fields ones from reporting SQL (ex. look for "bloca"),
    // including those required by PrecomptePartenaire & Pivotal (Client,
    // but also aggregated fields in Client, Information_APV, OPT ContactClient)
    
    
    /////////////////////
    // fields required when creerPrecompte called (and when calling Client) :

    private String identifiantClientPivotal;

    // tdr_bloca... : nomstructure, adresse, cp, ville, telephone, fax, email, url, sirensiret typestructure, champaction
    // Association vacances familles    4 PLACE DE NAVARRE       95200    SARCELLES          01 56 30 29 92          bienvenue@vo.asso.fr     38193064300030   Association nat.        [vacances loisirs éducation, médico-social, Caritative / humanitaire, Autre]
    
    @NotEmpty // validation definition, see http://www.mkyong.com/spring-mvc/spring-3-mvc-and-jsr303-valid-example/
    private String nomStructure;
    private String typeStructure; // ex. Association nat. TODO enum ?? TODO another than TypeStructure ?
    private String champAction;

    private String adresse;
    private String cp;
    private String ville;
    
    private String telephone;
    private String email;
    private String siteWeb;
    
    private String sirenSiret; // OPT rule
    
    /////////////////////
    // fields also required when calling Client :

    // tdr_tdb : partenairedepuis, montantdisponible, dotationglobale, reliquatanneeprecedente, dotationannuelle, sommeutilisee, reliquat, nbbeneficiairesapv
    // 1992  54020  267890  54020   213870   0   54020   0
    // 0    0   35000   0   35000   0   0   0
    @Embedded 
    private TdrTdb tdrTdb;
    
    // OPT contacts

    // NOK evaluation, commandes, avenants

    // OPT tdr_utilisateur : userid, prenom, nom, telephone, email, motdepasse, confirmmotdepasse
    // 22525   Karine Tissot     01 45 35 13 13  karinetissot@avf.asso.fr
    // 22723   Elie Langlois 01 39 01 09 34  e.langlois_csf@yahoo.fr
    
    public List<Projet> getProjets() {
        return projets;
    }
    
    public String getIdentifiantClientPivotal() {
        return identifiantClientPivotal;
    }

    public void setIdentifiantClientPivotal(String identifiantClientPivotal) {
        this.identifiantClientPivotal = identifiantClientPivotal;
    }

    public String getNomStructure() {
        return nomStructure;
    }

    public void setNomStructure(String nomStructure) {
        this.nomStructure = nomStructure;
    }
    
    public TdrTdb getTdrTdb() {
        return tdrTdb;
    }

    public void setTdrTdb(TdrTdb tdrTdb) {
        this.tdrTdb = tdrTdb;
    }

    public String getTypeStructure() {
        return typeStructure;
    }

    public void setTypeStructure(String typeStructure) {
        this.typeStructure = typeStructure;
    }

    public String getChampAction() {
        return champAction;
    }

    public void setChampAction(String champAction) {
        this.champAction = champAction;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public String getSirenSiret() {
        return sirenSiret;
    }

    public void setSirenSiret(String sirenSiret) {
        this.sirenSiret = sirenSiret;
    }
    
}
