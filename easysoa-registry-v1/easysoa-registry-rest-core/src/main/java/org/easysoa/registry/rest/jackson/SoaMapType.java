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

package org.easysoa.registry.rest.jackson;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeName;


/**
 * Map wrapper that replaces Map impls.
 * 
 * Such a wrapper object allows to annotate its list field by
 * @JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
 * which will let contained objects be written as tercely as possible
 * (ex. of int : 1, to compare with ex. of long : {Long:1}), which wouldn't have
 * been possible by extending ex. HashMap<String,Serializable> instead.
 * 
 * This solution requires the json map / object to be wrapped twice : 
 * "map" : { "value" : { "a":"a", "b":1, "c":"etc." } }
 * but this is a (comparatively small) price to pay to preserve said inner terceness
 * (which using @JsonValue woudln't have) while not writing custom Json(De)Serializer.
 * And in any way, such a wrapper object is required to specify the "map" type
 * (rather than ex. a long : {Long:1}), and this solution also allows
 * to handle any Map impl (ex. HashMap) without specifying all of them among
 * @JsonSubTypes({ @JsonSubTypes.Type(HashMap.class)... of encompassing SoaListType
 * or SoaMapType.
 * 
 * @author mdutoo
 *
 */
@JsonTypeName("map")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class SoaMapType implements Serializable {
    
    private static final long serialVersionUID = 7770164276462168639L;
    
    /**
     * Its @JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT) lets contained
     * objects be written as tercely as possible (ex. of int : 1, to compare with
     * ex. of long : {Long:1}). Alternatively, Id.MINIMAL_CLASS is powerful but far
     * less pretty (shows full Java class names), and As.PROPERTY is not as terce
     * (additional "property=" for ALL objects including ex. int) and possibly a
     * source of conflict with other map elements. 
     * Its @JsonSubTypes is required, else error ex. :
     * Could not resolve type id 'Long' into a subtype of [simple type, class java.io.Serializable]
     */
    @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(SoaDateType.class),
        @JsonSubTypes.Type(SoaMapType.class), @JsonSubTypes.Type(SoaListType.class),
        @JsonSubTypes.Type(Integer.class), @JsonSubTypes.Type(Long.class),
        @JsonSubTypes.Type(Float.class), @JsonSubTypes.Type(Double.class)})
    @JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
    protected Map<String, Serializable> value;
    
    public Map<String, Serializable> getValue() {
        return value;
    }

    public void setValue(Map<String, Serializable> value) {
        this.value = value;
    }

    public SoaMapType() {
        super();
    }

    public SoaMapType(Map<String, Serializable> value) {
        this.value = value;
    }

    /*
    public SoaPropertiesType(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SoaPropertiesType(int initialCapacity) {
        super(initialCapacity);
    }

    public SoaPropertiesType(Map<? extends String, ? extends Object> m) {
        super(m);
    }
    */

}
