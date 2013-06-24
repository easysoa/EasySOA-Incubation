    
    <#include "/views/EasySOA/macros.ftl">
    <#include "/views/EasySOA/urlMacros.ftl">
    
    <#-- Document Macros -->
    
    <#macro displayPhase subprojectId>
        <#assign phaseShortId = subprojectId?replace('/default-domain/', '')/>
        <#assign phaseShortIdVersionIndex = phaseShortId?last_index_of('_v')/>
        ${phaseShortId?substring(0, phaseShortIdVersionIndex)?replace('/', ' / ')}
        (version <#if phaseShortId?ends_with('_v')>en cours<#else>${phaseShortId?substring(phaseShortIdVersionIndex + 2, phaseShortId?length)}</#if>)
    </#macro>
    
    <#macro displayPhaseIfOutsideContext subprojectIdToDisplay>
        <#if !subprojectId?has_content || subprojectIdToDisplay != subprojectId>
            <#assign parsedSubprojectId = Root.parseSubprojectId(subprojectId)/>
            <#assign parsedSubprojectIdToDisplay = Root.parseSubprojectId(subprojectIdToDisplay)/>
            <#if parsedSubprojectIdToDisplay.getProjectName() != parsedSubprojectId.getProjectName()>
                ${parsedSubprojectIdToDisplay.getProjectName()} /
            </#if> 
            ${parsedSubprojectIdToDisplay.getSubprojectName()}
            <#assign version = parsedSubprojectIdToDisplay.getVersion()/>
            <#-- (version <#if version?length == 0>en cours<#else>${version}</#if>) -->
            <#if version?length == 0>en cours<#else>${version}</#if>
        </#if>
    </#macro>

    <#macro displayServiceTitle service subprojectId visibility>
        <span title="Phase : <@displayPhase service['spnode:subproject']/>" style="color:grey; font-style: italic;">
        
        <#assign phaseName =  Root.parseSubprojectId(service['spnode:subproject']).getSubprojectName()/>
        <#if phaseName == 'Specifications'>
        <#if service['iserv:providerActor']?has_content>
            <#-- NB.  to create a new object, see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html -->
            <#assign providerActor = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['iserv:providerActor']))/>
        <#else>
            <#assign providerActor = ""/>
        </#if>
        <#if service['acomp:componentId']?has_content>
            <#assign component = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['acomp:componentId']))/>
        <#else>
            <#assign component = ""/>
        </#if>
        <@displaySoaNodeTitle providerActor ""/> / <@displaySoaNodeTitle component ""/>
        <#elseif phaseName == 'Realisation'>
            <#-- "technical" service -->
            <#assign actualImpls = Root.getDocumentService().getActualImplementationsOfService(service, service['spnode:subproject'])/>
            <#if actualImpls?has_content && actualImpls?is_sequence>
                <#assign deliverable = Root.getDocumentService().getSoaNodeParent(actualImpls[0], 'Deliverable')/>
                <#if deliverable?has_content && !deliverable?is_string>
                    <#assign applicationName = deliverable['del:application']/>
                </#if>
            </#if>
            <img src="/nuxeo/icons/application.png" alt="application"/>
            <#if applicationName?has_content && applicationName?is_string && applicationName?length != 0>
               ${applicationName}
            <#else>
               (inconnu)
            </#if>
        <#else><#-- if phaseName == 'Deploiement' -->
            <img src="/nuxeo/icons/deliverable.png" alt="serveur"/>
            <!-- TODO -->
            (inconnu)
        </#if>
        
        / </span>
        <span title="SOA ID: ${service['soan:name']}"><@displaySoaNodeTitle service ""/>
        </span> - <@displayPhaseIfOutsideContext service['spnode:subproject']/>
        <#--  (v${service.versionLabel}) -->
    </#macro>
    
    <#macro displayActorLink actor>
        <a href="<@urlToLocalNuxeoDocumentsUi actor/>"/><@displayActorTitle actor/></a>
    </#macro>
    
    <#macro displayActorTitle actor>
        <@displaySoaNodeTitle actor ""/>
    </#macro>
    
    <#macro displayComponentLink component>
        <a href="<@urlToLocalNuxeoDocumentsUi component/>"/><@displayComponentTitle component/></a>
    </#macro>
    
    <#macro displayComponentTitle component>
        <@displaySoaNodeTitle component ""/>
    </#macro>
    
    <#macro displaySoaNodeLink soaNode type>
       <#if soaNode?is_string>
         (inconnu)
       <#else>
         <a href="<@urlToLocalNuxeoDocumentsUi soaNode/>"/><@displaySoaNodeTitle soaNode/></a>
       </#if>
    </#macro>
    
    <#macro displaySoaNodeTitle soaNode iconType>
       <#if soaNode?is_string>
          (inconnu)
       <#else>
          <#if iconType?length = 0>
              <#assign type = soaNode.type/>
          <#else>
              <#assign type = iconType/>
          </#if>
          <#if soaNode?has_content>
             <#assign soaNodeTitle = soaNode.title/>
          <#else>
             <#assign soaNodeTitle = '(inconnu)'/>
          </#if>
          <img src="/nuxeo/icons/${type?lower_case}.png" alt="${type}"/>${soaNodeTitle}
       </#if>
    </#macro>

    <#macro displayImplementationTitle serviceimpl subprojectId visibility>
        <#if providerActor?has_content>
            <#assign providerActorTitle = providerActor.title/>
        <#else>
            <#assign providerActorTitle = '(no actor)'/>
        </#if>
        <#if serviceimpl['impl:ismock'] = '1'><#-- TODO test component... -->
            <#assign componentTitle = '(test component)'/>
        <#elseif component?has_content>
            <#assign componentTitle = component.title/>
        <#else>
            <#assign componentTitle = '(no component)'/>
        </#if>
        <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/>
        <#if deliverable?has_content>
            <#assign deliverableTitle = '(' + deliverable['del:application'] + ') / ' + deliverable['soan:name']/>
        <#else>
            <#assign deliverableTitle = '/ (no deliverable)'/>
        </#if>
        <span title="Phase : <@displayPhase serviceimpl['spnode:subproject']/>" style="color:grey; font-style: italic;">
        ${providerActorTitle} / ${componentTitle} ${deliverableTitle}</span>
        / <span title="SOA ID: ${serviceimpl['soan:name']}">${serviceimpl.title}</span>
        - <@displayPhaseIfOutsideContext serviceimpl['spnode:subproject']/>
        <#--  (v${serviceimpl.versionLabel}) -->
    </#macro>

    <#macro displayEndpointTitle endpoint subprojectId visibility>
        <#assign serviceimpl = Root.getDocumentService().getServiceImplementationFromEndpoint(endpoint)/><#-- TODO if none ?? -->
        <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/><#-- TODO if none ?? -->
        <#if deliverable?has_content && deliverable['del:application']?has_content && deliverable['del:application']?length != 0>
            <#assign applicationTitle = deliverable['del:application']/><!-- + ' / ' + deliverable['soan:name'] -->
        <#else>
            <#-- assign applicationTitle = '(no application)'/ -->
            <#if providerActor?has_content>
                <#assign providerActorTitle = providerActor.title/>
            <#else>
                <#assign providerActorTitle = '(no actor)'/>
            </#if>
            <#if component?has_content>
                <#assign componentTitle = component.title/>
            <#else>
                <#assign componentTitle = '(no component)'/>
            </#if>
            <#assign applicationTitle = providerActorTitle + '/' + componentTitle/>
        </#if>
        <span title="Phase : <@displayPhase endpoint['spnode:subproject']/>" style="color:grey; font-style: italic;">
        <img src="/nuxeo/icons/environment.png" alt="Environment"/>${endpoint['env:environment']} / <img src="/nuxeo/icons/application.png" alt="Application"/>${applicationTitle}</span>
        / <span title="SOA ID: ${endpoint['soan:name']}">${endpoint.title}</span>
        - <@displayPhaseIfOutsideContext endpoint['spnode:subproject']/>
        <#--  (v${endpoint.versionLabel}) -->
    </#macro>
    
    <#macro displayServiceShort service subprojectId visibility>
        <a href="<@urlToFicheSOA service subprojectId visibility/>"><@displayServiceTitle service subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi service/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>
    
    <#macro displayImplementationShort serviceimpl subprojectId visibility>
        <a href="<@urlToFicheSOA serviceimpl subprojectId visibility/>"><@displayImplementationTitle serviceimpl subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi serviceimpl/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>
    
    <#macro displayEndpointShort endpoint subprojectId visibility>
        <a href="<@urlToFicheSOA endpoint subprojectId visibility/>"><@displayEndpointTitle endpoint subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi endpoint/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>

    <#macro displayServicesShort services subprojectId visibility>
    <#if services?size = 0>
    Pas de services.
    <#else>
    <ul>
        <#list services as service>
            <li><@displayServiceShort service subprojectId visibility/></li>
        </#list>
    </ul>
    </#if>
    </#macro>

    <#macro displayImplementationsShort impls subprojectId visibility>
    <#if impls?size = 0>
    Pas d'implémentations de services.
    <#else>
    <ul>
        <#list impls as impl>
            <li><@displayImplementationShort impls subprojectId visibility/></li>
        </#list>
    </ul>
    </#if>
    </#macro>

    <#macro displayEndpointsShort endpoints subprojectId visibility>
    <#if endpoints?size = 0>
    Pas de services déployés.
    <#else>
    <ul>
        <#list endpoints as endpoint>
            <li><@displayEndpointShort endpoint subprojectId visibility/></li>
        </#list>
    </ul>
    </#if>
    </#macro>


    <#macro displayImplementationDetail actualImpl>
        <#-- @displayDoc actualImpl shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/>
        <br/ -->
        Ecrite en ${actualImpl['impl:language']} expose le service par ${actualImpl['impl:technology']}
        et est <@displayTested actualImpl/>
        <p/>
         
         <#assign deliverable = Root.getDocumentService().getSoaNodeParent(actualImpl, 'Deliverable')/>
         <b>Son délivrable</b> ${deliverable.title}<!-- : <br/ -->
         a <span title="<#list deliverable['del:dependencies'] as dependency>${dependency}, </#list>" style="color:grey;">${deliverable['del:dependencies']?size} dépendances (détail)</span>
         <!-- br/ -->
         <!-- TODO consumptions of : separate internal impls from external services -->
         <#assign delConsumptions = Root.getDocumentService().getDeliverableConsumptions(deliverable)/>
         <#-- ((consomme les classes d'implémentations :
         <#list delConsumptions as delConsumption>
            <@displayDocsShort Root.getDocumentService().getConsumedInterfaceImplementationsOfJavaConsumption(delConsumption, subprojectId, true, false)/>
              <#if delConsumption_index != delConsumptions?size - 1> ;</#if>
         </#list>))
         <br/ -->
           et consomme les services :
           <#list delConsumptions as delConsumption>
              <#assign delConsumedService = Root.getDocumentService().getConsumedJavaInterfaceService(delConsumption, subprojectId)/>
              <#if delConsumedService?has_content>
                 <br/>&nbsp;-&nbsp;&nbsp;<@displayServiceShort delConsumedService subprojectId visibility/>
              <#else>
                 <br/>&nbsp;-&nbsp;&nbsp;${delConsumption['wsdlinfo:wsdlPortTypeName']}
              </#if>
              (java ${delConsumption['javasc:consumedInterface']})
              <#if delConsumption_index != delConsumptions?size - 1> ;</#if>
           </#list>
    </#macro>
    
    <#macro displayProductionImplementationDetail productionImpl>
        Fournit par l'<b>Application</b> :
        <#assign productionDeliverable = Root.getDocumentService().getSoaNodeParent(productionImpl, 'Deliverable')/>
        <#if productionDeliverable?is_string>
           <#assign productionDeliverableApplication = "délivrable inconnu"/>
        <#else>
           <#assign productionDeliverableApplication = productionDeliverable['del:application']/>
        </#if>
        ${actorTitle} / ${componentTitle} (${productionDeliverableApplication})<#-- (TODO jars) -->
        nécessitant les services :
        <#if productionDeliverable?is_string>
           (délivrable inconnu)
        <#else>
         <!-- TODO list deliverables by application -->
         <#assign productionDelConsumptions = Root.getDocumentService().getDeliverableConsumptions(productionDeliverable)/>
         <#list productionDelConsumptions as servicecons>
            <#if servicecons['sc:isTest'] != 'true'>
               <#list Root.getDocumentService().getSoaNodeChildren(productionDeliverable, 'ServiceImplementation') as serviceimpl>
                  <#if servicecons['javasc:consumedInterface'] = serviceimpl['javasi:implementedInterface'] && serviceimpl['serviceimpl:ismock'] != 'true'>
                     <#assign foundLocalNonMockConsumedServiceImpl = serviceimpl/>
                     <!-- TODO + location -->
                  </#if>
               </#list>
               <#if !foundLocalNonMockConsumedServiceImpl?has_content>
                    <br/>&nbsp;-&nbsp;&nbsp;externe au délivrable ${servicecons['title']} (par l'interface ${servicecons['javasc:consumedInterface']})
                 <#else>
                    <br/>&nbsp;-&nbsp;&nbsp;(interne au délivrable ${servicecons['title']}, par l'implémentation ${foundLocalNonMockConsumedServiceImpl['title']} d'interface ${servicecons['javasc:consumedInterface']})
                 </#if>
              </#if>
         </#list>
        </#if>
        <p/>
    </#macro>
    

    <#macro displayTagShort tag subprojectId visibility>
         <#-- ${Root.path}/tag${tag['spnode:subproject']?xml}:${tag['soan:name']?xml}?subprojectId=${subprojectId}&visibility=${visibility} -->
         <a href="<@urlToFicheSOA tag subprojectId visibility/>">${tag['title']} (<#if tag.children?has_content>${tag['children']?size}<#else>0</#if>) - <@displayPhaseIfOutsideContext tag['spnode:subproject']/> (v${tag.versionLabel})</a>
         <a href="<@urlToLocalNuxeoDocumentsUi tag/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>

    <#macro displaySoaNodeShort soaNode subprojectId visibility>
        <#if soaNode.schemas?seq_contains('endpoint')>
           <@displayEndpointShort soaNode subprojectId visibility/>
        <#elseif soaNode.facets?seq_contains('ServiceImplementationData')>
           <@displayImplementationShort soaNode subprojectId visibility/>
        <#elseif soaNode.facets?seq_contains('InformationServiceData')>
           <@displayServiceShort soaNode subprojectId visibility/>
        <#elseif soaNode.facets?seq_contains('System')>
           <@displayTagShort soaNode subprojectId visibility/>
        <#else>
           <a href="<@urlToFicheSOA soaNode subprojectId visibility/>">${soaNode.title} - <@displayPhaseIfOutsideContext soaNode['spnode:subproject']/> (v${soaNode.versionLabel})</a>
           <a href="<@urlToLocalNuxeoDocumentsUi soaNode/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
        </#if>
    </#macro>
    
    
    <#macro displayActor actor actorKind parentObject>
            <span style="font-weight: bold;">${actorKind} :</span>
            <#if actor?has_content && !actor?is_string>
                <a href="<@urlToLocalNuxeoDocumentsUi actor/>">${actor.title}</a>
            <#else>
                <span style="color: red;">MANQUANT</span> (<a href="<@urlToLocalNuxeoDocumentsUi parentObject/>">résoudre</a>)
            </#if>
    </#macro>
    <#macro displayComponent component parentObject>
        <@displayActor component 'Component' parentObject/>
    </#macro>
    
    
    <#macro displaySoaNodeChildrenShort businessService childType>
           <span style="font-weight: bold;">${childType}s :</span>
           <#assign olas = Root.getDocumentService().getSoaNodeChildren(businessService, childType)/>
           <#if olas?size = 0>
               aucun
           <#else>
               <#list olas as ola>
                   <a href="<@urlToLocalNuxeoDocumentsUi ola/>">${ola.title}</a>
                   <#if ola_index != olas?size - 1>, </#if> 
               </#list>
           </#if>
    </#macro>
    
    <#macro displayChildrenShort businessService childType>
           <#assign businessServiceRef = new_f('org.nuxeo.ecm.core.api.IdRef', businessService.ref)/>
           <#assign specDocs = Session.getChildren(businessServiceRef, childType)/>
           <#if specDocs?size = 0>
               aucun
           <#else>
               <#list specDocs as specDoc>
                   <a href="<@urlToLocalNuxeoDocumentsUi ola/>">${specDoc.title}</a>,
                   <#if specDoc_index != specDoc?size - 1>, </#if> 
               </#list>
           </#if>
    </#macro>
    

    <#macro displayDocShort doc>
        <#if doc['spnode:subproject']?has_content>
         ${doc['title']} - <@displayPhaseIfOutsideContext doc['spnode:subproject']/> (v${doc.versionLabel})
        <#else>
         ${doc['title']} - ${doc['path']} (${doc.versionLabel})
        </#if>
        <a href="<@urlToLocalNuxeoDocumentsUi doc/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>
		
    <#macro displayDocsShort docs>
         <#list docs as doc>
             <@displayDocShort doc/>
             <#if doc_index != docs?size - 1> ;</#if>
         </#list>
    </#macro>
    
<#assign documentPropNames=["lifeCyclePolicy", "versionLabel", "facets", "children", "type", "sourceId", "id", "author", "title", "repository", "created", "name", "path", "schemas", "parent", "lifeCycleState", "allowedStateTransitions", "isLocked", "modified", "content", "ref", "versions", "isFolder", "sessionId", "session", "proxies"]/>
<#assign shortDocumentPropNames=["title", "type", "path", "author", "versionLabel"]/>
<#assign mostDocumentPropNames=["title", "type", "name", "path", "author", "created", "versionLabel", "isLocked", "modified",  "id"]/>


<#assign informationServicePropNames=["iserv:linkedBusinessService", "iserv:providerActor"]/>
<#assign architectureComponentPropNames=["acomp:componentId", "acomp:componentCategory",
    "acomp:linkedInformationService", "acomp:providerActor"]/>
<#assign platformPropNames=["platform:ide", "platform:language", "platform:build", "platform:serviceLanguage",
    "platform:deliverableNature", "platform:deliverableRepositoryUrl",
    "platform:serviceProtocol", "platform:transportProtocol", "platform:serviceRuntime", "platform:appServerRuntime",
    "platform:serviceSecurity", "platform:serviceSecurityManagerUrl",
    "platform:serviceMonitoring", "platform:serviceMonitoringManagerUrl"]/>

<#assign wsdlInfoPropNames=["wsdlinfo:transport", "wsdlinfo:wsdlVersion", "wsdlinfo:wsdlPortTypeName",
    "wsdlinfo:wsdl_service_port_binding_type_name", "wsdlinfo:wsdlServiceName",
    "wsdlinfo:wsdlFileName", "wsdlinfo:wsdlDigest"]/>
