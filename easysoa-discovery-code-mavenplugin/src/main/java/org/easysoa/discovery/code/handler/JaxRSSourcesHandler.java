package org.easysoa.discovery.code.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryMojo;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.model.JavaServiceImplementationInformation;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.rest.client.types.InformationServiceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.java.JavaServiceImplementation;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * In project & current deliverable, reports :
 * * JAXRS service impl and their impl'd service interface if any (from impl and also by themselves),
 * both by having class or method(s) annotated by @Path
 * * thanks to InterfaceHandlerBase, member (field or bean setter)-injected service :
 * typed by interfaces having class or method(s) annotated by @Path
 * 
 * About implementation : see references at
 * spec http://jax-rs-spec.java.net/
 * doc http://jax-ws.java.net/jax-ws-ea3/docs/annotations.html
 * resteasy impl http://docs.jboss.org/resteasy/docs/1.2.GA/userguide/html_single/
 * wink impl https://cwiki.apache.org/WINK/jax-rs-request-and-response-entities.html
 * fairly complete tutorial http://www.vogella.com/articles/REST/article.html
 * 
 * TODO in bytecode
 * 
 */
public class JaxRSSourcesHandler extends AbstractJavaSourceHandler implements SourcesHandler {

    private static final String ANN_PATH = "javax.ws.rs.Path";
    private static final String ANN_CONSUMES = "javax.ws.rs.Consumes";
    private static final String ANN_PRODUCES = "javax.ws.rs.Produces";
    private static final String[] ANN_METHODS = new String[] {
        "javax.ws.rs.GET", "javax.ws.rs.POST", "javax.ws.rs.PUT",
        "javax.ws.rs.HEAD", "javax.ws.rs.OPTIONS"
      };
    private static final String ANN_PATH_PARAM = "javax.ws.rs.PathParam";
    private static final String ANN_QUERY_PARAM = "javax.ws.rs.QueryParam";
    private static final String ANN_FORM_PARAM = "javax.ws.rs.FormParam";
    private static final String ANN_HEADER_PARAM = "javax.ws.rs.HeaderParam";
    private static final String ANN_COOKIE_PARAM = "javax.ws.rs.CookieParam";
    
    public JaxRSSourcesHandler(CodeDiscoveryMojo codeDiscovery) {
        super(codeDiscovery);
    }
    
    @Override
    public Map<String, JavaServiceInterfaceInformation> findWSInterfaces(JavaSource source,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient,
            Log log) throws Exception {
        Map<String, JavaServiceInterfaceInformation> wsInjectableTypeSet = new HashMap<String, JavaServiceInterfaceInformation>();
        
        // Pass 1 : Find all WS interfaces if any
        JavaClass[] classes = source.getClasses();
        for (JavaClass c : classes) {
            if (c.isInterface() && !ParsingUtils.isTestClass(c)) {
                // Check JAX-RS annotation
                ArrayList<JavaMethod> pathMethods = null;
                for (JavaMethod method : c.getMethods()) {
                    if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                        if (pathMethods == null) {
                            pathMethods = new ArrayList<JavaMethod>(c.getMethods().length);
                        }
                        pathMethods.add(method);
                    }
                }
                if (pathMethods != null || ParsingUtils.hasAnnotation(c, ANN_PATH)) {
            		  // TODO target wsName[space]
                      wsInjectableTypeSet.put(c.getFullyQualifiedName(), new JavaServiceInterfaceInformation(
                              this.codeDiscovery.getSubproject(),
                              mavenDeliverable.getGroupId(), mavenDeliverable.getArtifactId(),
                              c.getFullyQualifiedName(), null, null, null));
                }
            }
        }
        
