<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Cartographie - Fiche Service/title>
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
		    <div id="logoLink">&nbsp;</div>
	    	<div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
            <div id="headerContextBar">
                <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
            </div>
			EasySOA REST Services Documentation
	    </div>
	</div>

	<div id="container">

		<h1>Service</h1>
		<@displayServiceShort service subprojectId visibility/>

		<h2>Tagged in</h2>

		<#-- compute currentTagIds for later "not yet used tags" display : -->
		<#assign currentTagIds=[]/>
		<#if service['proxies']?has_content>
		<ul>
        <#list service['proxies'] as serviceProxy>
                    <li>
            <#-- in Specifications, can also be : IntelligentSystem, BusinessService, BusinessService, Folder  -->
            <#if serviceProxy['parent'].facets?seq_contains('SoaNode')><#-- serviceProxy['parent'].type = 'TaggingFolder' -->
                    <@displayTagShort serviceProxy['parent'] subprojectId visibility/>
            <#else>
                    <@displayDocShort serviceProxy['parent']/><#-- TODO display in this case ?? -->
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
