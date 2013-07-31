package org.easysoa.registry.rest;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;

public class RegistryApiJaxbTest {

    private static Logger logger = Logger.getLogger(RegistryApiJaxbTest.class);
    
    @Test
    public void test() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(SoaNodeInformation.class);
        
        SoaNodeInformation soaNodeInfo = RegistryApiSamples.buildSoaNodeInformation1(null);
 
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(soaNodeInfo, bos);
        String serializedSoaNodeInfo = bos.toString();
        logger.info(serializedSoaNodeInfo);

        
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        SoaNodeInformation unmarshalledSoaNodeInfo = (SoaNodeInformation) unmarshaller.unmarshal(new StringInputStream(serializedSoaNodeInfo));
        logger.info(unmarshalledSoaNodeInfo);
        logger.info(unmarshalledSoaNodeInfo.getParentDocuments());
        logger.info(unmarshalledSoaNodeInfo.getProperties());

        Assert.assertEquals(soaNodeInfo.getProperties().size(), unmarshalledSoaNodeInfo.getProperties().size());
        Assert.assertEquals(1, unmarshalledSoaNodeInfo.getProperty("testintnative"));
        Assert.assertEquals(soaNodeInfo.getProperty("testfloat"), unmarshalledSoaNodeInfo.getProperty("testfloat"));
        Assert.assertEquals(soaNodeInfo.getProperty("testdate"), unmarshalledSoaNodeInfo.getProperty("testdate"));
        Assert.assertNotNull(unmarshalledSoaNodeInfo.getProperties());
    }
    
}