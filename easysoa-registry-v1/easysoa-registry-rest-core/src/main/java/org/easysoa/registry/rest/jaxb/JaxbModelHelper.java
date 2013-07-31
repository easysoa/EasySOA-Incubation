package org.easysoa.registry.rest.jaxb;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Helps build XML/JAXB-dedicated model of RegistryApi.
 * Used by SoaNodeInformation JAXB-specific getter & setter.
 * 
 * Ex.
 * 
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<soaNodeInformation>
    <id>
        <subprojectId>/default-domain/MyProject/Realisation_v</subprojectId>
        <name>test:http://www.easysoa.org/myService</name>
        <type>Endpoint</type>
    </id>
    <parentDocuments>
        <subprojectId>/default-domain/MyProject/Realisation_v</subprojectId>
        <name>WS:my.Service=my.ServiceImpl</name>
        <type>JavaServiceImplementation</type>
    </parentDocuments>
    <properties>
        <float value="2.1" name="testfloatnative"/>
        <string value="http://www.easysoa.org/myService" name="endp:url"/>
        <properties name="testmap">
            <long value="5" name="f"/>
            <float value="4.1" name="g"/>
            <integer value="3" name="e"/>
            <string value="d" name="c"/>
            <string value="b" name="a"/>
            <date value="2013-07-31T15:34:22.574+02:00" name="j"/>
            <double value="5.1" name="hn"/>
            <boolean value="true" name="in"/>
            <double value="5.1" name="h"/>
            <boolean value="true" name="i"/>
            <float value="4.1" name="gn"/>
            <integer value="4" name="en"/>
            <long value="5" name="fn"/>
        </properties>
        <date value="2013-07-31T15:34:22.574+02:00" name="testdate"/>
        <long value="3" name="testlongnative"/>
        <list name="testarray">
            <string value="a"/>
            <double value="3.1"/>
        </list>
        <double value="3.1" name="testdoublenative"/>
        <string value="test" name="env:environment"/>
        <boolean value="true" name="testbooleannative"/>
        <list name="testlistmap">
            <string value="a"/>
            <integer value="3"/>
            <date value="2013-07-31T15:34:22.574+02:00"/>
            <properties>
                <long value="5" name="f"/>
                <float value="4.1" name="g"/>
                <integer value="3" name="e"/>
                <string value="d" name="c"/>
                <string value="b" name="a"/>
                <date value="2013-07-31T15:34:22.574+02:00" name="j"/>
                <double value="5.1" name="hn"/>
                <boolean value="true" name="in"/>
                <double value="5.1" name="h"/>
                <boolean value="true" name="i"/>
                <float value="4.1" name="gn"/>
                <integer value="4" name="en"/>
                <long value="5" name="fn"/>
            </properties>
        </list>
        <boolean value="true" name="testboolean"/>
        <string value="test:http://www.easysoa.org/myService" name="title"/>
        <string value="test:http:||www.easysoa.org|myService" name="name"/>
        <integer value="2" name="testint"/>
        <double value="3.1" name="testdouble"/>
        <long value="3" name="testlong"/>
        <float value="2.1" name="testfloat"/>
        <integer value="1" name="testintnative"/>
        <string value="test - http://www.easysoa.org/myService" name="dc:title"/>
    </properties>
</soaNodeInformation>
 * 
 * @author mdutoo
 *
 */
public class JaxbModelHelper {
    
    private static Logger logger = Logger.getLogger(JaxbModelHelper.class);

    public static SoaNodeProperties toSoaNodeProperties(Map<String, Serializable> properties) {
        SoaNodeProperties soaNodeProperties = new SoaNodeProperties();
        if (properties == null) {
            List<SoaNodeProperty> soaNodePropertyList = new ArrayList<SoaNodeProperty>(0);
            soaNodeProperties.setList(soaNodePropertyList);
            return soaNodeProperties;
        }
        
        List<SoaNodeProperty> soaNodePropertyList = new ArrayList<SoaNodeProperty>(properties.size());
        soaNodeProperties.setList(soaNodePropertyList);
        
        for (String name : properties.keySet()) {
            Serializable value = properties.get(name);
            SoaNodeProperty soaNodeProperty = toSoaNodeProperty(value);
            if (soaNodeProperty == null && value != null) {
                logger.warn("custom object not handled: " + value.getClass() + " - " + value);
                // TODO LATER or using map ? or mapper ??
                continue;
            }
            soaNodeProperty.setName(name);
            soaNodePropertyList.add(soaNodeProperty);
        }
        return soaNodeProperties;
    }
    
