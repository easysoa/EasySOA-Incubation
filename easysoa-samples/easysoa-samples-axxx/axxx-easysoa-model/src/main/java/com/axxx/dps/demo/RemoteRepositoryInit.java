package com.axxx.dps.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
import org.easysoa.registry.types.Project;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.FileBlob;

import com.sun.jersey.api.client.UniformInterfaceException;


/**
 * Inits a (remote) EasySOA registry with the SOA model of the AXXX use case.
 * 
 * To run it :
 * * from Eclipse : right-click > Run as Java application) to init full
 * EasySOA model of AXXX use case in running EasySOA Registry.
 * * from Maven command line :
 * mvn clean install exec:java -Dexec.mainClass="com.axxx.dps.demo.RemoteRepositoryInit"
 * 
 * In arguments may be given (in maven by -Dexec.args="[space separated arguments]") :
 * 
 * A. which step to play (if none are given, all of them will be played) :
 * * clean : deletes the existing model
 * * (Specifications are always (updated or) created)
 * * (Realisation must be done by source discovery)
 * * Deploiement : creates a Prod endpoint for PrecomptePartenaireService
 * * Exploitation : creates an SOA monitoring indicator for it (by calling EndpointStateService)
 * 
 * B. properties, in [key]=[value] syntax
 * * username (default : Administrator
 * * password (default : Administrator
 * * apvHost (default : localhost
 * * pivotalHost (default : localhost
 * * registryHost (default : localhost
 * 
 * For instance, if you which to wipe the registry out, then fill Specifications and
 * create Realisation subproject, do :
 * 
 * mvn clean install -Dexec.mainClass="com.axxx.dps.demo.RemoteRepositoryInit" -Dexec.args="clean Specifications Realisation username=Administrator password=Administrator"
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
	
	@SuppressWarnings("serial")
	public static final Map<String, Integer> subprojectNameToIndex = new HashMap<String, Integer>(3) {{
		put(Subproject.SPECIFICATIONS_SUBPROJECT_NAME, 1);
		put(Subproject.REALISATION_SUBPROJECT_NAME, 2);
		put(Subproject.DEPLOIEMENT_SUBPROJECT_NAME, 3);
	}};
	
	private static RegistryApi registryApi;
	private static Session session;
    private static SimpleRegistryService simpleRegistryService;
    private static EndpointStateService endpointStateService;
    
    private static String apvHost = null;
    private static String pivotalHost = null;
    private static String registryHost = null;

	public static final void main(String[] args) throws Exception {
	    String url = "http://localhost:8080/nuxeo/site";
        String username = "Administrator";
        String password = "Administrator";
        HashSet<String> steps = new HashSet<String>(args.length);
	    for (String arg : Arrays.asList(args)) {
	        String[] keyValueArg = arg.split("=");
                if (keyValueArg.length > 1) {
                    String key = keyValueArg[0];
                    String value = keyValueArg[1];
                    if ("url".equals(key)) {
                        url = value;
                    } else if ("username".equals(key)) {
                        username = value;
                    } else if ("password".equals(key)) {
                        password = value;
                    } else if ("apvHost".equals(key)) {
                        apvHost = value;
                    } else if ("pivotalHost".equals(key)) {
                        pivotalHost = value;
                    } else if ("registryHost".equals(key)) {
                        registryHost = value;
                    } else if ("hostMode".equals(key)) {
                        if (true) { // TODO more modes ??
                            pivotalHost = "owsi-vm-easysoa-axxx-pivotal.accelance.net";
                            apvHost = "owsi-vm-easysoa-axxx-apv.accelance.net";
                            registryHost = "owsi-vm-easysoa-axxx-registry.accelance.net";
                        }
                    } else {
                        System.err.println("Unknown key value argument " + keyValueArg);
                    }
                } else {
                    steps.add(arg);
                }
	    }
	    
		initClients(url, username, password);

        String projectName = "Intégration DPS - DCV";
        String rootPath = "/default-domain";
        String projectPath = rootPath + "/" + projectName;

        
        if (steps.isEmpty() || steps.contains("clean")) {
         
        System.out.println("RemoteRepositoryInit clean");
        
        // Delete subprojects first then project.
        delete("SELECT * FROM " + Subproject.DOCTYPE + " WHERE spnode:subproject = '" + projectPath + "/%' AND ecm:isProxy=0");
        delete("SELECT * FROM " + Subproject.DOCTYPE + " WHERE spnode:subproject = '" + projectPath + "/%' AND ecm:isProxy=1");
        delete("SELECT * FROM " + Project.DOCTYPE + " WHERE ecm:path='" + projectPath + "' AND ecm:isProxy=0");
        delete("SELECT * FROM " + Project.DOCTYPE + " WHERE ecm:path='" + projectPath + "' AND ecm:isProxy=1");
        //TODO better import NXQL and rather use NXQL.ECM_ISPROXY
        // NB. these exact requests are required else errors :
        // No ORDER BY ecm:isProxy, else QueryMaker$QueryMakerException: Cannot order by column: ecm:isProxy
        // No ecm:path LIKE, else QueryMaker$QueryMakerException: ecm:path requires = or <> operator
        // Non-proxies first, else when their time comes (ex. subproject published version proxy) :
        // ClientException: Failed to fetch document xxx before removal, DocumentException: Unknown document type: null
        }
        
        
        // PHASE 1: Specs
        
        String specificationsPath = projectPath + '/' + Subproject.SPECIFICATIONS_SUBPROJECT_NAME;
        
        if (steps.isEmpty() || steps.contains("Specifications")) {

        // Project and its folders
        //String projectPath = createSoaNode(new SoaNodeId(TaggingFolder.DOCTYPE, "Intégration APV Web - Pivotal"), rootPath);
        /*String projectPath = */createDocument(Project.DOCTYPE, projectName, rootPath);
        /*String specificationsPath = createDocument("Folder", "1. Spécifications", projectPath);
        createDocument("Folder", "2. Réalisation", projectPath);
        createDocument("Folder", "3. Déploiements", projectPath);*/

        System.out.println("RemoteRepositoryInit Specifications");
        
        //String specificationsSubprojectId = (String) getDocByPath(specificationsPath).getProperties().get("spnode:subproject");
        Document specificationsSubprojectDoc = getSubproject(specificationsPath + "_v");
        if (specificationsSubprojectDoc == null) {
            /*String specificationsPath = */createSubproject(projectPath, Subproject.SPECIFICATIONS_SUBPROJECT_NAME, null);
            specificationsSubprojectDoc = getSubproject(specificationsPath + "_v");
        }
        String specificationsSubprojectId = (String) specificationsSubprojectDoc.getProperties().get(SubprojectNode.XPATH_SUBPROJECT);
        
        //String platformArchitecturePath = "/default-domain/repository/Platform";
        String platformArchitecturePath = specificationsPath + "/repository/Platform";//TODO within project or out (i.e. global) ??
        String platformSubprojectId = specificationsSubprojectId;
        
        
		// Specifications sub-folders
		String businessArchitecturePath = createDocument("Folder", "1. Business architecture", specificationsPath);
		String infoArchitecturePath = createDocument("Folder", "2. Information architecture", specificationsPath);
		String compArchitecturePath = createDocument("Folder", "3. Component architecture", specificationsPath);
		
		// Actors
		SoaNodeId actUniId = new SoaNodeId(specificationsSubprojectId, ACTOR_DOCTYPE, "Uniserv"); createSoaNode(actUniId);
		SoaNodeId actCommId = new SoaNodeId(specificationsSubprojectId, ACTOR_DOCTYPE, "Commercial_AXXX"); createSoaNode(actCommId);
		SoaNodeId actCRMId = new SoaNodeId(specificationsSubprojectId, ACTOR_DOCTYPE, "CRM DCV"); createSoaNode(actCRMId);
        SoaNodeId actPartSoc = new SoaNodeId(specificationsSubprojectId, ACTOR_DOCTYPE, "Partenaires_Sociaux"); createSoaNode(actPartSoc);
		SoaNodeId actSIDPSId = new SoaNodeId(specificationsSubprojectId, ACTOR_DOCTYPE, "SI DPS"); createSoaNode(actSIDPSId);
		SoaNodeId actSIDCVId = new SoaNodeId(specificationsSubprojectId, ACTOR_DOCTYPE, "SI DCV"); createSoaNode(actSIDCVId);
		
		
		// Business services
		
		// OPT Event "demande création précompte"
		SoaNodeId bsDdeCrePrecompteId = new SoaNodeId(specificationsSubprojectId, BUSINESS_SERVICE_DOCTYPE, "Dde_Cré_Précompte");
		String bsDdeCrePrecomptePath = createSoaNode(bsDdeCrePrecompteId, businessArchitecturePath,
				BUSINESS_SERVICE_PROVIDER_ACTOR + "=" + getIdRef(actSIDCVId),
				BUSINESS_SERVICE_CONSUMER_ACTOR + "=" + getIdRef(actCommId)
				);
		createDocument("File", "BusinessArchitecture.doc", businessArchitecturePath);
		createDocument("File", "SpecificationsExternes (=DocUtilisateur).doc", businessArchitecturePath);
		createSoaNode(actCommId, bsDdeCrePrecomptePath);
		createSoaNode(actSIDCVId, bsDdeCrePrecomptePath);
		SoaNodeId slaDdeCrePrecompteId = new SoaNodeId(specificationsSubprojectId, SLA_DOCTYPE, "SLA Dde_Cré_Précompte");
		createSoaNode(slaDdeCrePrecompteId, bsDdeCrePrecomptePath,
		        "dc:description=temps de réponse, taux de disponibilité par période ; Nb_Benef_Prev_N / Ancienneté");
        
		// OPT Event "demande projet vacances"
		SoaNodeId bsDdeProjetVacancesId = new SoaNodeId(specificationsSubprojectId, BUSINESS_SERVICE_DOCTYPE, "Dde_Projet_Vacances");
        String bsDdeProjetVacancesPath = createSoaNode(bsDdeProjetVacancesId, businessArchitecturePath,
                BUSINESS_SERVICE_PROVIDER_ACTOR + "=" + getIdRef(actSIDPSId),
                BUSINESS_SERVICE_CONSUMER_ACTOR + "=" + getIdRef(actPartSoc)
                );
        //createDocument("File", "BusinessArchitecture.doc", businessArchitecturePath);
        //createDocument("File", "SpecificationsExternes (=DocUtilisateur).doc", businessArchitecturePath);
        createSoaNode(actPartSoc, bsDdeProjetVacancesPath);
        createSoaNode(actSIDPSId, bsDdeProjetVacancesPath);
        SoaNodeId slaDdeProjetVacancesId = new SoaNodeId(specificationsSubprojectId, SLA_DOCTYPE, "SLA Dde_Projet_Vacances");
        createSoaNode(slaDdeProjetVacancesId, bsDdeProjetVacancesPath,
                "dc:description=tps de réponse max, taux de disponibilité par période ; Nb_Benef_N / Nb_Benef_Prev_N");
 
        
		// Information services
        
        createSoaNode(new SoaNodeId(specificationsSubprojectId, InformationService.DOCTYPE, "Cré_Précpte"), infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDCVId),
                InformationService.XPATH_LINKED_BUSINESS_SERVICE + "=" + getIdRef(bsDdeCrePrecompteId));
        
        createSoaNode(new SoaNodeId(specificationsSubprojectId, InformationService.DOCTYPE, "Projet_Vacances"), infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDPSId),
                InformationService.XPATH_LINKED_BUSINESS_SERVICE + "=" + getIdRef(bsDdeProjetVacancesId));
        
		SoaNodeId isCheckAddressId = new SoaNodeId(specificationsSubprojectId, InformationService.DOCTYPE, "checkAddress");
		String isCheckAddressPath = createSoaNode(isCheckAddressId, infoArchitecturePath,
		        InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actUniId)/*,
                InformationService.XPATH_WSDL_PORTTYPE_NAME + "={http://ipf.webservice.rt.saas.uniserv.com}InternationalPostalValidationPortType"*/);
        uploadWsdl(isCheckAddressPath, "../axxx-easysoa-model/src/main/resources/InternationalPostalValidation_nourl.wsdl");
            // or manually upload WSDL on it
        ///isCheckAddressId = new SoaNodeId(isCheckAddressId.getType(), "ws:http://ipf.webservice.rt.saas.uniserv.com:InternationalPostalValidationPortType");
            // updating soaname to what the wsdl upload has set it to
            // TODO or never let it change ?!
        SoaNodeId olaCheckAddressId = new SoaNodeId(specificationsSubprojectId, OLA_DOCTYPE, "OLA checkAddress");
        createSoaNode(olaCheckAddressId, isCheckAddressPath,
                "dc:description=temps de réponse max, taux de disponibilité par période");

        SoaNodeId isPrecomptePartenaireServiceId = new SoaNodeId(specificationsSubprojectId, InformationService.DOCTYPE, "PrecomptePartenaireService"); // OLD TdrWebService
        String isPrecomptePartenaireServicePath = createSoaNode(isPrecomptePartenaireServiceId, infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDPSId)/*,
		        InformationService.XPATH_WSDL_PORTTYPE_NAME + "={http://www.axxx.com/dps/apv}PrecomptePartenaireService"*/);
		uploadWsdl(isPrecomptePartenaireServicePath, "../axxx-dps-apv/axxx-dps-apv-core/src/main/resources/api/PrecomptePartenaireService.wsdl");
		    // or manually upload WSDL on it
		///isPrecomptePartenaireServiceId = new SoaNodeId(isPrecomptePartenaireServiceId.getType(), "ws:http://www.axxx.com/dps/apv:PrecomptePartenaireService");
		    // updating soaname to what the wsdl upload has set it to
		    // TODO or never let it change ?!
		SoaNodeId olaPrecomptePartenaireServiceId = new SoaNodeId(specificationsSubprojectId, OLA_DOCTYPE, "OLA PrecomptePartenaireService"); // OLD OLA TdrWebService
		createSoaNode(olaPrecomptePartenaireServiceId, isPrecomptePartenaireServicePath,
		        "dc:description=temps de réponse max, taux de disponibilité par période");
		
		SoaNodeId isInformationAPVId = new SoaNodeId(specificationsSubprojectId, InformationService.DOCTYPE, "Information_APV");
		String isInformationAPVPath = createSoaNode(isInformationAPVId, infoArchitecturePath,
                InformationService.XPATH_PROVIDER_ACTOR + "=" + getIdRef(actSIDCVId)/*,
                InformationService.XPATH_WSDL_PORTTYPE_NAME + "={http://pivotal.axxx.fr/}ContactSvcSoap"*/);
        uploadWsdl(isInformationAPVPath, "../axxx-dcv-pivotal/src/main/resources/api/ContactSvc.asmx.wsdl");
        SoaNodeId olaInformationAPVId = new SoaNodeId(specificationsSubprojectId, OLA_DOCTYPE, "OLA Information_APV");
        createSoaNode(olaInformationAPVId, isInformationAPVPath,
                "dc:description=temps de réponse max, taux de disponibilité par période");
		
        // OPT contactClient
        
		
		// Platforms
        
        SoaNodeId axxxDotNETWSPlatform = new SoaNodeId(platformSubprojectId, Platform.DOCTYPE, "AXXX .NET WS");
        createSoaNode(axxxDotNETWSPlatform, platformArchitecturePath,
                "dc:description=Note : les 'vrais' critères d'une plateforme MS .NET ne sont pas appliqués "
                + "afin que malgré l'implémentation de Pivotal dans AXXX en Java/FraSCAti ses services matchent tout de même.",
                Platform.XPATH_IDE + "=" + "Visual Studio",
                ////Platform.XPATH_LANGUAGE + "=" + "C#", //// not to prevent our Pivotal on FStudio impl
                //Platform.XPATH_BUILD + "=" + "", // Visual Studio ?
                ////Platform.XPATH_SERVICE_LANGUAGE + "=" + "C#", // ? //// not to prevent our Pivotal on FStudio impl
                //Platform.XPATH_DELIVERABLE_NATURE + "=" + "Maven", // ?
                //Platform.XPATH_DELIVERABLE_REPOSITORY_URL + "=" + "http://owsi-vm-easysoa-axxx-registry.accelance.net/maven", // simulated CI // ?
                Platform.XPATH_SERVICE_PROTOCOL + "=" + "SOAP",
                Platform.XPATH_TRANSPORT_PROTOCOL + "=" + "HTTP"
                ////Platform.XPATH_SERVICE_RUNTIME + "=" + ".NET", // ? //// not to prevent our Pivotal on FStudio impl
                ////Platform.XPATH_APP_SERVER_RUNTIME + "=" + ".NET" // ? //// not to prevent our Pivotal on FStudio impl
                        );
		
        SoaNodeId axxxJavaWSPlatform = new SoaNodeId(platformSubprojectId, Platform.DOCTYPE, "AXXX Java WS");
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
        
        SoaNodeId genericWSPlatform = new SoaNodeId(platformSubprojectId, Platform.DOCTYPE, "Generic WS");
        createSoaNode(genericWSPlatform, platformArchitecturePath,
                Platform.XPATH_SERVICE_PROTOCOL + "=" + "SOAP",
                Platform.XPATH_TRANSPORT_PROTOCOL + "=" + "HTTP");

        SoaNodeId axxxTalendESBPlatform = new SoaNodeId(platformSubprojectId, Platform.DOCTYPE, "AXXX Talend ESB");
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
        
        SoaNodeId cptCheckAddressId = new SoaNodeId(specificationsSubprojectId, COMPONENT_DOCTYPE, "Uniserv"); // TODO ? OLD checkAddress
        createSoaNode(cptCheckAddressId, compArchitecturePath,
				COMPONENT_TYPE + "=Application",
				COMPONENT_INFORMATION_SERVICE + "=" + getIdRef(isCheckAddressId)); // réalise IS checkAddress TODO not the other way ?!?
        createSoaNode(cptCheckAddressId, getPath(genericWSPlatform)); // again, to give it its platform
		
		SoaNodeId cptPrecomptePartenaireServiceId = new SoaNodeId(specificationsSubprojectId, COMPONENT_DOCTYPE, "APV"); // OLD PrecomptePartenaireService, TdrWebService
		createSoaNode(cptPrecomptePartenaireServiceId, compArchitecturePath,
				COMPONENT_TYPE + "=Application",
				/*"platform:serviceLanguage=JAX-WS",*/
                COMPONENT_INFORMATION_SERVICE + "=" + getIdRef(isPrecomptePartenaireServiceId)); // TODO the other way ?!?
        createSoaNode(cptPrecomptePartenaireServiceId, getPath(axxxJavaWSPlatform)); // again, to give it its platform
		
        SoaNodeId cptInformationAPVId = new SoaNodeId(specificationsSubprojectId, COMPONENT_DOCTYPE, "Pivotal"); // OLD Information_APV
        createSoaNode(cptInformationAPVId, compArchitecturePath,
				COMPONENT_TYPE + "=Application",
                COMPONENT_INFORMATION_SERVICE + "=" + getIdRef(isInformationAPVId)); // TODO the other way ?!?
        createSoaNode(cptInformationAPVId, getPath(axxxDotNETWSPlatform)); // again, to give it its platform
		
        SoaNodeId cptOrchestrationDCVId = new SoaNodeId(specificationsSubprojectId, COMPONENT_DOCTYPE, "Orchestration_DCV");
        createSoaNode(cptOrchestrationDCVId, compArchitecturePath,
				COMPONENT_TYPE + "=Application"); // TODO réalise IS Cré_Précpte
        createSoaNode(cptOrchestrationDCVId, getPath(axxxTalendESBPlatform)); // again, to give it its platform

        // TODO ??!!??
        SoaNodeId cptOrchestrationDPSId = new SoaNodeId(specificationsSubprojectId, COMPONENT_DOCTYPE, "Orchestration_DPS");
        createSoaNode(cptOrchestrationDPSId, compArchitecturePath,
				COMPONENT_TYPE + "=Application"); // TODO réalise IS Projet_Vacances
        createSoaNode(cptOrchestrationDPSId, getPath(axxxTalendESBPlatform)); // again, to give it its platform
		
        createSoaNode(new SoaNodeId(specificationsSubprojectId, COMPONENT_DOCTYPE, "Pivotal"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
		
        createSoaNode(new SoaNodeId(specificationsSubprojectId, COMPONENT_DOCTYPE, "APV_Web"), compArchitecturePath,
				COMPONENT_TYPE + "=Application");
        
        // OPT contactClient

		//}
        
        }

        
        
        // PHASE 2: Realisation

        String realisationPath = projectPath + '/' + Subproject.REALISATION_SUBPROJECT_NAME;
        
		if (steps.isEmpty() || steps.contains("Realisation")) {
        
        System.out.println("RemoteRepositoryInit Realisation");

        Document realisationSubprojectDoc = getSubproject(realisationPath + "_v"); // trying to reuse existing one
        if (realisationSubprojectDoc != null) {
            System.out.println("   reusing existing Realisation subproject");
        } else {
            Document realisationSubprojectParentSpecificationsDoc = getExistingVersionedElseLiveSubprojectId(specificationsPath);
            
            System.out.println("   creating Realisation subproject");
            /*String realisationPath = */createSubproject(projectPath, Subproject.REALISATION_SUBPROJECT_NAME,
            		realisationSubprojectParentSpecificationsDoc.getId());
            realisationSubprojectDoc = getDocByPath(realisationPath);
        }
        String realisationSubprojectId = (String) realisationSubprojectDoc.getProperties().get(SubprojectNode.XPATH_SUBPROJECT);
        
		// (checkout AXXX source and)
		// do a source discovery
		//publish(specificationsPath, realizationPath);
//		createSoaNode(new SoaNodeId(realisationSubprojectId, ???.DOCTYPE, "checkAddress"), realizationPath);
//		createSoaNode(new SoaNodeId(realisationSubprojectId, ???.DOCTYPE, "PrecomptePartenaireService"), realizationPath,
//				?????? + "=" + getIdRef(new SoaNodeId(COMPONENT_DOCTYPE, "PrecomptePartenaireService")));
//		createSoaNode(new SoaNodeId(realisationSubprojectId, ???.DOCTYPE, "Information_APV"), realizationPath);
//		createSoaNode(new SoaNodeId(realisationSubprojectId, ???.DOCTYPE, "Orchestration_DCV"), realizationPath);
//		createSoaNode(new SoaNodeId(realisationSubprojectId, ???.DOCTYPE, "Orchestration_DPS"), realizationPath);

		}

        
        // PHASE 3: Deploiement
		
        String deploiementPath = projectPath + '/' + Subproject.DEPLOIEMENT_SUBPROJECT_NAME;
        
		if (steps.isEmpty() || steps.contains("Deploiement")) {
	        
        System.out.println("RemoteRepositoryInit Deploiement");

        Document deploiementSubprojectDoc = getSubproject(deploiementPath + "_v"); // trying to reuse existing one
        if (deploiementSubprojectDoc != null) {
            System.out.println("   reusing existing Deploiement subproject");
        } else {
            Document deploiementSubprojectParentRealisationDoc = getExistingVersionedElseLiveSubprojectId(realisationPath);
            
            System.out.println("   creating Deploiement subproject");
            /*String deploiementPath = */createSubproject(projectPath, Subproject.DEPLOIEMENT_SUBPROJECT_NAME,
            		deploiementSubprojectParentRealisationDoc.getId());
            deploiementSubprojectDoc = getDocByPath(deploiementPath);
        }
        String deploiementSubprojectId = (String) deploiementSubprojectDoc.getProperties().get(SubprojectNode.XPATH_SUBPROJECT);
        
        SoaNodeId precomptePartenaireServiceProdEndpointId = new SoaNodeId(deploiementSubprojectId, Endpoint.DOCTYPE, "Prod:http://" + getApvHost() + ":7080/apv/services/PrecomptePartenaireService");//TODO host
        SoaNodeId checkAddressProdEndpointId = new SoaNodeId(deploiementSubprojectId, Endpoint.DOCTYPE, "Prod:" + fromEnc("iuuq;00xxx/ebub.rvbmjuz.tfswjdf/dpn0npdlxtr5220tfswjdft0JoufsobujpobmQptubmWbmjebujpo/JoufsobujpobmQptubmWbmjebujpoIuuqTpbq22Foeqpjou0"));//checkAddressProdEndpoint
        SoaNodeId informationAPVProdEndpointId = new SoaNodeId(deploiementSubprojectId, Endpoint.DOCTYPE, "Prod:http://" + getPivotalHost() + ":7080/WS/ContactSvc.asmx");//TODO host // 18000

		// manually : do a web discovery
		// TODO web discovery : upload wsdl
		    
        String precomptePartenaireServiceProdEndpointPath = createSoaNode(precomptePartenaireServiceProdEndpointId, (String) null,
                Endpoint.XPATH_ENDP_ENVIRONMENT + "=Prod", // required even with soaname else integrity check fails
                Endpoint.XPATH_URL + "=http://" + getApvHost() + ":7080/apv/services/PrecomptePartenaireService"/*, // required even with soaname else integrity check fails
                Endpoint.XPATH_WSDL_PORTTYPE_NAME + "={http://www.axxx.com/dps/apv}PrecomptePartenaireService"*/);
        uploadWsdl(precomptePartenaireServiceProdEndpointPath, "../axxx-dps-apv/axxx-dps-apv-core/src/main/resources/api/PrecomptePartenaireService.wsdl");
            // or do a web discovery
            // TODO web discovery : upload wsdl (for now manually upload WSDL on it)

        String checkAddressProdEndpointPath = createSoaNode(checkAddressProdEndpointId, (String) null,
                Endpoint.XPATH_ENDP_ENVIRONMENT + "=Prod", // required even with soaname else integrity check fails
                Endpoint.XPATH_URL + "=" + fromEnc("iuuq;00xxx/ebub.rvbmjuz.tfswjdf/dpn0npdlxtr5220tfswjdft0JoufsobujpobmQptubmWbmjebujpo/JoufsobujpobmQptubmWbmjebujpoIuuqTpbq22Foeqpjou0")/*, // required even with soaname else integrity check fails
                Endpoint.XPATH_WSDL_PORTTYPE_NAME + "={http://ipf.webservice.rt.saas.uniserv.com}InternationalPostalValidationPortType"*/);
        uploadWsdl(checkAddressProdEndpointPath, "../axxx-easysoa-model/src/main/resources/InternationalPostalValidation_nourl.wsdl");
            // or do a web discovery
            // TODO web discovery : upload wsdl (for now manually upload WSDL on it)

        String informationAPVProdEndpointPath = createSoaNode(informationAPVProdEndpointId, (String) null,
                Endpoint.XPATH_ENDP_ENVIRONMENT + "=Prod", // required even with soaname else integrity check fails
                Endpoint.XPATH_URL + "=http://" + getPivotalHost() + ":7080/WS/ContactSvc.asmx"/*, // required even with soaname else integrity check fails
                Endpoint.XPATH_WSDL_PORTTYPE_NAME + "={http://ipf.webservice.rt.saas.uniserv.com}InternationalPostalValidationPortType"*/);
        uploadWsdl(informationAPVProdEndpointPath, "../axxx-dcv-pivotal/src/main/resources/api/ContactSvc.asmx.wsdl");
            // or do a web discovery
            // TODO web discovery : upload wsdl (for now manually upload WSDL on it)
        
        // TODO Cré_Précpte, Projet_Vacances with component / platform
        
		}
		
		
		if (steps.isEmpty() || steps.contains("Exploitation")) {
		    // (Phase 4) Exploitation
	        
	        System.out.println("RemoteRepositoryInit Exploitation");
            
            Document specificationsSubprojectDoc = getExistingVersionedElseLiveSubprojectId(specificationsPath);
            String specificationsSubprojectId = (String) specificationsSubprojectDoc.getProperties().get(SubprojectNode.XPATH_SUBPROJECT);
	        
	        Document deploiementSubprojectDoc = getExistingVersionedElseLiveSubprojectId(deploiementPath);
	        String deploiementSubprojectId = (String) deploiementSubprojectDoc.getProperties().get(SubprojectNode.XPATH_SUBPROJECT);

	        // copied from Specifications
	        SoaNodeId isPrecomptePartenaireServiceId = new SoaNodeId(specificationsSubprojectId, InformationService.DOCTYPE, "PrecomptePartenaireService");
	        String isPrecomptePartenaireServicePath = createSoaNode(isPrecomptePartenaireServiceId);
	        SoaNodeId olaPrecomptePartenaireServiceId = new SoaNodeId(specificationsSubprojectId, OLA_DOCTYPE, "OLA PrecomptePartenaireService");
	        createSoaNode(olaPrecomptePartenaireServiceId);
	        
	        // copied from Deploiement
	        SoaNodeId precomptePartenaireServiceProdEndpointId = new SoaNodeId(deploiementSubprojectId, Endpoint.DOCTYPE, "Prod:http://" + getApvHost() + ":7080/apv/services/PrecomptePartenaireService");//TODO host
	        String precomptePartenaireServiceProdEndpointPath = createSoaNode(precomptePartenaireServiceProdEndpointId);
	        //String precomptePartenaireServiceProdEndpointNuxeoId = getIdRef(precomptePartenaireServiceProdEndpointId); // alt, does not work in jasmine
	        String precomptePartenaireServiceProdEndpointNuxeoId = registryApi.get(precomptePartenaireServiceProdEndpointId.getSubprojectId(),
	                precomptePartenaireServiceProdEndpointId.getType(), precomptePartenaireServiceProdEndpointId.getName()).getUuid();
	        
	        SlaOrOlaIndicators slaOrOlaIndicators = new SlaOrOlaIndicators();
	        slaOrOlaIndicators.setSlaOrOlaIndicatorList(new ArrayList<SlaOrOlaIndicator>());
	        SlaOrOlaIndicator slaOrOlaIndicator = new SlaOrOlaIndicator();
	        slaOrOlaIndicator.setEndpointId(precomptePartenaireServiceProdEndpointNuxeoId);
	        slaOrOlaIndicator.setSlaOrOlaName(olaPrecomptePartenaireServiceId.getName());
	        slaOrOlaIndicator.setTimestamp(new Date());
	        slaOrOlaIndicator.setServiceLevelHealth(ServiceLevelHealth.gold);
	        slaOrOlaIndicator.setServiceLevelViolation(false);
            slaOrOlaIndicators.getSlaOrOlaIndicatorList().add(slaOrOlaIndicator);
            try {
                endpointStateService.createSlaOlaIndicators(slaOrOlaIndicators);
                // TODO still pb :
                // Exception in thread "main" com.sun.jersey.api.client.UniformInterfaceException: POST http://localhost:8080/nuxeo/site/easysoa/endpointStateService/slaOlaIndicators returned a response status of 204 No Content
                // for now, rather use the other Jersey client like in EndpointStateServiceImpl
            } catch  (UniformInterfaceException uiex) {
                if (!uiex.getMessage().contains("returned a response status of 204 No Content")) {
                    throw uiex; // TODO catch & log
                } // else expected error because of void return
            }
		}
	}

    private static String getApvHost() {
        if (apvHost == null) {
            return "localhost";
        }
        return apvHost;
    }

    private static String getPivotalHost() {
        if (pivotalHost == null) {
            return "localhost";
        }
        return pivotalHost;
    }

    private static String getRegistryHost() {
        if (registryHost == null) {
            return "localhost";
        }
        return registryHost;
    }
    
    private static Document getExistingVersionedElseLiveSubprojectId(String subprojectPath) throws Exception {
        Document deploiementSubprojectDoc = getSubproject(subprojectPath + "_v0.1"); // trying to use versioned one
        if (deploiementSubprojectDoc != null) {
            System.out.println("   reusing versioned subproject " + subprojectPath);
        } else {
            System.out.println("   reusing live subproject " + subprojectPath);
            deploiementSubprojectDoc = getSubproject(subprojectPath + "_v"); // reusing live one
            if (deploiementSubprojectDoc == null) {
                throw new Exception("Exploitation : missing subproject " + subprojectPath);
            }
        }
        return deploiementSubprojectDoc;
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
        Document soaNode = getSoaNode(soaNodeId);
        if (soaNode != null) {
        	return soaNode.getId();
        }
        return null;
	}

    public static String getPath(SoaNodeId soaNodeId) throws Exception {
        Document soaNode = getSoaNode(soaNodeId);
        if (soaNode != null) {
        	return soaNode.getPath();
        }
        return null;
    }

    public static Document getSoaNode(SoaNodeId soaNodeId) throws Exception {
        Documents info = (Documents) session
                .newRequest("Document.Query")
                .set("query", "SELECT * FROM " + soaNodeId.getType() + 
                        " WHERE spnode:subproject = '" + soaNodeId.getSubprojectId() + "'" +
                        " AND soan:name = '" + soaNodeId.getName() + "'" +
                        " AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0").execute();
		if (info != null) {
			if (info.size() == 1) {
				return info.get(0);
			} else if (!info.isEmpty()) {
				System.out.println("WARNING more than one document with soaNodeId=" + soaNodeId);
			}
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
				.newRequest("Document.Query").setHeader("X-NXDocumentProperties", "*")
				.set("query", "SELECT * FROM Document WHERE ecm:path = '" + path + "'").execute();
		if (docs != null && !docs.isEmpty()) {
			return docs.get(0);
		}
		else {
			return null;
		}
	}
    
    private static Document getSubproject(String subprojectId) throws Exception {
        Documents docs = (Documents) session
                .newRequest("Document.Query").setHeader("X-NXDocumentProperties", "*")
                .set("query", "SELECT * FROM Subproject WHERE spnode:subproject = '" + subprojectId + "'").execute();
        if (docs != null && !docs.isEmpty()) {
            return docs.get(0);
        }
        else {
            return null;
        }
    }

	public static String createDocument(String doctype, String name, String path, String... properties) throws Exception {
		if (path == null) {
			throw new Exception("Path is null");
		}
		
		// returns existing one if already exists (to allow for several steps)
		Document parentDocument = getDocByPath(path);
        Documents existingDocuments = (Documents) session
                .newRequest("Document.Query")
                .set("query", "SELECT * FROM " + doctype + " WHERE ecm:parentId = '" + parentDocument.getId()
                        + "' AND ecm:name='" + name + "'").execute();
        if (existingDocuments != null && !existingDocuments.isEmpty()) {
            return existingDocuments.get(0).getPath();
        }
		
		OperationRequest request = session.newRequest("Document.Create")
				.setInput(parentDocument).set("type", doctype)
				.set("name", name);
		StringBuilder propertiesString = new StringBuilder();
		boolean customTitle = false;
		for (String property : properties) {
			propertiesString.append('\n' + property);
			customTitle = customTitle || property.startsWith("dc:title=");
		}
		if (!customTitle) {
			propertiesString.append('\n' + "dc:title=" + name);
		}
		request.set("properties", propertiesString);
		Document createdDocument = (Document) request.execute();
		return createdDocument.getPath();
	}

	private static String createSoaNode(SoaNodeId soaNodeId) throws Exception {
		return createSoaNode(soaNodeId, (String) null);
	}
	
	public static Document createSubproject(String projectPath, String subprojectName,
			String deploiementSubprojectParentRealisationDocId) throws Exception {
        ///System.out.println("   creating " + subprojectName + " subproject");
        int subprojectIndex = subprojectNameToIndex.get(subprojectName);
        String titlePropLine = "dc:title=" + subprojectIndex + ". " + subprojectName;
        String subprojectPath;
        if (deploiementSubprojectParentRealisationDocId == null || deploiementSubprojectParentRealisationDocId.isEmpty()) {
        	subprojectPath= createDocument(Subproject.DOCTYPE, subprojectName, projectPath, titlePropLine);
        } else {
        	subprojectPath= createDocument(Subproject.DOCTYPE, subprojectName, projectPath, titlePropLine,
                Subproject.XPATH_PARENT_SUBPROJECTS + "=" + deploiementSubprojectParentRealisationDocId);
        }
        //String realisationSubprojectId = (String) getDocByPath(realisationPath).getProperties().get("spnode:subproject");
        return getDocByPath(subprojectPath); 
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
	
	public static void initClients(String url, String username, String password) {
                System.out.println("Logging in " + username + "/" + password);
		ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(url);
        clientBuilder.setCredentials(username, password);
		registryApi = clientBuilder.constructRegistryApi();
        simpleRegistryService = clientBuilder.constructSimpleRegistryService();
		endpointStateService = clientBuilder.constructEndpointStateService();
		
		HttpAutomationClient client = new HttpAutomationClient(url + "/automation");
		session = client.getSession(username, password);
	}
	
	public static String getSourceFolderPath(String doctype) {
        return Subproject.DEFAULT_SUBPROJECT_PATH + '/' + Repository.REPOSITORY_NAME + '/' + doctype; 
    }

    public RegistryApi getRegistryApi() {
        return registryApi;
    }
    
}
