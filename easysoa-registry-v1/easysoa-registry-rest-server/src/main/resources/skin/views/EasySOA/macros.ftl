    <#macro displayPhase subprojectId>
        <#assign phaseShortId = subprojectId?replace('/default-domain/', '')/>
        <#assign phaseShortIdVersionIndex = phaseShortId?last_index_of('_v')/>
        ${phaseShortId?substring(0, phaseShortIdVersionIndex)?replace('/', ' / ')}
        (version <#if phaseShortId?ends_with('_v')>en cours<#else>${phaseShortId?substring(phaseShortIdVersionIndex + 2, phaseShortId?length)}</#if>)
    </#macro>

    <#macro displayServiceTitle service subprojectId visibility>
        <#assign providerActor = service['iserv:providerActor']/>
        <#assign component = service['acomp:componentId']/>
        <#-- NB.  to create a new object, see http://freemarker.624813.n4.nabble.com/best-practice-to-create-a-java-object-instance-td626021.html -->
        <#assign providerActorTitle = ""/>
        <#if providerActor?has_content>
            <#assign providerActor = service['session'].getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', providerActor))/>
            <#assign providerActorTitle = providerActor.title + ' / '/>
        </#if>
        <#assign componentTitle = ""/>
        <#if component?has_content>
            <#assign component = service['session'].getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['acomp:componentId']))/>
            <#assign componentTitle = component.title + ' / '/>
        </#if>
        <span title="Phase : <@displayPhase service['spnode:subproject']/>" style="color:grey; font-style: italic;">${providerActorTitle} ${componentTitle}</span> <span title="SOA ID: ${service['soan:name']}">${service.title}</span> - <@displayPhase service['spnode:subproject']/> (((${service.versionLabel})))
    </#macro>
    
    <#macro displayServiceShort service subprojectId visibility>
        <a href="${Root.path}/path${service['spnode:subproject']?xml}:${service['soan:name']?xml}?subproject=${service['spnode:subproject']}&subprojectId=${subprojectId}&visibility=${visibility}"><@displayServiceTitle service subprojectId visibility/></a>
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
         <a href="${Root.path}/tag${tag['spnode:subproject']?xml}:${tag['soan:name']?xml}?subprojectId=${subprojectId}&visibility=${visibility}">${tag['title']} (<#if tag.children?has_content>${tag['children']?size}<#else>0</#if>) - <@displayPhase tag['spnode:subproject']/> (((${tag.versionLabel})))</a>
    </#macro>

    <#macro displayDocShort doc>
        <#if doc['spnode:subproject']?has_content>
         ${doc['title']} - <@displayPhase doc['spnode:subproject']/> (((${doc.versionLabel})))
        <#else>
         ${doc['title']} - ${doc['path']} (${doc.versionLabel})
        </#if>
    </#macro>
		
    <#macro displayDocsShort docs>
         <#list docs as doc><@displayDocShort doc/> ; </#list>
    </#macro>
		
    <#macro displayEndpointShort endpoint subprojectId visibility>
         <li>Qualité de service : <a href="${Root.path}/envIndicators/${endpoint.nuxeoID}?subprojectId=${subprojectId}&visibility=${visibility}">${endpoint.name}</a></li>
    </#macro>
		
    <#macro displayEndpointsShort endpoints subprojectId visibility>
        <#if endpoints?has_content>
            <ul>
            <#list endpoints as endpoint><@displayEndpointShort endpoint subprojectId visibility/></#list>
            </ul>
        <#else>
            Aucun service déployé
        </#if>
    </#macro>		
		
    <#macro displayIndicatorShort indicator>
        <tr>
            <td>
                <a href="<@urlToLocalNuxeoDocumentsUiShort indicator.path/>?tabIds=%3ATAB_EDIT" target="_blank"><i class="icon-edit"></i></a>
                <a href="#" data-toggle="tooltip" data-placement="top" title="${indicator.description}">${indicator.slaOrOlaName}</a>
            </td>
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
        <table class="table table-bordered">
            <tr>
                <!--
                <td>Indicator name</td>
         	<td>Timestamp</td>
         	<td>Service level health</td>
         	<td>Service level violation</td>
                -->
                <td>Indicateur</td>
         	<td>Horodatage</td>
         	<td>Niveau de santé du service</td>
         	<td>Niveau de violation du service</td>
            </tr>
            <#list indicators as indicator><@displayIndicatorShort indicator/></#list>
         </table>
    </#macro>		

    <#macro displayProjectsPhasesAndVersionsShort projectVersionsList>
        <div class="accordion" id="accordion2">
            <#list projectVersionsList?keys as project>
            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${project?replace(' ', '_')}">${project}</a>
                </div>
                <div id="${project?replace(' ', '_')}" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <ul>
                            <li><@displayLiveShort projectVersionsList project/></li>
                            <li><@displayVersionsShort projectVersionsList project/></li>
                        </ul>
                    </div>
                </div>
            </div>
            </#list>
        </div>
    </#macro>
    
    <#macro displayLiveShort projectVersionsList project>
        Versions courantes
        <ul>
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["live"] as live>
            <li> 
                ${live['dc:title']} - ${live.versionLabel} (<a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=deep">Avec</a>, <a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=strict">Sans</a>)
            </li>
        </#list>
        </ul>
    </#macro>

    <#macro displayVersionsShort projectVersionsList project>
        Anciennes versions
        <ul>        
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["versions"] as version>
            <li> 
                ${version['dc:title']} - ${version.versionLabel} (<a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=deep">Avec</a>, <a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=strict">Sans</a>)
            </li>
        </#list>
        </ul>
    </#macro>

    <#macro displayCurrentVersion subprojectId contextInfo visibility>
        <#if subprojectId>
            ${contextInfo.project} / ${contextInfo.phase}&nbsp;
            <#if contextInfo.version != "">
            (version ${contextInfo.version})
            <#else>
            (version courante)
            </#if>
            <span class="label">Visibilité : <#if visibility == "deep">Avec phases parentes<#else>Sans phases parentes</#if></span>
        <#else>
            Perspective globale
        </#if>
    </#macro>

    <#-- Display the context bar as a Bootstrap full width thumbnail -->
    <#macro displayContextBar subprojectId contextInfo visibility button>
        <li class="span12">
            <div class="thumbnail">
                <img data-src="holder.js/300x200" alt="">
                <table class="table-hidden">
                    <tr>
                        <td class="td-hidden"><strong>Perspective :</strong>&nbsp<@displayCurrentVersion subprojectId contextInfo visibility/>&nbsp;&nbsp;</td>
                        <td class="td-hidden" style="text-align:right">
                            <#if button == "true">
                            <a class="btn btn-primary" href="/nuxeo/site/easysoa/context/">Changer la perspective</a>
                            </#if>
                        </td>
                    </tr>
                </table>
            </div>
        </li>
    </#macro>
    
    <#macro displayIndicatorsInTable indicators category>
        <table class="table table-bordered">
        <#list indicators?keys as indicatorsKey>
            <#if indicators[indicatorsKey].count != -1 && indicators[indicatorsKey].containsCategory(category) == "true">
            <tr>
                <td>Nombre de ${indicatorsKey}</td>
                <td><b>${indicators[indicatorsKey].count}</b></td>
                <!-- TODO : Display the percentage ??? -->
                <td>    <#if indicators[indicatorsKey].percentage != -1>
                            Pourcentage : <b>${indicators[indicatorsKey].percentage}%</b>
                    <#else>
                            Pourcentage : <b>N.A.</b>
                    </#if>
                </td>
            </tr>
            </#if>
        </#list>    
        </table>
    </#macro>
    
    <#macro displayUserInfo user>
        <#if user == "">
            <img src="/nuxeo/site/easysoa/skin/img/user.png" /> Non connecté (<a href="/login.html">Connexion</a>)
        <#else>
            <img src="/nuxeo/site/easysoa/skin/img/user.png" /> Bonjour <span id="username">${user}</span> (<a href="/nuxeo/logout">Déconnexion</a>)
        </#if>
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
        <#-- DEBUG displayProps ${propName}<br/ -->
        <#if !props?has_content>
            <#elseif props?is_string || props?is_number || props?is_boolean>${props}
            <#elseif props?is_date>${props?string("yyyy-MM-dd HH:mm:ss zzzz")}
            <#elseif props?is_hash><#if allPropHashNames?keys?seq_contains(propName)>%HH%<@displayPropsHash props propName allPropHashNames[propName]/><#else>PB ${propName} hash property not known <#-- list props?values as propValue><@displayProps propValue propName/> _ </#list --></#if>
            <#elseif props?is_sequence>%SS%<#list props as item><@displayProps1 item propName/> ; </#list>
            <#else>Error : type not supported
        </#if>
    </#macro>
	
    <#macro displayPropsHash props propName propHashNames>
        DEBUG displayPropsHash ${props?size} ${propName} ${propHashNames?size}<br/>
        <#list propHashNames as itemName><#if props[itemName]?has_content && !props[itemName]?is_method>${itemName} : <#assign subPropName = propName + '/' + itemName/><@displayProps1 props[itemName] subPropName/></#if> ; </#list>
        <#-- NEITHER list propHashNames as itemName><#if props?keys?seq_contains(itemName) && !props[itemName]?is_method>${itemName} : <@displayProps1 props[itemName] itemName/></#if> ; </#list -->
    </#macro>

    <#macro displayProp doc propName>
        <@displayProps1 doc[propName] propName/>
    </#macro>


    <#macro escapeUrl path>${path?replace('/', '____')?url?replace('____', '/')}</#macro>
    
    <#--
        see http://doc.nuxeo.com/display/NXDOC/Navigation+URLs , http://answers.nuxeo.com/questions/3203/how-to-buildrequest-a-previewdownload-url-for-a-document
    -->
    <#macro urlToLocalNuxeoDocumentsUi doc>
        <@urlToLocalNuxeoDocumentsUiShort doc['path']/>
    </#macro>
    <#macro urlToLocalNuxeoDocumentsUiShort doc>/nuxeo/nxpath/default<@escapeUrl doc/>@view_documents</#macro>
    <#macro urlToLocalNuxeoPreview doc>/nuxeo/nxdoc/default/${doc.id}/preview_popup</#macro>
    <#macro urlToLocalNuxeoPrint doc>/nuxeo/site/admin/repository<@escapeUrl doc['path']/>/@views/print</#macro>
    
    
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