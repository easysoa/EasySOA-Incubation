<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Gouvernance</title>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/flot/jquery.flot._js"></script>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/flot/jquery.flot.categories._js"></script>
        <@includeResources/>
    </head>

    <body>
        <#include "/views/governance/governanceMacros.ftl">

        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                <div id="headerContextBar">
                    <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                    <#assign subprojectId=subprojectId!"">
                    <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
                </div>
                EasySOA - Suivi des phases
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h2>Suivi des phases</h2>
                        <p>
                        Ce graphique montre le degré d'avancement (en pourcentage) de chaque phase :
                        </p>
                        <!-- Display a graph with phases state in % -->
                        <table style="border: 0">
                            <tr>
                                <td style="width: 20%"><div id="legendholder"></div></td>
                                <td style="width: 80%"><div id="phaseGraph" style="width:600px;height:400px"></div></td>
                            </tr>
                        </table>

                        <h4>Détails :</h4>
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

                        <h4>Ces indicateurs globaux d'avancement reposent sur les indicateurs suivants :</h4>
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
    <!-- Phase monitoring graph -->
    <@displayPhaseMonitoringDiagram indicators/>

</html>
