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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.easysoa.registry.rest.OperationResult;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;
import org.easysoa.registry.rest.integration.EndpointInformations;
import org.easysoa.registry.rest.integration.ServiceInformations;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;


/**
 * 
 * @obsolete using rather a wrapper list node (ex. SoaNodeInformations).
 * Map to JsonNode then entity is not optimal, and LATER not available in Jackson 2
 * (would have to be replaced by JacksonJsonProvider or do as in ProviderBase)
 *
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JsonMessageReader implements MessageBodyReader<Object> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType); // && (type.isArray() || type.getPackage().equals("org.easysoa.registry.rest.marshalling"))
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {

        JsonNode jsonNode = mapper.readValue(entityStream, JsonNode.class);
        if (jsonNode.isArray()) {
            return mapper.readValue(jsonNode, SoaNodeInformation[].class);
        }
        else {
            JsonNode idNode = jsonNode.get("id");
            if (idNode != null && idNode.has("type") && idNode.has("name")) {
                return mapper.readValue(jsonNode, SoaNodeInformation.class);
            }
            else if (jsonNode.has("soaNodeInformations")) {
                return mapper.readValue(jsonNode, SoaNodeInformations.class);
            }
            else if (jsonNode.has("result")) {
                return mapper.readValue(jsonNode, OperationResult.class);
            }
            else if(jsonNode.has("serviceInformations")) {
                return mapper.readValue(jsonNode, ServiceInformations.class);
            }
            else if(jsonNode.has("endpointInformations")) {
                return mapper.readValue(jsonNode, EndpointInformations.class);
            }
            else if(jsonNode.has("slaOrOlaIndicators")) {
                return mapper.readValue(jsonNode, SlaOrOlaIndicators.class);
            }
            else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JsonFactory factory = new JsonFactory(new ObjectMapper());
                JsonGenerator jsonGenerator = factory.createJsonGenerator(baos);
                jsonGenerator.useDefaultPrettyPrinter();
                jsonGenerator.writeObject(jsonNode);
                return baos.toString();
            }
        }
    }

}
