<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Gouvernance</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                <div id="headerContextBar">
                    <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                    <#assign subprojectId=subprojectId!"">
                    <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
                </div>
                EasySOA Gouvernance
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Gouvernance</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/prodPhaseMonitoring?subprojectId=${subprojectId}&visibility=${visibility}">Suivi des Phases de production SOA</a>
                                <small> : Affiche un graphique présentant l'état d'avancement de chaque phase.</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/governanceIndicators?subprojectId=${subprojectId}&visibility=${visibility}">Indicateurs de complétion et gouvernance</a>
                                <small> : Indicateurs détaillés de complétion des services</small>
                            </p>
                            <p>
                                <#if subprojectId>
                                    <#assign nuxeoUrl = "/nuxeo/nxdoc/default/"
                                        + Session.query("select * from Subproject where spnode:subproject='" + subprojectId + "'")[0].id
                                        + "/view_documents"/>
                                    <#if !subprojectId?ends_with("_v")>
                                        <#assign nuxeoUrl = nuxeoUrl + "?version=true" />
                                    </#if>
                                <#else>
                                    <#assign nuxeoUrl = "/nuxeo/nxpath/default/default-domain@view_documents"/>
                                </#if>
                                <a class="btn" href="${nuxeoUrl}">Edition collaborative du modèle SOA</a>
                                <small> : </small>
                            </p>
                        </p>

                        <h3>Indicateurs</h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <@displayIndicatorsInTable indicators "steering"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
