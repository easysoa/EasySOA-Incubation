<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

<head>
	<title>EasySOA Matching dashboard</title>
    <@includeResources/>
</head>

<body>

<div id="header">
	<div id="headerContents">
	<div id="logoLink">&nbsp;</div>
    	<div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
        <div id="headerContextBar">
            <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
            <#assign subprojectId=subprojectId!"">
            <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
        </div>
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