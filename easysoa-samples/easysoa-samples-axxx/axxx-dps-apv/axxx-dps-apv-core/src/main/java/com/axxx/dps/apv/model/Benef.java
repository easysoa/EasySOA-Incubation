package com.axxx.dps.apv.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.axxx.dps.apv.persistence.GenericEntity;

/**
 * OPT
 * 
 * @author mdutoo
 *
 */
@Entity
@Table(name = "apv_benef")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Benef extends GenericEntity<Benef> {

    private static final long serialVersionUID = 8294833510801365063L;
    
    @ManyToOne(optional=false)
    private Projet projet;

    // enfants
    //...
    //...
    // adresse, ville, cp, age, sexe, zonehabitation, territoirepolitiqueville, statutsocial, typeressources, montantmensuelressources, typelogement, dernierdepart, personnemalade, montantapv, coutparjour, montantapvparjour, partapvfinancement
    // 5 rue des cordiers   st maloString 35400   12  M   Zone urbaine    Non Elève   [Salaire, Allocations familiales]       de 501 à 1000 € Logement autonome   de 1 à 3 ans    Non   60   22  6   27.272728
    
    // ?
    private String adresse;
    private String ville;
    private int cp;
    private int age;
    private char sexe;
    private String zoneHabitation; // enum ?
    private String territoirePolitiqueVille; // enum ?
    private String statutSocial; // enum ?
    private String typeRessources; // enum ?
    private String montantMensuelRessources; // enum ?
    private String typeLogement; // enum ?
    private String dernierDepart; // enum ?
    private boolean personneMalade;
    
    private double montantApv; // tdb ?
    ///private double coutparjour; // tdb ?
    private double montantApvParJour; // tdb ?
    ///private int partapvfinancement; // tdb ?
    

    public Projet getProjet() {
        return projet;
    }
    
}
