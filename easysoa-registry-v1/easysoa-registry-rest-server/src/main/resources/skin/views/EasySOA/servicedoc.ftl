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

                <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>

		<h2>Service <@displayServiceTitle service subprojectId visibility/></h2>

		Vous voulez :
		<ul>
			<li>vous en servir par une application</li>
			<li>(prototyper)</li>
			<li>développer avec[doc,essai en ligne, test endpoint, simulation]</li>
			<li>le développer(specs, protos(mocks))</li>
		</ul>

		<h3>(description)</h3>
		${service['dc:description']}
		
		<h3>Métier</h3>
        <#assign providerActorTitle = ""/>
        <#if providerActor?has_content>
            <#assign providerActorTitle = providerActor.title/>
        </#if>
        <#assign componentTitle = ""/>
        <#if component?has_content>
            <#assign componentTitle = component.title/>
        </#if>
        <span style="font-weight: bold;">Acteur fournisseur :</span> ${providerActorTitle}<br/>
        <span style="font-weight: bold;">Composant :</span> ${componentTitle}<br/>
        <p/>

		<h3>Documentation</h3>
		
		<h4>Documentation - extraite :</h4>
		du WSDL :
		<p/>
		<@displayProp service 'iserv:operations'/>
		<p/>
		de l'impl (TODO mashupper) :
		<p/>
		<#-- doc of first (?) TODO non-test impl : -->
		<#if actualImpls?size != 0>
		${actualImpls[0]['impl:documentation']}
		<@displayProp actualImpls[0] 'impl:operations'/>
		<#if actualImpls?size != 1>
		<br/>(other implementations...)
		</#if>
		</#if>
		<p/>
		
		<#assign iserv_operations=service['iserv:operations']>
		<#if actualImpls?size != 0><#assign impl_operations=actualImpls[0]['impl:operations']></#if>
		<table style="width: 600px;">
            <#if iserv_operations?size != 0>
                <#-- title (for RPC style operation display) NOT REQUIRED -->
		        <#-- tr>
		            <th style="font-weight: bold;">Name</th>
		            <th style="font-weight: bold;">Parameters</th>
		            <th style="font-weight: bold;">Returns</th>
		            <th style="font-weight: bold;">Documentation</th>
		        </tr -->
                <#list iserv_operations as iserv_operation>
                <tr><td>
                <table style="border-spacing: 0px 10px;">
                    <#-- RPC style operation display : -->
                    <#-- tr>
                        <td><h:outputText value="${entry.get('operationName').getValue()}" /></td>
                        <td><h:outputText value="${entry.get('operationParameters').getValue()}" /></td>
                        <td><h:outputText value="${entry.get('operationReturnParameters').getValue()}" /></td>
                        <td><h:outputText value="${entry.get('operationDocumentation').getValue()}" /></td>
                    </tr -->
                    <#-- in/out (message) style operation display : -->
                    <#-- finding corresponding operation in (first) non mock impl : -->
                    <#assign impl_operation = {}>
                    <#list impl_operations as impl_operation_cur>
                        <#if impl_operation_cur['operationName'] = iserv_operation['operationName']>
                            <#assign impl_operation = impl_operation_cur>
                        </#if>
                    </#list>
                    <#-- displaying mashupped iserv & impl operation, with pretty display of enum values : -->
                    <tr>
                        <td colspan="2" style="font-weight: bold; text-decoration: underline;">${iserv_operation['operationName']}</td>
                        <td style="font-style: italic;">${iserv_operation['operationDocumentation']}<#if actualImpls?size != 0> (${impl_operation['operationDocumentation']})</#if></td>
                    </tr>
                    <tr>
                        <td title="${iserv_operation['operationInContentType']}">  <b>In</b> <span style="color:grey">${mimeTypePrettyNames[iserv_operation['operationInContentType']]}</span></td>
                        <td colspan="2">${iserv_operation['operationParameters']}<#if actualImpls?size != 0> (${impl_operation['operationParameters']})</#if></td>
                    </tr>
                    <tr>
                        <td title="${iserv_operation['operationOutContentType']}">  <b>Out</b> <span style="color:grey">${mimeTypePrettyNames[iserv_operation['operationOutContentType']]}</span></td>
                        <td colspan="2">${iserv_operation['operationReturnParameters']}<#if actualImpls?size != 0> (${impl_operation['operationReturnParameters']})</#if></td>
                    </tr>
                </table>
                </td></tr>
                </#list>
            <#else>
                <tr>
                    <td colspan="3" style="text-align: center">No operations.</td>
                </tr>
            </#if>
        </table>
		<p/>
		
		TODO LATER OPT du WSDL de l'endpoint :<p/>
		
		<h4>Documentation - manuelle :</h4>
		TODO lister les non-SoaNodes fils du InformationService NON les fichiers joints, ou sinon du BusinessService<p/>
		<@displayProp service 'files:files'/>
		<p/>
		
		<#assign service_files_files=service['files:files']>
		<#assign service_children=service['children']><#-- TODO LATER -->
		<table style="width: 600px;">
            <#if service_files_files?size != 0>
                <tr><td>
                <table style="border-spacing: 0px 10px;">
                    <#list service_files_files as service_files_file>
                    <tr>
                        <td colspan="3"><a href="/nuxeo/nxfile/default/${service.id}/files:files/${service_files_file_index}/file/${service_files_file['filename']}">${service_files_file['filename']}</a></td>
                        <td title="${service_files_file['file']['mimeType']}">${mimeTypePrettyNames[service_files_file['file']['mimeType']]}</td>
                        <td>${service_files_file['file']['length']}</td>
                    </tr>
                    </#list>
                </table>
                </td></tr>
            <#else>
                <tr>
                    <td colspan="3" style="text-align: center">Pas de fichiers joints</td>
                </tr>
            </#if>
            <#-- TODO LATER also children documents -->
        </table>
		<p/>

		<h3>Usages</h3>
		oé (applications : le déployant ; architecture : le consommant)<p/>

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

		<br/><a href="${Root.path}/path${service['spnode:subproject']?xml}:${service['soan:name']?xml}/tags?subprojectId=${subprojectId}&visibility=${visibility}">Also tag in...</a>

		<br/>exemples d'appel

		<br/>(autres tags)

		<h3>Interface(s)</h3>
		WSDL, JAXRS...
		
		<h3>Implementation(test)</h3>
		et consomme, dépend de (en mode non test)

		<br/><b>Implementations :</b><br/>
      <#-- @displayDocsShort actualImpls/ -->
      <@displayDocs actualImpls shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/>
		<br/>
		<br/><b>Mocks :</b><br/>
		<#-- @displayDocsShort mockImpls/ -->
      <@displayDocs mockImpls shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/>
		<#-- TODO TEST ismock : ${mockImpls[0]['impl:ismock']} -->

		<h3>Endpoint</h3>
		Déployé à : URL
		<br/>Et a déploiements de test :

		<h2>test</h2>

		<h3>Contenu dans</h3>
		<#if service['proxies']?has_content><#list service['proxies'] as proxy><@displayDocShort proxy['parent']/> ; </#list></#if>

		<h3>Contient</h3>
		<#if service['children']?has_content><@displayDocsShort service['children']/></#if>

		<h3>log : all props</h3>
		<@displayDoc service/>
		<p/>
		Information Service:
		<p/>
		<@displayPropsAll service informationServicePropNames/>
		<p/>
		Architecture Component :
		<p/>
		<@displayPropsAll service architectureComponentPropNames/>
		<p/>
		Platform :
		<p/>
		<@displayPropsAll service platformPropNames/>
	</div>

        <div id="container">
            <a href="${Root.path}?subprojectId=${subprojectId}&visibility=${visibility}">Back to services</a>
        </div>

</body>

</html>