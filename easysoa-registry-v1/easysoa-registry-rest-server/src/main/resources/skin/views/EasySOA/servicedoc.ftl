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
        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>        
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

                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <p>Version de Phase : <@displayCurrentVersion subprojectId visibility/></p>

		<h1>Documentation du service ${service.path} ${service.title} (${service['soan:name']})</h1>

		Vous voulez :
		<ul>
			<li>vous en servir par une application</li>
			<li>(prototyper)</li>
			<li>développer avec[doc,essai en ligne, test endpoint, simulation]</li>
			<li>le développer(specs, protos(mocks))</li>
		</ul>

		<h2>(description)</h2>
		${service['dc:description']}

		<h2>Documentation(manuelle, extraite)</h2>
		<#-- doc of first impl, since none on service itself : -->
		<#if actualImpls?size != 0>
		${actualImpls[0]['impl:documentation']}
		<@displayProps actualImpls[0]['impl:operations'] ''/>
		</#if>

		<h2>Usages</h2>
		oé (applications : le déployant ; architecture : le consommant)

		<#-- IntelligentSystems tagging it, since only Applications from now : -->
		<b>Applications :</b><br/>
		<#if service['proxies']?has_content>
		<ul>
		<#list service['proxies'] as serviceProxy>
			<#if serviceProxy['parent'].type = 'IntelligentSystem'>
					<li><@displayDocShort serviceProxy/></li>al
			</#if>
		</#list>
		</ul>
		</#if>

		<#-- TaggingFolder tagging it, since only Applications from now : -->
		<br/><b>Business Processes :</b><br/>
		<#if service['proxies']?has_content>
		<ul>
		<#list service['proxies'] as serviceProxy>
			<#if serviceProxy['parent'].type = 'TaggingFolder'>
					<li><@displayTagShort serviceProxy['parent'] subprojectId visibility/></li>
			</#if>
		</#list>
		</ul>
		</#if>

		<br/><a href="${Root.path}/${service['soan:name']?xml}/tags?subprojectId=${subprojectId}&visibility=${visibility}">Also tag in...</a>

		<br/>exemples d'appel

		<br/>(autres tags)

		<h2>Interface(s)</h2>
		WSDL, JAXRS...
		
		<h2>Implementation(test)</h2>
		et consomme, dépend de (en mode non test)

		<br/><b>Implementations :</b><br/>
      <#-- @displayDocsShort actualImpls/ -->
      <@displayDocs actualImpls shortDocumentPropNames + serviceImplementationPropNames/>
		<br/>
		<br/><b>Mocks :</b><br/>
		<#-- @displayDocsShort mockImpls/ -->
      <@displayDocs mockImpls shortDocumentPropNames + serviceImplementationPropNames/>
		<#-- TODO TEST ismock : ${mockImpls[0]['impl:ismock']} -->

		<h2>Endpoint</h2>
		Déployé à : URL
		<br/>Et a déploiements de test :

		<h1>test</h1>

		<h2>Contenu dans</h2>
		<#if service['proxies']?has_content><#list service['proxies'] as proxy><@displayDocShort proxy['parent']/> ; </#list></#if>

		<h2>Contient</h2>
		<#if service['children']?has_content><@displayDocsShort service['children']/></#if>

		<h2>log : all props</h2>
		<@displayDoc service/>
	</div>

        <div id="container">
            <a href="${Root.path}?subprojectId=${subprojectId}&visibility=${visibility}">Back to services</a>
        </div>

</body>

</html>