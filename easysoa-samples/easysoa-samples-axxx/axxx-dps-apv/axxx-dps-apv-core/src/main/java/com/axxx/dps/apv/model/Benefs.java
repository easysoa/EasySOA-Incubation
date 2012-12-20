package com.axxx.dps.apv.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.axxx.dps.apv.persistence.GenericEntity;

@Entity
@Table(name = "apv_benefs")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE) // if inheritance, above
//@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING) // if inheritance, above
//@DiscriminatorValue("jeunes") // if inheritance, below
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Benefs extends GenericEntity<Benefs> {

    private static final long serialVersionUID = 850307431203808368L;

    @ManyToOne(optional=false)
    private Projet projet;
    
    // nbbeneficiaires, couttotal, montantapv, coutparjourparpersonne, montantapvparjourparpersonne, partapvfinancement
    // NB. /// = cout
    private int nbBeneficiaires; // for Information_APV.Nombre, Client.Nb_Benef_N
    ///private double couttotal;
    private double montantApv; // for Information_APV.Montant_Utilise_N
    ///private double coutparjourparpersonne;
    //private double montantapvparjourparpersonne;
    ///private int partapvfinancement;

    public Projet getProjet() {
        return projet;
    }
    public int getNbBeneficiaires() {
        return nbBeneficiaires;
    }
    public void setNbBeneficiaires(int nbBeneficiaires) {
        this.nbBeneficiaires = nbBeneficiaires;
    }
    public double getMontantApv() {
        return montantApv;
    }
    public void setMontantApv(double montantApv) {
        this.montantApv = montantApv;
    }
    
}
