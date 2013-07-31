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

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper for Date or Calendar impls value
 * 
 * @author mdutoo
 *
 */
//@JsonTypeName("date")
@XmlRootElement(name="date")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoaNodeDate extends SoaNodePropertyValue {

    /**
     * JAXB serializes it as pseudo ISO8601 ex. 2013-07-31T11:08:28.693+02:00
     */
    @XmlAttribute
    private Date value;

    public SoaNodeDate() {
    }

    public SoaNodeDate(Serializable value) {
        this.setValue((Date) value);
    }

    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }

}
