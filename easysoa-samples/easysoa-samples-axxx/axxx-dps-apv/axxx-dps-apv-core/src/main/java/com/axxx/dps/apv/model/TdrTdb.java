package com.axxx.dps.apv.model;

import javax.persistence.Embeddable;
import javax.persistence.SequenceGenerator;


@Embeddable // denormalized in tdr, rather than @Entity @Table(name = "apv_tdrtdb")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
public class TdrTdb { // does not extend GenericEntity because embedded in Tdr
    
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 8125430146099410023L;

    // tdr_tdb : partenairedepuis, montantdisponible, dotationglobale, reliquatanneeprecedente, dotationannuelle, sommeutilisee, reliquat, nbbeneficiairesapv
    // 1992  54020  267890  54020   213870   0   54020   0
    // 0    0   35000   0   35000   0   0   0
    private int partenaireDepuis; // > 0 ; for Client.Anciennete
    
    private double dotationGlobale; // computed during conventionnement, > 0 to approve it ; for Client.Dot_Glob_APV_N (should be reliquatanneeprecedente + dotationannuelle)
    private double reliquatAnneePrecedente; // set during conventionnement ; for Client.Dont_Reliquat_N_1
    private double dotationAnnuelle; // set during conventionnement ; for Dont_Dot_N (& Client.Dot_Glob_APV_N) ; = dotationglobale + reliquatanneeprecedente
    
    private double sommeUtilisee; // for Client.Montant_Utilise_N ; = sum of projet.montant for all approved projets
    private double montantDisponible; // = dotationglobale - sommeutilisee
    //private double reliquat; // no meaning here (in APV would have been about year N-1, or somme commandee vs utilisee ?)
    private int nbBeneficiairesApv; // for Client.Nb_Benef_N ; = sum of tdr.nbx for every public x ; = sum of projet.nb for all approved projets
    

    // tdr : annee, identifiantclientpivotal, status
    // 2010    0x000000000009851B  approved
    // 0   0x0000000000095081  approved
    private int annee; // for Information_APV.Bilan_Annee

    /** (created,) approved (i.e. convention signed) OPT for this year */
    private String status;
    
    
    /////////////////////
    // fields also required when calling Information_APV :
    
    // for each public : Bilan_Libelle (hardcoded / enum), Nombre (from below), Bilan_Annee (from annee)

    // tdr_tdb_beneficiaires : nbenfants, nbjeunes, nbadultesisoles, nbseniors
    // 0   0   4   24
    // 18  232   3   0
    private int nbEnfants; // for Information_APV.Bilan_Annee
    private int nbJeunes; // for Information_APV.Bilan_Annee
    private int nbAdultesIsoles; // for Information_APV.Bilan_Annee
    private int nbSeniors; // for Information_APV.Bilan_Annee
    
    //private List<PublicTdb> publicTdb; // TODO test
    
    
    // NO tdr_bilan (N-1 !!) : dotationglobale, reliquatanneeprecedente, dotationannuelle, sommeutilisee, reliquat, nbbeneficiairesapv, montantmoyenapvbeneficiaire
    // 243520  32813   243513  216645  26868   2122    102
    // 850000  109820  850000  688630  161370  8587    79
    
    // NO tdr_bilan_beneficiaires (N-1 !!) : nbenfants, nbjeunes, nbfamilles, nbadultesisoles, nbseniors
    // 0   218   1894   10   0
    // 0   696   7575   131   143

    // tdr_convention : reliquat, dotation, numerodecision, datetransmispartenaire, dateretourpartenaire, datetransmisdg, datesignaturedg
    // 43116   276884  APV 2010-013    \N  \N  \N  \N
    // 84790   335210  APV 2010-017    \N  \N  \N  \N
    ///private double reliquat; // for Client.Dont_Reliquat_N_1 ; USE TDRTDB'S
    ///private double dotation; // for Dont_Dot_N (& Client.Dot_Glob_APV_N) ; USE TDRTDB'S
    //private String numerodecision; // business id ; OPT for Information_APV.Numero_Decision
    private int nbBeneficiairesPrevisionnel; // for Client.Nb_Benef_Prev_N
    // OPT convention ? (=> conventionnement, status="approved")
    // NO passage annee

    
    public int getPartenaireDepuis() {
        return partenaireDepuis;
    }

    public void setPartenaireDepuis(int partenaireDepuis) {
        this.partenaireDepuis = partenaireDepuis;
    }

    public double getDotationGlobale() {
        return dotationGlobale;
    }

    public void setDotationGlobale(double dotationGlobale) {
        this.dotationGlobale = dotationGlobale;
    }

    public double getReliquatAnneePrecedente() {
        return reliquatAnneePrecedente;
    }

    public void setReliquatAnneePrecedente(double reliquatAnneePrecedente) {
        this.reliquatAnneePrecedente = reliquatAnneePrecedente;
    }

    public double getDotationAnnuelle() {
        return dotationAnnuelle;
    }

    public void setDotationAnnuelle(double dotationAnnuelle) {
        this.dotationAnnuelle = dotationAnnuelle;
    }

    public double getSommeUtilisee() {
        return sommeUtilisee;
    }

    public void setSommeUtilisee(double sommeUtilisee) {
        this.sommeUtilisee = sommeUtilisee;
    }

    public double getMontantDisponible() {
        return montantDisponible;
    }

    public void setMontantDisponible(double montantDisponible) {
        this.montantDisponible = montantDisponible;
    }

    public int getNbBeneficiairesApv() {
        return nbBeneficiairesApv;
    }

    public void setNbBeneficiairesApv(int nbBeneficiairesApv) {
        this.nbBeneficiairesApv = nbBeneficiairesApv;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNbEnfants() {
        return nbEnfants;
    }

    public void setNbEnfants(int nbEnfants) {
        this.nbEnfants = nbEnfants;
    }

    public int getNbJeunes() {
        return nbJeunes;
    }

    public void setNbJeunes(int nbJeunes) {
        this.nbJeunes = nbJeunes;
    }

    public int getNbAdultesIsoles() {
        return nbAdultesIsoles;
    }

    public void setNbAdultesIsoles(int nbAdultesIsoles) {
        this.nbAdultesIsoles = nbAdultesIsoles;
    }

    public int getNbSeniors() {
        return nbSeniors;
    }

    public void setNbSeniors(int nbSeniors) {
        this.nbSeniors = nbSeniors;
    }

    public int getNbBeneficiairesPrevisionnel() {
        return nbBeneficiairesPrevisionnel;
    }

    public void setNbBeneficiairesPrevisionnel(int nbBeneficiairesPrevisionnel) {
        this.nbBeneficiairesPrevisionnel = nbBeneficiairesPrevisionnel;
    }
    
}
