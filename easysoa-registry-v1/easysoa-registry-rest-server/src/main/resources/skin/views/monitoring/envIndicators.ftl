<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Endpoint indicators dashboard</title>
        <@includeResources/>
    </head>

    <body>
        <#include "/views/EasySOA/docMacros.ftl">
        <#include "/views/monitoring/macros.ftl">
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                <div id="headerContextBar">
                    <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                    <#assign subprojectId=subprojectId!"">
                    <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
                </div>
                EasySOA Usage
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Indicateurs pour le service <@displayServiceTitleWithoutPhase service subprojectId visibility/><a href="<@urlToLocalNuxeoDocumentsUiShort servicePath/>" target="_blank"><i class="icon-file-alt"></i></a></h3>
                        <span><#-- to solve img pb just below Bootstrap .thumbnail (actually display:block) -->
                        <#-- OLD Déployé en <@displayEnvironment endpoint['env:environment']/> à ${endpoint['endp:url']} -->
                        Déployé à <@displayEndpointTitleWithoutPhase endpoint subprojectId visibility/>
                        </span>
                        <p>
                        <@displayIndicatorsShort indicators/>
                        </p>
                    </div>
                </li>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <a class="btn" href="${Root.path}?subprojectId=${subprojectId}&visibility=${visibility}">Retour à la liste des services déployés</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

</html>
