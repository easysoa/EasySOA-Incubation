package org.easysoa.registry.rest;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.easysoa.registry.rest.jackson.JacksonModelHelper;
import org.easysoa.registry.rest.jackson.SoaListType;
import org.easysoa.registry.rest.jackson.SoaMapType;
import org.junit.Test;

public class RegistryApiJacksonTest {

    private static Logger logger = Logger.getLogger(RegistryApiJacksonTest.class);


    @Test
    public void test() throws Exception {
        
        // marshalling jaxb-configured model using jackson :
        // see http://ondra.zizka.cz/stranky/programovani/java/jaxb-json-jackson-howto.texy
        ObjectMapper mapper = new ObjectMapper();
        
        /// mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false); // for Date, see SoaNodeDate
        //mapper.getSerializationConfig().withDateFormat(new SoaDateType().getDateFormat());
        ////mapper.getDeserializationConfig().withDateFormat(new SoaDateType().getDateFormat()); // custom
        
        //AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        // make deserializer use JAXB annotations (only)
        //mapper.getDeserializationConfig().withAnnotationIntrospector(introspector); // for Date, see SoaNodeDate
        // make serializer use JAXB annotations (only)
        //mapper.getSerializationConfig().withAnnotationIntrospector(introspector); // for Date, see SoaNodeDate
        
        ObjectWriter prettyPrintWriter = mapper.defaultPrettyPrintingWriter(); // see http://stackoverflow.com/questions/6176881/how-do-i-make-jacksons-build-method-pretty-print-its-json-output

        
        HashMap<String, Serializable> testmap = RegistryApiSamples.buildTestMap1();

        ByteArrayOutputStream testSoaPropertiesBos = new ByteArrayOutputStream();
        prettyPrintWriter.writeValue(testSoaPropertiesBos, JacksonModelHelper.toJacksonValue(testmap));
        String serializedTestSoaProperties = testSoaPropertiesBos.toString();
        System.out.println(serializedTestSoaProperties);
        
        // unmarshalling :
        SoaMapType deserializedTestSoaProperties = mapper.readValue(new StringInputStream(serializedTestSoaProperties), SoaMapType.class);
        System.out.println(deserializedTestSoaProperties);
        System.out.println(JacksonModelHelper.fromJacksonValue(deserializedTestSoaProperties));
        
        
        ArrayList<Serializable> testlist = RegistryApiSamples.buildTestList1();
        
        ByteArrayOutputStream testSoaListBos = new ByteArrayOutputStream();
        prettyPrintWriter.writeValue(testSoaListBos, JacksonModelHelper.toJacksonValue(testlist));
        String serializedTestSoaList = testSoaListBos.toString();
        System.out.println(serializedTestSoaList);
        
        // unmarshalling :
        SoaListType deserializedTestSoaList = mapper.readValue(new StringInputStream(serializedTestSoaList), SoaListType.class);
        System.out.println(deserializedTestSoaList);
        System.out.println(JacksonModelHelper.fromJacksonValue(deserializedTestSoaList));


        SoaNodeInformation soaNodeInfo = RegistryApiSamples.buildSoaNodeInformation1(null);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        prettyPrintWriter.writeValue(bos, soaNodeInfo);
        String serializedSoaNodeInfo = bos.toString();
        System.out.println(serializedSoaNodeInfo);
        
        // unmarshalling :
        SoaNodeInformation deserializedSoaNodeInfo = mapper.readValue(new StringInputStream(serializedSoaNodeInfo), SoaNodeInformation.class);
        System.out.println(deserializedSoaNodeInfo);
        System.out.println(deserializedSoaNodeInfo.getParentDocuments());
        System.out.println(deserializedSoaNodeInfo.getProperties());

        Assert.assertEquals(soaNodeInfo.getProperties().size(), deserializedSoaNodeInfo.getProperties().size());
        Assert.assertEquals(1, deserializedSoaNodeInfo.getProperty("testintnative"));
        Assert.assertEquals(soaNodeInfo.getProperty("testfloat"), deserializedSoaNodeInfo.getProperty("testfloat"));
        Assert.assertEquals(soaNodeInfo.getProperty("testdate"), deserializedSoaNodeInfo.getProperty("testdate"));
        Assert.assertNotNull(deserializedSoaNodeInfo.getProperties());
    }
    
}
