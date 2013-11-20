<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("governance")}</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("governance")}
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("governance")}</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/prodPhaseMonitoring?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("soaProjectProgress")}</a>
                                <small> : ${Root.msg("soaProjectProgress.doc")}</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/governanceIndicators?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("governanceAndCompletionIndicators")}</a>
                                <small> : ${Root.msg("governanceAndCompletionIndicators.doc")}</small>
                            </p>
                            <p>
                                <a class="btn" href="<@urlToLocalNuxeoDocumentsSoaProject subprojectId/>">${Root.msg("collaborativeEditionOfSoaModel")}</a>
                                <small> : ${Root.msg("collaborativeEditionOfSoaModel.doc")}</small>
                            </p>
                        </p>

                        <h3>${Root.msg("indicators")} <@displayIndicatorsExport subprojectId visibility "steering"/></h3>
                        <p>
                            <@displayIndicatorsInTable indicators "steering"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
