package com.axxx.dps.apv.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.axxx.dps.apv.persistence.GenericEntity;


@Entity
@Table(name = "apv_projet")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
public class Projet extends GenericEntity<Projet> {

    private static final long serialVersionUID = 2661251735222689510L;

    private String typeLieu;
    
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
    
    public String getTypeLieu() {
        return typeLieu;
    }

    public void setTypeLieu(String typeLieu) {
        this.typeLieu = typeLieu;
    }
}
