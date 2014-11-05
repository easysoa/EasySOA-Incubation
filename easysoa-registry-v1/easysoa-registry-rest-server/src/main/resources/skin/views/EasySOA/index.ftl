<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">
    <#include "/views/EasySOA/urlMacros.ftl">

    <head>
        <title>EasySOA ${Root.msg("Home")}</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">

                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("serviceCartography")}</h3>
                        <p>
                            <#-- TODO : Replace these UI hard coded links by macros ? -->
                            <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("sourceDiscovery")}</a>
                                <a class="btn" href="<@urlToWebDiscovery subprojectId visibility/>" target="_blank">${Root.msg("webDiscovery")}</a>
                                <a class="btn" href="${Root.path}/cartography/runDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("runtimeDiscovery")}</a>
                            </div>
                            </p>
                            <div class="btn-group">
                                <#-- a class="btn" href="<@urlToLocalNuxeoDocumentsSoaProject subprojectId/>">${Root.msg("collaborativeEditionOfSoaModel")}</a -->
                                <a class="btn" href="${Root.path}/services/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("serviceBrowsingUI")}</a>
                            </div>
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <@displayIndicatorInTableShort indicators["count.InformationService"]/>
                                <@displayIndicatorInTableShort indicators["count.ServiceImplementation"]/>
                                <@displayIndicatorInTableShort indicators["count.Endpoint"]/>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/cartography/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("more")}</a>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("compliance")}</h3>
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("matchingDashboard")}</a>
                            </div>
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <@displayIndicatorInTableShort indicators["ServiceImplementationPlaceholders"]/>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/services/matchingFull/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("more")}</a>
                    </div>
                </li>
            </ul>

            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("monitoring")}</h3>
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/monitoring/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("serviceLevel")}</a>
                                <a class="btn" href="${Root.path}/monitoring/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("statistics")}</a>
                                <#-- a class="btn" href="#">${Root.msg("appropriationOfSoaModelByEasySOAUsers")}</a -->
                            </div>
                        </p>
                        <p>
                            <#-- TODO Replace by a new indicator showing the the nb or % of services in default -->
                            <table class="table table-bordered" width="100%">
                                <@displayIndicatorInTableShort indicators["serviceInViolation"]/>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/monitoring/usage/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("more")}</a>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("governance")}</h3>
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/governance/prodPhaseMonitoring?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("soaProjectProgress")}</a>
                                <a class="btn" href="${Root.path}/governance/governanceIndicators?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("governanceAndCompletionIndicators")}</a>
                            </div>
                            <p/>
                            <div class="btn-group">
                                <a class="btn" href="<@urlToLocalNuxeoDocumentsSoaProject subprojectId/>">${Root.msg("collaborativeEditionOfSoaModel")}</a>
                                <#-- et / ou Aide à la prise de décisions ? Registry des services, implementations ?? -->
                            </div>
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <@displayIndicatorInTableShort indicators["serviceWithoutConsumption"]/>
                                <@displayIndicatorInTableShort indicators["serviceWithoutUserTag"]/>
                            </table>
                        </p>
                        <a class="btn btn-primary" href="${Root.path}/governance?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("more")}</a>
                    </div>
                </li>
            </ul>

            <#-- 2 categories with a separator -->
            <#-- hr style="color:black; background-color:black; height:3px;" / -->

            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("tools")}</h3>
                        <#-- p>Statistiques et indicateurs d'appropiation du modèle SOA par les utilisateurs de EasySOA...</p -->
                        <p>
                            <div class="btn-group">
                                <a class="btn" href="${Root.path}/tools/apis?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("easysoaRestApi")}</a>
                                <a class="btn" href="${Root.path}/tools/soapui?subprojectId=${subprojectId}&visibility=${visibility}">SOAPUI</a>
                                <a class="btn" <#if !Root.isDevModeSet()>disabled</#if> href="">Scaffolder proxy</a><#-- disabled -->
                                <a class="btn" <#if !Root.isDevModeSet()>disabled</#if> href="">Proxy run manager</a><#-- disabled -->
                                <a class="btn" <#if !Root.isDevModeSet()>disabled</#if> href="${frascatiStudio_url}">OW2 FraSCAti Studio</a>
                            </div>

                            <#-- TODO : add links -->
                            <#--<ul>
                                <li>Scaffolder proxy
                                    <small> : Take advantage of the services you find, by using them in this secured service scaffolder</<small>
                                </li>
                                <li>Proxy run manager</li>
                                <li><a href="${frascatiStudio_url}">FraSCAti Studio</a></li>
                            </ul>-->
                        </p>
                        <br/>
                        <a class="btn btn-primary" href="${Root.path}/tools?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("more")}</a><#-- TODO -->
                        <br/>
                        <h3>${Root.msg("examples")}</h3>
                        <p>
                            <ul>
                                <li><a href="<@urlToPureAirFlowers />" target="_blank">Pure Air Flowers</a></li>
                                <li><a href="<@urlToIntranet />" target="_blank">Intranet</a></li>
                                <li><a href="${Root.path}/dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("matchingDashboardSamples")}</a></li>
                                
                                <#if Root.isDevModeSet()>
                                <li><a href="<@urlToApvPivotal />" target="_blank">APV Pivotal</a></li>
                                <li><a href="<@urlToAxxxDpsApv />" target="_blank">AXXX DPS APV</a></li>
                                </#if>
                            </ul>
                        </p>
                    </div>
                </li>
            </ul>

        </div>

    </body>

</html>