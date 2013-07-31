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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.types.ids.EndpointId;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.easysoa.registry.types.ids.ServiceNameType;
import org.easysoa.registry.types.ids.SoaNodeId;


/**
 * Provides common samples to serialization, client & server tests.
 * @author mdutoo
 *
 */
public class RegistryApiSamples {

    public static SoaNodeInformation buildSoaNodeInformation1(String title) {
        if (title == null) {
            title = "test:http://www.easysoa.org/myService";
        }
        SoaNodeId soaNodeId =  new EndpointId("/default-domain/MyProject/Realisation_v", "test", "http://www.easysoa.org/myService");
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("name", title.replaceAll("/", "|"));
        properties.put("title", title);
        properties.put("testint", new Integer(2));
        properties.put("testintnative", 1);
        properties.put("testlongnative", new Long(3).longValue());
        properties.put("testlong", new Long(3));
        properties.put("testfloat", new Float(2.1));
        properties.put("testfloatnative", new Float(2.1).floatValue());
        properties.put("testdouble", new Double(3.1));
        properties.put("testdoublenative", new Double(3.1).doubleValue());
        properties.put("testboolean", new Boolean(true));
        properties.put("testbooleannative", true);
        properties.put("testdate", new Date());
        ArrayList<Serializable> testlist = buildTestList1();
        properties.put("testlist", testlist);
        properties.put("testarray", new Object[] { "a", 1, new Integer(2), new Long(3).longValue(), new Long(3),
                new Float(2.1), new Float(2.1).floatValue(), new Double(3.1), new Double(3.1).doubleValue() });
        HashMap<String,Serializable> testmap = buildTestMap1();
        properties.put("testmap", testmap);
        ArrayList<Serializable> testlistmap = new ArrayList<Serializable>();
        testlistmap.add(testmap);
        properties.put("testlistmap", testlistmap);
        List<SoaNodeId> parentDocuments = new ArrayList<SoaNodeId>();
        parentDocuments.add(new SoaNodeId("/default-domain/MyProject/Realisation_v", "JavaServiceImplementation",
                new ServiceImplementationName(ServiceNameType.WEB_SERVICE, "my.Service", "my.ServiceImpl").toString()));
        SoaNodeInformation soaNodeInfo = new SoaNodeInformation(soaNodeId, properties, parentDocuments);
        soaNodeInfo.getProperties().put("testclone", new HashMap<String,Serializable>(properties));
        //soaNodeInfo.setIsPlaceholder(isPlaceholder)
        return soaNodeInfo;
    }

    public static ArrayList<Serializable> buildTestList1() {
        ArrayList<Serializable> testlist = new ArrayList<Serializable>();
        testlist.add("a");
        testlist.add("b");
        testlist.add(3);
        testlist.add(new Integer(4));
        testlist.add(new Long(5));
        testlist.add(new Long(5).longValue());
        testlist.add(new Float(4.1));
        testlist.add(new Float(4.1).floatValue());
        testlist.add(new Double(5.1));
        testlist.add(new Double(5.1).doubleValue());
        testlist.add(new Boolean(true));
        testlist.add(true);
        testlist.add(new Date());
        return testlist;
    }

    public static HashMap<String, Serializable> buildTestMap1() {
        HashMap<String,Serializable> testmap = new HashMap<String,Serializable>();
        testmap.put("a", "b");
        testmap.put("c", "d");
        testmap.put("e", new Integer(3));
        testmap.put("en", 4);
        testmap.put("f", new Long(5));
        testmap.put("fn", new Long(5).longValue());
        testmap.put("g", new Float(4.1));
        testmap.put("gn", new Float(4.1).floatValue());
        testmap.put("h", new Double(5.1));
        testmap.put("hn", new Double(5.1).doubleValue());
        testmap.put("i", new Boolean(true));
        testmap.put("in", true);
        testmap.put("j", new Date());
        return testmap;
    }
    
}
