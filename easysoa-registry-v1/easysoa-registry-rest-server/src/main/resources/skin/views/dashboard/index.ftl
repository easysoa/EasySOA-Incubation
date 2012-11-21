<!DOCTYPE html>
<html>

<head>
	<title>EasySOA Matching dashboard</title>
	<meta charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="skin/favicon.ico" /> 
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery.js"></script>
	<style type="text/css">
	  .clickable:hover { cursor: pointer; background-color: #FFC; }
	  .id { display: none }
	  .selected { background-color: #CFC; }
	</style>
</head>
	
<body>

<div id="header">
	<div id="headerContents">
	    <div id="logoLink">&nbsp;</div>
    	<div id="headerUserBar"></div>
		EasySOA Matching dashboard
    </div>
</div>

<div id="container">
  <div id="selectedServiceImpl" style="display: none">${selectedServiceImpl}</div>

  <form action="/nuxeo/site/easysoa/dashboard/samples" method="post" style="position: absolute; right: 20px; top: 20px">
  	<input type="submit" value="Fill with samples" />
  </form>

  <#if servWithoutSpecs?has_content>
  <h1 style="color: red; border-color: red">Service implementations to classify</h1>
  
  <table style="width: 500px">
    <tr>
      <th style="background-color: #FDC">Service implementation</th>
    </tr>
    <#list servWithoutSpecs as servWithoutSpec>
      <tr>
      	<td class="clickable serviceImpl" id="${servWithoutSpec.id}">
      	  <div style="float: right">
      	  	<input class="components" type="button" value="Select component" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/components/${servWithoutSpec.id}'" />
      	  	<input class="suggestions" type="button" value="Get suggestions" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/suggest/${servWithoutSpec.id}'" />
      	  </div>
      	  <#assign document = servWithoutSpec>
      	  <#include "/views/dashboard/document.ftl">
      	  	
          <#if selectedServiceImpl == servWithoutSpec.id>
          <table style="width: 500px">
           <tr>
  	         <th style="background-color: #FFA">Suggested services <#if selectedComponentTitle??>from <i>${selectedComponentTitle}</i></#if></th>
	         </tr>
           <#if suggestions?has_content>
           <#list suggestions as suggestion>
              <tr>
              	<td class="clickable infoService">
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
	          <th style="background-color: #FFA">Suggested services <#if selectedComponentTitle??>from <i>${selectedComponentTitle}</i></#if> (any platform)</th>
	         </tr>
           <#if anyPlatformSuggestions?has_content>
             <#list anyPlatformSuggestions as suggestion>
                <tr>
                	<td class="clickable infoService">
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
          </table>
          </#if>
          
          <#if components?has_content && selectedServiceImpl == servWithoutSpec.id>
            <table style="width: 500px">
              <th style="background-color: #DDD">Components list</th>
              <#list components as component>
                <tr>
                	<td class="clickable component" onclick="window.location.href='/nuxeo/site/easysoa/dashboard/suggest/${selectedServiceImpl}/${component.id}'">
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
	  Click on an a service implementation and an information service without impl, then click:<br />
	  <input type="submit" value="Create a link" />
	  <input id="infoServiceId" name="infoServiceId" type="hidden" />
	  <input id="serviceImplId" name="serviceImplId" type="hidden" />
  </fieldset>    
  </form>
  
	</#if>
		
  <h1>My services</h1>
  
  <table>
    <tr>
	    <th>Information service</th>
	    <th></th>
	    <th>Service implementation</th>
    </tr>
  <#list matchedImpls as matchedImpl>
    <tr>
    	<td>
    	  <#assign document = infoServicesById[matchedImpl['impl:linkedInformationService']]>
    	  <#include "/views/dashboard/document.ftl">
    	</td>
    	<td><b>&gt;</b></td>
    	<td>
    	  <#if document['wsdl:wsdlPortTypeName'] != matchedImpl['wsdl:wsdlPortTypeName']>
      	  <form style="float: right" method="post">
      	  	<input type="submit" value="X" style="width: 30px" />
      	  	<input type="hidden" name="serviceImplId" value="${matchedImpl.id}" />
      	  </form>
    	  </#if>
    	  <#assign document = matchedImpl>
    	  <#include "/views/dashboard/document.ftl">
    	</td>
    </tr>
  </#list>
  <#list unimplementedServs as unimplementedServ>
    <tr>
    	<td class="clickable infoService">
    	  <#assign document = unimplementedServ>
    	  <#include "/views/dashboard/document.ftl">
    	</td>
    	<td>x</td>
    	<td></td>
    </tr>
  </#list>
  </table>
    
  <script>
  $(document).ready(function() {
  
  function getId($el) {
    return $('.id', $el).html();
  }
  
  $('.clickable').click(function() {
    var $el = $(this);
    if ($el.hasClass('infoService')) {
      $('.infoService').removeClass('selected');
      $el.addClass('selected');
      $('#infoServiceId').attr('value', getId($el));
    }
    else {
      $('.serviceImpl').removeClass('selected');
      $el.addClass('selected');
      $('#serviceImplId').attr('value', getId($el));
    }
  });
  
  });
  
  // FIXME
  /*var selectedServiceImpl = $('#selectedServiceImpl').html();
  if (selectedServiceImpl != '') {
    $('#' + selectedServiceImpl).click();
  }*/
  </script>
    
</div>
  
</body>

</html>