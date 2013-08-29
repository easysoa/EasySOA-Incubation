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
 * This property impl replaces a generic List or array by a List of SoaNodePropertyValue.
 * (That may be in maps in Nuxeo complex properties, see
 * http://answers.nuxeo.com/questions/4417/how-to-programatically-populate-complex-multivalued-fields )
 * 
 * The regular way to serialize a list in JAXB is to model it in such a wrapper element,
 * see http://theopentutorials.com/tutorials/java/jaxb/jaxb-marshalling-and-unmarshalling-list-of-objects/
 * 
 * @author mdutoo
 *
 */
//@JsonTypeName("list")
@XmlRootElement(name="list")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoaNodeListProperty extends SoaNodeProperty {

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
     * is needed in XML (and to tell about the which class to use for the value), and so it
     * advantageously replaces @XmlElementWrapper("list").
     */
    //@JsonTypeInfo(use = Id.Name, include = As.WRAPPER_OBJECT)
    @XmlElementRef
    private List<SoaNodePropertyValue> value;

    public SoaNodeListProperty() {
    }

    public SoaNodeListProperty(List<SoaNodePropertyValue> list) {
        this.setValue((List<SoaNodePropertyValue>) list);
    }

    public List<SoaNodePropertyValue> getValue() {
        return value;
    }

    public void setValue(List<SoaNodePropertyValue> value) {
        this.value = value;
    }
}
