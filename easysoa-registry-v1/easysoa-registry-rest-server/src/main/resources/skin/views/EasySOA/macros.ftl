<!--
Nuxeo Freemarker quick reference :
(see also )

Available context variables are :
(as seen in AbstractWebContext and at http://doc.nuxeo.com/pages/viewpage.action?pageId=11044493 )

* Context : the View extending AbstractWebContext, which provides :
   i18n (module's messages) & locale, logging, cookies, principal (user),
   properties (context variables shared among scripts),
   user session, running scripts, loginPath, headers, request, form
   and everything below (path /url...)
* Root : the controller. So you can put there (or in the ModuleRoot class it extends)
   code available here that requires the request.
* Module : NOT your own module Class. Provides class loading, adapters & resources, validators.
* Runtime : Framework.getRuntime()
* Engine : WebEngine
* basePath : /nuxeo/site
* skinPath : /nuxeo/site/skin/easysoa
* contextPath : /nuxeo
* This : the Web Object if any
* Document : its (adapted) DocumentModel if any
* Adapter : the adapter of the first WebEngine resource having one, starting from the controller
* Session : CoreSession
* & what's put by controller

-->
    
    <#-- URL Constants -->
    <#assign web_discovery_url= Root.getWebDiscoveryUrl() /><!-- VM : owsi-vm-easysoa-axxx-registry -->
    <#assign jasmine_url= Root.getJasmineUrl() /><!-- VM : owsi-vm-easysoa-axxx-pivotal -->
    
    <#-- Macros -->
		
    <#macro displayEndpointShort endpoint subprojectId visibility>
         <li>Déployé en ${endpoint['env:environment']} à <a href="${Root.path}/envIndicators/${endpoint.id}?subprojectId=${subprojectId}&visibility=${visibility}">${endpoint['endp:url']}</a></li>
    </#macro>
		
    <#macro displayEndpointsShort endpoints subprojectId visibility>
        <#if endpoints?has_content>
        <div class="accordion" id="accordion2">
            <#list endpoints?keys as service>
            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${service}">${service}</a>
                </div>
                <div id="${service}" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <ul>
                            <#list endpoints[service] as endpoint>
                                <@displayEndpointShort endpoint subprojectId visibility/>
                            </#list>
                        </ul>
                    </div>
                </div>            
            </div>
            </#list>
        </div>
        <#else>
            Aucun service déployé
        </#if>
        
        <#--<ul>
            <#list endpoints?keys as service>
            ${service.name}
                <#list endpoints[service] as endpoint>
                    <@displayEndpointShort endpoint subprojectId visibility/>
                </#list>
            </#list>
            </ul>-->

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
            <td>
                <#if indicator.isServiceLevelViolation()>
                    <#--${indicator.serviceLevelViolation}-->
                    <#-- TODO : how to display the lvl violation ?? round icon with red an d green color ?? -->
                    <span class="icon-circle" style="color: red"></span>
                <#else>
                <span class="icon-circle" style="color: green"></span>
                </#if>
            </td>
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
            <#if project != "Project Template">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <#if project == "MyProject" >
                        <#assign projectTag="Projet par défaut">
                    <#else>
                        <#assign projectTag="Projet">
                    </#if>
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#${project?replace(' ', '_')}">${projectTag} ${project}</a>
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
            </#if>
            </#list>
        </div>
    </#macro>
    
    <#macro displayLiveShort projectVersionsList project>
        Versions courantes
        <ul>
        <#assign liveAndVersions = projectVersionsList[project]/>
        <#list liveAndVersions["live"] as live>
            <li> 
                ${live['dc:title']} - ${live.versionLabel} (<a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=deep">Avec</a>, <a href="${Root.path}/../?subprojectId=${live['spnode:subproject']}&visibility=strict">Sans</a> les phases parentes)
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
                ${version['dc:title']} - ${version.versionLabel} (<a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=deep">Avec</a>, <a href="${Root.path}/../?subprojectId=${version['spnode:subproject']}&visibility=strict">Sans</a> les phases parentes)
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
                            <a class="btn btn-primary" href="/nuxeo/site/easysoa/context?subprojectId=${subprojectId}&visibility=${visibility}">Changer la perspective</a>
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
            <#--<#if indicators[indicatorsKey].count != -1 && indicators[indicatorsKey].containsCategory(category) == "true">-->
            <#if indicators[indicatorsKey].containsCategory(category) == "true">
            <tr>
                <#--<td>${indicatorsKey}</td>-->
                <td>${indicators[indicatorsKey].name}</td>
                <td><b>
                    <#if indicators[indicatorsKey].count != -1>
                        ${indicators[indicatorsKey].count}
                    <#else>
                        <#if indicators[indicatorsKey].date>
                            ${indicators[indicatorsKey].date?date}
                        </#if>
                    </#if>
                </b></td>
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
    
   
    <#macro displayReturnToIndexButtonBar>
        <@displayBackButtonBar "Retour à l'accueil"/>
    </#macro>
    
    <#macro displayBackButtonBar buttonLabel>
        <li class="span12">
            <div class="thumbnail">
                <img data-src="holder.js/300x200" alt=""/>
                <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">${buttonLabel}</a>
            </div>
        </li>
    </#macro>
    
    