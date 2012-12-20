package com.axxx.dps.apv.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.axxx.dps.apv.persistence.GenericEntity;


/**
 * LATER
 * 
 * @author mdutoo
 *
 */
@Entity
@Table(name = "apv_beneffi")
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "hibernate_sequence")
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BenefFinancement extends GenericEntity<BenefFinancement> {

    private static final long serialVersionUID = 1811609275397004234L;

    // enfants_l___coutsfinancement_couts
    // couttransport, couthebergement, coutrestauration, coutloisirs, coutautres
    // 0   0   0   0   220
    // 66  50  80.660004   16.67   0
    
    // enfants_l_in_sources
    // apvaxxx, caf, msa, conseilregional, conseilgeneral, communeintercommunalite, organismepdp, autofinancement, financeurprive, autre
    // 60  0   0   0   0   60  0   100 0   0

    
}
