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
        
        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>        
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
		
		<h1>Indicators for endpoint</h1>

                <#if subprojectId>
                Indicators for version : ${subprojectId}, visibility ${visibility}
                <#else>
                Global indicators
                </#if>

		<@displayIndicatorsShort indicators/>
		
		<br/>
		
		<a href="${Root.path}?subprojectId=${subprojectId}&visibility=${visibility}">Back to endpoint list</a>
		
	</div>
</body>

</html>
