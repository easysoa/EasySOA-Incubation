<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Gouvernance</title>
        <meta charset="utf-8" />
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
    </head>
    
    <body>
        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                EasySOA Gouvernance
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <h3>Gouvernance</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/governance/prodPhaseMonitoring?subprojectId=${subprojectId}&visibility=${visibility}">Suivi des Phases de production SOA</a>
                                <small> : Affiche un graphique présentant l'état d'avancement de chaque phase.</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/governance/governanceIndicators?subprojectId=${subprojectId}&visibility=${visibility}">Indicateurs de complétion et gouvernance</a>
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
                
                <!--<li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Retour à l'accueil</a>
                    </div>
                </li>-->
                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
