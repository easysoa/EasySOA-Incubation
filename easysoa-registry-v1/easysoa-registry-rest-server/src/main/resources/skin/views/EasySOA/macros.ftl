<#--

### Nuxeo WebEngine Freemarker templates quick reference :

Available context variables are :
(as seen in AbstractWebContext and at http://doc.nuxeo.com/pages/viewpage.action?pageId=11044493 )

* Context : the View extending AbstractWebContext, which provides :
   i18n (module's messages) & locale (ex. ${Context.getMessage('my.msg', [date])} with my.msg=Date is {0}
   and no space in key. To test it, add a ex. language=fr request parameter (EasySOA-specific)),
   logging, cookies, principal (user),
   properties (context variables shared among scripts),
   user session, running scripts, loginPath, headers, request, form
   and everything below (path /url...)
* Root : the controller. So you can put there (or in the ModuleRoot class it extends)
   code available here that requires the request.
   EasySOA's (EasysoaModuleRoot) provides :
   msg() (shortcut for Context.getMessage()), getCurrentUser() (built on Context.getPrincipal()),
   getServerUrl(), getServerHost(), isDevModeSet() (using Framework),
   and EasySOA-specific ones :
   getProperties() (DocumentService's), getXxxUrl() to various features,
   getXxxService() for various services, buildSubprojectCriteria(), parseSubprojectId()
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

    <#include "/views/context/macros.ftl"><#-- to display Phase (displayContextBar) -->
    <#include "/views/EasySOA/urlMacros.ftl"><#-- for document urls -->

    <#macro includeResources>
        <meta charset="utf-8" />
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><#-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->

        <#-- Prettify to display code in examples -->
        <link href="/nuxeo/site/easysoa/skin/css/prettify.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/prettify/prettify._js"></script>

        <#-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen"/>
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>

        <#-- EasySOA base style -->
    	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />

        <#-- font-awesome style for icons -->
        <link rel="stylesheet" href="/nuxeo/site/easysoa/skin/css/font-awesome.css"/>

    	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" />
    </#macro>
    
    <#macro headerContentsDefault>
       <a href="/nuxeo/site/easysoa"><div id="logoLink">&nbsp;</div></a>
       <div id="headerLinksBar">
           <a onclick="window.open(this.href); return false;" href="http://www.easysoa.org/">Website</a>
           -
           <a onclick="window.open(this.href); return false;" href="https://github.com/easysoa/EasySOA/wiki">Documentation</a>
           -
           <a onclick="window.open(this.href); return false;" href="http://www.openwide.fr">Support</a>
       </div>
       <div id="headerPartnersBar">
           <a onclick="window.open(this.href); return false;" href="http://www.openwide.fr"><img style="height:22px; width:auto;" src="/nuxeo/site/easysoa/skin/img/logos/logo-openwide.png" alt="Open Wide"/></a>
           &nbsp;
           <a onclick="window.open(this.href); return false;" href="http://www.nuxeo.com/"><img style="height:14px; width:auto;" src="/nuxeo/site/easysoa/skin/img/logos/logo-nuxeo.png" alt="Nuxeo"/></a>
           <a onclick="window.open(this.href); return false;" href="http://www.talend.com"><img style="height:14px; width:auto;" src="/nuxeo/site/easysoa/skin/img/logos/logo-talend.png" alt="Talend"/></a>
           <a onclick="window.open(this.href); return false;" href="http://www.bull.com"><img style="height:12px; width:auto;" src="/nuxeo/site/easysoa/skin/img/logos/logo-bull.png" alt="Bull"/></a>
           <a onclick="window.open(this.href); return false;" href="http://www.easifab.net"><img style="height:14px; width:auto;" src="/nuxeo/site/easysoa/skin/img/logos/logo-easifab.png" alt="EasiFab"/></a>
           <a onclick="window.open(this.href); return false;" href="http://www.inria.fr"><img style="height:14px; width:auto;" src="/nuxeo/site/easysoa/skin/img/logos/logo-inria.png" alt="Inria"/></a>
       </div>
       <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
       <div id="headerContextBar">
           <#assign visibility=visibility!""><#-- Required to set a default value when the query variables are not present -->
           <#assign subprojectId=subprojectId!"">
           <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
       </div>
    </#macro>


    <#macro displayIndicatorsInTable indicators category>
        <#-- TODO : Add table headers ? -->
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
                <td>${Root.msg(indicator.name, indicator.args)}</td>
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
                <td width="80%" title="${indicator.description}">${Root.msg(indicator.name, indicator.args)}</td>
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
                <td width="30%" title="${indicator.description}">${Root.msg(indicator.name, indicator.args)}</td>
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
            <td width="80%" title="${indicator.description}">${Root.msg(indicator.name, indicator.args)} :</td>
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
                            ${Root.msg("percentage")} : <b>${indicator.percentage}%</b>
                    <#else>
                            ${Root.msg("percentage")} : <b>${Root.msg("NA")}</b>
                    </#if>
    </#macro>

    <#macro displayIndicatorsExport subprojectId visibility category>
        <a href="/nuxeo/site/easysoa/indicators/export.csv?subprojectId=${subprojectId}&visibility=${visibility}&category=${category}">
        <span class="icon-file-text" style="color:grey" title="${Root.msg("csvExportOfIndicators")}"></span></a>
    </#macro>

    <#macro displayUserInfo user>
        <#if user == "">
            <img src="/nuxeo/site/easysoa/skin/img/user.png" /> ${Root.msg("notLogged")} (<a href="/login.html">${Root.msg("logIn")}</a>)
        <#else>
            <img src="/nuxeo/site/easysoa/skin/img/user.png" /> ${Root.msg("welcome")} 
            <span id="username">${user}</span> (<a href="/nuxeo/logout?requestedUrl=site%2Feasysoa">${Root.msg("logout")}</a>)
        </#if>
    </#macro>


    <#macro displayReturnToIndexButtonBar>
        <@displayBackButtonBar Root.msg("backToHome")/>
    </#macro>

    <#macro displayBackButtonBar buttonLabel>
        <li class="span12">
            <div class="thumbnail">
                <img data-src="holder.js/300x200" alt=""/>
                <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">${buttonLabel}</a>
            </div>
        </li>
    </#macro>

    <#macro displayReturnToPreviousPageButtonBar>
        <li class="span12">
            <div class="thumbnail">
                <img data-src="holder.js/300x200" alt=""/>
                <a class="btn" href="javascript:history.go(-1)">${Root.msg("goBackToPreviousPage")}</a>
            </div>
        </li>
    </#macro>