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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helps build JSON/Jackson-dedicated model of RegistryApi.
 * Used by SoaNodeInformation Jackson-specific getter & setter.
 * 
 * Example :
{
  "id" : {
    "subprojectId" : "/default-domain/MyProject/Realisation_v",
    "name" : "test:http://www.easysoa.org/myService",
    "type" : "Endpoint"
  },
  "parentDocuments" : [ {
    "subprojectId" : "/default-domain/MyProject/Realisation_v",
    "name" : "WS:my.Service=my.ServiceImpl",
    "type" : "JavaServiceImplementation"
  } ],
  "properties" : {
    "testfloatnative" : {
      "Float" : 2.1
    },
    "testmap" : {
      "map" : {
        "value" : {
          "f" : {
            "Long" : 5
          },
          "g" : {
            "Float" : 4.1
          },
          "e" : 3,
          "c" : "d",
          "a" : "b",
          "j" : {
            "date" : "2013-07-31T13:38:51.405+0000"
          },
          "hn" : 5.1,
          "in" : true,
          "h" : 5.1,
          "i" : true,
          "gn" : {
            "Float" : 4.1
          },
          "en" : 4,
          "fn" : {
            "Long" : 5
          }
        }
      }
    },
    "endp:url" : "http://www.easysoa.org/myService",
    "testdate" : {
      "date" : "2013-07-31T13:38:51.405+0000"
    },
    "testlongnative" : {
      "Long" : 3
    },
    "testdoublenative" : 3.1,
    "testarray" : {
      "list" : {
        "value" : [ "a", 1, 2, {
          "Long" : 3
        }, {
          "Long" : 3
        } ]
      }
    },
    "testbooleannative" : true,
    "env:environment" : "test",
    "testboolean" : true,
    "testlistmap" : {
      "list" : {
        "value" : [ "a", 4, {
          "Long" : 5
        }, {
          "Float" : 4.1
        }, 5.1, true, {
          "date" : "2013-07-31T13:38:51.405+0000"
        }, {
          "map" : {
            "value" : {
              "f" : {
                "Long" : 5
              },
              "g" : {
                "Float" : 4.1
              },
              "e" : 3,
              "c" : "d",
              "a" : "b",
              "j" : {
                "date" : "2013-07-31T13:38:51.405+0000"
              },
              "hn" : 5.1,
              "in" : true,
              "h" : 5.1,
              "i" : true,
              "gn" : {
                "Float" : 4.1
              },
              "en" : 4,
              "fn" : {
                "Long" : 5
              }
            }
          }
        } ]
      }
    },
    "title" : "test:http://www.easysoa.org/myService",
    "testdouble" : 3.1,
    "testint" : 2,
    "name" : "test:http:||www.easysoa.org|myService",
    "testfloat" : {
      "Float" : 2.1
    },
    "testlong" : {
      "Long" : 3
    },
    "testintnative" : 1,
    },
    "dc:title" : "test - http://www.easysoa.org/myService"
  }
}
 * @author mdutoo
 *
 */
public class JacksonModelHelper {

    public static HashMap<String, Serializable> toJacksonProperties(Map<String, Serializable> properties) {
        HashMap<String, Serializable> map = new HashMap<String, Serializable>(properties.size());
        for (String name : properties.keySet()) {
            map.put(name, toJacksonValue(properties.get(name)));
        }
        return map;
    }

    /**
     * 
     * @param value
     * @return null if null, else SoaDateType if Date, SoaMapType if Map,
     * SoaListType if List or array, else itself
     */
    @SuppressWarnings("unchecked")
    public static Serializable toJacksonValue(Serializable value) {
        if (value == null) {
            return value; // else npex happens sometimes ex. on isArray
        }
        if (value instanceof Date) {
            return new SoaDateType((Date) value);
        }
        if (value instanceof Calendar) {
            // (else JsonMappingException: Could not resolve type id 'GregorianCalendar' into a
            // subtype of [simple type, class java.io.Serializable]
            return new SoaDateType(((Calendar) value).getTime());
        }
        if (value instanceof List<?>) {
            List<Serializable> listValue = (List<Serializable>) value; // TODO check ?
            ArrayList<Serializable> list = new ArrayList<Serializable>(listValue.size());
            for (Serializable item : listValue) {
                list.add(toJacksonValue(item));
            }
            return new SoaListType(list);
        }
        if (value.getClass().isArray()) {
            // (else ex. JsonMappingException: Could not resolve type id 'String;' into a
            // subtype of [simple type, class java.io.Serializable]
            int len = Array.getLength(value);
            ArrayList<Serializable> list = new ArrayList<Serializable>(len);
            for (int i = 0; i < len; i++) {
                list.add(toJacksonValue((Serializable) Array.get(value, i)));
            }
            return new SoaListType(list);
        }
        if (value instanceof Map<?,?>) {
            return new SoaMapType(toJacksonProperties((HashMap<String,Serializable>) value)); // TODO check ?
        }
        return value;
    }


    public static HashMap<String, Serializable> fromJacksonProperties(Map<String, Serializable> properties) {
        HashMap<String, Serializable> map = new HashMap<String, Serializable>(properties.size());
        for (String name : properties.keySet()) {
            map.put(name, fromJacksonValue(properties.get(name)));
        }
        return map;
    }

    public static Serializable fromJacksonValue(Serializable value) {
        if (value instanceof SoaDateType) {
            return ((SoaDateType) value).getValue();
        }
        if (value instanceof SoaListType) {
            List<Serializable> listValue = ((SoaListType) value).getValue();
            ArrayList<Serializable> list = new ArrayList<Serializable>(listValue.size());
            for (Serializable item : listValue) {
                list.add(fromJacksonValue(item));
            }
            return list;
        }
        if (value instanceof SoaMapType) {
            return fromJacksonProperties(((SoaMapType) value).getValue());
        }
        return value;
    }
    
}
