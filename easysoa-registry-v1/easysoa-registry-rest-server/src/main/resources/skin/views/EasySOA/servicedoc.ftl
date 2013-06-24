<!DOCTYPE html>
<html>
<head>
   <title>EasySOA Cartographie - Documentation</title>
	<meta charset="utf-8" />
	
   <!-- custom style and scripts -->
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
          EasySOA Cartographie - Documentation
       </div>
   </div>
   <br/>

   <div class="container" id="container">
      <ul class="thumbnails">
         <!-- Context bar -->
         <#assign visibility=visibility!"">
         <#assign subprojectId=subprojectId!"">
         <@displayContextBar subprojectId contextInfo visibility "false"/>

         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>
               
		<h3><@displayServiceTitle service subprojectId visibility/></h3>

      <#-- h3>description</h3 -->
      <@formatTextToHtml service['dc:description']/>
		<p/>

      <#assign parsedServiceSubprojectId = Root.parseSubprojectId(service['spnode:subproject'])/>
      
      <#if !consumerActor?has_content><#assign consumerActor = ""/></#if>
      <#if !providerActor?has_content><#assign providerActor = ""/></#if>
      <#if !component?has_content><#assign component = ""/></#if>

            </div>
         </li>
      
      
      <#if parsedServiceSubprojectId.getSubprojectName() = 'Specifications'>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>
		
		
		<h3>Métier</h3><!-- ((sert à)) -->
      
		<#if businessService?has_content>
          <#-- if businessService['businessservice:consumerActor']?has_content>
            <#assign bsConsumerActor = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', businessService['businessservice:consumerActor']))/>
          </#if -->
		    Fournit le service business <a href="<@urlToLocalNuxeoDocumentsUi businessService/>"><@displayDocShort businessService/></a> :
          <br/>
          
           &nbsp;&nbsp;&nbsp;<@displayActor consumerActor 'Acteur consommateur' businessService/>
           <br/>
           &nbsp;&nbsp;&nbsp;<@displayActor providerActor 'Acteur fournisseur' businessService/>
           <br/>
           &nbsp;&nbsp;&nbsp;<@displaySoaNodeChildrenShort businessService, 'SLA'/>
           <br/>
           &nbsp;&nbsp;&nbsp;<@displaySoaNodeChildrenShort businessService, 'OLA'/>
           <br/>
           &nbsp;&nbsp;&nbsp;Documents de spécification métier :
           <#-- assign businessServiceRef = new_f('org.nuxeo.ecm.core.api.IdRef', service['iserv:linkedBusinessService'])/ -->
           <@displayChildrenShort businessService 'Document'/>
		<#else>
		   Service intermédiaire (ne fournit pas de service business)
		   <!-- ((TODO sert à = consumptions ??)) -->
		</#if>
		<p/>
		<p/>
		
            </div>
         </li>


      </#if>
      

         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>
               
		<h3>Service</h3>
		<p/>

      <#-- h3>Interface(s)</h3 -->
      <@displayInterfaceResource service/>
      <p/>
		
		<#if parsedSubprojectIdToDisplay.getSubprojectName() = 'Specifications'>
		
          <!-- TODO CONFIGURATION & LISTALL -->
          Définitions d'exigences formelles opérationnelles i.e.
          <@displaySoaNodeChildrenShort service, 'OLA'/>
          <p/>
      
		    <#if !businessService?has_content>
              <@displayActor providerActor 'Acteur fournisseur' service/>
              <p/>
          </#if>
              
          <@displayComponent component service/>
          <p/>
          
      <#else>
          Service technique (ayant émergé lors de la phase de réalisation)
      </#if>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

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
		<#if actualImpls?has_content && actualImpls?size != 0>
		${actualImpls[0]['impl:documentation']}
		<@displayProp actualImpls[0] 'impl:operations'/>
		<#if actualImpls?size != 1>
		<br/>(other implementations...)
		</#if>
		</#if>
		<p/>
		-->
		
      <#if productionImpl?has_content>
         <#assign impl_operations=productionImpl['impl:operations']>
      <#elseif serviceimpl?has_content>
         <#assign impl_operations=serviceimpl['impl:operations']>
		<#elseif actualImpls?has_content && actualImpls?size != 0>
		   <#assign impl_operations=actualImpls[0]['impl:operations']>
		<#else>
		   <#assign impl_operations="none">
		</#if>
      <@displayOperations service['iserv:operations'] impl_operations/>
		<p/>
		
		<!-- TODO LATER OPT du WSDL de l'endpoint OR BELOW ON ENDPOINT :<p/ -->
		
		
		<h4>Documentation - manuelle :</h4>
		<!-- lister les non-SoaNodes fils du InformationService NON les fichiers joints, ou sinon du BusinessService -->
		<#-- @displayProp service 'files:files'/><p/ -->
		
		Fichiers joints :<br/>
		<@displayFiles service['files:files']/>
      <p/>		
      
      <!-- TODO CONFIGURATION & LISTALL -->
      Fichiers fils de 
      <@displaySoaNodeChildrenShort service, 'Resource'/><!-- TODO displayResource -->
      <p/>
        
      Tous les documents fils :<br/>
		<@displayChildrenShort service 'Document'/>
		<p/>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

		<h3>Usages</h3>
		<!-- (applications : le déployant ; architecture : le consommant)<p/ -->

		<#-- IntelligentSystems tagging it, since only Applications from now NOOO ALSO Folders : -->
		<#-- b>Applications :</b><br/ -->
		<b>Rangé dans les classifications :</b><br/>
		<#if service['proxies']?has_content && service['proxies']?size != 0>
		<ul>
		<#list service['proxies'] as serviceProxy>
			<#if serviceProxy['parent'].type = 'IntelligentSystem'>
					<li><@displayDocShort serviceProxy/></li>
			</#if>
		</#list>
		</ul>
      <#else>
      Aucune.
		</#if>

		<#-- TaggingFolder tagging it, since only Applications from now : -->
      <#-- br/><b>Business Processes :</b><br/ -->
		<br/><b>Taggé dans :</b><br/>
		<#if service['proxies']?has_content && service['proxies']?size != 0>
		<ul>
		<#list service['proxies'] as serviceProxy>
			<#if serviceProxy['parent'].facets?seq_contains('SoaNode')><#-- serviceProxy['parent'].type = 'TaggingFolder' -->
					<li><@displayTagShort serviceProxy['parent'] subprojectId visibility/></li>
			</#if>
		</#list>
		</ul>
      <#else>
      Aucun.
		</#if>

		<br/><a href="${Root.path}/path${service['spnode:subproject']?xml}:${service['soan:name']?xml}/tags?subprojectId=${subprojectId}&visibility=${visibility}">Tagger aussi dans...</a>

		<#-- br/>exemples d'appel --><!-- TODO -->

		<#-- br/>(autres tags) --><!-- TODO -->
      <p/>

		
            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>
		
		<h3>Implémentations de service<#-- (y compris de test) --></h3>
		<#-- et consomme, dépend de (en mode non test) <br/ -->
		
        <#if serviceimpl?has_content>
          Cette implémentation : <@displayImplementationShort serviceimpl subprojectId visibility/>
          <br/>
          <b>Documentation :</b><br/>
          <@formatTextToHtml serviceimpl['impl:documentation']/>
          <p/>
          
          <#-- @displayDocShort actualImpl/ -->
          <@displayImplementationDetail serviceimpl/>
          <p/>
		  </#if>
		  
        <#if !serviceimpl?has_content || !endpoint?has_content || endpoint['env:environment'] != 'Production'>
          Implémenté en <b>Production</b> par
          <#if productionImpl?has_content>
            <@displayImplementationShort productionImpl subprojectId visibility/>
             <br/>
             <b>Documentation :</b><br/>
             <@formatTextToHtml productionImpl['impl:documentation']/>
             <p/>
          
             <#-- @displayDocShort actualImpl/ -->
             <@displayImplementationDetail productionImpl/>
             <p/>
          <#else>
            (aucun)
          </#if>
          <p/>
        </#if>
        <p/>
        
        <#if isUserConsumer>
          Vos implémentations de test en tant que <b>consommateur</b> :
          <#list userConsumerImpls as userConsumerImpl>
             <@displayImplementationShort userConsumerImpl subprojectId visibility/>
             <br/>
             <b>Documentation :</b><br/>
             <@formatTextToHtml serviceimpl['impl:documentation']/>
             <p/>
          
             <#-- @displayDocShort actualImpl/ -->
             <@displayImplementationDetail serviceimpl/>
             <p/>
          </#list>
        </#if>
        <p/>
		
		<#if actualImpls?has_content && actualImpls?size != 0>
		<b>Autres implémentations :</b><br/>
		<#list actualImpls as actualImpl>
		   <#if (!productionImpl?has_content || productionImpl.id != actualImpl.id) && (!serviceimpl?has_content || serviceimpl.id != actualImpl.id)>
		   
          <b>Documentation :</b><br/>
          <@formatTextToHtml actualImpl['impl:documentation']/>
          <p/>
          
          <#-- @displayDocShort actualImpl/ -->
          <@displayImplementationDetail actualImpl/>
          <p/>
           
           </#if>
		</#list>
		</p>
		<@displayImplementationTools subprojectId visibility/>
		<p/>
		</#if>
		
      <#if mockImpls?has_content>
		<br/><b>Implémentations de test (mocks) :</b><br/>
      <#-- @displayDocs mockImpls shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/ -->
      <#-- @displayDocsShort mockImpls/ -->
		<#list mockImpls as mockImpl>
		  <#-- @displayDocShort mockImpl/ -->
        <@displayImplementationShort mockImpl subprojectId visibility/>
		</#list>
      <p/>
      </#if>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

		<h3>Services déployés<#-- (endpoints) --></h3>
		
        <#if endpoint?has_content>
          Cet endpoint est déployé en <b>${endpoint['env:environment']}</b> à
          <a href="${endpoint['endp:url']}">${endpoint['endp:url']}</a>
          par <@displayEndpointTitle endpoint subprojectId visibility/>
          <br/>
          Description: <@formatTextToHtml endpoint['dc:description']/>
          <p/>
          <@displayEndpointTools endpoint subprojectId visibility/>
          <p/>
          <@displayInterfaceResource endpoint/>
          <p/>
        </#if>
        
        <#if !endpoint?has_content || endpoint['env:environment'] != 'Production'>
          Déployé en <b>Production</b> à
          <#if productionEndpoint?has_content>
              <a href="${productionEndpoint['endp:url']}">${productionEndpoint['endp:url']}</a>
              par <@displayEndpointShort productionEndpoint subprojectId visibility/>
              <br/>
             Description: <@formatTextToHtml productionEndpoint['dc:description']/>
             <p/>
             <@displayEndpointTools productionEndpoint subprojectId visibility/>
             <p/>
             <@displayInterfaceResource productionEndpoint/>
             <p/>
          <#else>
             (aucun)
          </#if>
          <p/>
        </#if>
      
        <#if productionImpl?has_content>
            <@displayProductionImplementationDetail productionImpl/>
            <p/>
        </#if>
        
        <#if isUserConsumer>
          <b>Vos endpoints de test :</b><br/>
          <#list userConsumerEndpoints as userConsumerEndpoint>
            <@displayEndpointShort userConsumerEndpoint subprojectId visibility/>
            <br/>
            Description: <@formatTextToHtml endpoint['dc:description']/>
            <p/>
            <@displayInterfaceResource userConsumerEndpoint/>
            <p/>
            <@displayEndpointTools userConsumerEndpoint subprojectId visibility/>
            <br/>
          </#list>
          <p/>
        </#if>
		
        <#if actualEndpoints?has_content>
        <b>Autres déploiements :</b><br/>
		  <#-- @displayDocsShort actualEndpoints/ -->
        <#-- @displayDocs actualEndpoints shortDocumentPropNames + endpointPropNames/ -->
		  <#list actualEndpoints as actualEndpoint>
            <#if (!productionEndpoint?has_content || productionEndpoint.id != actualEndpoint.id) && (!endpoint?has_content || endpoint.id != actualEndpoint.id)>
                <@displayEndpointShort actualEndpoint subprojectId visibility/>
                Description: <@formatTextToHtml actualEndpoint['dc:description']/>
                <p/>
                <#-- @displayInterfaceResource service/>
                <p/>
                <@displayEndpointTools actualEndpoint subprojectId visibility/ -->
                <#-- br/>
                <@displayDoc actualEndpoint shortDocumentPropNames + endpointPropNames/>
                <p/ -->
            </#if>
        </#list>
        <p/>
        </#if>
        
        <#if userConsumerEndpoints?has_content>
            <b>Vos déploiements de test :</b><br/>
            <#-- @displayDocsShort userConsumerEndpoints/ -->
            <#list userConsumerEndpoints as userConsumerEndpoint>
                <@displayEndpointShort userConsumerEndpoint subprojectId visibility/>
                Description: <@formatTextToHtml userConsumerEndpoint['dc:description']/>
                <p/>
                <@displayInterfaceResource userConsumerEndpoint/>
                <#-- p/>
                <@displayEndpointTools userConsumerEndpoint subprojectId visibility/ -->
                <#-- br/>
                <@displayDoc userConsumerEndpoint shortDocumentPropNames + endpointPropNames/>
                <p/ -->
            </#list>
        <#elseif mockEndpoints?has_content>
            <b>Déploiements de test :</b><br/>
            <#list mockEndpoints as mockEndpoint>
                <@displayEndpointShort mockEndpoint subprojectId visibility/>
                Description: <@formatTextToHtml userConsumerEndpoint['dc:description']/>
                <p/>
                <#-- @displayInterfaceResource mockEndpoint/>
                <p/>
                <@displayEndpointTools mockEndpoint subprojectId visibility/ -->
                <#-- br/>
                <@displayDoc mockEndpoint shortDocumentPropNames + endpointPropNames/>
                <p/ -->
            </#list>
        </#if>
        
        <!-- TODO LATER endpoint consumptions (like service ones) -->


            </div>
         </li>

         <@displayReturnToIndexButtonBar/>


        <#if Root.isDevModeSet()>

        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>
        
		<h3>test</h3>
		
		<h4>User</h4>
		(actual) user name & company : ${Context.getPrincipal().getName()} ${Context.getPrincipal().getCompany()}<br/>
        (test) user & groups : ${user.getName()} '<#list user.getGroups() as groupName>${groupName}, </#list>)<br/>
        isUserConsumer : ${isUserConsumer?string} (${userConsumerGroupName})<br/>
		isUserProvider : ${isUserProvider?string} (${userConsumerGroupName})<br/>
        isUserBusinessAnalyst : ${isUserBusinessAnalyst?string} (${userConsumerGroupName})<br/>
        isUserDeveloper : ${isUserDeveloper?string} (${userConsumerGroupName})<br/>
        isUserOperator : ${isUserOperator?string} (${userConsumerGroupName})<br/>
        Choose other test user :
        <#assign otherUsers = Root.getUserManager().getUserIds()/>
        <#list otherUsers as otherUser>
           <#assign requestQuery = Context.getRequest().getQueryString()?replace('&testUser=' + user.getName(), '')/>
           <a href="${Context.getURL()}?${requestQuery}&testUser=${otherUser}">${otherUser}</a>
           <#if otherUser_index != otherUsers?size - 1>, </#if>
        </#list>

		<h4>Contenu dans</h4>
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
		
		
		<h4>UI Draft</h4>
		
        <h4>1. Display focus part according to role</h4>
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



        <h4>2. Display all global parts, and expand one (some) according to profile (role ??)</h4>


            </div>
         </li>
         
        
        </#if>
   
   
      </ul>
   </div>

</body>

</html>