<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Cartographie</title>
        <meta charset="utf-8" />
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
    </head>
    
    <body>
        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                EasySOA Cartographie
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId contextInfo visibility "false"/>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Cartographie des services</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/cartography/sourceDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte sources</a>
                                <!--<small> : Sources Discovery allows to automatically extract known services, implementations and consumptions from source code (for now, Java) by analyzing it at compilation time.</small>-->
                                <small> : La découverte source permet d'extraire automatiquement des services, des implementations et des clients depuis le code source (Java pour le moment) en l'analysant lors de la compilation.</small>
                            </p>
                            <p>
                                <a class="btn" href="${web_discovery_url}" target="_blank">Découverte web</a>
                                <!--<small> : Find any useful service from your intranet, or even the web: as you browse pages, informations on services are collected and sent the registry</small>-->
                                <small> : Trouvez des services utiles à partir de votre intranet, ou même sur le web : Pendant que vous naviguez, les informations sur les services sont collectées et envoyées dans le registre EasySOA</small>
                            </p>
                            <p>                                
                                <a class="btn" href="${Root.path}/cartography/runDiscovery?subprojectId=${subprojectId}&visibility=${visibility}">Découverte à l'exécution</a>
                                <!--<small> : Automatic discovery of web services by listenning exchanges between applications.</small>-->
                                <small> : Découverte automatique de web services par écoute des échanges entre applications.</small>
                            </p>
                            <p>                                
                                <a class="btn" href="${Root.path}/../services/?subprojectId=${subprojectId}&visibility=${visibility}">IHM de consultation du modèle SOA</a>
                                <small> : EasySOA provides an example of a simple "service portal" web user interface. It lists all services, allows to browse them by tags (a.k.a. TaggingFolder) and also to tag them. For each service, an example of a dedicated "service usage" documentation page can be displayed.</small>
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
