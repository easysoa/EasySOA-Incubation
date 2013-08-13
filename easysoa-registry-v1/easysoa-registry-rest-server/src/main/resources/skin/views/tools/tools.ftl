<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Tools</title>
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
                EasySOA - Outils
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Outils</h3>
                        <p>
                            <a class="btn" href="">Scaffolder proxy</a>
                            <!--<small> : Sources Discovery allows to automatically extract known services, implementations and consumptions from source code (for now, Java) by analyzing it at compilation time.</small>-->
                            <small> : Le scaffolder proxy est un outil permettant de générer des formulaires html à partir de fichiers WSDL, puis d'envoyer une requète avec les parametres renseignés.</small>
                        </p>
                        <p>
                            <a class="btn" href="">Proxy run manager</a>
                            <!--<small> : Find any useful service from your intranet, or even the web: as you browse pages, informations on services are collected and sent the registry</small>-->
                            <small> : Interface de pilotage du proxy, permet d'initialiser, arreter et d'enregistrer des runs. un run est un ensenble de requetes / réponses enregistré par le proxy HTTP.</small>
                        </p>
                        <p>
                            <a class="btn" href="http://owsi-vm-easysoa-axxx-registry.accelance.net:7080/easySoa/index.html" target="_blank">FraSCAti Studio</a>
                            <!--<small> : Automatic discovery of web services by listenning exchanges between applications.</small>-->
                            <small> : FraSCAti Studio (Création et déploiement d'applications SCA) basé sur la plate-forme d'éxécution SCA OW2 FraSCAti.</small>
                        </p>
                        <p>
                            <a class="btn" href="${Root.path}/apis?subprojectId=${subprojectId}&visibility=${visibility}">Easysoa API REST</a>
                            <!--<small> : EasySOA provides an example of a simple "service portal" web user interface. It lists all services, allows to browse them by tags (a.k.a. TaggingFolder) and also to tag them. For each service, an example of a dedicated "service usage" documentation page can be displayed.</small>-->
                            <small> : API's REST de communication avec le registry EasySOA.</small>
                        </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>