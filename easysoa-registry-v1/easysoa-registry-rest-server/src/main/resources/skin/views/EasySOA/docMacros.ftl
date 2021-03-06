    
    <#include "/views/EasySOA/macros.ftl">
    <#include "/views/EasySOA/urlMacros.ftl">
    
    <#-- Document Macros -->
    
    <#macro displayPhase subprojectIdToDisplay>
        <#if subprojectIdToDisplay?length != 0>
           <#assign phaseShortId = subprojectIdToDisplay?replace('/default-domain/', '')/>
           <#assign phaseShortIdVersionIndex = phaseShortId?last_index_of('_v')/>
           ${phaseShortId?substring(0, phaseShortIdVersionIndex)?replace('/', ' / ')}
           (${Root.msg("version")} <#if phaseShortId?ends_with('_v')>${Root.msg("current")}<#else>${phaseShortId?substring(phaseShortIdVersionIndex + 2, phaseShortId?length)}</#if>)
        </#if><#-- else no perspective / context -->
    </#macro>
    
    <#macro displayPhaseIfOutsideContext subprojectIdToDisplay>
        <#assign parsedSubprojectIdToDisplay = Root.parseSubprojectId(subprojectIdToDisplay)/>
        <#if parsedSubprojectIdToDisplay?has_content && parsedSubprojectIdToDisplay?is_hash>
           <#-- NB. and not is_string which returns true !! (multi-persona object ?) -->
           <#if (!subprojectId?has_content || subprojectId?length == 0)>
               ${parsedSubprojectIdToDisplay.getProjectName()} /
               ${parsedSubprojectIdToDisplay.getSubprojectName()}
               <#assign version = parsedSubprojectIdToDisplay.getVersion()/>
               <#-- (${Root.msg("version")} <#if version?length == 0>${Root.msg("current")}<#else>${version}</#if>) -->
               <#if version?length == 0>${Root.msg("current")}<#else>${version}</#if>
           <#-- elseif subprojectIdToDisplay == subprojectId --><#-- choice : display Phase name even is the current one-->
           <#else>
               <#assign parsedSubprojectId = Root.parseSubprojectId(subprojectId)/>
               <#if parsedSubprojectIdToDisplay.getProjectName() != parsedSubprojectId.getProjectName()>
                   ${parsedSubprojectIdToDisplay.getProjectName()} /
               </#if> 
               ${parsedSubprojectIdToDisplay.getSubprojectName()}
               <#assign version = parsedSubprojectIdToDisplay.getVersion()/>
               <#-- (${Root.msg("version")} <#if version?length == 0>${Root.msg("current")}<#else>${version}</#if>) -->
               <#if version?length == 0>${Root.msg("current")}<#else>${version}</#if>
           </#if>
        </#if><#-- else no perspective / context TODO passing subprojectIdToDisplay param to displayPhaseIfOutsideContext doesn't work -->
    </#macro>
    
    <#macro displayProviderActorAndComponent service withLink subprojectId visibility>
        <@displayProviderActorOfService service withLink/> / <@displayComponentOfService service withLink/>
    </#macro>
    
    <#macro displayProviderActorOfService service withLink>
        <#if service['iserv:providerActor']?has_content>
            <#-- NB. to create a new object, see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html -->
            <#assign providerActor = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['iserv:providerActor']))/>
            <#if withLink>
                <@displaySoaNodeLink providerActor ""/>
            <#else>
                <@displaySoaNodeTitle providerActor ""/>
            </#if>
        <#else>
            <@displaySoaNodeTitle "" "Actor"/>
        </#if>
    </#macro>
    
    <#macro displayComponentOfService service withLink>
        <#if service['acomp:componentId']?has_content>
            <#assign component = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['acomp:componentId']))/>
            <#if withLink>
                <@displaySoaNodeLink component ""/>
            <#else>
                <@displaySoaNodeTitle component ""/>
            </#if>
        <#else>
            <@displaySoaNodeTitle "" "Component"/>
        </#if>
    </#macro>
    
    <#macro displayServiceTitleWithoutPhase service withLink subprojectId visibility>
        <span title="${Root.msg("Phase")} : <@displayPhase service['spnode:subproject']/>" style="color:grey; font-style: italic;">
        
        <#assign phaseName =  Root.parseSubprojectId(service['spnode:subproject']).getSubprojectName()/>
        <#if phaseName == 'Specifications'>
            <@displayProviderActorAndComponent service withLink subprojectId visibility/>
        <#elseif phaseName == 'Realisation'>
            <#-- "technical" service -->
            <#assign actualImpls = Root.getDocumentService().getActualImplementationsOfService(service, service['spnode:subproject'])/>
            <#if actualImpls?has_content && actualImpls?is_sequence>
                <#assign deliverable = Root.getDocumentService().getSoaNodeParent(actualImpls[0], 'Deliverable')/>
                <#if deliverable?has_content && deliverable?is_hash>
                    <#assign applicationName = deliverable['del:application']/>
                </#if>
            </#if>
            <#if !applicationName?has_content>
               <#assign applicationName = '(${Root.msg("unknown")})'/>
            </#if>
            <@displayApplication applicationName/>
        <#else><#-- if phaseName == 'Deploiement' -->
            <img src="/nuxeo/icons/server.png" alt="serveur"/>
            <!-- TODO -->
            (${Root.msg("unknown")})
        </#if>
        
        / </span>
        <#if withLink><a href="<@urlToFicheSOA service subprojectId visibility/>"></#if>
        <span title="SOA ID: ${service['soan:name']}"><@displaySoaNodeTitle service ""/></span>
        <#if withLink></a></#if>
        <#if service['soan:isplaceholder'] = 'true'> (${Root.msg("unknown")})<#elseif phaseName != 'Specifications'> (${Root.msg("technical")})</#if>
        </span>
    </#macro>
    
    <#macro displayServiceTitle service withLink subprojectId visibility>
        <@displayServiceTitleWithoutPhase service withLink subprojectId visibility/>
        <span style="color:grey; font-style: italic;"> - <@displayPhaseIfOutsideContext service['spnode:subproject']/>
        <#--  (v${service.versionLabel}) -->
        </span>
    </#macro>
    
    <#macro displayActorLink actor>
        <a href="<@urlToLocalNuxeoDocumentsUi actor/>"/><@displayActorTitle actor/></a>
    </#macro>
    
    <#macro displayActorTitle actor>
        <@displaySoaNodeTitle actor "Actor"/>
    </#macro>
    
    <#macro displayComponentLink component>
        <a href="<@urlToLocalNuxeoDocumentsUi component/>"/><@displayComponentTitle component/></a>
    </#macro>
    
    <#macro displayComponentTitle component>
        <@displaySoaNodeTitle component "Component"/>
    </#macro>
    
    <#macro displayDeliverableTitle deliverable subprojectId visibility>
        <#if deliverable['del:application']?has_content && deliverable?is_hash
                && deliverable['del:application']?length != 0>
            <@displayApplication deliverable['del:application']/>
        <#else>
            <@displayApplication '(${Root.msg("unknown")})'/>
        </#if>
        / <@displaySoaNodeTitle deliverable 'Deliverable'/>
    </#macro>
    
    <#macro displayApplication application>
         <img src="/nuxeo/icons/application.png" alt="${Root.msg("Application")}"/> ${application}
    </#macro>
    
    <#macro displayEnvironment environment>
         <img src="/nuxeo/icons/environment.png" alt="${Root.msg("Environment")}"/>${environment}
    </#macro>
    
    <#macro displaySoaNodeLink soaNode type>
       <#if !soaNode?is_hash>
         (${Root.msg("unknown")})
       <#else>
         <a href="<@urlToLocalNuxeoDocumentsUi soaNode/>"/><@displaySoaNodeTitle soaNode type/></a>
         <#-- a href="<@urlToLocalNuxeoDocumentsUi soaNode/>"/><img src="/nuxeo/icons/edition.png" alt="${Root.msg("edit")}"/></a --><#-- too much in pathes -->
       </#if>
    </#macro>
    
    <#macro displaySoaNodeTitle soaNode iconType>
       <#if !soaNode?is_hash>
          <img src="/nuxeo/icons/${iconType?lower_case}.png" alt="${Root.msg(iconType)}"/>
          (${Root.msg("unknown")})
       <#else>
           <@displaySoaNodeText soaNode iconType soaNode.title/>
       </#if>
    </#macro>
    
    <#macro displaySoaNodeText soaNode iconType text>
          <#if iconType?length = 0>
              <#assign actualIconType = soaNode.type?lower_case/>
          <#else>
              <#assign actualIconType = iconType?lower_case/>
          </#if>
          <img src="/nuxeo/icons/${actualIconType}.png" alt="${Root.msg(soaNode.type)}"/>
          <span title="<@displayPhaseIfOutsideContext soaNode['spnode:subproject']/>">
          ${text}
          </span>
    </#macro>
    
    <#macro displaySoaNodeName soaNode iconType>
       <#if iconType?length = 0>
           <#assign type = soaNode.type/>
       <#else>
           <#assign type = iconType/>
       </#if>
       <img src="/nuxeo/icons/${type?lower_case}.png" alt="${Root.msg(type)}"/>
       <#if !soaNode?is_hash>
          (${Root.msg("unknown")})
       <#else>
          <#if soaNode?has_content>
             ${soaNode['soan:name']}
          <#else>
             (${Root.msg("unknown")})
          </#if>
       </#if>
    </#macro>

    <#macro displayImplementationTitleWithoutPhase serviceimpl subprojectId visibility>
        <span title="${Root.msg("Phase")} : <@displayPhase serviceimpl['spnode:subproject']/>" style="color:grey; font-style: italic;">
        <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/>
        <#if deliverable?has_content && deliverable['del:application']?has_content && deliverable['del:application']?length != 0>
            <@displayApplication deliverable['del:application']/>
        <#else>
            <@displayProviderActorAndComponent serviceimpl true subprojectId visibility/>
        </#if>
        / <@displaySoaNodeName deliverable 'Deliverable'/>
        /
        </span>
        <a href="<@urlToFicheSOA serviceimpl subprojectId visibility/>">
        <span title="SOA ID: ${serviceimpl['soan:name']}"><@displaySoaNodeTitle serviceimpl "ServiceImplementation"/></span>
        </a>
        <#if serviceimpl['soan:isplaceholder'] = 'true'> (${Root.msg("unknown")})</#if>
        <#if serviceimpl['impl:ismock'] = 'true'> (test)</#if>
    </#macro>
    
    <#macro displayImplementationTitle serviceimpl subprojectId visibility>
        <@displayImplementationTitleWithoutPhase serviceimpl subprojectId visibility/>
        <span style="color:grey; font-style: italic;"> - <@displayPhaseIfOutsideContext serviceimpl['spnode:subproject']/>
        <#--  (v${serviceimpl.versionLabel}) -->
        </span>
    </#macro>

    <#macro displayEndpointTitleWithoutPhase endpoint subprojectId visibility>
        <span title="${Root.msg("Phase")} : <@displayPhase endpoint['spnode:subproject']/>" style="color:grey; font-style: italic;">
        <@displayEnvironment endpoint['env:environment']/> /
        <#-- @displayHost endpoint['endp:host']/> / --><#-- NO already in endp:url -->  
        
        <#assign serviceimpl = Root.getDocumentService().getServiceImplementationFromEndpoint(endpoint)/><#-- TODO if none ?? -->
        <#if serviceimpl?has_content>
            <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/><#-- TODO if none ?? -->
        </#if>
        <#if deliverable?has_content && deliverable['del:application']?has_content && deliverable['del:application']?length != 0>
            <@displayApplication deliverable['del:application']/><#-- / <@displaySoaNodeName deliverable 'Deliverable'/ -->
        <#else>
            <@displayProviderActorAndComponent endpoint true subprojectId visibility/>
        </#if>
        /
        </span>
        <a href="<@urlToFicheSOA endpoint subprojectId visibility/>">
        <span title="SOA ID: ${endpoint['soan:name']}">
        <#-- @displaySoaNodeTitle endpoint ""/ --><@displaySoaNodeText endpoint "" endpoint['endp:url']/>
        </span>
        </a>
        <#if endpoint['soan:isplaceholder'] = 'true'> (${Root.msg("unknown")})</#if>
        <#if endpoint['impl:ismock'] = 'true'> (test)</#if>
    </#macro>
    
    <#macro displayEndpointTitle endpoint subprojectId visibility>
        <@displayEndpointTitleWithoutPhase endpoint subprojectId visibility/>
        <span style="color:grey; font-style: italic;"> - <@displayPhaseIfOutsideContext endpoint['spnode:subproject']/>
        <#--  (v${endpoint.versionLabel}) -->
        </span>
    </#macro>
    
    <#macro displayServiceShort service subprojectId visibility>
        <a href="<@urlToFicheSOA service subprojectId visibility/>"><@displayServiceTitle service true subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi service/>"/><img src="/nuxeo/icons/edition.png" alt="${Root.msg("edit")}"/></a>
    </#macro>
    
    <#macro displayImplementationShort serviceimpl subprojectId visibility>
        <a href="<@urlToFicheSOA serviceimpl subprojectId visibility/>"><@displayImplementationTitle serviceimpl subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi serviceimpl/>"/><img src="/nuxeo/icons/edition.png" alt="${Root.msg("edit")}"/></a>
    </#macro>
    
    <#macro displayEndpointShort endpoint subprojectId visibility>
        <a href="<@urlToFicheSOA endpoint subprojectId visibility/>"><@displayEndpointTitle endpoint subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi endpoint/>"/><img src="/nuxeo/icons/edition.png" alt="${Root.msg("edit")}"/></a>
    </#macro>

    <#macro displayServicesShort services subprojectId visibility>
    <#if services?size = 0>
    ${Root.msg("noServices")}.
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
    ${Root.msg("NoServiceImplementations")}.
    <#else>
    <ul>
        <#list impls as impl>
            <li><@displayImplementationShort impl subprojectId visibility/></li>
        </#list>
    </ul>
    </#if>
    </#macro>

    <#macro displayEndpointsShort endpoints subprojectId visibility>
    <#if endpoints?size = 0>
    ${Root.msg("NoServiceDeployments")}.
    <#else>
    <ul>
        <#list endpoints as endpoint>
            <li><@displayEndpointShort endpoint subprojectId visibility/></li>
        </#list>
    </ul>
    </#if>
    </#macro>


    <#macro displayImplementationDetail actualImpl>
        <#if actualImpl['soan:isplaceholder'] = 'true'>
        ${Root.msg("is")} <b>${Root.msg("unknown")}</b>
        <#else>
        <#-- @displayDoc actualImpl shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/>
        <br/ -->
        ${Root.msg("writtenIn")} ${actualImpl['impl:language']} ${Root.msg("exposesTheServiceThrough")} ${actualImpl['impl:technology']}
        ${Root.msg("and")} ${Root.msg("is")} <@displayTested actualImpl/>
        <p/>
         
         <#assign deliverable = Root.getDocumentService().getSoaNodeParent(actualImpl, 'Deliverable')/>
         <b>${Root.msg("ItsDeliverable")}</b>
         <#if deliverable?has_content && deliverable['soan:isplaceholder'] != 'true'>
         ${deliverable.title}
         ${Root.msg("has")} <span title="<#list deliverable['del:dependencies'] as dependency>${dependency}, </#list>" style="color:grey;">${deliverable['del:dependencies']?size} ${Root.msg("dependencies")} (${Root.msg("details")})</span>
         <!-- br/ -->
         <!-- TODO consumptions of : separate internal impls from external services -->
         <#assign delConsumptions = Root.getDocumentService().getDeliverableConsumptions(deliverable)/>
         <#-- ((consomme les classes d'implémentations :
         <#list delConsumptions as delConsumption>
            <@displayDocsShort Root.getDocumentService().getConsumedInterfaceImplementationsOfJavaConsumption(delConsumption, subprojectId, true, false)/>
              <#if delConsumption_index != delConsumptions?size - 1> ;</#if>
         </#list>))
         <br/ -->
           ${Root.msg("andConsumesServicesBesidesTest")} :
           <#list delConsumptions as delConsumption>
              <#if delConsumption['sc:isTest'] != 'true'>
                 <#assign delPossibleConsumedServices = Root.getDocumentService().getPossibleConsumedJavaInterfaceServices(delConsumption, subprojectId)/>
                 <br/>&nbsp;-&nbsp;&nbsp;interface <span style="color:grey;">${delConsumption['wsdlinfo:wsdlPortTypeName']}</span>
                 (java <span style="color:grey;">${delConsumption['javasc:consumedInterface']}</span>)
                 <#if delPossibleConsumedServices?size != 0>
                    ${Root.msg("withCorrespondingPossibleJavaWebServices")}
                    <#list delPossibleConsumedServices as delPossibleConsumedService>
                       <@displayServiceShort delPossibleConsumedService subprojectId visibility/>
                       <#if service?has_content && delPossibleConsumedService['soan:name'] = service['soan:name']>
                       (${Root.msg("itself")})
                       </#if> 
                       <#if delPossibleConsumedService_index != delPossibleConsumedServices?size - 1> ;</#if>
                    </#list>
                    ${Root.msg("andOtherCorrespondingPossibleWebServices")} <i>(${Root.msg("upcoming")})</i>
                 </#if>
                 <#if delConsumption_index != delConsumptions?size - 1> ;</#if>
              </#if>
           </#list>
         <#else>
             ${Root.msg("is")} ${Root.msg("unknown")}.
         </#if>
        </#if>
    </#macro>
    
    <#macro displayProductionImplementationDetail productionImpl>
        <#if productionImpl['soan:isplaceholder'] = 'true'>
        ${Root.msg("is")} <b>${Root.msg("unknown")}</b> et
        </#if>
        ${Root.msg("providedByThe")} <b>${Root.msg("Application")}</b> :
        <@displayProviderActorOfService productionImpl ""/> / <@displayComponentOfService productionImpl ""/>
        <#assign productionDeliverable = Root.getDocumentService().getSoaNodeParent(productionImpl, 'Deliverable')/>
        <#if !productionDeliverable?has_content>
           (${Root.msg("deliverable")} ${Root.msg("unknown")})
        <#else>
           (${productionDeliverable['del:application']})<#-- (TODO jars) -->
        
        ${Root.msg("thatRequiresServices")} :
         <!-- TODO list deliverables by application -->
         <#assign productionDelConsumptions = Root.getDocumentService().getDeliverableConsumptions(productionDeliverable)/>
         <#list productionDelConsumptions as servicecons>
            <#if servicecons['sc:isTest'] != 'true'>
               <#list Root.getDocumentService().getSoaNodeChildren(productionDeliverable, 'ServiceImplementation') as serviceimpl>
                  <#if servicecons['javasc:consumedInterface'] = serviceimpl['javasi:implementedInterface']
                        && serviceimpl['serviceimpl:ismock'] != 'true'>
                     <#assign foundLocalNonMockConsumedServiceImpl = serviceimpl/>
                     <!-- TODO + location -->
                  </#if>
               </#list>
               <#if !foundLocalNonMockConsumedServiceImpl?has_content>
                    <br/>&nbsp;-&nbsp;&nbsp;${Root.msg("outsideDeliverable")} <span style="color:grey;">${servicecons['title']}</span>
                    (${Root.msg("troughInterface")} <span style="color:grey;">${servicecons['javasc:consumedInterface']}</span>)
                 <#else>
                    <br/>&nbsp;-&nbsp;&nbsp;(${Root.msg("withinDeliverable")} <span style="color:grey;">${servicecons['title']}</span>,
                    ${Root.msg("throughImplementation")} ${foundLocalNonMockConsumedServiceImpl['title']} ${Root.msg("havingInterface")} <span style="color:grey;">${servicecons['javasc:consumedInterface']}</span>)
                 </#if>
              </#if>
         </#list>
        </#if>
        <p/>
    </#macro>
    
    <#macro displayEndpointConsumptionLink ec>
        <a href="<@urlToLocalNuxeoDocumentsUi ec/>"/><@displayEndpointConsumptionTitle ec/></a>
    </#macro>
    
    <#macro displayEndpointConsumptionTitle ec>
        <@displayHost ec['ec:consumerHost']/> (${ec['ec:consumerIp']}) / <@displaySoaNodeTitle ec 'ServiceConsumption'/>
    </#macro>
    
    <#macro displayEndpointConsumptionLink ec>
        <a href="<@urlToLocalNuxeoDocumentsUi ec/>"/><@displayEndpointConsumptionTitle ec/></a>
    </#macro>
    
    <#macro displayHost host>
         <img src="/nuxeo/icons/server.png" alt="${Root.msg("Host")}"/>${host}
    </#macro>
    

    <#macro displayTagShort tag subprojectId visibility>
        <#-- ${Root.path}/tag${tag['spnode:subproject']?xml}:${tag['soan:name']?xml}?subprojectId=${subprojectId}&visibility=${visibility} -->
        <@displaySoaNodeLink tag ""/>
        <#--  (v${soaNode.versionLabel}) --></a>
         (<#if tag.children?has_content>${tag['children']?size}<#else>0</#if>)
         - <@displayPhaseIfOutsideContext tag['spnode:subproject']/>
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
        <#elseif soaNode.schemas?seq_contains('requirementsdocument')>
           <@displaySoaNodeLink soaNode "RequirementsDocument"/> - <@displayPhaseIfOutsideContext soaNode['spnode:subproject']/>
        <#else>
           <@displaySoaNodeLink soaNode ""/> - <@displayPhaseIfOutsideContext soaNode['spnode:subproject']/>
           <#--  (v${soaNode.versionLabel}) --></a>
        </#if>
    </#macro>
    
    
    <#macro displaySoaNodeProp soaNodePropValue propTitle parentObject>
            <span style="font-weight: bold;">${propTitle} :</span>
            <#if soaNodePropValue?has_content && soaNodePropValue?is_hash>
                <@displaySoaNodeLink soaNodePropValue ""/>
            <#else>
                <span style="color: red;">${Root.msg("MISSING")}</span> (<a href="<@urlToLocalNuxeoDocumentsUi parentObject/>">${Root.msg("resolve")}</a>)
            </#if>
    </#macro>
    
    
    <#macro displaySoaNodeChildrenShort soaNode childType iconType>
           <span style="font-weight: bold;">${Root.msg(childType)}s :</span>
           <#assign soaNodeChildren = Root.getDocumentService().getSoaNodeChildren(soaNode, childType)/>
           <#if soaNodeChildren?size = 0>
               ${Root.msg("none")}
           <#else>
               <#if iconType?length == 0>
                   <#assign iconType = childType/>
               </#if>
               <#list soaNodeChildren as soaNodeChild>
                   <@displaySoaNodeLink soaNodeChild iconType/>
                   <#if soaNodeChild_index != soaNodeChildren?size - 1>, </#if> 
               </#list>
           </#if>
    </#macro>
    
    <#macro displayChildrenShort businessService childType>
           <#assign businessServiceRef = new_f('org.nuxeo.ecm.core.api.IdRef', businessService.ref)/>
           <#assign specDocs = Session.getChildren(businessServiceRef, childType)/>
           <#if specDocs?size = 0>
               ${Root.msg("none")}
           <#else>
               <#list specDocs as specDoc>
                   <a href="<@urlToLocalNuxeoDocumentsUi ola/>">${specDoc.title}</a>
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
        <a href="<@urlToLocalNuxeoDocumentsUi doc/>"/><img src="/nuxeo/icons/edition.png" alt="${Root.msg("edit")}"/></a>
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

<#assign resourcePropNames=["rdi:url", "rdi:downloadableUrl", "rdi:timestamp", "rdi:probeType", "rdi:probeInstanceId"]/>
<#assign wsdlInfoPropNames=["wsdlinfo:transport", "wsdlinfo:wsdlVersion", "wsdlinfo:wsdlPortTypeName",
    "wsdlinfo:wsdlServiceName", "wsdlinfo:wsdlFileName", "wsdlinfo:wsdlDigest"]/>
    <#-- TODO LATER "wsdlinfo:wsdl_service_port_binding_type_name", -->
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
    
    
    <#macro displayDocumentation textOrHtml>
        <div style="margin-top:4px;margin-bottom:12px;">
           <b>${Root.msg("Documentation")} :</b>
           <#if textOrHtml?has_content && textOrHtml?length != 0>
              <table style="width: 600px;">
                  <tr><td><@formatTextToHtml textOrHtml/></td></tr>
              </table>
           <#else>
              (${Root.msg("none")})
           </#if>
        </div>
    </#macro>
    
    <#macro formatTextToHtml text><#if text?matches('.*<[a-zA-Z]+/?>.*', 's')>${text}<#else>${text?replace('\n+', '<p/>')?replace('\n', '<br/>')}</#if></#macro>
    <#-- ex. matches "g<b>j\nb" -->
    