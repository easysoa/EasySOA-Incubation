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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This property impl replaces a Map by a List of SoaNodeProperty.
 * 
 * Otherwise Map would by serialized as <entry><key>name</key><value>value</value</entry>...
 * with the wrong namespaces, and the regular way to handle it in JAXB is using a (Map)Adapter
 * see http://www.javacodegeeks.com/2013/03/jaxb-and-java-util-map.html , but our way allows
 * even more control with less JAXB specificities.
 * 
 * @author mdutoo
 *
 */
//@JsonTypeName("properties")
@XmlRootElement(name="properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoaNodePropertiesProperty extends SoaNodeProperty {

    /**
     * @XmlElementRef tells it to use the class name as XML tag and vice versa ("substitution").
     * Otherwise xsi:type=class is used to know the actual class to use for the list item, ex. 
     * <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="soaNodeLongProperty"...
     * which is way longer, uglier and error-prone, see
     * http://blog.bdoughan.com/2010/11/jaxb-and-inheritance-using-substitution.html 
     * 
     * It can alternatively be done by @XmlSeeAlso({SoaNodeStringProperty.class, SoaNodeIntegerProperty.class ...
     * set on this class, but NOT on the parent SoaNodeProperty class and it's longer.
     * 
     * No need for @XmlElementRefs({ @XmlElementRef(name="string", type=SoaNodeStringProperty.class) ...
     * because it's said in @XmlSeeAlso in SoaNodeProperty.
     * NB. @XmlElementRefs({ @XmlElementRef(name="string", type=SoaNodeString.class) ... List<Serializable>
     * (or @XmlAnyElement(lax=true) ?) might work but in JAXB it's far better to use
     * a wrapper object for better serialization control since in any way an encompassing tag
     * is needed in XML (and to tell about the which class to use for the value, and so it
     * advantageously replaces @XmlElementWrapper("properties").
     */
    //@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
    @XmlElementRef
    private List<SoaNodeProperty> list;

    public SoaNodePropertiesProperty() {
    }

    public SoaNodePropertiesProperty(List<SoaNodeProperty> list) {
        this.list = list;
    }

    public List<SoaNodeProperty> getList() {
        return list;
    }

    public void setList(List<SoaNodeProperty> list) {
        this.list = list;
    }

}
