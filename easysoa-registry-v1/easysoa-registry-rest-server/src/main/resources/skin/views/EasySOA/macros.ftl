<!--

### Nuxeo WebEngine Freemarker templates quick reference :

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


### Nuxeo Freemarker good practices :

* Freemarker doesn't support null. So use instead something different from your normal variable
type, like "" (empty string) if it is an object, and test which one it is using builtins like
?string and ?is_hash (for objects). Don't use ?is_string on Java objects, because the may return
?is_string ==  true ! Also, calls to Java methods returning null actually return an empty string.
* macro don't support optional parameters, so pass instead something you can test not to be your
normal variable type, like an empty string if it should be an object (see above).

-->

    <#-- Macros -->

    <#include "/views/context/macros.ftl"><!-- to display Phase (displayContextBar) -->
    <#include "/views/EasySOA/urlMacros.ftl"><!-- for document urls -->

    <#macro displayIndicatorsInTable indicators category>
        <table class="table table-bordered">
        <#list indicators?keys as indicatorsKey>
            <#-- display indicator if in the right category, or if no (empty) category : -->
            <#if category?length = 0 || indicators[indicatorsKey].containsCategory(category) == "true">
                <@displayIndicatorInTable indicators[indicatorsKey]/>
            </#if>
        </#list>
        </table>
    </#macro>

    <#macro displayIndicatorInTable indicator>
            <tr>
                <td>${Context.getMessage(indicator.name, indicator.args)}</td>
                <td><b>
                    <@displayIndicatorValue indicator/>
                </b></td>
                <#-- Don't display percentage if not available -->
                <td>
                    <@displayIndicatorPourcentage indicator/>
                </td>
            </tr>
    </#macro>

    <#macro displayIndicatorInTable2 indicator>
            <tr>
                <td width="80%" title="${indicator.description}">${Context.getMessage(indicator.name, indicator.args)}</td>
                <td width="10%"><b>
                    <@displayIndicatorValue indicator/>
                </b></td>
                <#-- Don't display percentage if not available -->
                <td width="10%">
                    <@displayIndicatorPourcentage indicator/>
                </td>
            </tr>
    </#macro>

    <#macro displayIndicatorInTableWithDescription indicator>
            <tr>
                <td width="30%" title="${indicator.description}">${Context.getMessage(indicator.name, indicator.args)}</td>
                <td width="10%" title="${indicator.description}"><b>
                    <@displayIndicatorValue indicator/>
                </b></td>
                <#-- Don't display percentage if not available -->
                <td width="10%" title="${indicator.description}">
                    <@displayIndicatorPourcentage indicator/>
                </td>
                <td width="50%" title="${indicator.description}" style="color:grey; font-size: 85%">${indicator.description} %</td>
            </tr>
    </#macro>

    <#macro displayIndicatorInTableShort indicator>
        <tr>
            <td width="80%" title="${indicator.description}">${Context.getMessage(indicator.name, indicator.args)} :</td>
            <td width="20%"><@displayIndicatorValue indicator/></td>
        </tr>
    </#macro>

    <#macro displayIndicatorValue indicator>
                    <#if indicator.count != -1>
                        ${indicator.count}
                    <#elseif indicator.date>
                        ${indicator.date?date}
                    </#if>
    </#macro>

    <#macro displayIndicatorPourcentage indicator>
                    <#if indicator.percentage != -1>
                            Pourcentage : <b>${indicator.percentage}%</b>
                    <#else>
                            Pourcentage : <b>N.A.</b>
                    </#if>
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