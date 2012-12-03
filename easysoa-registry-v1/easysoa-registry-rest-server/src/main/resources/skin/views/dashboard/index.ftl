<!DOCTYPE html>
<html>

<head>
	<title>EasySOA Matching dashboard</title>
	<meta charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
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
  
  <#assign doctypeTitle = "Endpoint">
  <#assign targetDoctypeTitle = "implementation">
  <#assign unmatched = endpointWithoutImpl>
  <#include "/views/dashboard/matching.ftl">
  
  <#assign doctypeTitle = "Service implementation">
  <#assign targetDoctypeTitle = "service">
  <#assign unmatched = servWithoutSpecs>
  <#include "/views/dashboard/matching.ftl">
		
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
    if ($el.hasClass('target')) {
      $('.target').removeClass('selected');
      $el.addClass('selected');
      $('#targetId').attr('value', getId($el));
    }
    else {
      $('.unmatchedModel').removeClass('selected');
      $el.addClass('selected');
      $('#unmatchedModelId').attr('value', getId($el));
    }
  });
  
  });
  </script>
    
</div>
  
</body>

</html>