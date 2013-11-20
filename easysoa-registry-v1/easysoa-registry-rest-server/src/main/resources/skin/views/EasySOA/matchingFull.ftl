<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA ${Root.msg("compliance")}</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                EasySOA ${Root.msg("compliance")}
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>${Root.msg("technicalToBusinessMatchingDashboard")}</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/../dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">${Root.msg("compliance")}</a>
                                <small> : ${Root.msg("matchingDashboard.doc")}</small>
                            </p>
                        </p>

                        <h3>${Root.msg("indicators")} <@displayIndicatorsExport subprojectId visibility "matching"/></h3>
                        <p>
                            <@displayIndicatorsInTable indicators "matching"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
