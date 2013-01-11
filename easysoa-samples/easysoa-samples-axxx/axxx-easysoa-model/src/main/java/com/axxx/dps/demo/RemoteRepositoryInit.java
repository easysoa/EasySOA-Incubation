package com.axxx.dps.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.facets.WsdlInfoFacet;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.integration.EndpointStateService;
import org.easysoa.registry.rest.integration.ServiceLevelHealth;
import org.easysoa.registry.rest.integration.SimpleRegistryService;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicator;
import org.easysoa.registry.rest.integration.SlaOrOlaIndicators;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Platform;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.FileBlob;

import com.sun.jersey.api.client.WebResource;


/**
 * Inits a (remote) EasySOA registry with the SOA model of the AXXX use case.
 * 
 * To run it :
 * * from Eclipse : right-click > Run as Java application) to init full
 * EasySOA model of AXXX use case in running EasySOA Registry.
 * * from Maven command line :
 * mvn clean install exec:java -Dexec.mainClass="com.axxx.dps.demo.RemoteRepositoryInit"
 * 
 * The following arguments may be given (in maven by -Dexec.args="<space separated arguments>") to
 * specify which step to play (if none are given, all of them will be played) :
 * * clean : deletes the existing model
 * * (Specifications are always (updated or) created)
 * * (Realisation must be done by source discovery)
 * * Deploiement : creates a Prod endpoint for TdrWebService
 * * Exploitation : creates an SOA monitoring indicator for it (by calling EndpointStateService)
 * 
 * WARNING: Requires a running EasySOA Registry on port 8080 (or at least a launched Nuxeo DM
 * with the Nuxeo Studio project "EasySOA" deployed)
 * 
 * @author mkalam-alami
 *
 */
public class RemoteRepositoryInit {

	public static final String ACTOR_DOCTYPE = "Actor";
	public static final String BUSINESS_SERVICE_DOCTYPE = "BusinessService";
	public static final String BUSINESS_SERVICE_PROVIDER_ACTOR = "businessservice:providerActor";
	public static final String BUSINESS_SERVICE_CONSUMER_ACTOR = "businessservice:consumerActor";
	public static final String SLA_DOCTYPE = "SLA";
	public static final String OLA_DOCTYPE = "OLA";
	public static final String COMPONENT_DOCTYPE = "Component";
	public static final String COMPONENT_TYPE = "acomp:componentCategory";
	public static final String COMPONENT_INFORMATION_SERVICE = "acomp:linkedInformationService";
	public static final String COMPONENT_PROVIDER_ACTOR = "acomp:providerActor";
	
	private static RegistryApi registryApi;
	private static Session session;
    private static SimpleRegistryService simpleRegistryService;
    private static EndpointStateService endpointStateService;

