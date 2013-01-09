package com.axxx.dps.apv.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;

import com.axxx.dps.apv.persistence.GenericEntity;


@Entity
@Table(name = "apv_projet")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Projet extends GenericEntity<Projet> {

    private static final long serialVersionUID = 2661251735222689510L;
    
    @ManyToOne(optional=false)
    private Tdr tdr;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    // no mappedBy because this is the master side of the relation and has join & index
    @JoinColumn(name = "benefs_list_id")
    @IndexColumn(name = "benefs_list_index") // indexed list
    //@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Benef> benefs; // OPT
    
    private String status; // TODO enum ?? ; created,( approved,) published (i.e. it's taken into account when computing numbers of its tdr)
    
    private String typeLieu; // enum Mer, Montagne, Campagne
    
    private Integer departement; // number TODO validation ; integer ?!
    
    private Integer periode; // month number TODO validation ; integer ?!
    
    // TODO add fields : take enough meaningful ones from reporting SQL (look for "ejou"), including those required by PrecomptePartenaire & Pivotal (aggregated in Client, Information_APV, OPT ContactClient)
    // duree, typelieu, pays, departement, region, periode, vacancesscolaires, transport, distance, hebergement, hebergementautre, formule, dominantesportive) FROM stdin;
    // 7   Mer countries.France    departements.PyreneesAtlantiques    regions.Aquitaine   months.february hors vacances scolaires [Train,modes de déplacement doux (marche à pied, vélo)]       800 Hôtel       demi-pension    Non
    // 10  Mer countries.France    departements.Finistere  regions.Bretagne    months.may  hors vacances scolaires [autocar, modes de déplacement doux (marche à pied, vélo)]      250 colonies de vacances        Pension complète    Non

    // p_preparation : dureepreparation, objectif, nbseancespreparation, modalitespreparation, actions
    // 7   [Equilibre, Bien-être, Renforcement des liens sociaux]   3   individuelle    [Actions d'autofinancement]
    // 90  [Découverte, Renforcement des liens sociaux]     3   collective  [Ateliers]
    // 60  [Contenu éducatif]   3   collective  [Actions d'autofinancement]
    // 4   [Santé, Accéder aux vacances, Découverte, Renforcement des liens sociaux]    3   les deux    [Actions d'autofinancement]
    

    // NB. no accompagnateurs / aidants
    
    // benefs
    // nbbeneficiaires, couttotal, montantapv, coutparjourparpersonne, montantapvparjourparpersonne, partapvfinancement
    // 35   220 60  0.62857145  0.17142858  27.272728
    // 1  383 180 95.75   45  46.997391
    
    // aduisos
    // nb, couttotal, montantapv, coutparjourparpersonne, montantapvparjourparpersonne, partapvfinancement
    // 0...
    //
    @OneToOne(cascade=CascadeType.ALL, /*mappedBy="projet",*/ orphanRemoval=true, fetch=FetchType.EAGER)
    //@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Benefs totalBenefs; // OPT computed from other xxxBenefs here

    @OneToOne(cascade=CascadeType.ALL, /*mappedBy="projet",*/ orphanRemoval=true, fetch=FetchType.EAGER)
    //@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Benefs enfantsBenefs; // for Information_APV (Nombre), Client (Nb_Benef_N, Montant_Utilise_N)
    @OneToOne(cascade=CascadeType.ALL, /*mappedBy="projet",*/ orphanRemoval=true, fetch=FetchType.EAGER)
    //@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Benefs jeunesBenefs; // for Information_APV (Nombre), Client (Nb_Benef_N, Montant_Utilise_N)
    @OneToOne(cascade=CascadeType.ALL, /*mappedBy="projet",*/ orphanRemoval=true, fetch=FetchType.EAGER)
    //@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Benefs adultesIsolesBenefs; // for Information_APV (Nombre), Client (Nb_Benef_N, Montant_Utilise_N)
    @OneToOne(cascade=CascadeType.ALL, /*mappedBy="projet",*/ orphanRemoval=true, fetch=FetchType.EAGER)
    //@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Benefs seniorsBenefs; // for Information_APV (Nombre), Client (Nb_Benef_N, Montant_Utilise_N)
    
    // enfants
    //...
    //...
    // adresse, ville, cp, age, sexe, zonehabitation, territoirepolitiqueville, statutsocial, typeressources, montantmensuelressources, typelogement, dernierdepart, personnemalade, montantapv, coutparjour, montantapvparjour, partapvfinancement
    // 5 rue des cordiers   st malo 35400   12  M   Zone urbaine    Non Elève   [Salaire, Allocations familiales]       de 501 à 1000 € Logement autonome   de 1 à 3 ans    Non   60   22  6   27.272728
    
    // enfants_l___coutsfinancement
    // couttotal, financementtotal
    // 220 220
    
    // enfants_l___coutsfinancement_couts
    // couttransport, couthebergement, coutrestauration, coutloisirs, coutautres
    // 0   0   0   0   220
    // 66  50  80.660004   16.67   0
    
    // enfants_l_in_sources
    // apvaxxx, caf, msa, conseilregional, conseilgeneral, communeintercommunalite, organismepdp, autofinancement, financeurprive, autre
    // 60  0   0   0   0   60  0   100 0   0

    // familles ???!!
    
    // seniors
    
    // jeunes
    
    
    // apressejour
    // actionsbilan, actionsbilanautre, impact, impactautre, textelibretdr
    // [Entretien individuel, réunion collective]       [santé et bien-être, renforcement de savoir-être et de savoir-faire, autonomie,Impacts en termes de renforcement des liens sociaux]     

    
    // NB. pdp ?? (blocadmin ? utilisateurs ??)
    

    public Tdr getTdr() {
        return tdr;
    }

    public void setTdr(Tdr tdr) {
        this.tdr = tdr;
    }

    public List<Benef> getBenefs() {
        return benefs;
    }

    public void setBenefs(List<Benef> benefs) {
        this.benefs = benefs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeLieu() {
        return typeLieu;
    }

    public void setTypeLieu(String typeLieu) {
        this.typeLieu = typeLieu;
    }

    /**
     * @return the departement
     */
    public Integer getDepartement() {
        return departement;
    }

    /**
     * @param departement the departement to set
     */
    public void setDepartement(Integer departement) {
        this.departement = departement;
    }

    /**
     * @return the periode
     */
    public Integer getPeriode() {
        return periode;
    }

    /**
     * @param periode the periode to set
     */
    public void setPeriode(Integer periode) {
        this.periode = periode;
    }    
    
    public Benefs getEnfantsBenefs() {
        return enfantsBenefs;
    }

    public void setEnfantsBenefs(Benefs enfantsBenefs) {
        this.enfantsBenefs = enfantsBenefs;
        computeTotalBenef();        
    }

    public Benefs getJeunesBenefs() {
        return jeunesBenefs;
    }

    public void setJeunesBenefs(Benefs jeunesBenefs) {
        this.jeunesBenefs = jeunesBenefs;
        computeTotalBenef();
    }

    public Benefs getAdultesIsolesBenefs() {
        return adultesIsolesBenefs;
    }

    public void setAdultesIsolesBenefs(Benefs adultesIsolesBenefs) {
        this.adultesIsolesBenefs = adultesIsolesBenefs;
        computeTotalBenef();
    }

    public Benefs getSeniorsBenefs() {
        return seniorsBenefs;
      
    }

    public void setSeniorsBenefs(Benefs seniorsBenefs) {
        this.seniorsBenefs = seniorsBenefs;
        computeTotalBenef();
    }
    
    public Benefs getTotalBenefs() {
        return totalBenefs;
    }
    
    public void setTotalBenefs(Benefs totalBenefs) {
        this.totalBenefs = totalBenefs;
    }
    
    public void computeTotalBenef(){
        if(this.totalBenefs == null){
            this.totalBenefs = new Benefs();
        }
        if (this.getEnfantsBenefs() == null || this.getJeunesBenefs() == null
                || this.getAdultesIsolesBenefs() == null || this.getSeniorsBenefs() == null) {
            // not inited, abort
            return;
        }
        this.totalBenefs.setMontantApv(this.getEnfantsBenefs().getMontantApv() 
                + this.getJeunesBenefs().getMontantApv() 
                + this.getAdultesIsolesBenefs().getMontantApv() 
                + this.getSeniorsBenefs().getMontantApv());
        this.totalBenefs.setNbBeneficiaires(this.getEnfantsBenefs().getNbBeneficiaires() 
                + this.getJeunesBenefs().getNbBeneficiaires()
                + this.getAdultesIsolesBenefs().getNbBeneficiaires() 
                + this.getSeniorsBenefs().getNbBeneficiaires());
    }
    
}
