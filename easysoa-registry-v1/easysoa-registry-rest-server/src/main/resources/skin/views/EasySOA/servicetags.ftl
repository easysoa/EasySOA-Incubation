<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Cartographie - Fiche Service</title>
        <@includeResources/>
        <style type="text/css">
          .clickable:hover { cursor: pointer; background-color: #FFC; }
          .id { display: none }
          .selected { background-color: #CFC; }
        </style>
    </head>

    <body>

        <#include "/views/EasySOA/docMacros.ftl">
	<div id="header">
		<div id="headerContents">
         <@headerContentsDefault/>
			EasySOA REST Services Documentation
	    </div>
	</div>

    <br/>
    <div class="container" id="container">
        <ul class="thumbnails">
            <li class="span12">
                <div class="thumbnail">
               <img data-src="holder.js/300x200" alt=""></img>

		<h3>Service</h3>
		<@displayServiceShort service subprojectId visibility/>

		<h3>Taggé dans</h3>

		<#-- compute currentTagIds for later "not yet used tags" display : -->
		<#assign currentTagIds=[]/>
		<#if service['proxies']?has_content>
        <#list service['proxies'] as serviceProxy>
          &nbsp;&nbsp;-
            <#-- in Specifications, can also be : IntelligentSystem, BusinessService, BusinessService, Folder  -->
            <#if serviceProxy['parent'].facets?seq_contains('SoaNode')><#-- serviceProxy['parent'].type = 'TaggingFolder' -->
                    <@displayTagShort serviceProxy['parent'] subprojectId visibility/>
            <#else>
                    <@displayDocShort serviceProxy['parent']/><#-- TODO display in this case ?? -->
            </#if>
             -
               <form style="display:inline;" method="POST" action="${Root.path}/proxy/${serviceProxy.id}?subprojectId=${subprojectId}&visibility=${visibility}">
						<input name="delete" type="hidden" value=""/>
						<a href="##" onClick="this.parentNode.submit();">Détagger</a>
					</form>
					<#assign currentTagIds=currentTagIds+[serviceProxy['parent'].id]/>
			  <br/>
		  </#list>
		</#if>

		<h3>Tagger aussi dans...</h3>

		<#list tags as tag>
			<#if !currentTagIds?seq_contains(tag.id)>
			&nbsp;&nbsp;-
			<form style="display:inline;" method="POST" action="${Root.path}/path${service['spnode:subproject']?xml}:${service['soan:name']?xml}/tags?subprojectId=${subprojectId}&visibility=${visibility}">
				<input name="tagId" type="hidden" value="${tag.id}"/>
				<a href="##" onClick="this.parentNode.submit();">Tagger</a> dans <@displayTagShort tag subprojectId visibility/>
			</form>
		   <br/>
			</#if>
		</#list>

            </div>
         </li>

      </ul>
   </div>

</body>

</html>
