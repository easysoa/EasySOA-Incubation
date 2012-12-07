package com.axxx.dps.apv.ws;

import javax.xml.bind.annotation.XmlElement;

/**
 * TODO XmlAdapter to have the value as serialization
 * 
 * Association nat., Mission locale, Délegation Territoriale Associative, Centre social ou socio-culturel, (Autre...)
 * 
 * @author mdutoo
 *
 */
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
