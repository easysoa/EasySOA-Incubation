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

    private String nomStructure;

    public String getNomStructure() {
        return nomStructure;
    }

    public void setNomStructure(String nomStructure) {
        this.nomStructure = nomStructure;
    }
    
}