    public static List<SoaNodePropertyValue> toSoaNodePropertyValues(List<Serializable> values) {
        if (values == null) {
            return new ArrayList<SoaNodePropertyValue>(0);
        }
        List<SoaNodePropertyValue> soaNodePropertyValueList = new ArrayList<SoaNodePropertyValue>(values.size());
        
        for (Serializable value : values) {
            SoaNodePropertyValue soaNodePropertyValue = toSoaNodePropertyValue(value);
            if (soaNodePropertyValue == null && value != null) {
                logger.warn("custom object not handled: " + value.getClass() + " - " + value);
                // TODO LATER or using map ? or mapper ??
                continue;
            }
            soaNodePropertyValueList.add(soaNodePropertyValue);
        }
        return soaNodePropertyValueList;
    }

    /**
     * 
     * @param values
     * @return null if null, else an SoaNodeProperty wrapper around the value
     * according to its type
     */
    @SuppressWarnings("unchecked")
    public static SoaNodeProperty toSoaNodeProperty(Serializable value) {
        if (value == null) {
            return null; // else npex happens sometimes ex. on isArray
        }
        
        if (value instanceof String) {
            return new SoaNodeStringProperty((String) value);
        } else if (value instanceof Integer) {
            return new SoaNodeIntegerProperty((Integer) value);
        } else if (value instanceof Long) {
            return new SoaNodeLongProperty((Long) value);
        } else if (value instanceof Float) {
            return new SoaNodeFloatProperty((Float) value);
        } else if (value instanceof Double) {
            return new SoaNodeDoubleProperty((Double) value);
        } else if (value instanceof Boolean) {
            return new SoaNodeBooleanProperty((Boolean) value);
        } else if (value instanceof Date) {
            return new SoaNodeDateProperty((Date) value);
        } else if (value instanceof Calendar) {
            // (else JsonMappingException: Could not resolve type id 'GregorianCalendar' into a
            // subtype of [simple type, class java.io.Serializable]
            return new SoaNodeDateProperty(((Calendar) value).getTime());
        } else if (value instanceof List<?>) {
            return new SoaNodeListProperty(toSoaNodePropertyValues((List<Serializable>) value)); // TODO check ?
        } else if (value.getClass().isArray()) {
            // (else ex. JsonMappingException: Could not resolve type id 'String;' into a
            // subtype of [simple type, class java.io.Serializable]
            int len = Array.getLength(value);
            ArrayList<SoaNodePropertyValue> list = new ArrayList<SoaNodePropertyValue>(len);
            for (int i = 0; i < len; i++) {
                list.add(toSoaNodePropertyValue((Serializable) Array.get(value, i)));
            }
            return new SoaNodeListProperty(list);
        } else if (value instanceof Map<?,?>) {
            SoaNodeProperties soaNodeProperties = toSoaNodeProperties((Map<String,Serializable>) value); // TODO check ?
            SoaNodePropertiesProperty soaNodePropertiesProperty = new SoaNodePropertiesProperty(soaNodeProperties.getList());
            //return toSoaNodePropertiesProperty((Map<String,Serializable>) value); // TODO check ?
            return soaNodePropertiesProperty;
            
        } else { // value not null here
            logger.warn("custom object not handled: " + value.getClass() + " - " + value);
            // TODO also throw Exception ?
            // TODO LATER or using map ? or mapper ??
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    public static SoaNodePropertyValue toSoaNodePropertyValue(Serializable value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof String) {
            return new SoaNodeString(value);
        } else if (value instanceof Integer) {
            return new SoaNodeInteger(value);
        } else if (value instanceof Long) {
            return new SoaNodeLong(value);
        } else if (value instanceof Float) {
            return new SoaNodeFloat(value);
        } else if (value instanceof Double) {
            return new SoaNodeDouble(value);
        } else if (value instanceof Boolean) {
            return new SoaNodeBoolean(value);
        } else if (value instanceof Date) {
            return new SoaNodeDate(value);
        } else if (value instanceof List<?>) {
            return new SoaNodeList(toSoaNodePropertyValues((List<Serializable>) value)); // TODO check ?
        } else if (value instanceof Map<?,?>) {
            return toSoaNodeProperties((Map<String,Serializable>) value); // TODO check ?
        } else { // value not null here
            logger.warn("custom object not handled: " + value.getClass() + " - " + value);
            // TODO also throw Exception ?
            // TODO LATER or using map ? or mapper ??
            return null;
        }
    }


    public static HashMap<String, Serializable> fromSoaNodeProperties(
            SoaNodeProperties soaNodeProperties) {
        List<SoaNodeProperty> list = soaNodeProperties.getList();
        if (list == null) {
            return new HashMap<>(0);
        }
        HashMap<String, Serializable> map = new HashMap<>(list.size());
        for (SoaNodeProperty soaNodeProperty : list) {
            map.put(soaNodeProperty.getName(), fromSoaNodeProperty(soaNodeProperty));
        }
        return map;
    }
    
    public static Serializable fromSoaNodeProperty(/*SoaNodePropertyValue*/SoaNodeProperty value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof SoaNodeStringProperty) {
            return ((SoaNodeStringProperty) value).getValue();
        } else if (value instanceof SoaNodeIntegerProperty) {
            return ((SoaNodeIntegerProperty) value).getValue();
        } else if (value instanceof SoaNodeLongProperty) {
            return ((SoaNodeLongProperty) value).getValue();
        } else if (value instanceof SoaNodeFloatProperty) {
            return ((SoaNodeFloatProperty) value).getValue();
        } else if (value instanceof SoaNodeDoubleProperty) {
            return ((SoaNodeDoubleProperty) value).getValue();
        } else if (value instanceof SoaNodeBooleanProperty) {
            return ((SoaNodeBooleanProperty) value).isValue();
        } else if (value instanceof SoaNodeDateProperty) {
            return ((SoaNodeDateProperty) value).getValue();
        } else if (value instanceof SoaNodeListProperty) {
            return fromSoaNodePropertyValues(((SoaNodeListProperty) value).getValue());
        } else if (value instanceof SoaNodePropertiesProperty) {
            SoaNodeProperties soaNodeProperties = new SoaNodeProperties();
            soaNodeProperties.setList(((SoaNodePropertiesProperty) value).getList());
            return fromSoaNodeProperties(soaNodeProperties);
            
        } else { // value not null here
            logger.warn("unknown SoaNodePropertyValue impl, should not happen: "
                    + value.getClass() + " - " + value);
            // TODO LATER or using map ? or mapper ??
            return null;
        }
    }

