<!DOCTYPE html>
<html>

<head>
	<title>EasySOA Matching dashboard</title>
	<meta charset="utf-8" />

        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->        
        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>

        <!-- font-awesome style for icons -->
        <link rel="stylesheet" href="/nuxeo/site/easysoa/skin/css/font-awesome.css">

        <!-- To solve temporarily the conflict between CSS styles -->
        <link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
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

<br />

<div id="container">
    
    <!--<p>&lt;&lt;&lt; <a href="javascript:history.go(-1)">Go back to previous page</a>-->
    <p>&lt;&lt;&lt; <a class="btn" href="javascript:history.go(-1)">Retour</a>
    
    <h1 style="color: red">Erreur<!--ERROR--></h1>
    
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