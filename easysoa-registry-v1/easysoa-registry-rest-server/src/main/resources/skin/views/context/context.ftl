<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>${Root.msg("Perspective")}</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                ${Root.msg("Perspective")}
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt=""/>
                        <h1>${Root.msg("projectsPhasesAndVersions")} :</h1>
                        <br/>
                        <@displayContextBar subprojectId contextInfo visibility "false" "true"/>
                        <br/>
                        <br/>
                        ${Root.msg("chooseProjectPhaseAndVersion")}
                        <br/>
                        <br/>
                        <@displayProjectsPhasesAndVersionsShort projectIdToSubproject/>
                    </div>
                </li>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <a class="btn" href="${Root.path}/../">${Root.msg("backToGlobalPerspective")}</a>
                        &nbsp;
                        <a class="btn" href="javascript:window.history.back();">${Root.msg("Cancel")}</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

</html>