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

import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;


/**
 * Wraps a generic value (possibly a list entry), extend it with specific value types
 * with "value" field as XML attribute (annotated @XmlAttribute).
 * 
 * Uses @XmlSeeAlso to tell JAXB all of its possible implementations,
 * which information is used to serialize generically typed
 * SoaNodePropertyValue fields (provided they are annotated by @XmlElementRef).
 * Alternatively @XmlElementRefs({ @XmlElementRef(name="string", type=SoaNodeStringProperty.class), ...
 * put on all generic references (i.e. SoaNodeProperties/List(Property).value)
 * could have been used, but this requires less configuration and is closer
 * the the spirit of inheritance.
 * 
 * @author mdutoo
 *
 */
@XmlSeeAlso({SoaNodeString.class, SoaNodeInteger.class, SoaNodeLong.class,
    SoaNodeFloat.class, SoaNodeDouble.class, SoaNodeBoolean.class,
    SoaNodeDate.class, SoaNodeList.class, SoaNodeProperties.class})
@JsonSubTypes({ @JsonSubTypes.Type(SoaNodeString.class), @JsonSubTypes.Type(SoaNodeInteger.class), @JsonSubTypes.Type(SoaNodeLong.class),
    @JsonSubTypes.Type(SoaNodeFloat.class), @JsonSubTypes.Type(SoaNodeDouble.class), @JsonSubTypes.Type(SoaNodeBoolean.class),
    @JsonSubTypes.Type(SoaNodeDate.class), @JsonSubTypes.Type(SoaNodeList.class), @JsonSubTypes.Type(SoaNodeProperties.class) })
public abstract class SoaNodePropertyValue {

}
