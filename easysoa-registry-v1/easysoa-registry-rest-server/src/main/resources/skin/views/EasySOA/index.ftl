<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <head>
        <title>EasySOA index</title>
        <meta charset="utf-8" />

        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>

        <link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" /><!-- Remove this css, replaced by bootstrap -->
        <link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" />
    </head>

    <body>

        <#include "/views/EasySOA/macros.ftl">

        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
            </div>
        </div>

        <br/>

        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Display the context bar -->
                <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "true"/>

                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Cartographie des services</h3>
                        <p>
                            <!-- TODO : Replace this hard coded link by a build in link ? -->
                            <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte sources</a>
                                <a class="btn" href="${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}" target="_blank">Découverte web</a>
                                <a class="btn" href="${Root.path}/cartography/runDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte à l'éxécution</a>
                            </div>
                            </p>
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de services définis :</td>
                                    <td width="20%">${indicators["Nombre de InformationService"].count}</td>
                                </tr>
                                <tr>
                                    <td width="80%">Nombre services implémentés :</td>
                                    <td width="20%">${indicators["Nombre de ServiceImplementation"].count}</td>
                                </tr>
                                <tr>
                                    <td width="80%">Nombre de services déployés :</td>
                                    <td width="20%">${indicators["Nombre de Endpoint"].count}</td>
                                </tr>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/cartography/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Conformité</h3>
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Réconciliation</a>
                            </div>
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de Placeholders (implémentations inconnues d'endpoints) :</td>
                                    <td width="20%">${indicators["Nombre de Placeholders"].count}</td>
                                </tr>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/services/matchingFull/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>
                    </div>
                </li>
            </ul>

            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Monitoring</h3>
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/monitoring/?subprojectId=${subprojectId}&visibility=${visibility}">Qualité de service</a>
                                <a class="btn" href="${Root.path}/monitoring/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}">Statistiques</a>
                                <!--<a class="btn" href="#">Appropriation du modèle SOA par les utilisateurs de EasySOA</a>-->
                            </div>
                        </p>
                        <p>
                            <!-- Replace by a new indicator showing the the nb or % of services in default -->
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de services en défaut :</td>
                                    <td width="20%">25%<#--${indicators["Never consumed services"].count}--></td>
                                </tr>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/monitoring/usage/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Gouvernance</h3>
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/governance/prodPhaseMonitoring?subprojectId=${subprojectId}&visibility=${visibility}">Suivi des Phases de production SOA</a>
                                <a class="btn" href="${Root.path}/governance/governanceIndicators?subprojectId=${subprojectId}&visibility=${visibility}">Indicateurs de complétion et gouvernance</a>
                            </div>
                            <p/>
                            <div class="btn-group">
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
                                <!-- TODO make it work for versions -->
                                <!-- et / ou Aide à la prise de décisions ? Registry des services, implementations ?? -->
                            </div>
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de service jamais consomés :</td>
                                    <td width="20%">${indicators["Never consumed services"].count}</td>
                                </tr>
                                <tr>
                                    <td width="80%">Nombre de services n'ayant aucun tag utilisateur :</td>
                                    <td width="20%">${indicators["Services without at least one user tag"].count}</td>
                                </tr>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/governance?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a><!-- TODO -->
                    </div>
                </li>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/services/?subprojectId=${subprojectId}&visibility=${visibility}">IHM de consultation du modèle SOA</a>
                            </div>
                        </p>
                    </div>
                </li>
            </ul>

            <!-- 2 categories with a separator -->
            <hr style="color:black; background-color:black; height:3px;" />

            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>A classer</h3>
                        <p>Statistiques et indicateurs à l'exécution, (?) d'appropiation du modèle SOA par les utilisateurs de EasySOA...</p>

                        <p>
                            <ul>
                                <!-- TODO : add links -->
                                <li>Scaffolder proxy
                                    <small> : Take advantage of the services you find, by using them in this secured service scaffolder</<small>
                                </li>
                                <li>Proxy run manager</li>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:7080/easySoa/index.html">FraSCAti Studio</a></li>
                            </ul>
                        </p>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Exemples</h3>
                        <p>
                            <ul>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/demo-intranet/index.html" target="_blank">Pure Air Flowers</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/demo-intranet/index.html" target="_blank">Intranet</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-pivotal.accelance.net:7080/pivotal/index.html">APV Pivotal</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-apv.accelance.net:7080/apv/index.jsp">AXXX dps apv</a></li>
                            </ul>
                        </p>
                    </div>
                </li>
            </ul>
        </div>

        <a class="btn btn-primary" href="${Root.path}/indicators/?subprojectId=${subprojectId}&visibility=${visibility}">Old indicators page (to remove when finished)</a>

    </body>

</html>