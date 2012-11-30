<#if unmatched?has_content>
<h1 style="color: red; border-color: red">${doctypeTitle}s to classify</h1>
  
<table style="width: 500px">
<tr>
  <th style="background-color: #FDC">${doctypeTitle}</th>
</tr>
<#list unmatched as unmatched>
  <tr>
  	<td class="clickable unmatchedModel" id="${unmatched.id}">
  	  <div style="float: right">
  	  	<input class="components" type="button" value="Select component" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/components/${unmatched.id}'" />
  	  	<input class="suggestions" type="button" value="Get suggestions" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/suggest/${unmatched.id}'" />
  	  </div>
  	  <#assign document = unmatched>
  	  <#include "/views/dashboard/document.ftl">
  	  	
      <#if suggestions?? && selectedModel == unmatched.id>
      <table style="width: 500px">
       <tr>
         <th style="background-color: #FFA">Suggested ${targetDoctypeTitle}s <#if selectedComponentTitle??>from <i>${selectedComponentTitle}</i></#if></th>
         </tr>
       <#if suggestions?has_content>
       <#list suggestions as suggestion>
          <tr>
          	<td class="clickable target">
          	  <#assign document = suggestion>
          	  <#include "/views/dashboard/document.ftl">
          	</td>
          </tr>
       </#list>
       <#else>
      	<td style="text-align: center; font-style: italic">
      	  No matches
      	</td>
       </#if>
       <tr>
          <th style="background-color: #FFA">Suggested ${targetDoctypeTitle} <#if selectedComponentTitle??>from <i>${selectedComponentTitle}</i></#if> (any platform)</th>
         </tr>
       <#if anyPlatformSuggestions?has_content>
         <#list anyPlatformSuggestions as suggestion>
            <tr>
            	<td class="clickable target">
            	  <#assign document = suggestion>
            	  <#include "/views/dashboard/document.ftl">
            	</td>
            </tr>
         </#list>
       <#elseif anyPlatformSuggestionsCount??>
      	<td style="text-align: center">
      	  ${anyPlatformSuggestionsCount} additional matches
      	</td>
       <#else>
      	<td style="text-align: center; font-style: italic">
      	  No matches
      	</td>
       </#if>
       <#if allFromComponent?? && allFromComponent?has_content>
         <tr>
          <th style="background-color: #FFA">All ${targetDoctypeTitle}s from <i>${selectedComponentTitle}</i></th>
         </tr>
         <#list allFromComponent as suggestion>
            <tr>
            	<td class="clickable target">
            	  <#assign document = suggestion>
            	  <#include "/views/dashboard/document.ftl">
            	</td>
            </tr>
         </#list>
       </#if>
      </table>
      </#if>
      
      <#if components?has_content && selectedModel == unmatched.id>
        <table style="width: 500px">
          <th style="background-color: #DDD">Components list</th>
          <#list components as component>
            <tr>
            	<td class="clickable component" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/suggest/${selectedModel}/${component.id}'">
            	  <#assign document = component>
            	  <#include "/views/dashboard/document.ftl">
            	</td>
            </tr>
          </#list>
        </table>
      </#if>
  	</td>
  </tr>
</#list>
</table>
	
<form action="/nuxeo/site/easysoa/dashboard" method="post" style="float: left; width: 100%; margin-top: 10px">
	<fieldset style="width: 400px; padding: 10px;">
	Click on an a ${doctypeTitle} and a ${targetDoctypeTitle}, then click:<br />
	<input type="submit" value="Create a link" />
	<input id="unmatchedModelId" name="unmatchedModelId" type="hidden" />
	<input id="targetId" name="targetId" type="hidden" />
	</fieldset>    
</form>
  
</#if>