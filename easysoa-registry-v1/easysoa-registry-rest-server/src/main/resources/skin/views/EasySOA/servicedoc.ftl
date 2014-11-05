<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">
<head>
   <title>EasySOA ${Root.msg("cartography")} - ${Root.msg("serviceBrowsing")}</title>
    <@includeResources/>
	<style type="text/css">
	  .clickable:hover { cursor: pointer; background-color: #FFC; }
	  .id { display: none }
	  .selected { background-color: #CFC; }
	</style>
</head>

<body>
    <#include "/views/EasySOA/docMacros.ftl">
    <#include "/views/EasySOA/soaMacros.ftl">

    <div id="header">
        <div id="headerContents">
            <@headerContentsDefault/>
            EasySOA ${Root.msg("cartography")} - ${Root.msg("Documentation")}
        </div>
    </div>
    <br/>
    <div class="container" id="container">
        <ul class="thumbnails">
            <li class="span12">
                <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

      <#if endpoint?has_content>
          <h3>${Root.msg("Endpoint")} <@displayEndpointTitle endpoint subprojectId visibility/>
          <@linkToLocalNuxeoDocumentsUi endpoint/>
          </h3>
      <#elseif serviceimpl?has_content>
          <h3>${Root.msg("Implementation")} <@displayImplementationTitle serviceimpl subprojectId visibility/>
          <@linkToLocalNuxeoDocumentsUi serviceimpl/>
          </h3>
      <#else>
		    <h3>${Root.msg("Definition")} <@displayServiceTitle service true subprojectId visibility/>
          <@linkToLocalNuxeoDocumentsUi service/>
		    </h3>
		</#if>


      <#-- h3>description</h3 -->
      <#if service?has_content>
          <@displayDocumentation service['dc:description']/>
          <#assign parsedServiceSubprojectId = Root.parseSubprojectId(service['spnode:subproject'])/>
      <#elseif serviceimpl?has_content>
          <@displayDocumentation serviceimpl['dc:description']/>
      <#else>
          <@displayDocumentation endpoint['dc:description']/>
      </#if>
		<p/>


      <#if !consumerActor?has_content><#assign consumerActor = ""/></#if>
      <#if !providerActor?has_content><#assign providerActor = ""/></#if>
      <#if !component?has_content><#assign component = ""/></#if>

            </div>
         </li>


      <#if service?has_content && parsedServiceSubprojectId.getSubprojectName() = 'Specifications'>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>


		<h3>${Root.msg("Business")}</h3><!-- ((sert à)) -->

		<#if businessService?has_content>
          <#-- if businessService['businessservice:consumerActor']?has_content>
            <#assign bsConsumerActor = Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', businessService['businessservice:consumerActor']))/>
          </#if -->
		    ${Root.msg("ProvidesBusinessService")} <@displaySoaNodeLink businessService ""/> :

          <#if !serviceimpl?has_content && !endpoint?has_content || isUserBusinessAnalyst>
          <br/>
           &nbsp;&nbsp;&nbsp;<@displaySoaNodeProp consumerActor 'Acteur consommateur' businessService/>
           <br/>
           &nbsp;&nbsp;&nbsp;<@displaySoaNodeProp providerActor 'Acteur fournisseur' businessService/>
           <br/>
           &nbsp;&nbsp;&nbsp;<@displaySoaNodeChildrenShort businessService, 'SLA', 'RequirementsDocument'/>
           <br/>
           &nbsp;&nbsp;&nbsp;<@displaySoaNodeChildrenShort businessService, 'OLA', 'RequirementsDocument'/>
           <br/>
           &nbsp;&nbsp;&nbsp;${Root.msg("BusinessSpecificationDocuments")} :
           <#-- assign businessServiceRef = new_f('org.nuxeo.ecm.core.api.IdRef', service['iserv:linkedBusinessService'])/ -->
           <@displayChildrenShort businessService 'Document'/>
           </#if>
		<#else>
		   ${Root.msg("IntermediateServiceNotBusiness")}
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

		<h3>${Root.msg("Service")}</h3>
		<p/>


      <#if !service?has_content>
          ${Root.msg("NoLinkedService")}.
      <#else>


      <#if serviceimpl?has_content || endpoint?has_content>
          ${Root.msg("LinkedServiceDefinitionIs")} <@displayServiceShort service subprojectId visibility/>.
      <#else>
          ${Root.msg("ThisServiceIs")} <@displayServiceTitle service false subprojectId visibility/>.
      </#if>
      <p/>

      <#-- h3>Interface(s)</h3 -->
      <@displayResources service/>
      <p/>

		<#if parsedSubprojectIdToDisplay.getSubprojectName() = 'Specifications'>

          <!-- TODO CONFIGURATION & LISTALL -->
          ${Root.msg("OperationalRequirementsFormalDefinitions")}
          <@displaySoaNodeChildrenShort service, 'OLA', 'RequirementsDocument'/>
          <p/>

		    <#if !businessService?has_content>
              <@displaySoaNodeProp providerActor 'Acteur fournisseur' service/>
              <p/>
          </#if>

          <@displaySoaNodeProp component 'Component' service/>
          <p/>

      <#else>
          ${Root.msg("TechnicalServiceOutOfDevelopment")}
      </#if>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

		<h3>${Root.msg("Documentation")}</h3>

		<h4>${Root.msg("Documentation")} - ${Root.msg("extracted")} :</h4>
		(${Root.msg("outOfDefinitionAndActualImplementation")})
		<#--
		du WSDL :
		<p/>
		<@displayProp service 'iserv:operations'/>
		<p/>
		de l'impl (first (?) non-test one) :
		<p/>
		<#if actualImpls?has_content && actualImpls?size != 0>
		${actualImpls[0]['impl:documentation']} TODO !!!!!!!!!!!!!!!!!!!!!!!
		<@displayProp actualImpls[0] 'impl:operations'/>
		<#if actualImpls?size != 1>
		<br/>(other implementations...)
		</#if>
		</#if>
		<p/>
		-->

      <#if serviceimpl?has_content>
         <#assign impl_operations=serviceimpl['impl:operations']><#-- or after productionImpl ? -->
      <#elseif productionImpl?has_content>
         <#assign impl_operations=productionImpl['impl:operations']>
		<#elseif actualImpls?has_content && actualImpls?size != 0>
		   <#assign impl_operations=actualImpls[0]['impl:operations']>
		<#else>
		   <#assign impl_operations="none">
		</#if>
      <@displayOperations service['iserv:operations'] impl_operations/>
		<p/>

		<!-- TODO LATER OPT du WSDL de l'endpoint OR BELOW ON ENDPOINT :<p/ -->


		<h4>${Root.msg("Documentation")} - ${Root.msg("manual")} :</h4>
		<!-- lister les non-Resources (or SoaNodes) joints ou fils du InformationService, ou sinon du BusinessService -->
		${Root.msg("Documents")} (${Root.msg("attachedAndChildren")}) :<br/>
      <@displayNonSoaFilesAndDocuments service/>
		<#-- OBSOLETE
		@displayFiles service['files:files']/>
      <br/>
      <@displayChildrenShort service 'Document'/ -->
      <p/>

      <h4>${Root.msg("ExamplesOfCallingIt")} :</h4>
      (${Root.msg("none")})
      <!-- TODO -->

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

		<h3>${Root.msg("Uses")}</h3>
		<!-- (applications : le déployant ; architecture : le consommant)<p/ -->

      <b>${Root.msg("UsedBy")} :</b>
      <p/>
      <!-- bsConsumerActor (ses Components et evt leurs Applications) -->
      <i>${Root.msg("byConsumingActorsAndTheirComponents")} :</i>
      <br/>
      <#if consumerActor?has_content>
          &nbsp;&nbsp;-&nbsp;${Root.msg("TheActor")} <@displayActorLink consumerActor/>
          ${Root.msg("thatProvidesComponents")}
          <#assign consumerServices = Root.getDocumentService().getInformationServicesProvidedBy(consumerActor, subprojectId)/>
          <#if consumerServices?size = 0>
              (${Root.msg("none")})
          <#else>
              <#list consumerServices as consumerService>
                 <#if consumerService['acomp:componentId']?has_content>
                     <@displayComponentLink Session.getDocument(new_f('org.nuxeo.ecm.core.api.IdRef', service['acomp:componentId']))/>
                 <#else>
                     ${Root.msg("notKnownByService")} <@displayServiceTitle consumerService true subprojectId visibility/>
                 </#if>
                 <#if consumerService_index != consumerServices?size - 1>, </#if>
              </#list>
          </#if>
      </#if>
      <p/>
      <i>${Root.msg("byDeploymentsAndTheirServers")} :</i>
      <br/>
      <!-- EndpointConsumptions (son host voire l'application des endpoints déployés dessus / son providerActor) consommant son endpoint Production (sinon Staging...) et sinon un non mock -->
      <#if productionEndpoint?has_content><!-- TODO if endpoint != null -->
          <#assign ecs = Root.getDocumentService().getSoaNodeParents(productionEndpoint, 'EndpointConsumption')/>
      <#elseif endpoint?has_content && endpoint['impl:ismock'] != 'true'>
          <#assign ecs = Root.getDocumentService().getSoaNodeParents(endpoint, 'EndpointConsumption')/>
      <#elseif actualEndpoints?has_content && actualEndpoints?size != 0>
          <#assign ecs = Root.getDocumentService().getSoaNodeParents(actualEndpoints[0], 'EndpointConsumption')/>
      </#if>
      <#if ecs?has_content && ecs?size != 0>
          <#list ecs as ec>
              &nbsp;&nbsp;-&nbsp;<@displayEndpointConsumptionLink ec/>
              <br/>
          </#list>
      <#else>
          (${Root.msg("none")})
      </#if>
      <p/>
      <i>${Root.msg("lastACompatibleServiceIsConsumedByImplementationsAndTheirApplications")} :</i>
      <br/>
      <!-- candidats JavaServiceConsumptions non de test (sa classe / délivérable / application / providerActor matchant) consommant son interface -->
      <#assign javaSCs = Root.getDocumentService().getJavaServiceConsumptions(service, subprojectId)/>
      <#if javaSCs?has_content && javaSCs?size != 0>
          <#list javaSCs as javaSC>
              <#if javaSC['sc:isTest'] != 'true'>
              <#assign javaSCDel = Root.getDocumentService().getSoaNodeParent(javaSC, 'Deliverable')/>
              &nbsp;&nbsp;-&nbsp;<a href="<@urlToLocalNuxeoDocumentsUi javaSCDel/>"/><@displayDeliverableTitle javaSCDel subprojectId visibility/></a>
              (${Root.msg("by")} <a href="<@urlToLocalNuxeoDocumentsUi javaSC/>">${Root.msg("injectionInClass")} ${javaSC['javasc:consumerClass']}</a>)
              <br/>
              </#if>
          </#list>
      <#else>
          (${Root.msg("none")})
      </#if>
      <p/>
      &nbsp;<p/>
      
      <#-- IntelligentSystems tagging it, since only Applications from now NOOO ALSO Folders : -->
      <#-- b>Applications :</b><br/ -->
      <b>${Root.msg("FiledInClassifications")} :</b><br/>
      <#if service['proxies']?has_content && service['proxies']?size != 0>
      <ul>
      <#list service['proxies'] as serviceProxy>
         <#if serviceProxy['parent'].type = 'IntelligentSystem'>
               <li><@displayDocShort serviceProxy/></li>
         </#if>
      </#list>
      </ul>
      <#else>
      (${Root.msg("none")}).
      </#if>

      <#-- TaggingFolder tagging it, since only Applications from now : -->
      <#-- br/><b>Business Processes :</b><br/ -->
      <br/><b>${Root.msg("TaggedIn")} :</b><br/>
      <#assign isTagged=false/> 
      <#if service['proxies']?has_content>
      <ul>
      <#list service['proxies'] as serviceProxy>
         <#if serviceProxy['parent'].facets?seq_contains('SoaNode')><#-- serviceProxy['parent'].type = 'TaggingFolder' -->
               <#assign isTagged=true/> 
               <li><@displayTagShort serviceProxy['parent'] subprojectId visibility/></li>
         </#if>
      </#list>
      </ul>
      <#if !isTagged>
      ${Root.msg("none")}.
      </#if>
      </#if>
      <p/>
      
      <a class="btn" href="${Root.path}/path${service['spnode:subproject']?xml}:${service['soan:name']?xml}/tags?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("TagAlsoIn")}...</a>

      <#-- br/>(autres tags) --><!-- TODO -->

      <p/>


            </div>
         </li>



      </#if>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

		<h3>${Root.msg("ServiceImplementations")}<#-- (y compris de test) --></h3>
		<#-- et consomme, dépend de (en mode non test) <br/ -->

        <#if serviceimpl?has_content>
          <div style="margin-bottom:30px;">
          ${Root.msg("ThisImplementation")}
          <#if serviceimpl['impl:ismock'] = 'true'><b>de test</b></#if>
          <#if productionImpl?has_content && productionImpl.id = serviceimpl.id><b>${Root.msg("in")} Production</b></#if>
          ${Root.msg("is")}
          <@displayImplementationShort serviceimpl subprojectId visibility/>
          <@displayDocumentation serviceimpl['impl:documentation']/>
          <p/>

          <#-- @displayDocShort actualImpl/ -->
          <@displayImplementationDetail serviceimpl/>
          <p/>
          <@displayImplementationTools subprojectId visibility/>
          </div>
		  </#if>

		  <#-- Production as default preferred deployment ; TODO others according to role -->
        <#if !serviceimpl?has_content || productionImpl?has_content && productionImpl.id != serviceimpl.id>
          <div style="margin-bottom:30px;">
          ${Root.msg("ImplementedIn")} <b>Production</b> par
          <#if productionImpl?has_content>
             <@displayImplementationShort productionImpl subprojectId visibility/>
             <@displayDocumentation productionImpl['impl:documentation']/>
             <p/>

             <#-- @displayDocShort actualImpl/ -->
             <@displayImplementationDetail productionImpl/>
             <p/>
             <@displayImplementationTools subprojectId visibility/>
             <p/>
          <#else>
             (${Root.msg("none")})
          </#if>
          </div>
        </#if>
        <p/>

        <#if (isUserConsumer || !isUserProvider) && !(serviceimpl?has_content && serviceimpl['impl:ismock'] = 'true')>
          <div style="margin-bottom:30px;">
          <#if isUserConsumer>
          <b>${Root.msg("AsAConsumer")}</b>, ${Root.msg("yourTestImplementationsAre")} :
          <#elseif !isUserProvider>
          <b>${Root.msg("SinceYoureNotProvider")}</b>, ${Root.msg("youMayUseTestImplementations")} :
          </#if>
          <br/>
          <#list userConsumerImpls as userConsumerImpl>
             &nbsp;&nbsp;-&nbsp;<@displayImplementationShort userConsumerImpl subprojectId visibility/>
             <@displayDocumentation userConsumerImpl['impl:documentation']/>
             <p/>

             <#-- @displayDocShort actualImpl/ -->
             <@displayImplementationDetail userConsumerImpl/>
             <p/>
             <@displayImplementationTools subprojectId visibility/>
             <p/>
          </#list>
          </div>
        </#if>
        <p/>

		<#if actualImpls?has_content && actualImpls?size != 0>
		<b>${Root.msg("OtherImplementations")} :</b><br/>
		<#list actualImpls as actualImpl>
		   <#if (!productionImpl?has_content || productionImpl.id != actualImpl.id) && (!serviceimpl?has_content || serviceimpl.id != actualImpl.id)>

		   &nbsp;&nbsp;-&nbsp;<@displayImplementationShort actualImpl subprojectId visibility/>
		   <@displayDocumentation actualImpl['impl:documentation']/>
          <p/>

          <#-- @displayDocShort actualImpl/ -->
          <@displayImplementationDetail actualImpl/>
          <p/>
          <@displayImplementationTools subprojectId visibility/>
          <p/>

           </#if>
		</#list>
		</p>
		</#if>

      <#if mockImpls?has_content && !(isUserConsumer || !isUserProvider)>
		<br/><b>${Root.msg("OtherTestMockImplementations")} :</b><br/>
      <#-- @displayDocs mockImpls shortDocumentPropNames + serviceImplementationPropNames + deliverableTypePropNames/ -->
      <#-- @displayDocsShort mockImpls/ -->
		<#list mockImpls as mockImpl>
		   <#if !serviceimpl?has_content || serviceimpl.id != mockImpl.id>
		      <#-- @displayDocShort mockImpl/ -->
            &nbsp;&nbsp;-&nbsp;<@displayImplementationShort mockImpl subprojectId visibility/>
            <p/>
         </#if>
		</#list>
      <p/>
      </#if>

            </div>
         </li>


         <li class="span12">
            <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

		<h3>${Root.msg("Endpoints")}<#-- (endpoints) --></h3>

        <#if endpoint?has_content>
          <div style="margin-bottom:30px;">
          ${Root.msg("ThisDeployedServiceIn")} <b>${endpoint['env:environment']}</b> à
          <a href="${endpoint['endp:url']}">${endpoint['endp:url']}</a>
          <br/>
          ${Root.msg("by")} <@displayEndpointTitle endpoint subprojectId visibility/>
          <@displayDocumentation endpoint['dc:description']/>
          <p/>
          <@displayEndpointTools endpoint subprojectId visibility/>
          <p/>
          <@displayResources endpoint/>
          <p/>
          <#if endpoint['env:environment'] = 'Production' && productionImpl?has_content>
              <@displayProductionImplementationDetail productionImpl/>
              <p/>
          </#if>
          </div>
        </#if>

        <#if !endpoint?has_content || endpoint['env:environment'] != 'Production'>
          <div style="margin-bottom:30px;">
          ${Root.msg("deployedIn")} <b>Production</b> à
          <#if productionEndpoint?has_content>
             <a href="${productionEndpoint['endp:url']}">${productionEndpoint['endp:url']}</a>
             <br/>
             ${Root.msg("by")} <@displayEndpointShort productionEndpoint subprojectId visibility/>
             <@displayDocumentation productionEndpoint['dc:description']/>
             <p/>
             <@displayEndpointTools productionEndpoint subprojectId visibility/>
             <p/>
             <@displayResources productionEndpoint/>
             <p/>
             <#if productionImpl?has_content>
                 <@displayProductionImplementationDetail productionImpl/>
                 <p/>
             </#if>
          <#else>
             (${Root.msg("none")})
          </#if>
          </div>
        </#if>

        <#if (isUserConsumer || !isUserProvider) && !(serviceimpl?has_content && serviceimpl['impl:ismock'] = 'true')>
          <div style="margin-bottom:30px;">
          <#if isUserConsumer>
          ${Root.msg("AsA")} <b>${Root.msg("consumer")}</b>, ${Root.msg("yourTestDeploymentsAre")} :
          <#elseif !isUserProvider>
          ${Root.msg("SinceYoureNot")} <b>${Root.msg("notProvider")}</b>, ${Root.msg("youMayUseTestDeployments")} :
          </#if>
          <br/>
          <#list userConsumerEndpoints as userConsumerEndpoint>
            &nbsp;&nbsp;-&nbsp;<@displayEndpointShort userConsumerEndpoint subprojectId visibility/>
            <@displayDocumentation userConsumerEndpoint['dc:description']/>
            <p/>
            <@displayResources userConsumerEndpoint/>
            <p/>
            <@displayEndpointTools userConsumerEndpoint subprojectId visibility/>
            <br/>
          </#list>
          </div>
        </#if>

        <#if actualEndpoints?has_content>
        <b>${Root.msg("OtherDeployments")} :</b><p/>
		  <#-- @displayDocsShort actualEndpoints/ -->
        <#-- @displayDocs actualEndpoints shortDocumentPropNames + endpointPropNames/ -->
		  <#list actualEndpoints as actualEndpoint>
            <#if (!productionEndpoint?has_content || productionEndpoint.id != actualEndpoint.id) && (!endpoint?has_content || endpoint.id != actualEndpoint.id)>
                &nbsp;&nbsp;-&nbsp;<@displayEndpointShort actualEndpoint subprojectId visibility/>
                <#-- @displayDocumentation actualEndpoint['dc:description']/>
                <p/>
                <@displayResources actualEndpoint/>
                <p/>
                <@displayEndpointTools actualEndpoint subprojectId visibility/ -->
                <#-- br/>
                <@displayDoc actualEndpoint shortDocumentPropNames + endpointPropNames/ -->
                <p/>
            </#if>
        </#list>
        <p/>
        </#if>

        <#if userConsumerEndpoints?has_content && !(serviceimpl?has_content && serviceimpl['impl:ismock'] = 'true')>
            <div style="margin-bottom:30px;">
            <b>${Root.msg("YourTestDeploymentsAsAConsumer")} :</b><br/>
            <#-- @displayDocsShort userConsumerEndpoints/ -->
            <#list userConsumerEndpoints as userConsumerEndpoint>
                &nbsp;&nbsp;-&nbsp;<@displayEndpointShort userConsumerEndpoint subprojectId visibility/>
                <@displayDocumentation userConsumerEndpoint['dc:description']/>
                <p/>
                <@displayResources userConsumerEndpoint/>
                <#-- p/>
                <@displayEndpointTools userConsumerEndpoint subprojectId visibility/ -->
                <#-- br/>
                <@displayDoc userConsumerEndpoint shortDocumentPropNames + endpointPropNames/>
                <p/ -->
            </#list>
            </div>
        <#elseif mockEndpoints?has_content && !(isUserConsumer || !isUserProvider)>
            <div style="margin-bottom:30px;">
            <b>${Root.msg("TestDeployments")} :</b><br/>
            <#list mockEndpoints as mockEndpoint>
                &nbsp;&nbsp;-&nbsp;<@displayEndpointShort mockEndpoint subprojectId visibility/>
                <#-- @displayDocumentation userConsumerEndpoint['dc:description']/>
                <p/>
                <@displayResources mockEndpoint/>
                <p/>
                <@displayEndpointTools mockEndpoint subprojectId visibility/ -->
                <#-- br/>
                <@displayDoc mockEndpoint shortDocumentPropNames + endpointPropNames/ -->
                <p/>
            </#list>
            </div>
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



      <#if endpoint?has_content>
          <#assign doc=endpoint/>
      <#elseif serviceimpl?has_content>
          <#assign doc=serviceimpl/>
      <#else>
          <#assign doc=service/>
      </#if>
		<h4>Contenu dans</h4>
		<#if doc['proxies']?has_content><#list doc['proxies'] as proxy><@displayDocShort proxy['parent']/> ; </#list><p/></#if>

		<h3>Contient</h3>
		<#if doc['children']?has_content><@displayDocsShort doc['children']/><p/></#if>

		<h3>log : all props</h3>

		<@displayDoc doc/>
		<p/>
		&nbsp;<p/>

		
		
      <#if service?has_content>
		<b>Information Service:</b>
		<p/>
		<@displayPropsAll service informationServicePropNames/>
		<p/>
      Resource :
      <p/>
      <@displayPropsAll service resourcePropNames/>
      <p/>
      WsdlInfo :
      <p/>
      <@displayPropsAll service wsdlInfoPropNames/>
      <p/>
      RestInfo :
      <p/>
      <@displayPropsAll service restInfoPropNames/>
      <p/>
		Architecture Component :
		<p/>
		<@displayPropsAll service architectureComponentPropNames/>
		<p/>
		Platform :
		<p/>
		<@displayPropsAll service platformPropNames/>
		<p/>
      &nbsp;<p/>
      </#if>

		<#if serviceimpl?has_content>
      <b>Implementation :</b>
      <@displayPropsAll serviceimpl serviceImplementationPropNames/>
      <p/>
      WsdlInfo :
      <p/>
      <@displayPropsAll serviceimpl wsdlInfoPropNames/>
      <p/>
      RestInfo :
      <p/>
      <@displayPropsAll serviceimpl restInfoPropNames/>
      <p/>
		Deliverable type :
      <@displayPropsAll serviceimpl deliverableTypePropNames/>
		<p/>
		&nbsp;<p/>
		</#if>

      <#if endpoint?has_content>
      <b>Endpoint :</b>
      <p/>
      <@displayPropsAll endpoint endpointPropNames/>
      <p/>
      Resource :
      <p/>
      <@displayPropsAll endpoint resourcePropNames/>
      <p/>
      WsdlInfo :
      <p/>
      <@displayPropsAll endpoint wsdlInfoPropNames/>
      <p/>
      RestInfo :
      <p/>
      <@displayPropsAll endpoint restInfoPropNames/>
      <p/>
      &nbsp;<p/>
      </#if>


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