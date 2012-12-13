package org.easysoa.registry.rest.marshalling;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;

@Provider
@Produces({ MediaType.APPLICATION_JSON, "application/x-javascript" })
public class JsonMessageWriter implements MessageBodyWriter<Object> {

    private JsonFactory factory;

    public JsonMessageWriter() {
        factory = new JsonFactory(new ObjectMapper());
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {

        return !AbstractBlob.class.isAssignableFrom(type)
                && (MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType) || "application/x-javascript".equals(mediaType.toString()));
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        JsonGenerator jsonGenerator = factory.createJsonGenerator(entityStream);
        jsonGenerator.writeObject(t);
    }

}
