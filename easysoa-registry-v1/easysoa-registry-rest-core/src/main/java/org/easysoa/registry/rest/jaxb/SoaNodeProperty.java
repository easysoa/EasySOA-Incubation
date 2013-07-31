/**
 * EasySOA Registry
 * Copyright 2011-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.rest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * Wraps a generic property (i.e. map entry or at some time maybe a mapped object field),
 * extend it with specific property types with "value" field as XML attribute
 * (annotated @XmlAttribute).
 * 
 * Such an inheritance tree separated from SoaNodePropertyValue (instead of
 * a concrete SoaNodeProperty having an SoaNodePropertyValue field) is used
 * to allow a tercer output than Map's <entry><key>name</key><value>value</value</entry>...
 * It has also been preferred to a SoaNodePropertyOrValue with an optional name field
 * to ensure that all Map-like subelements have a name.
 * 
 * Uses @XmlSeeAlso to tell JAXB all of its possible implementations,
 * which information is used to serialize generically typed
 * SoaNodeProperty fields (provided they are annotated by @XmlElementRef).
 * Alternatively @XmlElementRefs({ @XmlElementRef(name="string", type=SoaNodeStringProperty.class), ...
 * put on all generic references (i.e. SoaNodeProperties/List(Property).value)
 * could have been used, but this requires less configuration and is closer
 * the the spirit of inheritance.
 * 
 * @author mdutoo
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({SoaNodeStringProperty.class, SoaNodeIntegerProperty.class, SoaNodeLongProperty.class,
    SoaNodeFloatProperty.class, SoaNodeDoubleProperty.class, SoaNodeBooleanProperty.class,
    SoaNodeDateProperty.class, SoaNodeListProperty.class, SoaNodePropertiesProperty.class})
@JsonSubTypes({ @JsonSubTypes.Type(SoaNodeStringProperty.class), @JsonSubTypes.Type(SoaNodeIntegerProperty.class), @JsonSubTypes.Type(SoaNodeLongProperty.class),
    @JsonSubTypes.Type(SoaNodeFloatProperty.class), @JsonSubTypes.Type(SoaNodeDoubleProperty.class), @JsonSubTypes.Type(SoaNodeBooleanProperty.class),
    @JsonSubTypes.Type(SoaNodeDateProperty.class), @JsonSubTypes.Type(SoaNodeListProperty.class), @JsonSubTypes.Type(SoaNodePropertiesProperty.class) })
public abstract class SoaNodeProperty {

    @XmlAttribute
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
