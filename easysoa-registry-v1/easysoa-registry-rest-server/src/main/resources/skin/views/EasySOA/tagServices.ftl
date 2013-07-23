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
		    <div id="logoLink">&nbsp;</div>
	    	<div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
            <div id="headerContextBar">
                <#assign visibility=visibility!""><!-- Required to set a default value when the query variables are not present -->
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "true" "false"/>
            </div>
			EasySOA Services Documentation
	    </div>
	</div>

	<div id="container">

      <h3>Services (${tagServices?size}) du tag <@displayTagShort tag subprojectId visibility/></h3>
      <@displayServicesShort tagServices subprojectId visibility/>

      <h3>Détails du tag</h3>
      <@displayDoc tag/>

	</div>

</body>

</html>