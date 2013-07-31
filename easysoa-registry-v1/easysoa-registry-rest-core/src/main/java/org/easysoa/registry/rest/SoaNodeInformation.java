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

package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.easysoa.registry.rest.jackson.JacksonModelHelper;
import org.easysoa.registry.rest.jackson.SoaDateType;
import org.easysoa.registry.rest.jackson.SoaListType;
import org.easysoa.registry.rest.jackson.SoaMapType;
import org.easysoa.registry.rest.jaxb.JaxbModelHelper;
import org.easysoa.registry.rest.jaxb.SoaNodeProperties;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.ListUtils;


/**
 * Models all information about an SOA node.
 * 
 * Uses JacksonModelHelper & JaxbModelHelper for respectively JSON/Jackson
 * & XML/JAXB serialization of its generic properties.
 * 
 * @author mdutoo
 *
 */
@XmlRootElement(name = "soaNodeInformation")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class SoaNodeInformation implements SoaNode {

    protected SoaNodeId id;
    /**
     * SoaNodeInformation properties i.e. Nuxeo's without Blobs.
     * Not serialized directly, but through (jackson or jaxb) model builder getter & setter
     */
    @JsonIgnore
    @XmlTransient
    protected Map<String, Serializable> properties;
    protected List<SoaNodeId> parentDocuments;

    protected SoaNodeInformation() {
    }

    public SoaNodeInformation(SoaNodeId id, Map<String, Serializable> properties, List<SoaNodeId> parentDocuments) {
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Serializable>() : properties;
        if (id != null) {
            this.properties.putAll(id.getDefaultPropertyValues());
        } // else edge Subproject case
        this.parentDocuments = (parentDocuments == null) ? new ArrayList<SoaNodeId>() : parentDocuments;
    }

    /**
     * JAXB model getter for SoaNodeInformation properties (Nuxeo's without Blobs).
     * (and also a tentative jackson setup).
     * because SoaNodeProperties
     * @return
     */
    @XmlElement(name="properties")
    public SoaNodeProperties getJaxbProperties() {
        return JaxbModelHelper.toSoaNodeProperties(this.properties);
    }
    
    /**
     * JAXB model setter for SoaNodeInformation properties (Nuxeo's without Blobs).
     * (and also a tentative jackson setup).
     * See conf on getter.
     * @param soaNodeProperties
     */
    public void setJaxbProperties(SoaNodeProperties soaNodeProperties) {
        this.properties = JaxbModelHelper.fromSoaNodeProperties(soaNodeProperties);
    }
    
    
    /**
     * Jackson model getter for SoaNodeInformation properties (Nuxeo's without Blobs).
     * Its @JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT) lets contained
     * objects be written as tercely as possible (ex. of int : 1, to compare with
     * ex. of long : {Long:1}). Alternatively, Id.MINIMAL_CLASS is powerful but far
     * less pretty (shows full Java class names), and As.PROPERTY is not as terce
     * (additional "property=" for ALL objects including ex. int).
     * Its @JsonSubTypes is required, else error ex. :
     * Could not resolve type id 'Long' into a subtype of [simple type, class java.io.Serializable]
     * @return
     */
    @JsonProperty("properties")
    @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(SoaDateType.class),
        @JsonSubTypes.Type(SoaMapType.class), @JsonSubTypes.Type(SoaListType.class),
        @JsonSubTypes.Type(Integer.class), @JsonSubTypes.Type(Long.class),
        @JsonSubTypes.Type(Float.class), @JsonSubTypes.Type(Double.class) })
    @JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
    public Map<String, Serializable> getJacksonProperties() {
        return JacksonModelHelper.toJacksonProperties(properties);
    }

    /**
     * Jackson model setter for SoaNodeInformation properties (Nuxeo's without Blobs).
     * See getter.
     * @param properties
     */
    @JsonProperty("properties")
    @JsonSubTypes({ @JsonSubTypes.Type(String.class), @JsonSubTypes.Type(SoaDateType.class),
        @JsonSubTypes.Type(SoaMapType.class), @JsonSubTypes.Type(SoaListType.class),
        @JsonSubTypes.Type(Integer.class), @JsonSubTypes.Type(Long.class),
        @JsonSubTypes.Type(Float.class), @JsonSubTypes.Type(Double.class) })
    @JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
    public void setJacksonProperties(Map<String, Serializable> properties) {
        this.properties = JacksonModelHelper.fromJacksonProperties(properties);
    }

    @Override
    public SoaNodeId getSoaNodeId() {
        return id;
    }

    public void setSoaNodeId(SoaNodeId id) {
        this.id = id;
    }

    @Override
    public String getSoaName() {
        return id.getName();
    }

    @Override
    public String getSubprojectId() {
        return (String) properties.get(SubprojectNode.XPATH_SUBPROJECT);
    }

    public Map<String, Serializable> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }

    @Override
    public Object getProperty(String xpath) throws Exception {
        return this.properties.get(xpath);
    }

    @Override
    public void setProperty(String xpath, Serializable value) throws Exception {
        this.properties.put(xpath, value);
    }

    public List<SoaNodeId> getParentDocuments() {
        return parentDocuments;
    }

    public void setParentDocuments(List<SoaNodeId> correlatedDocuments) {
        this.parentDocuments = correlatedDocuments;
    }

    public void addParentDocument(SoaNodeId correlatedDocument) {
        this.parentDocuments.add(correlatedDocument);
    }

    @Override
    public String getName() {
        return (String) properties.get(SoaNode.XPATH_NAME);
    }

    @Override
    public String getTitle() {
        return (String) properties.get(SoaNode.XPATH_TITLE);
    }

    @Override
    public void setTitle(String title) {
        properties.put(SoaNode.XPATH_TITLE, title);
    }

    @Override
    public String getDescription() {
        return (String) properties.get(SoaNode.XPATH_DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        properties.put(SoaNode.XPATH_DESCRIPTION, description);
    }

    @JsonIgnore // else serializable event though JsonAutoDetect, why ??
    @Override
    public boolean isPlaceholder() throws Exception {
        return (Boolean) properties.get(SoaNode.XPATH_TITLE);
    }

    @JsonIgnore
    @Override
    public void setIsPlaceholder(boolean isPlaceholder) throws Exception {
        properties.put(SoaNode.XPATH_TITLE, isPlaceholder);
    }

    @Override
    public String toString() {
        return this.id.toString();
    }

    @Override
    public List<SoaNodeId> getParentIds() throws Exception {
        Serializable[] parentsIdsArray = (Serializable[]) properties.get(XPATH_PARENTSIDS);
        List<String> parentsIdsStringList = ListUtils.toStringList(parentsIdsArray);
        List<SoaNodeId> parentsIds = new ArrayList<SoaNodeId>();
        for (String parentIdString : parentsIdsStringList) {
            SoaNodeId parentId = SoaNodeId.fromString(parentIdString);
            if (parentId != null) {
                parentsIds.add(parentId);
            }
        }
        return parentsIds;
    }

    @Override
    public SoaNodeId getParentOfType(String doctype) throws Exception {
        Serializable[] parentsIdsArray = (Serializable[]) properties.get(XPATH_PARENTSIDS);
        for (Serializable parentIdString : parentsIdsArray) {
            SoaNodeId parentId = SoaNodeId.fromString((String) parentIdString);
            if (parentId.getType().equals(doctype)) {
                return parentId;
            }
        }
        return null;
    }

	@Override
	public List<SoaNodeId> getParentsOfType(String doctype) throws Exception {
        Serializable[] parentsIdsArray = (Serializable[]) properties.get(XPATH_PARENTSIDS);
		List<SoaNodeId> res = new ArrayList<SoaNodeId>(parentsIdsArray.length);
        for (Serializable parentIdString : parentsIdsArray) {
            SoaNodeId parentId = SoaNodeId.fromString((String) parentIdString);
            if (parentId.getType().equals(doctype)) {
                res.add(parentId);
            }
        }
        return res;
	}

    @Override
    public void setParentIds(List<SoaNodeId> parentIds) throws Exception {
        List<String> parentsIdsStringList = new ArrayList<String>();
        for (SoaNodeId parentId : parentIds) {
            parentsIdsStringList.add(parentId.toString());
        }
        properties.put(XPATH_PARENTSIDS, (Serializable) parentsIdsStringList);
    }

    @Override
    public String getUuid() throws Exception {
        return (String) properties.get(XPATH_UUID);
    }
}
