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
        <#include "/views/EasySOA/soaMacros.ftl">
    
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


        <!-- 1. Display focus part according to role
        (profile ex. Developer, relationship ex. providerActor, phase ex. Specifications -->


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



        <!-- 2. Display all global parts, and expand one (some) according to profile (role ??) -->



		<h3>(description)</h3>
		${service['dc:description']}
		
		<h3>Métier ((sert à))</h3>
		<#if service['iserv:linkedBusinessService']?has_content>
		  <#assign businessServiceRef = new_f('org.nuxeo.ecm.core.api.IdRef', service['iserv:linkedBusinessService'])/>
		  <#assign businessService = Session.getDocument(businessServiceRef)/>
		</#if>
		<#if businessService?has_content>
		  Fournit le service business <a href="<@urlToLocalNuxeoDocumentsUi businessService/>"><@displayDocShort businessService/></a> :<br/>
          <#if businessService['businessservice:consumerActor']?has_content>
            <#assign bsConsumerActor = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', businessService['businessservice:consumerActor']))/>
          </#if>
          <span style="font-weight: bold;">Acteur consommateur :</span>
            <#if bsConsumerActor?has_content>
                <a href="<@urlToLocalNuxeoDocumentsUi bsConsumerActor/>">${bsConsumerActor.title}</a>
            <#else>
                <span style="color: red;">MANQUANT</span> (<a href="<@urlToLocalNuxeoDocumentsUi businessService/>">résoudre</a>)
            </#if>
            <br/>
            <span style="font-weight: bold;">Acteur fournisseur :</span>
            <#if providerActor?has_content>
                <a href="<@urlToLocalNuxeoDocumentsUi providerActor/>">${providerActor.title}</a>
            <#else>
                <span style="color: red;">MANQUANT</span> (<a href="<@urlToLocalNuxeoDocumentsUi businessService/>">résoudre</a>)
            </#if>
            <br/>
           SLAs :
           <#assign slas = Root.getDocumentService().getSoaNodeChildren(businessService, 'SLA')/>
           <#if slas?size = 0>
               aucun
           <#else>
               <#list slas as sla>
                   <a href="<@urlToLocalNuxeoDocumentsUi sla/>">${sla.title}</a>
                   <#if sla_index != slas?size - 1>, </#if>
               </#list>
           </#if>
           <br/>
           OLAs :
           <#assign olas = Root.getDocumentService().getSoaNodeChildren(businessService, 'OLA')/>
           <#if olas?size = 0>
               aucun
           <#else>
               <#list olas as ola>
                   <a href="<@urlToLocalNuxeoDocumentsUi ola/>">${ola.title}</a>,
                   <#if ola_index != olas?size - 1>, </#if> 
               </#list>
           </#if>
           <br/>
           Documents de spécification :
           <#assign specDocs = Session.getChildren(businessServiceRef, 'Document')/>
           <#if specDocs?size = 0>
               aucun
           <#else>
               <#list specDocs as specDoc>
                   <a href="<@urlToLocalNuxeoDocumentsUi ola/>">${specDoc.title}</a>,
                   <#if specDoc_index != specDoc?size - 1>, </#if> 
               </#list>
           </#if>
           <br/>
           <br/>
		<#else>
		   Service intermédiaire (ne fournit pas de service business) ((TODO sert à = consumptions ??))
		</#if>
		<br/>
		
		<span style="font-weight: bold;">Acteur fournisseur :</span>
        <#if providerActor?has_content>
            <a href="<@urlToLocalNuxeoDocumentsUi providerActor/>">${providerActor.title}</a>
        <#else>
            aucun
        </#if>
        <br/>
        
        <span style="font-weight: bold;">Composant :</span>
        <#if providerActor?has_content>
            <a href="<@urlToLocalNuxeoDocumentsUi component/>">${component.title}</a>
        <#else>
            <span style="color: red;">MANQUANT</span> (<a href="<@urlToLocalNuxeoDocumentsUi service/>">résoudre</a>)
        </#if>
        <br/>
        
        <p/>

		<h3>Documentation</h3>
		
		<h4>Documentation - extraite :</h4>
		(de la définition et de l'implémentation réelle)
		<#--
		du WSDL :
		<p/>
		<@displayProp service 'iserv:operations'/>
		<p/>
		de l'impl (first (?) non-test one) :
		<p/>
		<#if actualImpls?size != 0>
		${actualImpls[0]['impl:documentation']}
		<@displayProp actualImpls[0] 'impl:operations'/>
		<#if actualImpls?size != 1>
		<br/>(other implementations...)
		</#if>
		</#if>
		<p/>
		-->
		
		<#if actualImpls?size != 0>
		   <#assign impl_operations=actualImpls[0]['impl:operations']>
		<#else>
		   <#assign impl_operations="none">
		</#if>
        <@displayOperations service['iserv:operations'] impl_operations/>
		<p/>
		
		TODO LATER OPT du WSDL de l'endpoint OR BELOW ON ENDPOINT :<p/>
		
		<h4>Documentation - manuelle :</h4>
		TODO lister les non-SoaNodes fils du InformationService NON les fichiers joints, ou sinon du BusinessService<p/>
		<#-- @displayProp service 'files:files'/><p/ -->
		
		Attached files :<br/>
		<@displayFiles service['files:files']/>
		
        Child documents :<br/>
		<#list Session.getChildren(new_f('org.nuxeo.ecm.core.api.IdRef', service['id']), "Document") as childDocument>
            <@displayDocShort childDocument/>
		</#list>
		
        TODO CONFIGURATION & LISTALL Child Resources :<br/>
        <#list Root.getDocumentService().getChildren(service, "Resource") as childResource>
            <@displayDocShort childResource/>
        </#list>
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
		<#list actualImpls as actualImpl>
		   <#assign deliverable = Root.getDocumentService().getSoaNodeParent(actualImpl, 'Deliverable')/>
		   <span title="Phase : <@displayPhaseIfOutsideContext service['spnode:subproject']/>" style="color:grey; font-style: italic;">
		   ${componentTitle} / ${deliverable['del:application']} / ${deliverable['soan:name']} / </span> <span title="SOA ID: ${service['soan:name']}">
		   <#-- ${actualImpl['soan:name']} -->${actualImpl['title']}
		   </span> - <@displayPhaseIfOutsideContext actualImpl['spnode:subproject']/> (((${actualImpl.versionLabel})))
		   <br/>
           <@displayTested productionImpl/><br/>
           <b>Son délivrable :</b></br>
		   dépend de : <#list deliverable['del:dependencies'] as dependency>${dependency}, </#list>
		   <br/>
           <!-- TODO consumptions of : separate internal impls from external services -->
		   <#assign delConsumptions = Root.getDocumentService().getDeliverableConsumptions(deliverable)/>
           ((consomme les classes d'implémentations :
		   <#list delConsumptions as delConsumption>
		      <@displayDocsShort Root.getDocumentService().getConsumedInterfaceImplementationsOfJavaConsumption(delConsumption, subprojectId, true, false)/>))
              <#if delConsumption_index != delConsumptions?size - 1> ;</#if>
		   </#list>))
		   <br/>
           consomme les services :
           <#list delConsumptions as delConsumption>
              <@displayDocShort Root.getDocumentService().getConsumedJavaInterfaceService(delConsumption, subprojectId)/>))
              <#if delConsumption_index != delConsumptions?size - 1> ;</#if>
           </#list>
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
           <span title="Phase : <@displayPhaseIfOutsideContext service['spnode:subproject']/>" style="color:grey; font-style: italic;">
           ${productionEndpoint['env:environment']} / </span> <span title="SOA ID: ${productionEndpoint['soan:name']}">
           <#-- ${productionEndpoint['soan:name']} -->${productionEndpoint['endp:url']}
           </span> - <@displayPhaseIfOutsideContext productionEndpoint['spnode:subproject']/> (((${productionEndpoint.versionLabel})))
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
        <a href="frascatiStudio_url + '/easySoa/GeneratedAppService'">FraSCAti Studio new application</a>
        <!-- a href="">FraSCAti Studio application A</a -->
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
        </#if>
		<br/>Tous les déploiements :<!-- Et à déploiement de test : -->
		<#-- @displayDocsShort endpoints/ -->
        <@displayDocs endpoints shortDocumentPropNames + endpointPropNames/>




        <#if Root.isDevModeSet()>

        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        
		<h2>test</h2>
		
		<h3>User</h3>
        (test) user name : ${userName}<br/>
		isUserProvider : ${isUserProvider?string}<br/>
        isUserDeveloper : ${isUserDeveloper?string}<br/>

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