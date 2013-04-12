<!DOCTYPE html>
<html>

<head>
	<title>EasySOA Matching dashboard</title>
	<meta charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="skin/favicon.ico" /> 
	<script type="text/javascript" src="skin/js/jquery.js"></script>
        
        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>        
</head>
	
<body>

<div id="header">
        <#include "/views/EasySOA/macros.ftl">
	<div id="headerContents">
	    <div id="logoLink">&nbsp;</div>
    	<div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
		EasySOA Light summary
    </div>
</div>

<div id="container">
    
    <p>&lt;&lt;&lt; <a href="javascript:history.go(-1)">Go back to previous page</a>
    
    <h1 style="color: red">ERROR</h1>
    
    <p>${error}</p>
    
    <div style="font-family: monospace; padding: 10px;">
    <#list error.getStackTrace() as line>
      <#if line?contains("easysoa")>
        <b>${line}</b>
      <#else>
        ${line}
      </#if>
    	<br />
    </#list>
    </pre>
    
</div>
  
</body>

</html>