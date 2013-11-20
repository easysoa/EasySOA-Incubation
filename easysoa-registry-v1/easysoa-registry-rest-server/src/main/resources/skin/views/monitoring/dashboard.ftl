<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("monitoring")} - ${Root.msg("serviceLevel")}</title>
        <@includeResources/>
    </head>

    <body>
        <#include "/views/monitoring/macros.ftl">
	<div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("monitoring")} - ${Root.msg("serviceLevel")}
	    </div>
	</div>
        <br/>
	<div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h1>${Root.msg("serviceLevelOfDeployedServices")}</h1>
                        <p>
                        <@displayEndpointsShort servicePathToEndpoints pathToServices subprojectId visibility/>
                        </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>