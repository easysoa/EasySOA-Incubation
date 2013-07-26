package org.easysoa.discovery.code.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceProvider;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryMojo;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.handler.consumption.ImportedServicesConsumptionFinder;
import org.easysoa.discovery.code.model.JavaServiceConsumptionInformation;
import org.easysoa.discovery.code.model.JavaServiceImplementationInformation;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.rest.client.types.InformationServiceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformations;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.OperationInformation;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.ids.ServiceImplementationName;
import org.easysoa.registry.types.ids.ServiceNameType;
import org.easysoa.registry.types.java.JavaServiceImplementation;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;


/**
 * In project & current deliverable, reports :
 * * JAXWS service impl and their impl'd service interface (from impl and also by themselves),
 * both by being annotated by @WebService
 * * thanks to InterfaceHandlerBase, member (field or bean setter)-injected service :
 * typed by @WebService annotated interfaces or @WebServiceClient annotated classes)
 *
 * For JAXWS, the documentation handler take the documention contained in the interface.
 *
 * @author mdutoo
 *
 */
public class JaxWSSourcesHandler extends AbstractJavaSourceHandler implements SourcesHandler {

    private static final String SOAP_CONTENT_TYPE = "application/soap+xml"; // ; charset=utf-8 http://www.w3schools.com/soap/soap_httpbinding.asp

    /* jaxws annotations */
    private static final String ANN_WSPROVIDER = "javax.jws.WebServiceProvider";
    private static final String ANN_WS = "javax.jws.WebService";
    private static final String ANN_WEBRESULT = "javax.jws.WebResult";
    private static final String ANN_WEBMETHOD = "javax.jws.WebMethod";
    private static final String ANN_WEBPARAM = "javax.jws.WebParam";

    /* injection annotations */
    private static final String ANN_XML_WSCLIENT = "javax.xml.ws.WebServiceClient";
    private static final String ANN_XML_WSREF = "javax.xml.ws.WebServiceRef";
    private static final String ANN_XML_WSPROVIDER = "javax.xml.ws.WebServiceProvider";

    private Map<Type, String> implsToInterfaces = new HashMap<Type, String>();

    public JaxWSSourcesHandler(CodeDiscoveryMojo codeDiscovery) {
        super(codeDiscovery);
        this.addAnnotationToDetect(ANN_WSPROVIDER);
        this.addAnnotationToDetect(ANN_XML_WSCLIENT);
        this.addAnnotationToDetect(ANN_XML_WSREF);
        this.addAnnotationToDetect(ANN_XML_WSPROVIDER);
    }

    // Pass 1 : Find all WS clients/interfaces
    public Map<String, JavaServiceInterfaceInformation> findWSInterfaces(JavaSource source,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        Map<String, JavaServiceInterfaceInformation> wsInjectableTypeSet
                = new HashMap<String, JavaServiceInterfaceInformation>();
        JavaClass[] classes = source.getClasses();
        for (JavaClass c : classes) {
            if (isWsInterface(c)) {
            	String wsName = getWsName(c);
            	String wsNamespace = getWsNamespace(c);
            	Map<String, OperationInformation> operations = getOperationsInformation(c, null);
                wsInjectableTypeSet.put(c.getFullyQualifiedName(),
                        new JavaServiceInterfaceInformation(this.codeDiscovery.getSubproject(),
                                mavenDeliverable.getGroupId(), mavenDeliverable.getArtifactId(),
                                c.getFullyQualifiedName(), wsNamespace, wsName, operations));
            }
        }

        return wsInjectableTypeSet;
    }

    public JavaServiceInterfaceInformation findWSInterfaceInClasspath(Class<?> c,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
        if (isWsInterface(c)) {
        	String wsName = getWsName(c);
        	String wsNamespace = getWsNamespace(c);
        	Map<String, OperationInformation> operations = getOperationsInformation(c);
            return new JavaServiceInterfaceInformation(this.codeDiscovery.getSubproject(),
                    mavenDeliverable.getGroupId(), mavenDeliverable.getArtifactId(),
                    c.getName(), wsNamespace, wsName, operations);
        }
        else {
            return null;
        }
    }

