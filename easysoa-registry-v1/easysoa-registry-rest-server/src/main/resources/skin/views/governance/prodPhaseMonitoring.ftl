<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("governance")} - ${Root.msg("soaProjectProgress")}</title>
        <@includeResources/>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/flot/jquery.flot._js"></script>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/flot/jquery.flot.categories._js"></script>
    </head>

    <body>
        <#include "/views/governance/governanceMacros.ftl">

        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA - ${Root.msg("soaProjectProgress")}
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h2>${Root.msg("soaProjectProgress")}</h2>
                        <p>
                        ${Root.msg("soaProjectProgress.graphdoc")} :
                        </p>
                        <#-- Display a graph with phases state in % -->
                        <table style="border: 0">
                            <tr>
                                <td style="width: 20%"><div id="legendholder"></div></td>
                                <td style="width: 80%"><div id="phaseGraph" style="width:600px;height:400px"></div></td>
                            </tr>
                        </table>

                        <h4>${Root.msg("Details")} :</h4>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <@displayIndicatorInTableWithDescription indicators["specificationsProgress"]/>
                                </tr>
                                <tr>
                                    <@displayIndicatorInTableWithDescription indicators["realisationProgress"]/>
                                </tr>
                                <tr>
                                    <@displayIndicatorInTableWithDescription indicators["deploiementProgress"]/>
                                </tr>
                            </table>
                        </p>

                        <h4>${Root.msg("theseIndicatorsAreBuiltOn")} :</h4>
                        <p>
                            <table class="table table-bordered" width="100%">
                            <#assign indicatorNames = indicatorsService.getIndicatorProvider("PhaseProgressIndicatorProvider").getRequiredIndicators()/>
                            <#list indicatorNames as indicatorName>
                                <@displayIndicatorInTable2 indicators[indicatorName] />
                            </#list>
                            </table>
                        </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>
    <#-- Phase monitoring graph -->
    <@displayPhaseMonitoringDiagram indicators/>

</html>
