<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

<head>
	<title>EasySOA ${Root.msg("matchingDashboard")}</title>
    <@includeResources/>
</head>

<body>

        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("matchingDashboard")}
            </div>
        </div>

        <br/>
        

        <div class="container" id="container">
            <ul class="thumbnails">
            
                <@displayReturnToPreviousPageButtonBar/>
                
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">


    <h1 style="color: red">${Root.msg("Error")}</h1>

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
                </li>

            </ul>
        </div>

</body>

</html>