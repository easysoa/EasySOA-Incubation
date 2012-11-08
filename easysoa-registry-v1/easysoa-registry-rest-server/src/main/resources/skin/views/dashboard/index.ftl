<!DOCTYPE html>
<html>

<head>
	<title>EasySOA Matching dashboard</title>
	<meta charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="skin/favicon.ico" /> 
	<script type="text/javascript" src="skin/js/jquery.js"></script>
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
		EasySOA Light summary
    </div>
</div>

<div id="container">
    
    <h1>Matched services</h1>
    
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
      	  <form style="float: right" method="post">
      	  	<input type="submit" value="X" style="width: 30px" />
      	  	<input type="hidden" name="serviceImplId" value="${matchedImpl.id}" />
      	  </form>
	    	  <#assign document = matchedImpl>
	    	  <#include "/views/dashboard/document.ftl">
	    	</td>
	    </tr>
    </#list>
    </table>
    
    <div style="float: left; width: 520px">
	    <h1>Unimplemented information services</h1>
	    <table style="width: 500px">
	      <tr>
		      <th>Information service</th>
	      </tr>
        <#list unimplementedServs as unimplementedServ>
	        <tr>
	        	<td class="clickable infoService">
	        	  <#assign document = unimplementedServ>
	        	  <#include "/views/dashboard/document.ftl">
	        	</td>
	        </tr>
        </#list>
		</table>
	</div>
	
    <div style="float: left; width: 500px">
	    <h1>Service implementations without specifications</h1>
	    <table style="width: 500px">
	      <tr>
		      <th>Service implementation</th>
	      </tr>
        <#list servWithoutSpecs as servWithoutSpec>
	        <tr>
	        	<td class="clickable serviceImpl">
	        	  <#assign document = servWithoutSpec>
	        	  <#include "/views/dashboard/document.ftl">
	        	</td>
	        </tr>
        </#list>
		</table>
	</div>
    
  <form method="post" style="float: left; width: 100%; margin-top: 10px">
  <fieldset style="width: 400px; padding: 10px;">
	  Click on an information service and a service implementation, then click:<br />
	  <input type="submit" value="Create a link" />
	  <input id="infoServiceId" name="infoServiceId" type="hidden" />
	  <input id="serviceImplId" name="serviceImplId" type="hidden" />
  </fieldset>    
  </form>
  
  <script>
  $(document).ready(function() {
  
  $('.clickable').click(function() {
    var $el = $(this);
    if ($el.hasClass('infoService')) {
      $('.infoService').removeClass('selected');
      $el.addClass('selected');
      $('#infoServiceId').attr('value', $('.id', $el).html());
    }
    else {
      $('.serviceImpl').removeClass('selected');
      $el.addClass('selected');
      $('#serviceImplId').attr('value', $('.id', $el).html());
    }
  });
  
  });
  </script>
    
</div>
  
</body>

</html>