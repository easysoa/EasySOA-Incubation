<!DOCTYPE html>
<html>

    <head>
	<title>EasySOA Endpoint indicators dashboard</title>
	<meta charset="utf-8" />
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        <!-- font-awesome style for icons -->
        <link rel="stylesheet" href="/nuxeo/site/easysoa/skin/css/font-awesome.css">
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" />
    </head>

    <body>
        <#include "/views/EasySOA/macros.ftl">
        <#include "/views/EasySOA/docMacros.ftl">
        <#include "/views/monitoring/macros.ftl">
	<div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
	    	<div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
            <div id="headerContextBar">
                <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "true"/>
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
                        <h3>Indicateurs pour le service <@displayServiceTitleWithoutPhase service subprojectId visibility/><a href="<@urlToLocalNuxeoDocumentsUiShort servicePath/>" target="_blank"><i class="icon-file-alt"></i></a></h3>
                        <span><#-- to solve img pb just below Bootstrap .thumbnail (actually display:block) -->
                        <#-- OLD Déployé en <@displayEnvironment endpoint['env:environment']/> à ${endpoint['endp:url']} -->
                        Déployé à <@displayEndpointTitleWithoutPhase endpoint subprojectId visibility/>
                        </span>
                        <p>
                        <@displayIndicatorsShort indicators/>
                        </p>
                    </div>
                </li>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <a class="btn" href="${Root.path}?subprojectId=${subprojectId}&visibility=${visibility}">Retour à la liste des services déployés</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

</html>
