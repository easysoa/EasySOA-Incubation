<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Cartographie</title>
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
                EasySOA Cartographie
            </div>
        </div>
        <br/>

        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Cartographie des services</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte sources</a>
                                <!--<small> : Sources Discovery allows to automatically extract known services, implementations and consumptions from source code (for now, Java) by analyzing it at compilation time.</small>-->
                                <small> : La découverte source permet d'extraire automatiquement des services, des implementations et des clients depuis le code source (Java pour le moment) en l'analysant lors de la compilation.</small>
                            </p>
                            <p>
                                <a class="btn" href="${web_discovery_url}?subprojectId=${subprojectId}&visibility=${visibility}&serverName=${serverName}" target="_blank">Découverte web</a>
                                <!--<small> : Find any useful service from your intranet, or even the web: as you browse pages, informations on services are collected and sent the registry</small>-->
                                <small> : Trouvez des services utiles à partir de votre intranet, ou même sur le web : Pendant que vous naviguez, les informations sur les services sont collectées et envoyées dans le registre EasySOA</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/runDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte à l'exécution</a>
                                <!--<small> : Automatic discovery of web services by listenning exchanges between applications.</small>-->
                                <small> : Découverte automatique de web services par écoute des échanges entre applications.</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/../services/?subprojectId=${subprojectId}&visibility=${visibility}">IHM de consultation du modèle SOA</a>
                                <!--<small> : EasySOA provides an example of a simple "service portal" web user interface. It lists all services, allows to browse them by tags (a.k.a. TaggingFolder) and also to tag them. For each service, an example of a dedicated "service usage" documentation page can be displayed.</small>-->
                                <small> : EasySOA fournit un exemple d'une interface web simple pour un "portail service". Cette interface liste tous les services, permet de parcourir ces services via des étiquettes (ou TaggingFolder) et de les étiquetter. Pour chaque service, un exemple d'une page de documention dédiée à l'utilisation du service peut etre affichée.</small>
                            </p>
                        </p>

                        <h3>Indicateurs</h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <@displayIndicatorsInTable indicators "cartography"/>
                        <p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>
