<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("cartography")} - ${Root.msg("serviceBrowsing")}</title>
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
			EasySOA ${Root.msg("cartography")} - ${Root.msg("serviceBrowsing")}
	    </div>
	</div>

	<div id="container">

      <h3>${Root.msg("Services")} (${tagServices?size}) ${Root.msg("ofTag")} <@displayTagShort tag subprojectId visibility/></h3>
      <@displayServicesShort tagServices subprojectId visibility/>

      <h3>${Root.msg("Details")} ${Root.msg("ofTag")}</h3>
      <@displayDoc tag/>

	</div>

</body>

</html>