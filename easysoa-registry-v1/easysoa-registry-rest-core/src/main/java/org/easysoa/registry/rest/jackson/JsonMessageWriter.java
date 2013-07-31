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

/**
 * 
 * @obsolete using rather a wrapper list node (ex. SoaNodeInformations)
 *
 */
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

    	String typeFqn = type.getName();
        return !(typeFqn.endsWith("Blob") && typeFqn.startsWith("org.nuxeo"))
        		// NB. hack to avoid problematic nuxeo Blobs without depending
        		// from nuxeo API like !AbstractBlob.class.isAssignableFrom(type)
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