	public static final void main(String[] args) throws Exception {
	    HashSet<String> steps = new HashSet<String>(Arrays.asList(args));
	    
		initClients();

        String rootName = "Projets collaboratifs";
        
		if (steps.isEmpty() || steps.contains("clean")) {
		 
		// Reset repository and root
		delete("SELECT * FROM " + SystemTreeRoot.DOCTYPE + " WHERE dc:title = '" + rootName + "'");
		delete("SELECT * FROM " + Repository.DOCTYPE);
		
		// Root
		delete("SELECT * FROM " + SystemTreeRoot.DOCTYPE + " WHERE dc:title = '" + rootName + "'");
        
        }
        
        //if (steps.isEmpty() || steps.contains("Specifications")) {
		
		String rootPath = createDocument(SystemTreeRoot.DOCTYPE, rootName, "/default-domain/workspaces");

        String platformArchitecturePath = "/default-domain/repository/Platform";//TODO or null ??
		
		// Project and its folders
		String projectPath = createSoaNode(new SoaNodeId(TaggingFolder.DOCTYPE, "Intégration APV Web - Pivotal"), rootPath);
		String specificationsPath = createDocument("Folder", "1. Spécifications", projectPath);
		createDocument("Folder", "2. Réalisation", projectPath);
		createDocument("Folder", "3. Déploiements", projectPath);
		
		
		// PHASE 1: Specs
		
		// Specifications sub-folders
		String businessArchitecturePath = createDocument("Folder", "1. Business architecture", specificationsPath);
		String infoArchitecturePath = createDocument("Folder", "2. Information architecture", specificationsPath);
		String compArchitecturePath = createDocument("Folder", "3. Component architecture", specificationsPath);
		
		// Actors
		SoaNodeId actUniId = new SoaNodeId(ACTOR_DOCTYPE, "Uniserv"); createSoaNode(actUniId);
		SoaNodeId actCommId = new SoaNodeId(ACTOR_DOCTYPE, "Commercial_AXXX"); createSoaNode(actCommId);
		SoaNodeId actCRMId = new SoaNodeId(ACTOR_DOCTYPE, "CRM DCV"); createSoaNode(actCRMId);
        SoaNodeId actPartSoc = new SoaNodeId(ACTOR_DOCTYPE, "Partenaires_Sociaux"); createSoaNode(actPartSoc);
		SoaNodeId actSIDPSId = new SoaNodeId(ACTOR_DOCTYPE, "SI DPS"); createSoaNode(actSIDPSId);
		SoaNodeId actSIDCVId = new SoaNodeId(ACTOR_DOCTYPE, "SI DCV"); createSoaNode(actSIDCVId);
		
		
		// Business services
		
		// OPT Event "demande création précompte"
		SoaNodeId bsDdeCrePrecompteId = new SoaNodeId(BUSINESS_SERVICE_DOCTYPE, "Dde_Cré_Précompte");
		String bsDdeCrePrecomptePath = createSoaNode(bsDdeCrePrecompteId, businessArchitecturePath,
				BUSINESS_SERVICE_PROVIDER_ACTOR + "=" + getIdRef(actSIDCVId),
				BUSINESS_SERVICE_CONSUMER_ACTOR + "=" + getIdRef(actCommId)
				);
		createDocument("File", "BusinessArchitecture.doc", businessArchitecturePath);
		createDocument("File", "SpecificationsExternes (=DocUtilisateur).doc", businessArchitecturePath);
		createSoaNode(actCommId, bsDdeCrePrecomptePath);
		createSoaNode(actSIDCVId, bsDdeCrePrecomptePath);
		SoaNodeId slaDdeCrePrecompteId = new SoaNodeId(SLA_DOCTYPE, "SLA Dde_Cré_Précompte");
		createSoaNode(slaDdeCrePrecompteId, bsDdeCrePrecomptePath,
		        "dc:description=temps de réponse, taux de disponibilité par période ; Nb_Benef_Prev_N / Ancienneté");
        
		// OPT Event "demande projet vacances"
		SoaNodeId bsDdeProjetVacancesId = new SoaNodeId(BUSINESS_SERVICE_DOCTYPE, "Dde_Projet_Vacances");
        String bsDdeProjetVacancesPath = createSoaNode(bsDdeProjetVacancesId, businessArchitecturePath,
                BUSINESS_SERVICE_PROVIDER_ACTOR + "=" + getIdRef(actSIDPSId),
                BUSINESS_SERVICE_CONSUMER_ACTOR + "=" + getIdRef(actPartSoc)
                );
        //createDocument("File", "BusinessArchitecture.doc", businessArchitecturePath);
        //createDocument("File", "SpecificationsExternes (=DocUtilisateur).doc", businessArchitecturePath);
        createSoaNode(actPartSoc, bsDdeProjetVacancesPath);
        createSoaNode(actSIDPSId, bsDdeProjetVacancesPath);
        SoaNodeId slaDdeProjetVacancesId = new SoaNodeId(SLA_DOCTYPE, "SLA Dde_Projet_Vacances");
        createSoaNode(slaDdeProjetVacancesId, bsDdeProjetVacancesPath,
                "dc:description=tps de réponse max, taux de disponibilité par période ; Nb_Benef_N / Nb_Benef_Prev_N");
 
        
		// Information services
        
        createSoaNode(new SoaNodeId(InformationService.DOCTYPE, "Cré_Précpte"), infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDCVId),
                InformationService.XPATH_LINKED_BUSINESS_SERVICE + "=" + getIdRef(bsDdeCrePrecompteId));
        
