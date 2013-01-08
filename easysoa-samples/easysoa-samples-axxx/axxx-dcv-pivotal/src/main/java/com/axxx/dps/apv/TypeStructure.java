
package com.axxx.dps.apv;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeStructure.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeStructure">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ASSOCIATION_NAT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeStructure")
@XmlEnum
public enum TypeStructure {

    ASSOCIATION_NAT("Association nat."),
    MISSION_LOCALE("Mission locale"),
    DELEGATION_TERRITORIALE_ASSOCIATIVE("Délegation Territoriale Associative"),
    CENTRE_SOCIAL("Centre social ou socio-culturel"),
    AUTRE("Autre");

    @XmlElement(nillable=false, required=true)
    private String name = null;

    private TypeStructure(String name) {
        this.name = name;
    }    
    
    public String value() {
        return name;
    }

    public static TypeStructure fromValue(String v) {
        //return valueOf(v);
        if(ASSOCIATION_NAT.name().equals(v)){
            return ASSOCIATION_NAT;
        } else if(MISSION_LOCALE.name().equals(v)){
            return MISSION_LOCALE;
        } else if(DELEGATION_TERRITORIALE_ASSOCIATIVE.name().equals(v)){
            return DELEGATION_TERRITORIALE_ASSOCIATIVE;
        } else if(CENTRE_SOCIAL.name().equals(v)){
            return CENTRE_SOCIAL;
        } else if(AUTRE.name().equals(v)){
            return AUTRE;
        } else {
            return null;
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }    

}
