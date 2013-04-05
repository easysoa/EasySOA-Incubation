<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Cartography</title>
        <meta charset="utf-8" />

        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>                
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
	<script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
    </head>
    
    <body>
        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"></div>
                EasySOA Cartographie
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <!-- Context bar -->
                <#assign visibility=visibility!"">
                <#assign subprojectId=subprojectId!"">
                <@displayContextBar subprojectId visibility "false"/>

                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <h3>Cartographie des services</h3>
                        <p>
                            <ul>
                                <!-- TODO : Replace this hard coded link by a build in link ? -->
                                <li>
                                    <a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/" target="_blank">Découverte de services</a>
                                    <small> : Find any useful service from your intranet, or even the web: as you browse pages, informations on services are collected and sent the registry</small>
                                </li>
                                <li><a href="${Root.path}/../services/?subprojectId=${subprojectId}&visibility=${visibility}">IHM de consultation du modèle SOA</a>
                                    <small> : EasySOA provides an example of a simple "service portal" web user interface. It lists all services, allows to browse them by tags (a.k.a. TaggingFolder) and also to tag them. For each service, an example of a dedicated "service usage" documentation page can be displayed.</small>
                                </li>
                                <li>Qualité
                                    <small></small>
                                </li>
                            </ul>
                        </p>
                        
                        <h3>Indicateurs</h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <@displayIndicatorsInTable indicators "cartography"/>
                        <p>
                    </div>
                </li>
                
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Back to dashboard</a>
                    </div>
                </li>                 
            </ul>
        </div>
    </body>

</html>
