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

        <#include "/views/EasySOA/macros.ftl">
        <#include "/views/EasySOA/docMacros.ftl">
    
	<div id="header">
		<div id="headerContents">
		    <div id="logoLink">&nbsp;</div>
	    	<div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
			EasySOA REST Services Documentation
	    </div>
	</div>

	<div id="container">

            <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>

		<h2>Service <@displayServiceTitle service subprojectId visibility/></h2>

		Vous voulez :
		<ul>
			<li>vous en servir par une application (applications et leurs IHMs, notamment web)</li>
			<li>(prototyper (créer des implémentations de test, des clients de test)</li>
			<li>développer avec (interface, documentation d'utilisation, exemples, essai en ligne, test endpoint, simulation)</li>
			<li>le développer(specs, protos(mocks) / générer client de test)</li>
            <li>le découvrir (découverte web/monit(/source/OPTimport) OPT scopée sur ce service/impl/endpoint)</li>
            <li>le définir (fournir l'interface, la prendre de l'impl/endpoint, LATER l'extraire d'échanges, OPT importer l'architecture)</li>
		</ul>
		
        Vous êtes Concepteur et vous voulez (ce service) :
        <ul>
            <li>(prototyper (créer des implémentations de test, des clients de test)</li>
            <li>le découvrir (découverte web/monit(/source/OPTimport) OPT scopée sur ce service/impl/endpoint)</li>
            <li>le définir (fournir l'interface, la prendre de l'impl/endpoint, LATER l'extraire d'échanges, OPT importer l'architecture)</li>
            <li>voir ses statistiques d'usage métier (répond-il aux attentes ?)</li>
        </ul>
		
        Vous êtes Développeur et vous voulez (cette serviceimpl) :
        <ul>
            <li>développer avec (interface, documentation d'utilisation, exemples, essai en ligne, test endpoint, simulation)</li>
            <li>(rôle consommateur) réutiliser / instancier l'impl (doc d'impl, deliverable et dépendances / application)</li>
            <li>(rôle fournisseur) le développer(specs, protos(mocks) / générer client de test)</li>
            <li>le découvrir (découverte source)</li>
            <li>(tous les autres, "ouvrables")</li>
        </ul>
        
        Vous êtes Exploitant et vous voulez (cet endpoint) :
        <ul>
            <li>le déployer : appli et services dépendants</li>
            <li>(vous en servir par une application (applications et leurs IHMs, notamment web))</li>
            <li>le découvrir (découverte web/monit(/source/OPTimport) OPT scopée sur ce service/impl/endpoint)</li>
            <li>voir ses statistiques d'usage techniques</li>
            <li>LATER gérer sa configuration (politiques)</li>
        </ul>
        
        Vous êtes Utilisateur et vous voulez (ce service) :
        <ul>
            <li>vous en servir par une application (applications et leurs IHMs, notamment web) TODO appli:urlUiWeb</li>
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
		
		<#macro displayOperationParameters params>
		   <#if params?has_content && params?length != 0>
		      <br/>
		      <div style="margin-left:10px; font-size:90%; color:grey;">${params?replace(', ', ')</span><br/>')?replace('=', ' <span style="font-size:90%">(')})</div
		      <br/>
		   </#if>
		</#macro>

		<#if actualImpls?size != 0><#assign impl_operations=actualImpls[0]['impl:operations']></#if>
        <@displayOperations service['iserv:operations'] impl_operations/>
        
        <#macro displayOperations iserv_operations impl_operations>        
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
                            <td><h:outputText value="${entry.get('operationParameters').getValue()}/>" /></td>
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
                            <td style="font-style: italic;">${iserv_operation['operationDocumentation']}<#if impl_operations?has_content> (${impl_operation['operationDocumentation']})</#if></td>
                        </tr>
                        <tr>
                            <td title="${iserv_operation['operationInContentType']}">  <b>In</b> <span style="color:grey"><@displayMimeType iserv_operation['operationInContentType']/></span></td>
                            <td colspan="2">${iserv_operation['operationParameters']}<#if impl_operations?has_content> [<@displayOperationParameters impl_operation['operationParameters']/>]</#if></td>
                        </tr>
                        <tr>
                            <td title="${iserv_operation['operationOutContentType']}">  <b>Out</b> <span style="color:grey"><@displayMimeType iserv_operation['operationOutContentType']/></span></td>
                            <td colspan="2">${iserv_operation['operationReturnParameters']}<#if impl_operations?has_content> [<@displayOperationParameters impl_operation['operationReturnParameters']/>]</#if></td>
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
        </#macro>
		<p/>
		
		TODO LATER OPT du WSDL de l'endpoint OR BELOW ON ENDPOINT :<p/>
		
		<h4>Documentation - manuelle :</h4>
		TODO lister les non-SoaNodes fils du InformationService NON les fichiers joints, ou sinon du BusinessService<p/>
		<#-- @displayProp service 'files:files'/><p/ -->
		
		<#macro displayMimeType mimeType>
		   <#assign mimeTypePrettyName = mimeTypePrettyNames[mimeType]/>
		   <#if !mimeTypePrettyName?has_content>
		      <#assign mimeTypePrettyName = mimeType/>
		   </#if>
		   <span title="${mimeType}">${mimeTypePrettyName}</span>
		</#macro>
        <#macro displayFile service_files_file downloadUrlPrefix>
                <tr>
                    <#-- <@urlToLocalNuxeoDownloadAttachment service service_files_file_index/>" -->
                    <td colspan="3"><a href="${downloadUrlPrefix}${service_files_file['filename']}">${service_files_file['filename']}
                    <#if service_files_file['file']['mimeType']?has_content && service_files_file['file']['mimeType']?starts_with('image/')>
                       <img src="${downloadUrlPrefix}${service_files_file['filename']}" height="35" width="35"/>
                    </#if>
                    </a></td>
                    <td><@displayMimeType service_files_file['file']['mimeType']/></td>
                    <td>${service_files_file['file']['length']}</td>
                    <td><span style="color:grey" title="${service_files_file['file']['digest']}">digest</td>
                </tr>
        </#macro>
		<#macro displayFiles service_files_files>
            <#-- assign service_files_files=service['files:files']>
            <#assign service_children=service['children'] --><#-- TODO LATER docs, Resources ?! -->
            <table style="width: 600px;">
            <#if service_files_files?size != 0>
                <tr><td>
                <table style="border-spacing: 0px 10px;">
                <#list service_files_files as service_files_file>
                    <@displayFile service_files_file '/nuxeo/nxfile/default/${service.id}/files:files/${service_files_file_index}/file/'/>
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
		</#macro>
		<@displayFiles service['files:files']/>
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
		<!-- TODO LATER resource -->
        <#assign interfaceFile=service['file:content']/><!-- TODO using WsdlBlob -->
		<#if service.facets?seq_contains('WsdlInfo') && service['wsdlinfo:wsdlPortTypeName']?has_content>
		SOAP Web Service ${service['wsdlinfo:wsdlPortTypeName']} ${service['wsdlinfo:wsdlServiceName']} ${service['wsdlinfo:transport']}
        <#if interfaceFile?has_content && interfaceFile['filename']?has_content
              && interfaceFile['filename']?ends_with('.wsdl')>
        WSDL XML ${interfaceFile['filename']}
        </#if>
		</#if>
        <#if service.facets?seq_contains('RestInfo') && service['restinfo:path']?has_content>
        REST
        <#if interfaceFile?has_content && interfaceFile['filename']?has_content
              && interfaceFile['filename']?ends_with('.jar')>
        JAXRS Java ${interfaceFile['filename']}
        </#if>
        </#if>
        <br/>
		<a href="<@urlToLocalNuxeoPreview service/>">preview</a>, <a href="<@urlToLocalNuxeoDownload service/>">download</a>, <a href="<@urlToLocalNuxeoDocumentsUi service/>">edit</a>, <a href="<@urlToLocalNuxeoPrint service/>">print</a>
        <br/>
        <#if fstudio_enabled>Mock impl client in FraSCAti Studio</#if>
        <br/>
        Resource : 
        <#if service['rdi:url']?has_content>
           external at <a href="${service['rdi:url']}">${service['rdi:url']}</a><!-- TODO shortened url display -->
           from <#if service['rdi:probeType']?has_content>${service['rdi:probeType']}<#else>IHM</#if>
           (<#if service['rdi:timestamp']?has_content>${service['rdi:timestamp']}</#if>)
        <#else>
           internal
        </#if>
		
		
		<h3>Implementation(test)</h3>
		et consomme, dépend de (en mode non test)
		
		<br/><b>Implementations :</b><br/>
        <#macro displayTested serviceimpl>
           <#assign deliverable = Root.getDocumentService().getSoaNodeParent(serviceimpl, 'Deliverable')/>
           <#list Root.getDocumentService().getSoaNodeChildren(deliverable, 'ServiceConsumption') as servicecons>
              <#-- ${servicecons['javasc:consumedInterface']} ?= ${serviceimpl['javasi:implementedInterface']}; -->
              <#if servicecons['javasc:consumedInterface'] = serviceimpl['javasi:implementedInterface']
                    && servicecons['javasc:consumerClass']?ends_with('Test')>
                 <#-- && serviceimpl['serviceimpl:ismock'] = 'false' -->
                 <#assign foundTestServiceCons = servicecons/>
                 <!-- TODO + location -->
              </#if>
           </#list>
           <#if foundTestServiceCons?has_content>
              <li><span title="${deliverable['title']} / ${foundTestServiceCons['javasc:consumerClass']}">TESTED
              (${foundTestServiceCons['javasc:consumerClass']?substring(foundTestServiceCons['javasc:consumerClass']?last_index_of('.') + 1, foundTestServiceCons['javasc:consumerClass']?length)})</span></li> 
           <#else>
              <li>not tested</li>
           </#if>
        </#macro>
		<#list actualImpls as actualImpl>
		   <#assign deliverable = Root.getDocumentService().getSoaNodeParent(actualImpl, 'Deliverable')/>
		   <span title="Phase : <@displayPhase service['spnode:subproject']/>" style="color:grey; font-style: italic;">
		   ${componentTitle} / ${deliverable['del:application']} / ${deliverable['soan:name']} / </span> <span title="SOA ID: ${service['soan:name']}">
		   <#-- ${actualImpl['soan:name']} -->${actualImpl['title']}
		   </span> - <@displayPhase actualImpl['spnode:subproject']/> (((${actualImpl.versionLabel})))
		   <br/>
           <@displayTested productionImpl/><br/>
		   Dépend de : <#list deliverable['del:dependencies'] as dependency>${dependency}, </#list>
           <p/>
		</#list>
        <#-- @displayDocsShort actualImpls/ -->
        <@displayDocs actualImpls shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/>
		<br/>
		Découverte :
        <a href="${Root.path}/cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Source</a>
		<br/>
		
		<br/><b>Mocks :</b><br/>
		<#-- @displayDocsShort mockImpls/ -->
        <@displayDocs mockImpls shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/>
		<#-- TODO TEST ismock : ${mockImpls[0]['impl:ismock']} -->


		<h3>Endpoints</h3>
		Déployé en Production à :
		<#if productionEndpoint?has_content>
		<a href="${productionEndpoint['endp:url']}">${productionEndpoint['endp:url']}</a>
        <br/>
           <span title="Phase : <@displayPhase service['spnode:subproject']/>" style="color:grey; font-style: italic;">
           ${productionEndpoint['env:environment']} / </span> <span title="SOA ID: ${productionEndpoint['soan:name']}">
           <#-- ${productionEndpoint['soan:name']} -->${productionEndpoint['endp:url']}
           </span> - <@displayPhase productionEndpoint['spnode:subproject']/> (((${productionEndpoint.versionLabel})))
           <br/>
        Resource : 
        <#if productionEndpoint['rdi:url']?has_content>
           external at <a href="${productionEndpoint['rdi:url']}">${productionEndpoint['rdi:url']}</a><!-- TODO shortened url display -->
           from <#if productionEndpoint['rdi:probeType']?has_content>${productionEndpoint['rdi:probeType']}<#else>IHM</#if>
           (<#if productionEndpoint['rdi:timestamp']?has_content>${productionEndpoint['rdi:timestamp']}</#if>)
        <#else>
           internal
        </#if>
		<br/>
		Découverte :
		<a href="${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}">Web</a>, <!-- TODO pass as probe params : Environment (, iserv/endpoint (, user, component)) -->
        <a href="${httpproxy_app_instance_factory_url}?subprojectId=${subprojectId}&visibility=${visibility}&user=${Root.currentUser}&environment=Production'}">Monitoring</a>,  <!-- TODO HTTP Proxy host prop, url to probe IHM, pass as probe params : subproject / Phase, Environment (, iserv/endpoint (, component)) -->
        (<a href="${Root.path}/cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Source</a>)
		<br/>
		Monitoring :
		<a href="${Root.path}/../monitoring/envIndicators/${productionEndpoint.id}?subprojectId=${subprojectId}&visibility=${visibility}">Usage</a>, 
		<a href="${Root.path}/../monitoring/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}">Statistiques</a>
		<br/>
		Test it using :
        <a href="${web_discovery_url + '/scaffoldingProxy/?wsdlUrl=' + productionEndpoint['rdi:url']}">Service Scaffolder</a>, <!-- TODO light.js, or function, rather endpointUrl?wsdl ?? -->
        <a href="">SOAPUI</a>,
        <a href="">FraSCAti Studio new application</a>
        <!-- a href="">FraSCAti Studio application A</a -->
		</#if>
		<br/>
		<#assign productionImpl = Root.getDocumentService().getSoaNodeParent(productionEndpoint, 'ServiceImplementation')/>
        <#assign productionDeliverable = Root.getDocumentService().getSoaNodeParent(productionImpl, 'Deliverable')/>
		Fournit par l'Application ${actorTitle} / ${componentTitle} / ${productionDeliverable['del:application']} (JARS)
		<br/>
		nécessitant les services
		<!-- TODO list deliverables by application -->
		<#list Root.getDocumentService().getSoaNodeChildren(productionDeliverable, 'ServiceConsumption') as servicecons>
		   <#list Root.getDocumentService().getSoaNodeChildren(productionDeliverable, 'ServiceImplementation') as serviceimpl>
		      <#if servicecons['javasc:consumedInterface'] = serviceimpl['javasi:implementedInterface'] && serviceimpl['serviceimpl:ismock'] = 'false'>
		         <#assign foundServiceimpl = serviceimpl/>
		         <!-- TODO + location -->
		      </#if>
		   </#list>
		   <#if !foundServiceimpl?has_content>
              <li>external ${servicecons['title']} through ${servicecons['javasc:consumedInterface']} (test ?)</li> 
           <#else>
              <li>(internal ${servicecons['title']} by ${foundServiceimpl['title']} through ${servicecons['javasc:consumedInterface']})</li>
           </#if>
		</#list>
		<p/>
		<br/>Tous les déploiements :<!-- Et à déploiement de test : -->
		<#-- @displayDocsShort endpoints/ -->
        <@displayDocs actualImpls shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/>




        <#if Root.isDevModeSet()>

        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        
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
        
        </#if>

</body>

</html>