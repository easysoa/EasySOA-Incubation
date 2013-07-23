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
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                <div id="headerContextBar">
                    <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                    <#assign subprojectId=subprojectId!"">
                    <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
                </div>
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
                        <@displayEndpointsShort endpoints subprojectId visibility/>
                        </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>