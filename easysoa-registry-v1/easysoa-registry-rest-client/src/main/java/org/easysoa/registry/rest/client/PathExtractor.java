package org.easysoa.registry.rest.client;

import java.lang.reflect.Method;

import javax.ws.rs.Path;

/**
 * For now doesn't look in superclasses.
 * This is OK since Jersey doesn't (mostly) inherit @Path either, see
 * http://jersey.576304.n2.nabble.com/Inheritance-of-Annotations-and-Jersey-td3225397.html
 * 
 * @author mdutoo
 *
 */
public class PathExtractor {

    public static String getPath(Class<?> c) {
        Path annotation = c.getAnnotation(Path.class);
        return annotation.value();
    }

    public static String getPath(Class<?> c, String methodName, Class<?>... parameterTypes)
            throws SecurityException, NoSuchMethodException {
        Method method = c.getDeclaredMethod(methodName, parameterTypes);
        return method.getAnnotation(Path.class).value();
    }
}
