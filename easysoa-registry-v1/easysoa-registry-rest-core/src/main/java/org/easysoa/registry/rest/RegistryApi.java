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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST (JSON & XML) discovery API.
 * 
 * Available RESTful operations are defined in JAXRS.
 *
 * Powerful but complex API allowing to add and return any SOA elements to and from Nuxeo,
 * with all Nuxeo properties they bear.
 * It is complex in a business way because it requires knowing the underlaying EasySOA
 * metamodel in Nuxeo.
 * 
 * Note : to get less complicated results from queries, use the SimpleRegistryService.
 * The SimpleRegistryService returns simple objects (through JAXB) with direct access
 * to the most common SOA Node properties.
 * 
 * Its implementation is complex technically in order to handle SoaNodeInformation properties
 * (actually, Nuxeo document properties) of any kind including any complex object types
 * as Map<String, Serializable>).
 * 
 * 
 * =================================================
 * JSON API
 * 
 * In JSON, handling SoaNodeInformation properties (actually, Nuxeo document properties)
 * of any kind including List & Map is done with the help of the Jackson JSON provider
 * with annotation-only configuration, as well as a Jackson-dedicated model built on-the-fly
 * (see SoaNodeInformation and JacksonModelHelper).
 * 
 * It wraps returned arrays with root elements because it is a small price to pay (also
 * for clients) to avoid configuring custom JSON / JAXRS providers such as the obsolete
 * JsonMessageReader and JsonMessageWriter in easysoa-registry-rest-core module.
 * 
 * The easiest way to consume it in Java is to reuse the original Java API and
 * (Jackson-serialized) model. This can be done by using the JsonRegistryApi (which
 * additionally tells the JAXRS client stack to use JSON) and by depending from the
 * minimal easysoa-registry-rest-core module (this class' module), which allows to
 * consume it using :
 * * the default (Nuxeo's) Jersey client, see the easysoa-registry-rest-client module
 * * CXF or FraSCAti / CXF clients, see the easysoa-registry-integration-base module
 * * or any other JAXRS client configured with the Jackson JSON provider.
 * 
 * Beyond that, this API can be consumed
 * * by writing the JSON "by hand". This API is as terce as possible in the common
 * cases (ex. String, int or boolean properties...).
 * * by developing clients for this API in another language.
 * 
 * Example :
 *
{
  "id" : {
    "subprojectId" : "/default-domain/MyProject/Realisation_v",
    "name" : "MyService0",
    "type" : "InformationService"
  },
  "parentDocuments" : null,
  "properties" : {
    "spnode:visibleSubprojectsCsv" : "'/default-domain/MyProject/Realisation_v'",
    "spnode:subproject" : "/default-domain/MyProject/Realisation_v",
    "iserv:operations" : {
      "map" : {
        "value" : {
        }
      }
    },
    "ecm:uuid" : "7d86df8f-7426-4e78-babd-e106356c8ad9",
    "dc:creator" : "Administrator",
    "dc:contributors" : {
      "list" : {
        "value" : [ "Administrator" ]
      }
    },
    "dc:created" : {
      "date" : "2013-07-31T13:42:43.040+0000"
    },
    "spnode:visibleSubprojects" : {
      "list" : {
        "value" : [ "/default-domain/MyProject/Realisation_v" ]
      }
    },
    "soan:isplaceholder" : false,
    "dc:subjects" : {
      "map" : {
        "value" : {
        }
      }
    },
    "soan:name" : "MyService0",
    "dc:modified" : {
      "date" : "2013-07-31T13:42:43.040+0000"
    },
    "soan:parentIds" : {
      "map" : {
        "value" : {
        }
      }
    },
    "dc:lastContributor" : "Administrator",
    "dc:title" : "MyService0"
  }
}
 * 
 * =========================================================
 * 
 * XML API
 * 
 * In XML, handling SoaNodeInformation properties (actually, Nuxeo document properties)
 * of any kind including List & Map is done  using JAXB and a JAXB-dedicated model
 * built on-the-fly (see SoaNodeInformation and JaxbModelHelper).
 * 
 * The easiest way to consume it in Java is to use the original Java API and
 * (JAXB-serialized) model. This can be done by using the XmlRegistryApi (which
 * additionally tells the JAXRS client stack to use XML) and by by depending from the
 * minimal easysoa-registry-rest-core module (this class' module), which allows to
 * consume it using :
 * * the default (Nuxeo's) Jersey client, see the easysoa-registry-rest-client module
 * * CXF or FraSCAti / CXF clients, see the easysoa-registry-integration-base module
 * * or any other JAXRS client configured with the JAXB XML provider.
 * 
 * Beyond that, this API can be consumed by developing clients for this API in another language,
 * after having first generated the API model in said language from the API's
 * XML schema (xsd) using said language's XML tools. Here are solutions to get this schema :
 * * JAXB schemagen command line tool http://jaxb.java.net/2.2.4/docs/schemagen.html
 * * same using Ant http://blog.paumard.org/cours/jaxb-rest/chap02-jaxb-classes-schema.html
 * * in maven, using above Ant task, or plugin http://mojo.codehaus.org/jaxb2-maven-plugin/schemagen-mojo.html
 * 
 * Example :
 * 
<soaNodeInformation>
    <id>
        <subprojectId>/default-domain/MyProject/Realisation_v</subprojectId>
        <name>MyService0</name>
        <type>InformationService</type>
    </id>
    <properties>
        <string value="'/default-domain/MyProject/Realisation_v'" name="spnode:visibleSubprojectsCsv"/>
        <string value="/default-domain/MyProject/Realisation_v" name="spnode:subproject"/>
        <properties name="iserv:operations"/>
        <string value="9f98ddb0-9d8a-4811-90c5-28d81a0b4e8c" name="ecm:uuid"/>
        <string value="Administrator" name="dc:creator"/>
        <list name="dc:contributors">
            <string value="Administrator"/>
        </list>
        <date value="2013-07-31T16:16:18.570+02:00" name="dc:created"/>
        <list name="spnode:visibleSubprojects">
            <string value="/default-domain/MyProject/Realisation_v"/>
        </list>
        <boolean value="false" name="soan:isplaceholder"/>
        <properties name="dc:subjects"/>
        <date value="2013-07-31T16:16:18.570+02:00" name="dc:modified"/>
        <properties name="soan:parentIds"/>
        <string value="MyService0" name="soan:name"/>
        <string value="MyService0" name="dc:title"/>
        <string value="Administrator" name="dc:lastContributor"/>
    </properties>
</soaNodeInformation>
 * 
 * @author mdutoo
 *
 */
@Path("easysoa/registry") // relative path among other EasySOA services
//@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public interface RegistryApi {

    /** discovers the given SOA node */
    @POST
    OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception;

    /** @return all non-proxy, not deleted SOA nodes in the given subproject returned by the given guery */
    @POST
    @Path("query")
    @Consumes(MediaType.TEXT_PLAIN)
    SoaNodeInformations query(/*@DefaultValue(null) */@QueryParam("subproject") String subprojectId,
            String query) throws Exception;

    /** Returns the subproject root. FIXME WorkspaceRoot is not a SoaNode */
    @GET
    SoaNodeInformation get(/*@DefaultValue(null) */@QueryParam("subproject") String subprojectId) throws Exception;

    /** @return all SOA nodes of the given type (TODO or facet) in the given subproject */
    @GET
    @Path("{doctype}")
    SoaNodeInformations get(/*@DefaultValue(null) */@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype) throws Exception;

    /** @return the given SOA node */
    @GET
    @Path("{doctype}/{name:.+}")
    SoaNodeInformation get(@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    /** Deletes the given SOA node */
    @DELETE
    @Path("{doctype}/{name:.+}")
    OperationResult delete(@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    /** deletes the proxy of the given SOA node that is below the given correlated SOA node */
    @DELETE
    @Path("{doctype}/{name}/{correlatedDoctype}/{correlatedName}")
    OperationResult delete(@QueryParam("subproject") String subprojectId, @PathParam("doctype") String doctype, @PathParam("name") String name,
            @QueryParam("correlatedSubprojectId") String correlatedSubprojectId,
            @PathParam("correlatedDoctype") String correlatedDoctype,
            @PathParam("correlatedName") String correlatedName) throws Exception;


}