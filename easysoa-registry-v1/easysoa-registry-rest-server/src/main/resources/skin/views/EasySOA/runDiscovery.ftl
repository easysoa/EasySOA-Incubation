<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Cartographie</title>
        <meta charset="utf-8" />
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        
        <link href="/nuxeo/site/easysoa/skin/css/prettify.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/prettify/prettify._js"></script>
        
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
                EasySOA - Découverte à l'éxécution
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
                        
                        <h2>Démarrage rapide</h2>    
                        <p>
                            La découverte à l'éxécution vous permet, en ecoutant les échanges entre vos appplications, de découvrir les services associés.

                            Pour l'utiliser :
                            <ol>
                                <li><a href="#proxyConfig">configurez</a> vos applications pour qu'elles utilisent le proxy HTTP EasySOA</li>
                                <li>Utilisez l'<a href="#runManager">interface de pilotage</a> du proxy pour démarrer vos sessions d'enregistrement</li>
                            </ol>
                            <br/>
                            <strong>A noter !</strong> Le proxy HTTP EasySOA ne fonctionne pas avec les échanges sécurisés (protocole https)</a>.
                        </p>
                        
                        <hr/>
                        
                        <h3>Objectif</h3>
                
                        <p>
                            Le but de la découverte à l'éxécution est de détecter les services en écoutant les échanges entre les applications et de les enregistrer dans le registre de servcies basé sur Nuxeo.

                            <!--Monitoring EasySOA Light services
                            Services calls done using EasySOA Light Service Scaffolder are monitored by the embedded HTTP proxy server by default.-->
    
                            <!--Monitoring your own external services-->

                        </p>
                        
                        <h3>Fonctionnement</h3>
                        <p>
                            La découverte à l'éxécution est réalisée par un proxy HTTP fournit par Easysoa. Il fonctionne uniquement avec les web services de type SOAP et RESTfull pour le moment.
                        </p>
                        
                        
                        <h3>Configuration</h3>
                        <a id="proxyConfig"></a>
                        <p>
                            <!--For that, you just have to tell your own service clients to use it as an HTTP proxy. You can either use the one that is builtin in the release (available at http://localhost:8082/), or deploy and run your own instance in your information system (see details below).-->
                            Il est nécéssaire de configurer vos applications pour qu'elles utilisent le proxy HTTP EasySOA.
                        </p>                            
                        
                        
                        <h3>Configuration</h3>
                        <a id="runManager"></a>
                        <p>
                            Une <a href="#">interface de pilotage</a> est disponible pour démarrer et arrèter une session d'enregistrement. Les services découverts sont automatiquement enregistrés dans le registre des services EasySOA.
                        </p>
                        <p>
                            <img src="/nuxeo/site/easysoa/skin/img/runManager.jpg" alt="Interface de pilotage">
                        </p>
                    </div>
                </li>
                
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Retour à l'accueil</a>
                    </div>
                </li>
            </ul>
        </div>
    </body>

    <script>
        !function ($) {
            $(function(){
                window.prettyPrint && prettyPrint()   
            })
        }(window.jQuery)
    </script>    
    
</html> 