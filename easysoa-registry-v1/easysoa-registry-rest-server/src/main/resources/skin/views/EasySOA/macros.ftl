    <#macro displayServiceShort service subprojectId visibility>
        <a href="${Root.path}/path/${service['soan:name']?xml}?subproject=${service['spnode:subproject']}&subprojectId=${subprojectId}&visibility=${visibility}"><@displayDocShort service/></a>
    </#macro>

    <#macro displayServicesShort services subprojectId visibility>
    <ul>
        <#list services as service>
            <li><@displayServiceShort service subprojectId visibility/></li>
	</#list>
    </ul>
    </#macro>

    <#macro displayTagShort tag subprojectId visibility>
         <a href="${Root.path}/tag/${tag['soan:name']?xml}?subproject=${tag['spnode:subproject']}&subprojectId=${subprojectId}&visibility=${visibility}">${tag['title']} (<#if tag.children?has_content>${tag['children']?size}<#else>0</#if>) - ${tag['path']}</a>
    </#macro>

    <#macro displayDocShort doc>
         ${doc['title']} - ${doc['path']}
    </#macro>
		
    <#macro displayDocsShort docs>
         <#list docs as doc><@displayDocShort doc/> ; </#list>
    </#macro>
		
    <#macro displayEndpointShort endpoint subprojectId visibility>
         <li><a href="${Root.path}/envIndicators/${endpoint.nuxeoID}?subprojectId=${subprojectId}&visibility=${visibility}">${endpoint.name}</a></li>
    </#macro>
		
    <#macro displayEndpointsShort endpoints subprojectId visibility>
         <ul>
         <#list endpoints as endpoint><@displayEndpointShort endpoint subprojectId visibility/></#list>
         </ul>
    </#macro>		
		
    <#macro displayIndicatorShort indicator>
        <tr>
            <td>${indicator.slaOrOlaName}</td>
            <td>${indicator.timestamp?datetime?string.long}</td>
            <td>
                <#if indicator.serviceLevelHealth=="gold">
                    <span class="label label-success">${indicator.serviceLevelHealth}</span>
                <#else>
                    <#if indicator.serviceLevelHealth=="silver">
                    <span class="label label-warning">${indicator.serviceLevelHealth}</span>
                    <#else> 
                    <span class="label label-important">{indicator.serviceLevelHealth}</span>
                    </#if>
                </#if>
            </td>
            <td>${indicator.serviceLevelViolation}</td>
        </tr>
    </#macro>
		
    <#macro displayIndicatorsShort indicators>
        <table>
            <tr>
                <td>Indicator name</td>
         	<td>Timestamp</td>
         	<td>Service level health</td>
         	<td>Service level violation</td>
            </tr>
            <#list indicators as indicator><@displayIndicatorShort indicator/></#list>
         </table>
    </#macro>		

    <#macro displayProjectsPhasesAndVersionsShort projectVersionsList>
        <ul>
        <#list projectVersionsList?keys as project>
            <li>${project}
                <@displayLiveShort projectVersionsList project/>
                <@displayVersionsShort projectVersionsList project/>
           </li>
        </#list>
        </ul>        
    </#macro>

    <#macro displayLiveShort projectVersionsList project>
        <ul>
        Currently in edition
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["live"] as live>
            <li> 
                ${live['dc:title']} - ${live.versionLabel} (<a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=deep">Deep</a>, <a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=strict">Strict</a>)
            </li>
        </#list>
        </ul>
    </#macro>

    <#macro displayVersionsShort projectVersionsList project>
        <ul>
        Older approved versions
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["versions"] as version>
            <li> 
                ${version['dc:title']} - ${version.versionLabel} (<a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=deep">Deep</a>, <a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=strict">Strict</a>)
            </li>
        </#list>
        </ul>
    </#macro>

<#assign documentPropNames=["lifeCyclePolicy", "versionLabel", "facets", "children", "type", "sourceId", "id", "author", "title", "repository", "created", "name", "path", "schemas", "parent", "lifeCycleState", "allowedStateTransitions", "isLocked", "modified", "content", "ref", "versions", "isFolder", "sessionId", "session", "proxies"]/>
<#assign shortDocumentPropNames=["title", "type", "path", "author", "versionLabel"]/>
<#assign mostDocumentPropNames=["title", "type", "name", "path", "author", "created", "versionLabel", "isLocked", "modified",  "id"]/>
<#assign deliverablePropNames=["soav:version", "del:nature", "del:application", "del:location", "del:dependencies"]/>
<#assign serviceImplementationPropNames=["impl:technology", "impl:operations", "impl:documentation", "impl:ismock", "impl:tests"]/>
<#assign serviceImplementationPropHashNames=["operationName", "operationParameters", "operationDocumentation"]/>
<#assign deployedDeliverablePropNames=["soan:name"]/>
<#assign endpointPropNames=["env:environment", "endp:url"]/>
<#assign intelligentSystemTreeRootPropNames=["istr:classifier", "istr:parameters"]/>
<#assign soaNodePropNames=["soan:name"]/>
<#assign allPropHashNames=serviceImplementationPropHashNames/>

		<#macro displayProps1 props propName>
         <#if propName = 'parent'>${props['title']} - ${props['path']}
			<#elseif propName = 'children'><#list props as child>${child['title']} - ${child['path']}</#list>
			<#elseif propName = 'proxies'><#list props as proxy>${proxy['title']} - ${proxy['path']}</#list>
			<#else><@displayProps props propName/></#if>
		</#macro>
		<#macro displayProps props propName>
         <#if !props?has_content>
			<#elseif props?is_string || props?is_number || props?is_boolean>${props}
			<#elseif props?is_date>${props?string("yyyy-MM-dd HH:mm:ss zzzz")}
			<#elseif props?is_hash><@displayPropsHash props/>
			<#elseif props?is_sequence>%%<#list props as item><@displayProps1 item propName/> ; </#list>
			<#else>Error : type not supported</#if>
		</#macro>
		<#macro displayPropsHash props propHashNames=allPropHashNames>
         <#list propHashNames as itemName><#if props[itemName]?has_content && !props[itemName]?is_method>${itemName} : <@displayProps1 props[itemName] itemName/></#if> ; </#list>
		</#macro>
		<#macro displayDoc doc propNames=documentPropNames>
		<ul>
		<#list propNames as propName>
			<li>${propName} : <#if doc[propName]?has_content><@displayProps1 doc[propName] propName/><#else>$$</#if></li>
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