    public static Serializable fromSoaNodePropertyValue(SoaNodePropertyValue value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof SoaNodeString) {
            return ((SoaNodeString) value).getValue();
        } else if (value instanceof SoaNodeInteger) {
            return ((SoaNodeInteger) value).getValue();
        } else if (value instanceof SoaNodeLong) {
            return ((SoaNodeLong) value).getValue();
        } else if (value instanceof SoaNodeFloat) {
            return ((SoaNodeFloat) value).getValue();
        } else if (value instanceof SoaNodeDouble) {
            return ((SoaNodeDouble) value).getValue();
        } else if (value instanceof SoaNodeBoolean) {
            return ((SoaNodeBoolean) value).isValue();
        } else if (value instanceof SoaNodeDate) {
            return ((SoaNodeDate) value).getValue();
        } else if (value instanceof SoaNodeList) {
            return fromSoaNodePropertyValues(((SoaNodeList) value).getValue());
        } else if (value instanceof SoaNodeProperties) {
            SoaNodeProperties soaNodeProperties = new SoaNodeProperties();
            soaNodeProperties.setList(((SoaNodeProperties) value).getList());
            return fromSoaNodeProperties(soaNodeProperties);
            
        } else { // value not null here
            logger.warn("unknown SoaNodePropertyValue impl, should not happen: "
                    + value.getClass() + " - " + value);
            // TODO LATER or using map ? or mapper ??
            return null;
        }
    }
    
    public static Serializable fromSoaNodePropertyValues(List<SoaNodePropertyValue> propertyValues) {
        if (propertyValues == null) {
            return new HashMap<>(0);
        }
        ArrayList<Serializable> values = new ArrayList<>(propertyValues.size());
        for (SoaNodePropertyValue soaNodePropertyValue : propertyValues) {
            values.add(fromSoaNodePropertyValue(soaNodePropertyValue));
        }
        return values;
    }

}
