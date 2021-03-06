﻿<!DOCTYPE html>
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

      <h3><@displaySoaNodeShort soaNode subprojectId visibility/></h3>

      <@displayDoc soaNode/>

	</div>

</body>

</html>