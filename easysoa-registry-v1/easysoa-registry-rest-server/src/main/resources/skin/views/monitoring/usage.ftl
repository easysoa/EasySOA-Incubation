<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Monitoring</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA Monitoring
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Monitoring</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/?subprojectId=${subprojectId}&visibility=${visibility}">Qualité de service</a>
                                <!--<small>EasySOA can be fed with runtime, live-computed business indicators that show how much expected business service levels are met.</small>-->
                                <small> : EasySOA peut être alimenté avec des indicateurs métier lors de l'éxécution qui montrent les niveaux de services atteints.</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}"">Statistiques</a>
                                <!--<small>Jasmine for EasySOA computes and display business indicators from technical service events according to EasySOA-known SLA configuration</small>-->
                                <small> : JASMINe pour EasySOA calcule et affiche des indicateurs metier issus d'événements de services techniques suivant les configurations SLA enregistrées dans EasySOA.</small>
                            </p>
                        </p>

                        <h3>Indicateurs <@displayIndicatorsExport subprojectId visibility "usage"/></h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <@displayIndicatorsInTable indicators "usage"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