<#assign restInfoPropNames=["restinfo:path", "restinfo:accepts", "restinfo:contentType"]/>

<#assign deliverableTypePropNames=["deltype:nature", "deltype:repositoryUrl"]/>
<#assign deliverablePropNames=["soav:version", "del:application", "del:location", "del:dependencies"]/>
<#assign serviceImplementationPropNames=["impl:ide", "impl:language", "impl:build", "impl:technology",
    "impl:documentation", "impl:ismock", "impl:operations", "impl:tests",
    "impl:providedInformationService", "impl:component", "impl:platform"]/>
<#assign deployedDeliverablePropNames=["soan:name"]/>
<#assign endpointPropNames=["env:environment", "endp:url"]/>
<#-- assign endpointConsumptionPropNames=[]/ -->

<#assign intelligentSystemTreeRootPropNames=["istr:classifier", "istr:parameters"]/>
<#assign subprojectNodePropNames=["spnode:subproject"]/>
<#assign subprojectPropNames=["subproject:parentSubprojects"] + subprojectNodePropNames/>
<#assign soaNodePropNames=["soan:name", "soan:parentIds", "soan:isplaceholder"]/>
<#assign allPropHashNames={
    "iserv:operations": ["operationName", "operationParameters", "operationReturnParameters",
    "operationDocumentation", "operationInContentType", "operationOutContentType"],
    "impl:operations": ["operationName", "operationParameters", "operationReturnParameters",
    "operationDocumentation", "operationInContentType", "operationOutContentType"],
    "files:files": ["filename", "file"],
    "files:files/file": [ "filename", "data", "length", "mimeType", "encoding", "digest" ]
    }/><#-- "length", "mimeType", "encoding", "digest" -->
    
