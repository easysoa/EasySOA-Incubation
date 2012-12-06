
package com.axxx.dps.apv;

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

    ASSOCIATION_NAT;

    public String value() {
        return name();
    }

    public static TypeStructure fromValue(String v) {
        return valueOf(v);
    }

}
