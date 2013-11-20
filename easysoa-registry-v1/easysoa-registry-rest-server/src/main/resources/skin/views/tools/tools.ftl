<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("tools")}</title>
        <@includeResources/>
    </head>

    <body>

        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("tools")}
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("tools")}</h3>
                        <p>
                            <a class="btn" href="${Root.path}/apis?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("easysoaRestApi")}</a>
                            <small> : ${Root.msg("easysoaRestApi.doc")}</small>
                        </p>
                        <p>
                            <a class="btn" href="${Root.path}/tools/soapui?subprojectId=${subprojectId}&visibility=${visibility}">SOAPUI</a>
                            <small> : ${Root.msg("soapui.doc")}</small>
                        </p>
                        <p>
                            <a class="btn" <#if !Root.isDevModeSet()>disabled</#if> href="">Scaffolder proxy</a><#-- disabled -->
                            <small> : ${Root.msg("scaffolderProxy.doc")}</small>
                        </p>
                        <p>
                            <a class="btn" <#if !Root.isDevModeSet()>disabled</#if> href="">Proxy run manager</a><#-- disabled -->
                            <small> : ${Root.msg("proxyRunManager.doc")}</small>
                        </p>
                        <p>
                            <a class="btn" <#if !Root.isDevModeSet()>disabled</#if> href="http://owsi-vm-easysoa-axxx-registry.accelance.net:7080/easySoa/index.html" target="_blank">OW2 FraSCAti Studio</a>
                            <small> : ${Root.msg("frascatiStudio.doc")}</small>
                        </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>