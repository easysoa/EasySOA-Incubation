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
    <#assign httpproxy_app_instance_factory_url= 'http://localhost:8082' /><!-- TODO proxy war or FStudio ?? VM : owsi-vm-easysoa-axxx-registry -->
    <#assign jasmine_url= Root.getJasmineUrl() /><!-- VM : owsi-vm-easysoa-axxx-pivotal -->

    <#-- Macros -->

    <#include "/views/context/macros.ftl"><!-- to display Phase (displayContextBar) -->
    <#include "/views/EasySOA/urlMacros.ftl"><!-- for document urls -->

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
