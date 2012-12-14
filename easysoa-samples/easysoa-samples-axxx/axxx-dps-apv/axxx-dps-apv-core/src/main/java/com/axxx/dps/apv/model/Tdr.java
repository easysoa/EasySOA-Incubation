package com.axxx.dps.apv.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.axxx.dps.apv.persistence.GenericEntity;


@Entity
@Table(name = "apv_tdr")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
public class Tdr extends GenericEntity<Tdr> {

    private static final long serialVersionUID = 8153363774843169652L;

    private String identifiantClientPivotal;
    
    private String nomStructure;

    // tdr : annee (NO), identifiantclientpivotal, status
    // 2010    0x000000000009851B  approved
    // 0   0x0000000000095081  approved
    
    // TODO add fields : take enough meaningful ones from reporting SQL (look for "bloca"), including those required by PrecomptePartenaire & Pivotal (Client, but also TODO ? aggregated fields in Client, Information_APV, OPT ContactClient)
    // nomstructure, adresse, cp, ville, telephone, fax, email, url, sirensiret, apenaf, anneecreation, typestructure, champaction
    // Association vacances familles    4 PLACE DE NAVARRE       95200    SARCELLES          01 56 30 29 92          bienvenue@vo.asso.fr     38193064300030      1991    Association nat.        [vacances loisirs éducation, médico-social, Caritative / humanitaire, Autre]

    // tdr_bilan (N-1 !!) : dotationglobale, reliquatanneeprecedente, dotationannuelle, sommeutilisee, reliquat, nbbeneficiairesapv, montantmoyenapvbeneficiaire
    // 243520  32813   243513  216645  26868   2122    102
    // 850000  109820  850000  688630  161370  8587    79
    
    // tdr_bilan_beneficiaires : nbenfants, nbjeunes, nbfamilles, nbadultesisoles, nbseniors
    // 0   218   1894   10   0
    // 0   696   7575   131   143

    // tdr_convention : reliquat, dotation, numerodecision, datetransmispartenaire, dateretourpartenaire, datetransmisdg, datesignaturedg
    // 43116   276884  APV 2010-013    \N  \N  \N  \N
    // 84790   335210  APV 2010-017    \N  \N  \N  \N
    
    // tdr_tdb : partenairedepuis, montantdisponible, dotationglobale, reliquatanneeprecedente, dotationannuelle, sommeutilisee, reliquat, nbbeneficiairesapv
    // 1992  54020  267890  54020   213870   0   54020   0
    // 0    0   35000   0   35000   0   0   0
    
    // tdr_tdb_beneficiaires : nbenfants, nbjeunes, nbfamilles, nbadultesisoles, nbseniors
    // 0   0   0   4   24
    // 18  232 0   3   0
    
    // OPT contacts ?
    
    // OPT convention ? (=> conventionnement, status="approved")
    // OPT annee ???

    // NOK evaluation, commandes, avenants

    // tdr_utilisateur ? userid, prenom, nom, telephone, email, motdepasse, confirmmotdepasse
    // 22525   Karine Tissot     01 45 35 13 13  karinetissot@avf.asso.fr
    // 22723   Elie Langlois 01 39 01 09 34  e.langlois_csf@yahoo.fr
    
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
    
}
