    
    <#include "/views/EasySOA/macros.ftl">
    
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
            (version <#if version?length == 0>en cours<#else>${version}</#if>)
        </#if>
    </#macro>

    <#macro displayServiceTitle service subprojectId visibility>
        <#-- NB.  to create a new object, see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html -->
        <#assign providerActorTitle = ""/>
        <#if service['iserv:providerActor']?has_content>
            <#assign providerActor = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['iserv:providerActor']))/>
            <#assign providerActorTitle = providerActor.title + ' / '/>
        </#if>
        <#assign componentTitle = ""/>
        <#if service['acomp:componentId']?has_content>
            <#assign component = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['acomp:componentId']))/>
            <#assign componentTitle = component.title + ' / '/>
        </#if>
        <span title="Phase : <@displayPhase service['spnode:subproject']/>" style="color:grey; font-style: italic;">
        ${providerActorTitle} ${componentTitle}</span> <span title="SOA ID: ${service['soan:name']}">${service.title}
        </span> - <@displayPhaseIfOutsideContext service['spnode:subproject']/> (v${service.versionLabel})
    </#macro>

    <#macro displayImplementationTitle serviceimpl subprojectId visibility>
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
        <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/>
        <#if deliverable?has_content>
            <#assign deliverableTitle = deliverable['del:application'] + ' / ' + deliverable['soan:name']/>
        <#else>
            <#assign deliverableTitle = '(no deliverable)'/>
        </#if>
        <span title="Phase : <@displayPhase serviceimpl['spnode:subproject']/>" style="color:grey; font-style: italic;">
        ${providerActorTitle} / ${componentTitle} / ${deliverableTitle} /</span>
        <span title="SOA ID: ${serviceimpl['soan:name']}">${serviceimpl.title}</span>
        - <@displayPhaseIfOutsideContext serviceimpl['spnode:subproject']/> (v${serviceimpl.versionLabel})
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
        ${endpoint['env:environment']} / ${applicationTitle} /</span>
        <span title="SOA ID: ${endpoint['soan:name']}">${endpoint.title}</span>
        - <@displayPhaseIfOutsideContext endpoint['spnode:subproject']/> (v${endpoint.versionLabel})
    </#macro>
    
    <#macro displayServiceShort service subprojectId visibility>
        <a href="${Root.path}/path${service['spnode:subproject']?xml}::${service['soan:name']?xml}?subproject=${service['spnode:subproject']}&subprojectId=${subprojectId}&visibility=${visibility}"><@displayServiceTitle service subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi service/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>
    
    <#macro displayImplementationShort serviceimpl subprojectId visibility>
        <a href="${Root.path}/path${serviceimpl['spnode:subproject']?xml}:${serviceimpl.type}:${serviceimpl['soan:name']?xml}?subproject=${serviceimpl['spnode:subproject']}&subprojectId=${subprojectId}&visibility=${visibility}"><@displayImplementationTitle serviceimpl subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi serviceimpl/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>
    
    <#macro displayEndpointShort endpoint subprojectId visibility>
        <a href="${Root.path}/path${endpoint['spnode:subproject']?xml}:${endpoint.type}:${endpoint['soan:name']?xml}?subproject=${endpoint['spnode:subproject']}&subprojectId=${subprojectId}&visibility=${visibility}"><@displayEndpointTitle endpoint subprojectId visibility/></a>
        <a href="<@urlToLocalNuxeoDocumentsUi endpoint/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
    </#macro>

    <#macro displayServicesShort services subprojectId visibility>
    <#if services?size = 0>
    No services.
    <#else>
    <ul>
        <#list services as service>
            <li><@displayServiceShort service subprojectId visibility/></li>
	</#list>
    </ul>
    </#if>
    </#macro>

    <#macro displayTagShort tag subprojectId visibility>
         <a href="${Root.path}/tag${tag['spnode:subproject']?xml}:${tag['soan:name']?xml}?subprojectId=${subprojectId}&visibility=${visibility}">${tag['title']} (<#if tag.children?has_content>${tag['children']?size}<#else>0</#if>) - <@displayPhaseIfOutsideContext tag['spnode:subproject']/> (v${tag.versionLabel})</a>
         <a href="<@urlToLocalNuxeoDocumentsUi tag/>"/><img src="/nuxeo/icons/edition.png" alt="edition"/></a>
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