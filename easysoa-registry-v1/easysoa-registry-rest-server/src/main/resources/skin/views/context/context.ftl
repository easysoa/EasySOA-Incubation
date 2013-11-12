<!DOCTYPE html>
<html>

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>Choose a project</title>
        <@includeResources/>
    </head>

    <body>
        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                Perspective
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt=""/>
                        <h1>Projets, phases et versions :</h1>
                        <br/>
                        <@displayContextBar subprojectId contextInfo visibility "false" "true"/>
                        <br/>
                        <!--Choose the versions of phase you want to use as a point of view and the visibility scope (strict or deep, i.e. with or without parent phases).-->
                        <br/>
                        Pour en changer, choisissez un projet, une phase et la version que vous voulez utiliser dans la perspective.
                        <br/>
                        <br/>
                        <@displayProjectsPhasesAndVersionsShort projectIdToSubproject/>
                    </div>
                </li>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <a class="btn" href="${Root.path}/../">Retour à la prespective globale</a>
                        &nbsp;
                        <a class="btn" href="javascript:window.history.back();">Annuler</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

</html>