        return wsInjectableTypeSet;
    }
   
    @Override
    public JavaServiceInterfaceInformation findWSInterfaceInClasspath(Class<?> candidateClass,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
        boolean hasPathMethods = false;
        for (Method method : candidateClass.getMethods()) {
            if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                hasPathMethods = true;
            }
        }
        if (hasPathMethods || ParsingUtils.hasAnnotation(candidateClass, ANN_PATH)) {
        		// TODO target wsName[space]
             return new JavaServiceInterfaceInformation(this.codeDiscovery.getSubproject(),
                      mavenDeliverable.getGroupId(), mavenDeliverable.getArtifactId(),
                      candidateClass.getName(), null, null, null);
        }
        else {
            return null;
        }
    }

    @Override
    public Collection<SoaNodeInformation> findWSImplementations(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces, MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        // Pass 2 : Find all WS impl, including those implementing known interfaces (though its not "classical" JAXRS)
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        for (JavaSource source : sources) {
            // TODO diff between main & tests
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (!c.isInterface()) {
                    JavaClass itf = getWsItf(c, wsInterfaces);

                    ArrayList<JavaMethod> pathMethods = null;
                    if (itf == null) {
                        // Check JAX-RS annotation
                        for (JavaMethod method : c.getMethods()) {
                            if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                                if (pathMethods == null) {
                                    pathMethods = new ArrayList<JavaMethod>(c.getMethods().length);
                                }
                                pathMethods.add(method);
                            }
                        }
                    }

                    if (itf != null || pathMethods != null || ParsingUtils.hasAnnotation(c, ANN_PATH)) {
                        
                        // Extract WS info
                        JavaServiceImplementationInformation serviceImpl = new JavaServiceImplementationInformation(
                                this.codeDiscovery.getSubproject(), c.getFullyQualifiedName());
                        serviceImpl.setTitle(c.getName());
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXRS);
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_DOCUMENTATION, c.getComment());
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_ISMOCK, ParsingUtils.isTestClass(c));
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTATIONCLASS, c.getFullyQualifiedName());
                        serviceImpl.addParentDocument(mavenDeliverable.getSoaNodeId());

                        // extract base jaxrs conf :
                        //TODO also methods & inheritance see http://fusesource.com/docs/esb/4.2/rest/RESTAnnotateInherit.html
                        String baseRestPath = ParsingUtils.getAnnotationPropertyString(c, ANN_PATH, "value");
                        if (baseRestPath == null) {
                            baseRestPath = ""; // else won't be known as REST in EasySOA Registry
                        }
                        String baseRestAccepts = ParsingUtils.getAnnotationPropertyString(c, ANN_CONSUMES, "value");
                        if (baseRestAccepts == null) {
                            baseRestAccepts = MediaType.WILDCARD; // */* see https://cwiki.apache.org/WINK/jax-rs-request-and-response-entities.html
                        }
                        String baseRestContentType = ParsingUtils.getAnnotationPropertyString(c, ANN_PRODUCES, "value");
                        if (baseRestContentType == null) {
                            baseRestContentType = MediaType.APPLICATION_OCTET_STREAM; // see https://cwiki.apache.org/WINK/jax-rs-request-and-response-entities.html
                        }
                        
                        if (itf != null) {
                            // Extract WS info
                            serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE, itf.getFullyQualifiedName());
                            JavaServiceInterfaceInformation interfaceInfo = wsInterfaces.get(itf.getFullyQualifiedName());
                            if (interfaceInfo != null) {
                                serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACELOCATION, interfaceInfo.getMavenDeliverableId().getName());
                            }
                            String itfSoaName = itf.getName(); // TODO better like JAXWS wsNamespace + ":" + wsName; ??
                            if (codeDiscovery.isMatchInterfacesFirst()) {
                                itfSoaName = "matchFirst:" + itfSoaName;
                            }
                            InformationServiceInformation serviceDef = new InformationServiceInformation(
                                    this.codeDiscovery.getSubproject(), itfSoaName);
                            //serviceDef.setOperations(operations);//TODO
                            serviceImpl.addParentDocument(serviceDef.getSoaNodeId());

                            if (this.codeDiscovery.isDiscoverInterfaces()) {
                                discoveredNodes.add(serviceDef);
                            }
                        }
                        
                        // set jaxrs conf
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_REST_PATH, baseRestPath);
                        if (baseRestAccepts != null) { // or defaults to "*" ?
                            serviceImpl.setProperty(JavaServiceImplementation.XPATH_REST_ACCEPTS, baseRestAccepts);
                        }
                        if (baseRestContentType != null) { // or defaults to "application/*" ??
                            serviceImpl.setProperty(JavaServiceImplementation.XPATH_REST_CONTENT_TYPE, baseRestContentType);
                        }
                        
                        // Extract operations info
                        List<OperationInformation> operations = serviceImpl.getOperations();
                        String baseRestPathPrefix = baseRestPath + ((baseRestPath.endsWith("/")) ? "" : '/');
                        if (pathMethods != null) {
                            for (JavaMethod method : c.getMethods()) {
                                // Get HTTP method if any
                                String httpMethod = null;
                                for (String annHttpMethod : ANN_METHODS) {
                                    if (ParsingUtils.hasAnnotation(method, annHttpMethod)) {
                                        httpMethod = annHttpMethod.replace("javax.ws.rs.", "");
                                        break;
                                    }
                                }
                                
                                if (httpMethod != null) {
                                    // Extract service path
                                    String path = ParsingUtils.getAnnotationPropertyString(method, ANN_PATH, "value");
                                    if (path == null) {
                                        path = method.getName();
                                    }
                                    // TODO LATER also base REST path but at first only show local path :
                                    //if (baseRestPath != null && !path.startsWith("/")) {
                                    //    path = baseRestPathPrefix + path;
                                    //}
                                    String operationName = httpMethod + " " + path;

                                    String operationConsumes = ParsingUtils.getAnnotationPropertyString(method, ANN_PRODUCES, "value");
                                    if (operationConsumes == null) {
                                        operationConsumes = baseRestAccepts;
                                    }
                                    String operationProduces = ParsingUtils.getAnnotationPropertyString(method, ANN_CONSUMES, "value");
                                    if (operationProduces == null) {
                                        operationProduces = baseRestContentType;
                                    }
                                    
                                    // Extract parameters info
                                    StringBuilder parametersInfo = new StringBuilder();
                                    for (JavaParameter parameter : method.getParameters()) {
                                        String paramName = extractRestParamName(parameter);
                                        
                                        if (paramName != null) {
                                            String parameterType = getParameterType(parameter.getType());
                                            parametersInfo.append(formatParameter(paramName, parameterType) + ", ");
                                        } // else can't be provided through REST
                                    }
                                    // removing trailing ", "
                                    if (parametersInfo.length() > 2) {
                                        parametersInfo.delete(parametersInfo.length()-2, parametersInfo.length());
                                    }

                                    // extract return parameter info
                                    String returnParameterType = getReturnParameterType(method);
                                    String returnParametersInfo = formatParameter("response", returnParameterType);
                                    
                                    // "in message" way of presenting parameters :
                                    //parametersInfo.insert(0, operationConsumes + ": ");
                                    // "out message" way of presenting return :
                                    //returnParametersInfo = operationProduces + ": " + returnParametersInfo;

                                    //TODO LATER also bare signature (or method.getName() ?) :
                                    //String signature = method.getCallSignature();
                                    //TODO lATER better as for JAXWS : operationMap.put(method.getName(), new OperationInformation(operationName...
                                    operations.add(new OperationInformation(operationName,
                                            parametersInfo.toString(), returnParametersInfo,
                                            method.getComment(),
                                            operationConsumes, operationProduces));
                                }
                                
                            }
                            serviceImpl.setOperations(operations);
                            

                            if (this.codeDiscovery.isDiscoverImplementations()) {
                                discoveredNodes.add(serviceImpl);
                            }
                        }
                    }
                }
            }
        }
        return discoveredNodes;
    }
    

    private String extractRestParamName(JavaParameter parameter) {
        String paramName = getParamName(parameter, ANN_PATH_PARAM, "path");
        if (paramName == null) {
            paramName = getParamName(parameter, ANN_QUERY_PARAM, "query");
        }
        if (paramName == null) {
            paramName = getParamName(parameter, ANN_FORM_PARAM, "form");
        }
        if (paramName == null) {
            paramName = getParamName(parameter, ANN_HEADER_PARAM, "header");
        }
        if (paramName == null) {
            paramName = getParamName(parameter, ANN_COOKIE_PARAM, "cookie");
        }
        return paramName;
    }

    private String getParamName(JavaParameter parameter, String paramAnnotation, String paramKind) {
        String paramName = ParsingUtils.getAnnotationPropertyString(parameter, paramAnnotation, "value");
        if (paramName != null) {
            return paramKind + ':' + paramName;
        }
        return null;
    }
    
}