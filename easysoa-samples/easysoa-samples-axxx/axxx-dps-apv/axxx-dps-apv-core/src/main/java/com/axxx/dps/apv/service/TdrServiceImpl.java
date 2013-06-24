package com.axxx.dps.apv.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.model.TdrTdb;
import com.axxx.dps.apv.persistence.GenericEntityServiceImpl;

import fr.axxx.pivotal.ContactSvcSoap;


/**
 * Only add there non-generic business methods.
 * 
 * @author mdutoo
 *
 */
@Service
public class TdrServiceImpl extends GenericEntityServiceImpl<Tdr> implements TdrService {

    @Autowired
    protected TdrDao tdrDao;

    /** The Pivotal application's (remote) service to call back to update dashboard information
     * about the APV program (number of people sent of holidays, amount of money spent...) */
    @Autowired
    @Qualifier("com.ancv.form.ws.esbContactSvcClient")
    private ContactSvcSoap pivotalContactService;
    
    @Override
    protected TdrDao getGenericDao() {
        return tdrDao;
    }


    @Override
    public List<Tdr> getTdrPrecomptes(){
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("tdrTdb.status", "created");
        Criterion filter = Restrictions.allEq(properties);
        Order order = Order.asc("nomStructure");
        return tdrDao.list(Tdr.class, filter, order, null, null);
    }

    @Override
    public List<Tdr> getTdrs() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("tdrTdb.status", "approved");
        Criterion filter = Restrictions.allEq(properties);
        Order order = Order.asc("nomStructure");
        return tdrDao.list(Tdr.class, filter, order, null, null);
    }    

    @Transactional
    @Override
    public void approve(Tdr tdr) {
        // TODO move code from TdrPrecompteController.approve() here
    }

    @Transactional
    @Override
    public void computeTdb(Tdr tdr) {
        //recomputes its tdr's impacted computed fields (by queries on all approved projets), saves tdr, then calls TdrService.publish()
        
        //private double dotationGlobale; // computed during conventionnement, > 0 to approve it ; for Client.Dot_Glob_APV_N (should be reliquatanneeprecedente + dotationannuelle)
        //private double reliquatAnneePrecedente; // set during conventionnement ; for Client.Dont_Reliquat_N_1
        //private double dotationAnnuelle; // set during conventionnement ; for Dont_Dot_N (& Client.Dot_Glob_APV_N)
        
        //private double sommeUtilisee; // for Client.Montant_Utilise_N ; = sum of projet.montant for all approved projets
        //private double montantDisponible; // = dotationglobale - sommeutilisee
        ///private double reliquat; // NO no meaning here (in APV would have been about year N-1, or somme commandee vs utilisee ?)
        //private int nbBeneficiairesApv; // for Client.Nb_Benef_N ; = sum of tdr.nbx for every public x ; = sum of projet.nb for all approved projets
        
        TdrTdb tdrTdb = tdr.getTdrTdb();
        // Compute fields
        tdrTdb.setMontantDisponible(tdrTdb.getDotationGlobale() - tdrTdb.getSommeUtilisee());
        // Get all approved projects and make the sum of montant apv 
        List<Projet> projets = tdr.getProjets();
        double sommeUtilisee = 0;
        int nbBeneficiaires = 0;
        int nbAdultesIsoles = 0;
        int nbEnfants = 0;
        int nbJeunes = 0;
        int nbSeniors = 0;
        for(Projet projet : projets){
            if("approved".equals(projet.getStatus())){
                sommeUtilisee = sommeUtilisee + projet.getTotalBenefs().getMontantApv();
                nbBeneficiaires = nbBeneficiaires + projet.getTotalBenefs().getNbBeneficiaires();
                
                nbAdultesIsoles = nbAdultesIsoles + projet.getAdultesIsolesBenefs().getNbBeneficiaires();
                nbEnfants = nbEnfants + projet.getEnfantsBenefs().getNbBeneficiaires();
                nbJeunes = nbJeunes + projet.getJeunesBenefs().getNbBeneficiaires();
                nbSeniors = nbSeniors + projet.getSeniorsBenefs().getNbBeneficiaires();
            }
        }
        
        tdrTdb.setNbAdultesIsoles(nbAdultesIsoles);
        tdrTdb.setNbEnfants(nbEnfants);
        tdrTdb.setNbJeunes(nbJeunes);
        tdrTdb.setNbSeniors(nbSeniors);        
        
        tdrTdb.setSommeUtilisee(sommeUtilisee);
        tdrTdb.setMontantDisponible(tdrTdb.getDotationGlobale() - tdrTdb.getSommeUtilisee());
        tdrTdb.setNbBeneficiairesApv(nbBeneficiaires);

        // Update TDR
        // TODO : problem TdrTdb is not updated ....
        this.update(tdr);
    }

    @Override
    public void publish(Tdr tdr) {
        // don't set those because don't have the info :
        String pays = null;
		String formeJuridique = null;
		String rib = null;
        TdrTdb tdrTdb = tdr.getTdrTdb();
        
        // update client global infos
		pivotalContactService.client(tdr.getIdentifiantClientPivotal(), tdr.getNomStructure(), tdrTdb.getAnnee(), tdr.getTypeStructure(),
        		tdr.getAdresse(), tdr.getEmail(), tdr.getCp(), tdr.getVille(), pays, tdr.getTelephone(),
        		rib, formeJuridique, tdr.getSirenSiret(),
        		BigDecimal.valueOf(tdrTdb.getDotationGlobale()), BigDecimal.valueOf(tdrTdb.getReliquatAnneePrecedente()),
        		BigDecimal.valueOf(tdrTdb.getDotationAnnuelle()), BigDecimal.valueOf(tdrTdb.getNbBeneficiairesPrevisionnel()),
        		BigDecimal.valueOf(tdrTdb.getSommeUtilisee()), BigDecimal.valueOf(tdrTdb.getNbBeneficiairesApv()));
		
		// update per public info
        pivotalContactService.informationAPV(tdr.getIdentifiantClientPivotal(), "enfants", tdrTdb.getNbEnfants(), tdrTdb.getAnnee());
        pivotalContactService.informationAPV(tdr.getIdentifiantClientPivotal(), "jeunes", tdrTdb.getNbJeunes(), tdrTdb.getAnnee());
        pivotalContactService.informationAPV(tdr.getIdentifiantClientPivotal(), "adultesisoles", tdrTdb.getNbAdultesIsoles(), tdrTdb.getAnnee());
        pivotalContactService.informationAPV(tdr.getIdentifiantClientPivotal(), "seniors", tdrTdb.getNbSeniors(), tdrTdb.getAnnee());
        //Call ContactSvc.Client once, and ContactSvc.Information_APV once per public (Information_APV.Bilan_Libelle) : enfants, jeunes, adultesisoles, seniors
    }

}
