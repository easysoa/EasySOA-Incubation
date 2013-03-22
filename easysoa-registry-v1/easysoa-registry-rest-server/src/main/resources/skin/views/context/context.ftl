<!DOCTYPE html>
<html>

<head>
	<title>Choose a project</title>
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

<#include "/views/EasySOA/macros.ftl">	

<body>
    <div id="header">
        <div id="headerContents">
            <div id="logoLink">&nbsp;</div>
	    <div id="headerUserBar"></div>
            Projects
	</div>
    </div>

    <div id="container">
        Projects phases and their versions :
        <@displayProjectsPhasesAndVersionsShort projectIdToSubproject/>
    </div>

    <div id="container">
        <a href="${Root.path}/../">Return to global context</a>
    </div>

</body>