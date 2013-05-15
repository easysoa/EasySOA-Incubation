package org.easysoa.registry.rest.marshalling;

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
import org.easysoa.registry.rest.integration.EndpointInformations;
import org.easysoa.registry.rest.integration.ServiceInformations;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;

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
