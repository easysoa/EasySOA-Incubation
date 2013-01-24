<!DOCTYPE html>
<html>

<head>
	<title>EasySOA REST Services Documentation</title>
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
			EasySOA REST Services Documentation
	    </div>
	</div>

	<div id="container">

		<#include "/views/EasySOA/macros.ftl">

		<h1>Services</h1>

		<@displayServicesShort services/>

		<h1>By tags</h1>

		<#list tags as tag>
		<#if tagId2Services?keys?seq_contains(tag.id)>
		<h3>Services (${tagId2Services[tag.id]?size}) of tag <@displayTagShort tag/></h3>
		<@displayServicesShort tagId2Services[tag.id]/>
		</#if>
		</#list>

		<h2>Untagged services (${untaggedServices?size})</h2>

		<@displayServicesShort untaggedServices/>

		<h2>Tags without services (${tags?size - tagId2Services?keys?size})</h2>

		<ul>
			<#list tags as tag>
			<#if !tagId2Services?keys?seq_contains(tag.id)>
			<li><@displayTagShort tag/></li>
			</#if>
			</#list>
		</ul>

	</div>

</body>

</html>