    @Override
    public Collection<SoaNodeInformation> findWSImplementations(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
        // Pass 2 : Explore each impl
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (isWsImplementation(c, wsInterfaces)) {
                    JavaServiceInterfaceInformation interfaceInfo = null;

                    // Extract interface info
                    //System.out.println("\ncp:\n" + System.getProperty("java.class.path"));
                    JavaClass itfClass = getWsItf(c, wsInterfaces);
                    if (itfClass != null) {
                        implsToInterfaces.put(c.asType(), itfClass.asType().getFullyQualifiedName());
                        interfaceInfo = wsInterfaces.get(itfClass.getFullyQualifiedName());
                    }
                    else {
                        log.warn("Couldn't find interface for class " + c.getFullyQualifiedName());
                    }

                    // Extract WS info
                    JavaServiceImplementationInformation serviceImpl = createServiceImplementation(c, itfClass, interfaceInfo);
                    serviceImpl.addParentDocument(mavenDeliverable.getSoaNodeId());

                    if (itfClass != null) {
                        // Extract operations info
                        Map<String, OperationInformation> itfOperationsInfo = interfaceInfo.getOperations();
                        Map<String, OperationInformation> implOperationsInfo = getOperationsInformation(c, itfOperationsInfo);
                        for (Entry<String, OperationInformation> implOperation : implOperationsInfo.entrySet()) {
                            if (itfOperationsInfo.containsKey(implOperation.getKey())) {
                                OperationInformation opInfo = itfOperationsInfo.get(implOperation.getKey());
                                opInfo.mergeWith(implOperation.getValue());
                            }
                        }
                        List<OperationInformation> operations = new ArrayList<OperationInformation>(itfOperationsInfo.values());

                        // Extract service info
                    	// TODO Cleaner porttype discovery
                        if (this.codeDiscovery.isDiscoverInterfaces()) {
                            InformationServiceInformation informationService = this.createInformationService(itfClass, interfaceInfo);
                            informationService.setOperations(operations);
                            discoveredNodes.add(informationService);
                        }

                        serviceImpl.setOperations(operations); //TODO or only code (signature) plus any info pointing to iserv operations ??
                    }

                    if (this.codeDiscovery.isDiscoverImplementations()) {
                        discoveredNodes.add(serviceImpl);
                    }
                }
            }
        }
        return discoveredNodes;
    }

	@Override
    public Collection<SoaNodeInformation> handleAdditionalDiscovery(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
         List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();

        // Additional pass : Find WS tests
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (!c.isInterface() && c.getSource().getURL().getPath().contains("src/test/")) {
                	boolean isUnitTestingClass = false;
                	for (JavaMethod method : c.getMethods()) {
                		isUnitTestingClass = isUnitTestingClass(isUnitTestingClass, method);
                		if (isUnitTestingClass) {
                			break;
                		}
                	}
                	if (isUnitTestingClass) {
                	    ImportedServicesConsumptionFinder importedServiceFinders = new ImportedServicesConsumptionFinder();
                	    List<JavaServiceConsumptionInformation> foundConsumptions = importedServiceFinders.find(
                	            c, mavenDeliverable, wsInterfaces);
                	    for (JavaServiceConsumptionInformation foundConsumption : foundConsumptions) {
                            // Try to attach test to existing non-mock impls
                            boolean foundOriginalImplementation = false;
                            SoaNodeInformations matchingRegistryImpls = registryClient
                                    .findImplsByInterface(this.codeDiscovery.getSubproject(),
                                            foundConsumption.getConsumedInterface());
                            for (SoaNodeInformation matchingRegistryImpl : matchingRegistryImpls.getSoaNodeInformationList()) {
                                foundOriginalImplementation = true;
                                discoveredNodes.add(createTestDiscovery(
                                        matchingRegistryImpl.getSoaName(),
                                        c.getFullyQualifiedName()));
                            }

                            // Otherwise, attach test info to all known implementations of the interface
                            if (!foundOriginalImplementation) {
                                for (Entry<Type, String> implToInterface : implsToInterfaces.entrySet()) {
                                    if (foundConsumption.getConsumedInterface().equals(implToInterface)) {
                                        discoveredNodes.add(createTestDiscovery(
                                                implToInterface.getKey().toGenericString(),
                                                c.getFullyQualifiedName()));
                                    }
                                }
                            }
                	    }
                	}
                }
            }
        }

        return discoveredNodes;
    }

	public JavaServiceImplementationInformation createTestDiscovery(String serviceImplName, String testName) throws Exception {
        JavaServiceImplementationInformation serviceImpl = new JavaServiceImplementationInformation(
                this.codeDiscovery.getSubproject(), serviceImplName);
        List<String> tests = new ArrayList<String>();
        tests.add(testName);
        serviceImpl.setTests(tests);
        return serviceImpl;
    }

	private String getWsNamespace(JavaClass c) {
	    String wsNamespace = null;
        wsNamespace = ParsingUtils.getAnnotationPropertyString(c, ANN_WS, "targetNamespace");
        if (wsNamespace == null) {
            wsNamespace = ParsingUtils.getAnnotationPropertyString(c, ANN_XML_WSCLIENT, "targetNamespace");
        }
        if (wsNamespace == null) {
            wsNamespace = ParsingUtils.getAnnotationPropertyString(c, ANN_XML_WSPROVIDER, "targetNamespace");
        }
        if (wsNamespace == null) {
            // defaults to package ex. http://ws.apv.dps.axxx.com/ // TODO or only for itfs and not impls ??
            wsNamespace = "http://" + StringUtils.reverseDelimited(c.getPackageName(), '.') + '/';
        }
        return wsNamespace;
	}

	private String getWsName(JavaClass c) {
        String wsName = null;
        wsName = ParsingUtils.getAnnotationPropertyString(c, ANN_WS, "name");
        if (wsName == null) {
            wsName = ParsingUtils.getAnnotationPropertyString(c, ANN_XML_WSCLIENT, "name");
        }
        if (wsName == null) {
            wsName = ParsingUtils.getAnnotationPropertyString(c, ANN_XML_WSPROVIDER, "serviceName");
        }
        if (wsName == null) {
            // defaults to java itf // TODO if itf ! impl ??
            wsName = c.getName();
        }
		return wsName;
	}

	private String getWsServiceName(JavaClass c) {
	    // service implementation name (used for binding & port)
        String serviceName = null;
        serviceName = ParsingUtils.getAnnotationPropertyString(c, ANN_WS, "serviceName");
        if (serviceName == null) {
            serviceName = ParsingUtils.getAnnotationPropertyString(c, ANN_XML_WSCLIENT, "name");
        }
		if (serviceName == null) {
		    // defaults to java impl + "Service" // TODO for impls ! itfs ??
			serviceName = c.getName() + "Service";
		}
		return serviceName;
	}

	/**
	 * For (qdox-parsed) source
	 * @param c
	 * @param existingOperationsInfo
	 * @return
	 */
	private Map<String, OperationInformation> getOperationsInformation(JavaClass c,
			Map<String, OperationInformation> existingOperationsInfo) {
        Map<String, OperationInformation> operations = new HashMap<String, OperationInformation>();

        boolean acceptNewNonExistingOperations = isWsServerInterface(c);
        // NB. for interface inheritance, since jaxws 2.1 https://issues.apache.org/jira/browse/OPENEJB-1020
        //TODO check that this is enough to support it

        for (JavaMethod method : c.getMethods()) {

            String operationName = ParsingUtils.getAnnotationPropertyString(method, ANN_WEBMETHOD, "operationName");
            if (operationName == null) {
                operationName = method.getName(); // even non-@WebMethod annotated interface methods are exposed
            }
            boolean doesOperationExist = false;
			if (/*operationName == null && */existingOperationsInfo != null) {
        		for (Entry<String, OperationInformation> operation : existingOperationsInfo.entrySet()) {
        			if (operation.getKey().equals(method.getName())) {
        			    operationName = operation.getValue().getName();
        			    doesOperationExist = true;
        			}
        		}
        	}

            if (acceptNewNonExistingOperations || doesOperationExist/*operationName != null*/) {
                // Extract parameters info
                StringBuilder parametersInfo = new StringBuilder();
                int noNameArgIndex = 0;
                for (JavaParameter parameter : method.getParameters()) {
                    String webParameterName = ParsingUtils.getAnnotationPropertyString(parameter, ANN_WEBPARAM, "name");
                    if (webParameterName == null) {
                        webParameterName = "arg" + noNameArgIndex;
                    }
                    noNameArgIndex++;
                    String webParameterType = getParameterType(parameter.getType());
                    parametersInfo.append(formatParameter(webParameterName, webParameterType) + ", ");
                }
                // removing trailing ", "
                if (parametersInfo.length() > 2) {
                    parametersInfo.delete(parametersInfo.length()-2, parametersInfo.length());
                }

                // extract return parameter info
                String webResultName = ParsingUtils.getAnnotationPropertyString(method, ANN_WEBRESULT, "name");
                if (webResultName == null) {
                    webResultName = operationName + "Response";
                }
                String returnParameterType = getReturnParameterType(method);
                String returnParametersInfo = formatParameter(webResultName, returnParameterType);
                //TODO LATER one-way, async...

                String operationInContentType = SOAP_CONTENT_TYPE;
                String operationOutContentType = SOAP_CONTENT_TYPE;
                // "in message" way of presenting parameters :
                //parametersInfo.insert(0, SOAP_CONTENT_TYPE + ": " + operationName + "{ ");
                //parametersInfo.append(" }");
                // "out message" way of presenting return :
                //returnParametersInfo = SOAP_CONTENT_TYPE + ": " + returnParametersInfo;

                //TODO LATER also bare signature (or method.getName() ?) :
                //String signature = method.getCallSignature();
                operations.put(method.getName(), new OperationInformation(operationName,
                        parametersInfo.toString(), returnParametersInfo, formatDoc(method),
                        operationInContentType, operationOutContentType));
            }
        }
        return operations;
	}




    private boolean isWsInterface(JavaClass c) {
        return isWsServerInterface(c) || isWsClientInterface(c);
    }
    private boolean isWsServerInterface(JavaClass c) {
        boolean isWs = ParsingUtils.hasAnnotation(c, ANN_WS);
        boolean isInterface = c.isInterface();
        return isWs && isInterface;
    }
    private boolean isWsClientInterface(JavaClass c) {
        return ParsingUtils.hasAnnotation(c, ANN_XML_WSCLIENT)
                || ParsingUtils.hasAnnotation(c, ANN_WSPROVIDER)
                || ParsingUtils.hasAnnotation(c, ANN_XML_WSPROVIDER);
    }

	private boolean isWsImplementation(JavaClass c, Map<String, JavaServiceInterfaceInformation> wsInterfaces) {
        JavaClass itfClass = getWsItf(c, wsInterfaces); // TODO several interfaces ???
        return !c.isInterface() && (ParsingUtils.hasAnnotation(c, ANN_WS) || itfClass != null); // TODO superclass ?
	}
	private JavaServiceImplementationInformation createServiceImplementation(JavaClass c,
			JavaClass itfClass, JavaServiceInterfaceInformation interfaceInfo) throws Exception {
	    String wsNamespace = getWsNamespace(c), wsName = getWsName(c), serviceName = getWsServiceName(c);

		String wsdlPortTypeName;
		if (interfaceInfo != null) {
		    wsdlPortTypeName = interfaceInfo.getWsPortTypeName();
		} else {
		    wsdlPortTypeName = toShortNsName(wsNamespace, wsName);
		}
		String wsdlServiceName = toShortNsName(wsNamespace, serviceName);

        JavaServiceImplementationInformation serviceImpl = new JavaServiceImplementationInformation(
                this.codeDiscovery.getSubproject(),
                new ServiceImplementationName(ServiceNameType.WEB_SERVICE, // ?????
                wsdlPortTypeName, c.getFullyQualifiedName())); // serviceName
        serviceImpl.setTitle(c.getName()); // c.getName() (shortcut) or serviceImpl.getSoaName() ?

		// TODO Cleaner porttype/servicename discovery + revert soanodeid
		serviceImpl.setProperty(JavaServiceImplementation.XPATH_WSDL_PORTTYPE_NAME, wsdlPortTypeName);
		serviceImpl.setProperty(JavaServiceImplementation.XPATH_WSDL_SERVICE_NAME, wsdlServiceName);

		serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPL_LANGUAGE, Platform.LANGUAGE_JAVA);
		serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPL_BUILD, Platform.BUILD_MAVEN);
		serviceImpl.setProperty(JavaServiceImplementation.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXWS);

		serviceImpl.setProperty(JavaServiceImplementation.XPATH_ISMOCK,
		        c.getSource().getURL().getPath().contains("src/test/"));
		serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTATIONCLASS, c.getFullyQualifiedName());
		if (itfClass != null) {
		    serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE, itfClass.getFullyQualifiedName());
		    if (interfaceInfo != null) {
		        serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACELOCATION,
		                interfaceInfo.getMavenDeliverableId().getName());
		    }
	        serviceImpl.setProperty(JavaServiceImplementation.XPATH_DOCUMENTATION, formatDoc(itfClass));
		}
		return serviceImpl;
	}

	/**
	 * Creates a new InformationService from the given info.
	 * However operations are set outside.
	 * @param itfClass
	 * @param interfaceInfo
	 * @return
	 * @throws Exception
	 */
	private InformationServiceInformation createInformationService(JavaClass itfClass,
			JavaServiceInterfaceInformation interfaceInfo) throws Exception {
	    String wsNamespace = getWsNamespace(itfClass), wsName = getWsName(itfClass);
	    String itfClassName = itfClass.getName();
	    String itfSoaName = wsNamespace + ":" + wsName;
	    if (codeDiscovery.isMatchInterfacesFirst()) {
	        itfSoaName = "matchFirst:" + itfSoaName;
	    }
	    InformationServiceInformation informationService = new InformationServiceInformation(
	            this.codeDiscovery.getSubproject(), itfSoaName);
        // TODO LATER if refactoring to allow technical matching of service interfaces :
        /*informationService.setProperty(InformationService.XPATH_LANGUAGE, Platform.LANGUAGE_JAVA);
        informationService.setProperty(InformationService.XPATH_BUILD, Platform.BUILD_MAVEN);
        informationService.setProperty(InformationService.XPATH_TECHNOLOGY, Platform.SERVICE_LANGUAGE_JAXRS);*/
	    String wsdlPortTypeName = toShortNsName(wsNamespace, wsName);
	    informationService.setProperty(InformationService.XPATH_WSDL_PORTTYPE_NAME, wsdlPortTypeName);
	    informationService.setTitle(itfClassName.substring(itfClassName.lastIndexOf(".") + 1));
	    // NB. operations set outside
	    return informationService;
	}

	public static String toShortNsName(String ns, String name) {
	    return '{' + ns + '}' + name;
	}

	private String getWsNamespace(Class<?> c) {
    	if (ParsingUtils.hasAnnotation(c, ANN_WS)) {
    		WebService wsAnnotation = (WebService) ParsingUtils.getAnnotation(c, ANN_WS);
    		return wsAnnotation.name();
    	}
    	else if (ParsingUtils.hasAnnotation(c, ANN_XML_WSCLIENT)) {
    		WebServiceClient wsAnnotation = (WebServiceClient) ParsingUtils.getAnnotation(c, ANN_XML_WSCLIENT);
    		return wsAnnotation.name();
    	}
    	else if (ParsingUtils.hasAnnotation(c, ANN_XML_WSPROVIDER)) {
    		WebServiceProvider wsAnnotation = (WebServiceProvider) ParsingUtils.getAnnotation(c, ANN_XML_WSPROVIDER);
    		return wsAnnotation.serviceName();
    	}
    	return c.getName();
	}

	private String getWsName(Class<?> c) {
    	if (ParsingUtils.hasAnnotation(c, ANN_WS)) {
    		WebService wsAnnotation = (WebService) ParsingUtils.getAnnotation(c, ANN_WS);
    		return wsAnnotation.targetNamespace();
    	}
    	else if (ParsingUtils.hasAnnotation(c, ANN_XML_WSCLIENT)) {
    		WebServiceClient wsAnnotation = (WebServiceClient) ParsingUtils.getAnnotation(c, ANN_XML_WSCLIENT);
    		return wsAnnotation.targetNamespace();
    	}
    	else if (ParsingUtils.hasAnnotation(c, ANN_XML_WSPROVIDER)) {
    		WebServiceProvider wsAnnotation = (WebServiceProvider) ParsingUtils.getAnnotation(c, ANN_XML_WSPROVIDER);
    		return wsAnnotation.targetNamespace();
    	}
    	return null;
	}

	private boolean isWsInterface(Class<?> c) {
		return isWsServerInterface(c) || isWsClientInterface(c);
	}
    private boolean isWsServerInterface(Class<?> c) {
        boolean isWs = ParsingUtils.hasAnnotation(c, ANN_WS);
        boolean isInterface = c.isInterface();
        return isWs && isInterface;
    }
    private boolean isWsClientInterface(Class<?> c) {
        return ParsingUtils.hasAnnotation(c, ANN_XML_WSCLIENT)
                || ParsingUtils.hasAnnotation(c, ANN_WSPROVIDER)
                || ParsingUtils.hasAnnotation(c, ANN_XML_WSPROVIDER);
    }

	/**
	 * For bytecode
	 * @param c
	 * @return
	 */
	private Map<String, OperationInformation> getOperationsInformation(Class<?> c) {
		Map<String, OperationInformation> operations = new HashMap<String, OperationInformation>();

        boolean acceptNewNonExistingOperations = isWsServerInterface(c);
        // NB. for interface inheritance, since jaxws 2.1 https://issues.apache.org/jira/browse/OPENEJB-1020
        //TODO check that this is enough to support it

        for (Method method : c.getMethods()) {
            String operationName = ParsingUtils.getAnnotationPropertyString(method, ANN_WEBMETHOD, "operationName");
            if (operationName == null) {
                operationName = method.getName(); // even non-@WebMethod annotated interface methods are exposed
            }

            boolean doesOperationExist = operationName != null;

            if (acceptNewNonExistingOperations || doesOperationExist/*operationName != null*/) {
                // Extract parameters info
                StringBuilder parametersInfo = new StringBuilder();
                int noNameArgIndex = 0;
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                	Class<?> parameter = method.getParameterTypes()[i];
                	String webParameterName = ParsingUtils.getAnnotationPropertyString(parameter, ANN_WEBPARAM, "name");
                    if (webParameterName == null) {
                        webParameterName = "arg" + noNameArgIndex;
                    }
                    noNameArgIndex++;
                	/*java.lang.annotation.Annotation[] parameterAnnotations = method.getParameterAnnotations()[i];
                	String webParamName = null;
                	if (parameterAnnotations != null) {
                		for (java.lang.annotation.Annotation parameterAnnotation : parameterAnnotations) {
                			if (ANN_WEBPARAM.equals(parameterAnnotation.annotationType().getName())) {
                				WebParam webParamAnn = (WebParam) parameterAnnotation;
                				webParamName = webParamAnn.name();
                				break;
                            }
                		}
                	}*/
                	String webParameterType = getParameterType(parameter);
                    parametersInfo.append(formatParameter(webParameterName, webParameterType) + ", ");
                }
                // removing trailing ", "
                if (parametersInfo.length() > 2) {
                    parametersInfo.delete(parametersInfo.length()-2, parametersInfo.length());
                }

                // extract return parameter info
                String webResultName = ParsingUtils.getAnnotationPropertyString(method, ANN_WEBRESULT, "name");
                if (webResultName == null) {
                    webResultName = operationName + "Response";
                }
                String returnParameterType = getParameterType(method.getReturnType());//TODO test for void, if nullpointerexception copy code for qdox from above
                String returnParametersInfo = formatParameter(webResultName, returnParameterType);

                String operationInContentType = SOAP_CONTENT_TYPE;
                String operationOutContentType = SOAP_CONTENT_TYPE;
                // "in message" way of presenting parameters :
                //parametersInfo.insert(0, SOAP_CONTENT_TYPE + ": " + operationName + "{ ");
                //parametersInfo.append(" }");
                // "out message" way of presenting return :
                //returnParametersInfo = SOAP_CONTENT_TYPE + ": " + returnParametersInfo;

                //TODO LATER also bare signature (or method.getName() ?) :
                //String signature = method.getCallSignature();
                operations.put(method.getName(), new OperationInformation(webResultName,
                        parametersInfo.toString(), returnParametersInfo, null,
                        operationInContentType, operationOutContentType));
            }
        }
        return operations;
	}

}
