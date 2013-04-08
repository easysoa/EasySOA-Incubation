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
        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
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
        
		<h1>Service</h1>
		<@displayServiceShort service subprojectId visibility/>

		<h2>Tagged in</h2>

		<#-- compute currentTagIds for later "not yet used tags" display : -->
		<#assign currentTagIds=[]/>
		<#if service['proxies']?has_content>
		<ul>
        <#list service['proxies'] as serviceProxy>zou
                    <li>
            <#-- in Specifications, can also be : IntelligentSystem, BusinessService, BusinessService, Folder  -->
            <#if serviceProxy['parent'].type = 'TaggingFolder'>
                    <@displayTagShort serviceProxy['parent'] subprojectId visibility/>
            <#else>
                    <@displayDocShort serviceProxy['parent']/>
            </#if>
             -
                    <form method="POST" action="${Root.path}/proxy/${serviceProxy.id}?subprojectId=${subprojectId}&visibility=${visibility}">
						<input name="delete" type="hidden" value=""/>
						<a href="##" onClick="this.parentNode.submit();">Untag</a>
					</form>
					</li>
					<#assign currentTagIds=currentTagIds+[serviceProxy['parent'].id]/>
		</#list>
		</ul>
		</#if>

		<h2>Also tag in...</h2>

		<#list tags as tag>
			<#if !currentTagIds?seq_contains(tag.id)>
			<form method="POST" action="${Root.path}/path${service['spnode:subproject']?xml}:${service['soan:name']?xml}/tags?subprojectId=${subprojectId}&visibility=${visibility}">
				<input name="tagId" type="hidden" value="${tag.id}"/>
				<a href="##" onClick="this.parentNode.submit();">Tag</a> in <@displayTagShort tag subprojectId visibility/>
			</form>
			</#if>
		</#list>

	</div>

</body>

</html>
