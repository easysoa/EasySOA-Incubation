<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Accueil</title>
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
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">

                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Cartographie des services</h3>
                        <p>
                            <!-- TODO : Replace this hard coded link by a build in link ? -->
                            <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte sources</a>
                                <a class="btn" href="${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}&serverName=${serverName}" target="_blank">Découverte web</a>
                                <a class="btn" href="${Root.path}/cartography/runDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte à l'éxécution</a>
                            </div>
                            </p>
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
                                <a class="btn" href="${Root.path}/services/?subprojectId=${subprojectId}&visibility=${visibility}">IHM de consultation SOA</a>
                            </div>
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <@displayIndicatorInTableShort indicators["InformationService"]/>
                                <@displayIndicatorInTableShort indicators["ServiceImplementation"]/>
                                <@displayIndicatorInTableShort indicators["Endpoint"]/>
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
                                <@displayIndicatorInTableShort indicators["ServiceImplementationPlaceholders"]/>
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
                                <@displayIndicatorInTableShort indicators["serviceInViolation"]/>
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
                                <@displayIndicatorInTableShort indicators["serviceWithoutConsumption"]/>
                                <@displayIndicatorInTableShort indicators["serviceWithoutUserTag"]/>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/governance?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a><!-- TODO -->
                    </div>
                </li>
            </ul>

            <#if Root.isDevModeSet()>

            <!-- 2 categories with a separator -->
            <hr style="color:black; background-color:black; height:3px;" />

            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Outils</h3>
                        <!--<p>Statistiques et indicateurs à l'exécution, (?) d'appropiation du modèle SOA par les utilisateurs de EasySOA...</p>-->
                        <p>
                            <div class="btn-group">
                                <a class="btn" disabled href="">Scaffolder proxy</a><!-- disabled -->
                                <a class="btn" disabled href="">Proxy run manager</a><!-- disabled -->
                                <a class="btn" href="http://owsi-vm-easysoa-axxx-registry.accelance.net:7080/easySoa/index.html">FraSCAti Studio</a>
                                <a class="btn" href="${Root.path}/tools/apis?subprojectId=${subprojectId}&visibility=${visibility}">Easysoa API REST</a>
                            </div>

                            <!-- TODO : add links -->
                            <!--<ul>
                                <li>Scaffolder proxy
                                    <small> : Take advantage of the services you find, by using them in this secured service scaffolder</<small>
                                </li>
                                <li>Proxy run manager</li>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:7080/easySoa/index.html">FraSCAti Studio</a></li>
                            </ul>-->
                        </p>
                        <br/>
                        <a class="btn btn-primary" href="${Root.path}/tools?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a><!-- TODO -->
                        <br/>
                        <h3>Exemples</h3>
                        <p>
                            <ul>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/demo-intranet/index.html" target="_blank">Pure Air Flowers</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/demo-intranet/index.html" target="_blank">Intranet</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-pivotal.accelance.net:7080/pivotal/index.html" target="_blank">APV Pivotal</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-apv.accelance.net:7080/apv/index.jsp" target="_blank">AXXX dps apv</a></li>
                            </ul>
                        </p>
                    </div>
                </li>
            </ul>

            </#if>

        </div>

    </body>

</html>