        createSoaNode(new SoaNodeId(InformationService.DOCTYPE, "Projet_Vacances"), infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDPSId),
                InformationService.XPATH_LINKED_BUSINESS_SERVICE + "=" + getIdRef(bsDdeProjetVacancesId));
        
		SoaNodeId isCheckAddressId = new SoaNodeId(InformationService.DOCTYPE, "checkAddress");
		String isCheckAddressPath = createSoaNode(isCheckAddressId, infoArchitecturePath,
		        InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actUniId)/*,
                InformationService.XPATH_WSDL_PORTTYPE_NAME + "={http://ipf.webservice.rt.saas.uniserv.com}InternationalPostalValidationPortType"*/);
        uploadWsdl(isCheckAddressPath, "../axxx-easysoa-model/src/main/resources/InternationalPostalValidation_nourl.wsdl");
            // or manually upload WSDL on it
        isCheckAddressId = new SoaNodeId(isCheckAddressId.getType(), "ws:http://ipf.webservice.rt.saas.uniserv.com:InternationalPostalValidationPortType");
            // updating soaname to what the wsdl upload has set it to
            // TODO or never let it change ?!
        SoaNodeId olaCheckAddressId = new SoaNodeId(OLA_DOCTYPE, "OLA checkAddress");
        createSoaNode(olaCheckAddressId, isCheckAddressPath,
                "dc:description=temps de réponse max, taux de disponibilité par période");

        SoaNodeId isTdrWebServiceId = new SoaNodeId(InformationService.DOCTYPE, "TdrWebService");
        String isTdrWebServicePath = createSoaNode(isTdrWebServiceId, infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDPSId)/*,
		        InformationService.XPATH_WSDL_PORTTYPE_NAME + "={http://www.axxx.com/dps/apv}PrecomptePartenaireService"*/);
		uploadWsdl(isTdrWebServicePath, "../axxx-dps-apv/axxx-dps-apv-core/src/main/resources/api/PrecomptePartenaireService.wsdl");
		    // or manually upload WSDL on it
		isTdrWebServiceId = new SoaNodeId(isTdrWebServiceId.getType(), "ws:http://www.axxx.com/dps/apv:PrecomptePartenaireService");
		    // updating soaname to what the wsdl upload has set it to
		    // TODO or never let it change ?!
		SoaNodeId olaTdrWebServiceId = new SoaNodeId(OLA_DOCTYPE, "OLA TdrWebService");
		createSoaNode(olaTdrWebServiceId, isTdrWebServicePath,
		        "dc:description=temps de réponse max, taux de disponibilité par période");
		
		String isInformationAPVPath = createSoaNode(new SoaNodeId(InformationService.DOCTYPE, "Information_APV"), infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDCVId));
        SoaNodeId olaInformationAPVId = new SoaNodeId(OLA_DOCTYPE, "OLA Information_APV");
        createSoaNode(olaInformationAPVId, isInformationAPVPath,
                "dc:description=temps de réponse max, taux de disponibilité par période");
		
        // OPT contactClient
        
		
		// Platforms
        
        SoaNodeId axxxDotNETWSPlatform = new SoaNodeId(Platform.DOCTYPE, "AXXX .NET WS");
        createSoaNode(axxxDotNETWSPlatform, platformArchitecturePath,
                Platform.XPATH_IDE + "=" + "Visual Studio",
                Platform.XPATH_LANGUAGE + "=" + "C#",
                //Platform.XPATH_BUILD + "=" + "", // Visual Studio ?
                Platform.XPATH_SERVICE_LANGUAGE + "=" + "C#", // ?
                //Platform.XPATH_DELIVERABLE_NATURE + "=" + "Maven", // ?
                //Platform.XPATH_DELIVERABLE_REPOSITORY_URL + "=" + "http://owsi-vm-easysoa-axxx-registry.accelance.net/maven", // simulated CI // ?
                Platform.XPATH_SERVICE_PROTOCOL + "=" + "SOAP",
                Platform.XPATH_TRANSPORT_PROTOCOL + "=" + "HTTP",
                Platform.XPATH_SERVICE_RUNTIME + "=" + ".NET", // ?
                Platform.XPATH_APP_SERVER_RUNTIME + "=" + ".NET" // ?
                        );
		
        SoaNodeId axxxJavaWSPlatform = new SoaNodeId(Platform.DOCTYPE, "AXXX Java WS");
        createSoaNode(axxxJavaWSPlatform, platformArchitecturePath,
                //Platform.XPATH_IDE + "=" + "Eclipse", // let open
                Platform.XPATH_LANGUAGE + "=" + "Java",
                Platform.XPATH_BUILD + "=" + "Maven",
                Platform.XPATH_SERVICE_LANGUAGE + "=" + Platform.SERVICE_LANGUAGE_JAXWS,
                Platform.XPATH_DELIVERABLE_NATURE + "=" + "Maven",
                Platform.XPATH_DELIVERABLE_REPOSITORY_URL + "=" + "http://owsi-vm-easysoa-axxx-registry.accelance.net/maven", // simulated CI
                Platform.XPATH_SERVICE_PROTOCOL + "=" + "SOAP",
                Platform.XPATH_TRANSPORT_PROTOCOL + "=" + "HTTP",
                Platform.XPATH_SERVICE_RUNTIME + "=" + "CXF",
                Platform.XPATH_APP_SERVER_RUNTIME + "=" + "Tomcat" // TODO Spring / Tomcat ??
                		); // NB. no security for now 

        SoaNodeId axxxTalendESBPlatform = new SoaNodeId(Platform.DOCTYPE, "AXXX Talend ESB");
        createSoaNode(axxxTalendESBPlatform, platformArchitecturePath,
                Platform.XPATH_IDE + "=" + "Talend Open Studio",
                Platform.XPATH_LANGUAGE + "=" + "Talend Open Studio",
                Platform.XPATH_BUILD + "=" + "Talend Open Studio",
                Platform.XPATH_SERVICE_LANGUAGE + "=" + Platform.SERVICE_LANGUAGE_JAXWS,
                Platform.XPATH_SERVICE_PROTOCOL + "=" + "SOAP",
                Platform.XPATH_TRANSPORT_PROTOCOL + "=" + "HTTP",
                Platform.XPATH_SERVICE_RUNTIME + "=" + "CXF", // TODO Talend (CXF) ??
                Platform.XPATH_APP_SERVER_RUNTIME + "=" + "Karaf" // TODO Talend, OSGi Karaf ??
                        );

        
		// Components
        
		createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "checkAddress"), compArchitecturePath,
				COMPONENT_TYPE + "=Application",
				COMPONENT_INFORMATION_SERVICE + "=" + getIdRef(isCheckAddressId)); // réalise IS checkAddress TODO not the other way ??
		
		SoaNodeId cptTdrWebServiceId = new SoaNodeId(COMPONENT_DOCTYPE, "TdrWebService");
		createSoaNode(cptTdrWebServiceId, compArchitecturePath,
				COMPONENT_TYPE + "=Application",
                "platform:serviceLanguage=JAX-WS",
                COMPONENT_INFORMATION_SERVICE + "=" + getIdRef(isTdrWebServiceId));
        createSoaNode(cptTdrWebServiceId, getPath(axxxJavaWSPlatform)); // again, to give it its platform
		
        SoaNodeId cptInformationAPVId = new SoaNodeId(COMPONENT_DOCTYPE, "Information_APV");
        createSoaNode(cptInformationAPVId, compArchitecturePath,
				COMPONENT_TYPE + "=Application");
        createSoaNode(cptInformationAPVId, getPath(axxxDotNETWSPlatform)); // again, to give it its platform
		
        SoaNodeId cptOrchestrationDCVId = new SoaNodeId(COMPONENT_DOCTYPE, "Orchestration_DCV");
        createSoaNode(cptOrchestrationDCVId, compArchitecturePath,
				COMPONENT_TYPE + "=Application"); // TODO réalise IS Cré_Précpte
        createSoaNode(cptOrchestrationDCVId, getPath(axxxTalendESBPlatform)); // again, to give it its platform

        // TODO ??!!??
        SoaNodeId cptOrchestrationDPSId = new SoaNodeId(COMPONENT_DOCTYPE, "Orchestration_DPS");
        createSoaNode(cptOrchestrationDPSId, compArchitecturePath,
				COMPONENT_TYPE + "=Application"); // TODO réalise IS Projet_Vacances
        createSoaNode(cptOrchestrationDPSId, getPath(axxxTalendESBPlatform)); // again, to give it its platform
		
        createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "Pivotal"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		
        createSoaNode(new SoaNodeId(COMPONENT_DOCTYPE, "APV_Web"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
        
        // OPT contactClient

		//}
		
        
		//if (steps.isEmpty() || steps.contains("Realisation")) {
		
		// PHASE 2: Realisation
		// (checkout AXXX source and)
		// do a source discovery
		//publish(specificationsPath, realizationPath);
//		createSoaNode(new SoaNodeId(???.DOCTYPE, "checkAddress"), realizationPath);
//		createSoaNode(new SoaNodeId(???.DOCTYPE, "TdrWebService"), realizationPath,
//				?????? + "=" + getIdRef(new SoaNodeId(COMPONENT_DOCTYPE, "TdrWebService")));
//		createSoaNode(new SoaNodeId(???.DOCTYPE, "Information_APV"), realizationPath);
//		createSoaNode(new SoaNodeId(???.DOCTYPE, "Orchestration_DCV"), realizationPath);
//		createSoaNode(new SoaNodeId(???.DOCTYPE, "Orchestration_DPS"), realizationPath);

		//}

        
        SoaNodeId tdrWebServiceProdEndpointId = new SoaNodeId(Endpoint.DOCTYPE, "Prod:http://localhost:7080/apv/services/PrecomptePartenaireService");//TdrWebServiceProdEndpoint
        SoaNodeId checkAddressProdEndpointId = new SoaNodeId(Endpoint.DOCTYPE, "Prod:" + fromEnc("iuuq;00xxx/ebub.rvbmjuz.tfswjdf/dpn0npdlxtr5220tfswjdft0JoufsobujpobmQptubmWbmjebujpo/JoufsobujpobmQptubmWbmjebujpoIuuqTpbq22Foeqpjou0"));//checkAddressProdEndpoint
        
		if (steps.isEmpty() || steps.contains("Deploiement")) {
		
        // PHASE 3: Deploiement
		// manually : do a web discovery
		// TODO web discovery : upload wsdl
		    
        String tdrWebServiceProdEndpointPath = createSoaNode(tdrWebServiceProdEndpointId, (String) null,
                Endpoint.XPATH_ENDP_ENVIRONMENT + "=Prod", // required even with soaname else integrity check fails
                Endpoint.XPATH_URL + "=http://localhost:7080/apv/services/PrecomptePartenaireService"/*, // required even with soaname else integrity check fails
                Endpoint.XPATH_WSDL_PORTTYPE_NAME + "={http://www.axxx.com/dps/apv}PrecomptePartenaireService"*/);
        uploadWsdl(tdrWebServiceProdEndpointPath,
                "../axxx-dps-apv/axxx-dps-apv-core/src/main/resources/api/PrecomptePartenaireService.wsdl");
            // or do a web discovery
            // TODO web discovery : upload wsdl (for now manually upload WSDL on it)

        String checkAddressProdEndpointPath = createSoaNode(checkAddressProdEndpointId, (String) null,
                Endpoint.XPATH_ENDP_ENVIRONMENT + "=Prod", // required even with soaname else integrity check fails
                Endpoint.XPATH_URL + "=" + fromEnc("iuuq;00xxx/ebub.rvbmjuz.tfswjdf/dpn0npdlxtr5220tfswjdft0JoufsobujpobmQptubmWbmjebujpo/JoufsobujpobmQptubmWbmjebujpoIuuqTpbq22Foeqpjou0")/*, // required even with soaname else integrity check fails
                Endpoint.XPATH_WSDL_PORTTYPE_NAME + "={http://ipf.webservice.rt.saas.uniserv.com}InternationalPostalValidationPortType"*/);
        uploadWsdl(checkAddressProdEndpointPath,
                "../axxx-easysoa-model/src/main/resources/InternationalPostalValidation_nourl.wsdl");
            // or do a web discovery
            // TODO web discovery : upload wsdl (for now manually upload WSDL on it)
        
		}
		
		
		if (steps.isEmpty() || steps.contains("Exploitation")) {
		    // (Phase 4) Exploitation
		    
	        SlaOrOlaIndicators slaOrOlaIndicators = new SlaOrOlaIndicators();
	        slaOrOlaIndicators.setSlaOrOlaIndicatorList(new ArrayList<SlaOrOlaIndicator>());
	        SlaOrOlaIndicator slaOrOlaIndicator = new SlaOrOlaIndicator();
	        slaOrOlaIndicator.setEndpointId(getIdRef(tdrWebServiceProdEndpointId));
	        slaOrOlaIndicator.setSlaOrOlaName(olaTdrWebServiceId.getName());
	        slaOrOlaIndicator.setTimestamp(new Date());
	        slaOrOlaIndicator.setServiceLevelHealth(ServiceLevelHealth.gold);
	        slaOrOlaIndicator.setServiceLevelViolation(false);
            slaOrOlaIndicators.getSlaOrOlaIndicatorList().add(slaOrOlaIndicator);
            endpointStateService.createSlaOlaIndicators(slaOrOlaIndicators);
            // TODO still pb :
            // Exception in thread "main" com.sun.jersey.api.client.UniformInterfaceException: POST http://localhost:8080/nuxeo/site/easysoa/endpointStateService/slaOlaIndicators returned a response status of 204 No Content
            // for now, rather use the other Jersey client like in EndpointStateServiceImpl
		}
	}

    protected static String toEnc(String s) {
        char[] sChars = s.toCharArray();
        for (int i = 0; i < sChars.length ; i++) {
            sChars[i] = (char) ((sChars[i] + 1) % 256);
        }
        return new String(sChars);
    }
    protected static String fromEnc(String s) {
        char[] sChars = s.toCharArray();
        for (int i = 0; i < sChars.length ; i++) {
            sChars[i] = (char) ((sChars[i] - 1) % 256);
        }
        return new String(sChars);
    }

    public static String getIdRef(SoaNodeId soaNodeId) throws Exception {
		Documents info = (Documents) session
				.newRequest("Document.Query")
				.set("query", "SELECT * FROM " + soaNodeId.getType() + 
						" WHERE soan:name = '" + soaNodeId.getName() + "'" +
						" AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0").execute();
		if (info != null && !info.isEmpty()) {
			return info.get(0).getId();
		}
		return null;
	}

    public static String getPath(SoaNodeId soaNodeId) throws Exception {
        Documents info = (Documents) session
                .newRequest("Document.Query")
                .set("query", "SELECT * FROM " + soaNodeId.getType() + 
                        " WHERE soan:name = '" + soaNodeId.getName() + "'" +
                        " AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0").execute();
        if (info != null && !info.isEmpty()) {
            return info.get(0).getPath();
        }
        return null;
    }
	
	public static String publish(String fromPath, String toParentPath) throws Exception {
		Document document = (Document) session.newRequest("Document.Publish")
			 .setInput(getDocByPath(fromPath))
			 .set("target", getDocByPath(toParentPath))
			 .execute();
		return document.getPath();
	}
	
	private static Document getDocByPath(String path) throws Exception {
		Documents docs = (Documents) session
				.newRequest("Document.Query")
				.set("query", "SELECT * FROM Document WHERE ecm:path = '" + path + "'").execute();
		if (docs != null && !docs.isEmpty()) {
			return docs.get(0);
		}
		else {
			return null;
		}
	}

	public static String createDocument(String doctype, String title, String path, String... properties) throws Exception {
		if (path == null) {
			throw new Exception("Path is null");
		}
		
		// returns existing one if already exists (to allow for several steps)
		Document parentDocument = getDocByPath(path);
        Documents existingDocuments = (Documents) session
                .newRequest("Document.Query")
                .set("query", "SELECT * FROM " + doctype + " WHERE ecm:parentId = '" + parentDocument.getId()
                        + "' AND dc:title='" + title + "'").execute();
        if (existingDocuments != null && !existingDocuments.isEmpty()) {
            return existingDocuments.get(0).getPath();
        }
		
		OperationRequest request = session.newRequest("Document.Create")
				.setInput(parentDocument).set("type", doctype)
				.set("name", title);
		StringBuilder propertiesString = new StringBuilder("dc:title=" + title);
		for (String property : properties) {
			propertiesString.append('\n' + property);
		}
		request.set("properties", propertiesString);
		Document createdDocument = (Document) request.execute();
		return createdDocument.getPath();
	}

	private static String createSoaNode(SoaNodeId soaNodeId) throws Exception {
		return createSoaNode(soaNodeId, (String) null);
	}
	
	public static String createSoaNode(SoaNodeId soaNodeId, String parentPath, String... properties) throws Exception {
		SoaNodeInformation soaNode = new SoaNodeInformation(soaNodeId, null, null);
		if (parentPath != null) {
			soaNode.addParentDocument(new SoaNodeId(null, parentPath));
		}
		for (String property : properties) {
			String[] splitProperty = property.split("=");
			soaNode.setProperty(splitProperty[0], splitProperty[1]);
		}
		registryApi.post(soaNode);
		return getPath(soaNodeId);
	}
	
	public static String createSoaNode(SoaNodeId soaNodeId, SoaNodeId parentSoaNode, String... properties) throws Exception {
		SoaNodeInformation soaNode = new SoaNodeInformation(soaNodeId, null, null);
		if (parentSoaNode != null) {
			soaNode.addParentDocument(parentSoaNode);
		}
		for (String property : properties) {
			String[] splitProperty = property.split("=");
			soaNode.setProperty(splitProperty[0], splitProperty[1]);
		}
		registryApi.post(soaNode);
		return getPath(soaNodeId);
	}

    public static void uploadWsdl(String path, String wsdlFilePath) throws Exception {
        // see http://doc.nuxeo.com/display/NXDOC/Using+Nuxeo+Automation+Client
        FileBlob wsdlBlob = new FileBlob(new File(wsdlFilePath));
        wsdlBlob.setMimeType("text/xml");
        session.newRequest("Blob.Attach").setHeader(
                Constants.HEADER_NX_VOIDOP, "true").setInput(wsdlBlob) // HEADER_NX_VOIDOP => will return null
                .set("document", path).execute();
    }

	public static void delete(String query) throws Exception {
		Documents docs = (Documents) session.newRequest("Document.Query").set("query", query).execute();
		for (Document doc : docs) {
			session.newRequest("Document.Delete").setInput(doc).execute();
		}
	}
	
	public static void initClients() {
		ClientBuilder clientBuilder = new ClientBuilder();
		registryApi = clientBuilder.constructRegistryApi();
        simpleRegistryService = clientBuilder.constructSimpleRegistryService();
		endpointStateService = clientBuilder.constructEndpointStateService();
		
		HttpAutomationClient client = new HttpAutomationClient(
		           "http://localhost:8080/nuxeo/site/automation");
		session = client.getSession("Administrator", "Administrator");
	}
	
	public static String getSourceFolderPath(String doctype) {
        return Repository.REPOSITORY_PATH + '/' + doctype; 
    }

}
