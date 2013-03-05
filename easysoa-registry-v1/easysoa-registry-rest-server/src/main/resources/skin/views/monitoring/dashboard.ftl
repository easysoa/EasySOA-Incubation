<!DOCTYPE html>
<html>

<head>
	<title>EasySOA Endpoint indicators dashboard</title>
	<meta charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
	<style type="text/css">
	  .clickable:hover { cursor: pointer; background-color: #FFC; }
	  .id { display: none }
	  .selected { background-color: #CFC; }
	</style>
</head>

<body>

	<div id="header">
		<div id="headerContents">
		    <div id="logoLink">&nbsp;</div>
	    	<div id="headerUserBar"></div>
			EasySOA Endpoint indicators dashboard
	    </div>
	</div>

	<div id="container">

	<#include "/views/EasySOA/macros.ftl">	

		<h1>Endpoints</h1>

                <#if subprojectId>
                Endpoints for version : ${subprojectId}
                <#else>
                Global endpoints
                </#if>

		<@displayEndpointsShort endpoints subprojectId/>
		
	</div>

        <div id="container">
            <a href="${Root.path}/../?subprojectId=${subprojectId}">Back to dashboard</a>
        </div>

</body>

</html>