<#assign typePropNames = {
    "iserv": [informationServicePropNames]
    }/><!-- TODO ?? -->
    
    
<#--  Mime types (see MediaTypes.java) : -->
<#assign mimeTypePrettyNames = { "text/xml" : "XML", "application/xml" : "XML", "text/plain" : "plain text", "text/html" : "HTML",
    "application/soap+xml" : "SOAP", "application/json" : "JSON", "application/atom+xml" : "ATOM",
    "application/xhtml+xml" : "XHTML", "application/x-www-form-urlencoded" : "Form", "multipart/form-data" : "Multipart" }>

    <#macro displayPropsAll props propNames>
        <ul>
            <#list propNames as propName>
                <li>${propName} : <#if props[propName]?has_content><@displayProp props propName/><#else>$$</#if></li>
            </#list>
        </ul>
    </#macro>
    
    <#macro displayProps1 props propName>
        <#if propName = 'parent'>${props['title']} - ${props['path']}
            <#elseif propName = 'children'><#list props as child>${child['title']} - ${child['path']}</#list>
            <#elseif propName = 'proxies'><#list props as proxy>${proxy['title']} - ${proxy['path']}</#list>
            <#else><@displayProps props propName/>
        </#if>
    </#macro>

    <#macro displayProps props propName>
        <#-- if Root.isDevModeSet()><@displayProps ${propName}<br/></#if -->
        <#if !props?has_content>
            <#elseif props?is_string || props?is_number || props?is_boolean>${props}
            <#elseif props?is_date>${props?string("yyyy-MM-dd HH:mm:ss zzzz")}
            <#elseif props?is_hash><#if allPropHashNames?keys?seq_contains(propName)>%HH%<@displayPropsHash props propName allPropHashNames[propName]/><#else>PB ${propName} hash property not known <#-- list props?values as propValue><@displayProps propValue propName/> _ </#list --></#if>
            <#elseif props?is_sequence>%SS%<#list props as item><@displayProps1 item propName/> ; </#list>
            <#else>Error : type not supported
        </#if>
    </#macro>
	
    <#macro displayPropsHash props propName propHashNames>
        <#if Root.isDevModeSet()>@displayPropsHash ${props?size} ${propName} ${propHashNames?size}<br/></#if>
        <#list propHashNames as itemName><#if props[itemName]?has_content && !props[itemName]?is_method>${itemName} : <#assign subPropName = propName + '/' + itemName/><@displayProps1 props[itemName] subPropName/></#if> ; </#list>
        <#-- NEITHER list propHashNames as itemName><#if props?keys?seq_contains(itemName) && !props[itemName]?is_method>${itemName} : <@displayProps1 props[itemName] itemName/></#if> ; </#list -->
    </#macro>

    <#macro displayProp doc propName>
        <@displayProps1 doc[propName] propName/>
    </#macro>


    
    <#macro displayDoc doc propNames=documentPropNames>
        <@displayDocShort doc/>
        <p/>
        View in <a href="<@urlToLocalNuxeoDocumentsUi doc/>">edition UI</a>, <a href="<@urlToLocalNuxeoPreview doc/>">preview</a>, <a href="<@urlToLocalNuxeoPrint doc/>">print</a>
        <p/>
        <ul>
            <#list propNames as propName>
                <li>${propName} : <#if doc[propName]?has_content><@displayProp doc propName/><#else>$$</#if></li>
            </#list>
        </ul>
        <#-- list doc['facets'] as facet>
        <b>${facet}:</b>
        <ul>
            <#list facet?keys as propName>WARNING CAN'T WORK
                <li>${propName} : <#if doc[propName]?has_content><@displayProps1 doc[propName] propName/><#else>$$</#if></li>
            </#list>
        </ul>
	</#list -->
    </#macro>
    
    <#macro displayDocs docs propNames=documentPropNames>
         <#list docs as doc>
            <h4><@displayDocShort doc/></h4>
            <@displayDoc doc propNames/>
	</#list>
    </#macro>
    
    <#macro formatTextToHtml text><#if text?matches('.*<[a-zA-Z]+/?>.*', 's')>${text}<#else>${text?replace('\n+', '<p/>')?replace('\n', '<br/>')}</#if></#macro>
    <#-- ex. matches "g<b>j\nb" -->
    