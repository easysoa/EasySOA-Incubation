<!DOCTYPE html>
<html>

    <head>
	<title>Choose a project</title>
	<meta charset="utf-8" />
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
    </head>

    <body>
        <#include "/views/EasySOA/macros.ftl">	
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                Perspective
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Display the context bar -->
                <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>
                
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt=""/>
                        <h1>Projets, phases et versions :</h1>
                        <br/>
                        <!--Choose the versions of phase you want to use as a point of view and the visibility scope (strict or deep, i.e. with or without parent phases).-->
                        Choisissez un projet, une phase et la version que vous voulez utiliser dans la perspective (avec ou sans les phases parentes).
                        <br/>
                        <@displayProjectsPhasesAndVersionsShort projectIdToSubproject/>
                    </div>
                </li>
                
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <a class="btn" href="${Root.path}/../">Retour à la prespective globale</a>
                        &nbsp;
                        <a class="btn" href="javascript:window.history.back();">Annuler</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>
    
</html>