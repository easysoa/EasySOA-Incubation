<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Endpoint indicators dashboard</title>
        <@includeResources/>
    </head>

    <body>
        <#include "/views/monitoring/macros.ftl">
	<div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA Usage
	    </div>
	</div>
        <br/>
	<div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h1>Qualité des services déployés</h1>
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