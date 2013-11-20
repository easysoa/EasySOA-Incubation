<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("cartography")}</title>
        <@includeResources/>
    </head>

    <body>

        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("cartography")}
            </div>
        </div>
        <br/>

        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("sourceDiscovery")}</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("sourceDiscovery")}</a>
                                <small> : ${Root.msg("sourceDiscovery.doc")}</small>
                            </p>
                            <p>
                                <a class="btn" href="${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}&serverName=${serverName}" target="_blank">${Root.msg("webDiscovery")}</a>
                                <small> : ${Root.msg("webDiscovery.doc")}</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/runDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("runtimeDiscovery")}</a>
                                <small> : ${Root.msg("runtimeDiscovery.doc")}</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/../services/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("serviceBrowsingUI")}</a>
                                <small> : ${Root.msg("serviceBrowsing.doc")}</small>
                            </p>
                        </p>

                        <h3>${Root.msg("indicators")} <@displayIndicatorsExport subprojectId visibility "cartography"/></h3>
                        <p>
                            <@displayIndicatorsInTable indicators "cartography"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
