<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("monitoring")}</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("monitoring")}
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("monitoring")}</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("serviceLevel")}</a>
                                <small> : ${Root.msg("serviceLevel.doc")}</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}"">${Root.msg("statistics")}</a>
                                <small> : ${Root.msg("statistics.doc")}</small>
                            </p>
                        </p>

                        <h3>${Root.msg("indicators")} <@displayIndicatorsExport subprojectId visibility "usage"/></h3>
                        <p>
                            <@displayIndicatorsInTable indicators "usage"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
