<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Monitoring</title>
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
                EasySOA Monitoring
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
                        <h3>Monitoring</h3>
                        <p>
                            <p>
                                <a class="btn" href="${Root.path}/../monitoring/?subprojectId=${subprojectId}&visibility=${visibility}">Qualité de service</a>
                                <!--<small>EasySOA can be fed with runtime, live-computed business indicators that show how much expected business service levels are met.</small>-->
                                <small> : EasySOA peut être alimenté avec des indicateurs métier lors de l'éxécution qui montrent les niveaux de services atteints.</small>
                            </p>
                            <p>
                                <a class="btn" href="${Root.path}/../monitoring/jasmine/?subprojectId=${subprojectId}&visibility=${visibility}"">Statistiques</a>
                                <!--<small>Jasmine for Easysoa computes and display business indicators from technical service events according to EasySOA-known SLA configuration</small>-->
                                <small> : JASMINe pour Easysoa calcule et affiche des indicateurs metier issus d'événements de services techniques suivant les configurations SLA enregistrées dans EasySOA.</small>
                            </p>
                        </p>
                        
                        <h3>Indicateurs</h3>
                        <p>
                            <!-- TODO : Add table headers ? -->
                            <@displayIndicatorsInTable indicators "usage"/>
                        <p>
                    </div>
                </li>
                
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Retour à l'acceuil</a>    
                    </div>
                </li>
            </ul>
        </div>
    </body>

</html>
