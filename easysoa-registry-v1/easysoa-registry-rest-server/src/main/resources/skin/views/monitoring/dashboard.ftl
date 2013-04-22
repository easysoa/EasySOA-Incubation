<!DOCTYPE html>
<html>

    <head>
	<title>EasySOA Endpoint indicators dashboard</title>
	<meta charset="utf-8" />
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
    </head>

    <body>
        <#include "/views/EasySOA/macros.ftl">
	<div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                EasySOA Usage
	    </div>
	</div>
        <br/>
	<div class="container" id="container">
            <ul class="thumbnails">
                <!-- Display the context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>
            
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h1>Qualité des services déployés</h1>
                        <p>
                        <@displayEndpointsShort endpoints subprojectId visibility/>
                        </p>
                    </div>
                </li>
                
                <!--<li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Retour à l'acceuil</a>    
                    </div>
                </li>-->
                <@displayReturnToIndexButtonBar/>                
            </ul>
        </div>
    </body>

</html>