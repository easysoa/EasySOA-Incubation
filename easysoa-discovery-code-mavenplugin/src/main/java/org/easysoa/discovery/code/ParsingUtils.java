package org.easysoa.discovery.code;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.annotation.AnnotationValue;

public class ParsingUtils {

    public static boolean hasAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        return getAnnotation(entity, fullyQualifiedAnnotationName) != null;
    }
    
    public static com.thoughtworks.qdox.model.Annotation getAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        for (com.thoughtworks.qdox.model.Annotation annotation : entity.getAnnotations()) {
            if (fullyQualifiedAnnotationName.equals(annotation.getType().getFullyQualifiedName())) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Supports String, arrays (of String else toString()), else toString(), strips quotes
     * @param method
     * @param annotation
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getAnnotationPropertyString(AbstractBaseJavaEntity method, String annotation, String property) {
        if (ParsingUtils.hasAnnotation(method, annotation)) {
            com.thoughtworks.qdox.model.Annotation webResultAnn = ParsingUtils.getAnnotation(method, annotation);
            AnnotationValue annValue = webResultAnn.getProperty(property);
            if (annValue != null) {
                Object parameterValue = annValue.getParameterValue();
                if (parameterValue == null) {
                    return null;
                }
                if (parameterValue instanceof String) {
                    return evaluateAnnotationStringValue(method.getParentClass(), (String) parameterValue);
                }
                if (parameterValue instanceof List<?>) {
                    StringBuffer sbuf = new StringBuffer();
                    for (Object s : (List<Object>) parameterValue) {
                        if (s instanceof String) {
                            s = evaluateAnnotationStringValue(method.getParentClass(), (String) s); 
                        }
                        sbuf.append(", ");
                        sbuf.append(s);
                    }
                    if (sbuf.length() != 0) {
                        sbuf.delete(0,  2);
                    }
                    return sbuf.toString();
                }
                
                // default :
                String stringValue = annValue.toString();
                if (stringValue != null) {
                    // removing parsed but useless double quotes :
                    return evaluateAnnotationStringValue(method.getParentClass(), stringValue);
                }
            }
        }
        return null;
    }
    
    /**
     * Supports "..." and (package.)Type.field constants
     * @param typeResolver 
     * @param s
     * @return
     */
    public static String evaluateAnnotationStringValue(JavaClass typeResolver, String stringValue) {
        // removing parsed but useless double quotes :
        if (stringValue.charAt(0) == '"' && stringValue.charAt(stringValue.length() - 1) == '"') {
            stringValue = stringValue.substring(1, stringValue.length() - 1);
        } else {
            // assuming it's in the form of (package.)Type.field :
            int lastIndexOfDot = stringValue.lastIndexOf('.');
            String typeName = stringValue.substring(0, lastIndexOfDot);
            String typeClassName = typeResolver.resolveType(typeName);
            if (typeClassName != null) {
                try {
                    Class<?> typeClass = typeResolver.getClass().getClassLoader().loadClass(typeClassName);
                    String staticFieldName = stringValue.substring(lastIndexOfDot + 1);
                    Field staticField = typeClass.getField(staticFieldName);
                    staticField.setAccessible(true); // in case of private constant in same class
                    Object staticFieldValue = staticField.get(null);
                    return (staticFieldValue == null) ? null : String.valueOf(staticFieldValue);
                } catch (Exception e) {
                    //log.warning("Unsupported annotation evaluation for " + stringValue + " in " + typeResolver);
                }
            }
            // TODO better : also imported fields...
        }
        // TODO better : other primitive types
        return stringValue;
    }

    public static boolean hasAnnotation(AnnotatedElement annotable, String fullyQualifiedAnnotationName) {
        return getAnnotation(annotable, fullyQualifiedAnnotationName) != null;
    }
    
    public static Annotation getAnnotation(AnnotatedElement annotable, String fullyQualifiedAnnotationName) {
        for (java.lang.annotation.Annotation annotation : annotable.getAnnotations()) {
            if (fullyQualifiedAnnotationName.equals(annotation.annotationType().getName())) {
                return annotation;
            }
        }
        return null;
    }

    public static String getAnnotationPropertyString(AnnotatedElement annotable, String fullyQualifiedAnnotationName, String property) {
        if (ParsingUtils.hasAnnotation(annotable, fullyQualifiedAnnotationName)) {
            Annotation ann = ParsingUtils.getAnnotation(annotable, fullyQualifiedAnnotationName);
            
            try {
                // annotation value introspection : see http://blog.dahanne.net/2012/05/07/dynamically-proxying-annotations-to-avoid-inducing-api-dependency/
                Method annMemberMethod = ann.getClass().getDeclaredMethod(property);
                Object annValue = annMemberMethod.invoke(ann, new Object[0]);
                if (annValue != null) {
                    if (annValue instanceof String[]) {
                        StringBuffer sbuf = new StringBuffer();
                        for (String s : (String[]) annValue) { //TODO or List<?> ? still test this
                            sbuf.append(", ");
                            sbuf.append(String.valueOf(s));
                        }
                        if (sbuf.length() != 0) {
                            sbuf.delete(0,  2);
                        }
                        return sbuf.toString();
                    }
                    return annValue.toString(); // TODO better : also String[] values...
                }
            } catch (Exception e) {
                throw new RuntimeException("Reflection error in getAnnotationPropertyString("
                        + annotable + ", " + fullyQualifiedAnnotationName + ", " + property + ")", e);
            }
        }
        return null;
    }
    
    public static boolean isTestClass(JavaClass c) {
        return c.getSource().getURL().getPath().contains("src/test/");
    }
}
