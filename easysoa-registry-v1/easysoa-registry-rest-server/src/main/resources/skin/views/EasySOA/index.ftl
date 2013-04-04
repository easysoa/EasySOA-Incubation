<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA index</title>
        <meta charset="utf-8" />
        <link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all" />
        <link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico" /> 
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        <style type="text/css">
          .clickable:hover { cursor: pointer; background-color: #FFC; }
          .id { display: none }
          .selected { background-color: #CFC; }
        </style>

        <!-- Bootstrap style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap.min.js"></script>
        
        <link rel="stylesheet/less" href="/nuxeo/site/easysoa/skin/css/bootstrap.less">
        <script src="/nuxeo/site/easysoa/skin/js/less.js"></script>
    </head>
    
    <body>

        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"></div>
                EasySOA Index page
            </div>
        </div>

        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Point de vue</h3>
                        <#assign visibility=visibility!"">
                        <#assign subprojectId=subprojectId!"">
                        <p>Version de Phase : <@displayCurrentVersion subprojectId visibility/></p>
                        <!-- TODO also : "all latest versions", "(all latest versions OR live) by global environment type XXX", "all live elements" (check that no deleted elements) -->
                        <a class="btn btn-primary" href="${Root.path}/context/">Changer le point de vue</a>
                    </div>
                </li>
            </ul>
            
            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Cartographie des services</h3>
                        <p>
                            <!-- TODO : Replace this hard coded link by a build in link ? -->
                            <a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/" target="_blank">Découverte de services</a>, 
                            <a href="${Root.path}/services/?subprojectId=${subprojectId}&visibility=${visibility}">IHM de consultation du modèle SOA</a>, 
                            Qualité
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de services :</td>
                                    <td width="20%">${indicators["InformationService"].count}</td>
                                </tr>
                                <tr>
                                    <td width="80%">Nombre d'implémentations de services :</td>
                                    <td width="20%">${indicators["ServiceImplementation"].count}</td>
                                </tr>
                                <tr>
                                    <td width="80%">Nombre d'endpoints :</td>
                                    <td width="20%">${indicators["Endpoint"].count}</td>
                                </tr>
                            </table>
                        </p>
                        
                        <a class="btn btn-primary" href="${Root.path}/services/cartography/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>                        
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Réconciliation technique / métier</h3><!-- ou (mise en) correspondance, métier / technique ? -->
                        <p>
                            <!--<ul>
                                <li>--><a href="${Root.path}/dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Matching Dashboard</a>
                                    <!--<small> : EasySOA Registry's Matching Dashboard lists all discovered SOA elements (implementations and endpoints) that its automatic matching algorithm couldn't link with the existing SOA model because of lack of information that could help it decide one way or the other, and further helps the SOA administrator link them by providing suggestions along the specified architecture (Components).</small>-->
                                <!--</li>-->
                                <!--<li>-->, Gestion des versions
                                <!--</li>
                            </ul>-->
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de Placeholders (implémentations inconnues d'endpoints) :</td>
                                    <td width="20%">${indicators["Placeholders"].count}</td>
                                </tr>
                            </table>
                        </p>                        
                        
                        <a class="btn btn-primary" href="${Root.path}/services/matchingFull/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>
                    </div>
                </li>
            </ul>

            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Usage des services</h3>
                        <p>
                            Statistiques et indicateurs à l'exécution, Appropiation du modèle SOA par les utilisateurs de EasySOA
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de service jamais consomés :</td>
                                    <td width="20%">${indicators["Never consumed services"].count}</td>
                                </tr>
                            </table>
                        </p>
                        
                        <a class="btn btn-primary" href="${Root.path}/monitoring/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Pilotage</h3>
                        <p>
                            Suivi des Phases de production SOA, Indicateurs de complétion et gouvernance, (?) Edition collaborative du modèle SOA<!-- ou Aide à la prise de décisions ? Registry des services, implementations ?? -->
                        </p>
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de services n'ayant aucun tag utilisateur :</td>
                                    <td width="20%">${indicators["Services without at least one user tag"].count}</td>
                                </tr>
                            </table>
                        </p>
                        <!-- TODO redirect only on the nuxeo index page, redirect on the choosen version phase -->
                        <a class="btn btn-primary" href="/nuxeo/" target="_blank">Plus...</a>
                    </div>
                </li>
            </ul>
        
            <!-- 2 categories with a separator -->
            <hr style="color:black; background-color:black; height:3px;" /> 
            
            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>A classer</h3>
                        <p>Statistiques et indicateurs à l'exécution, (?) d'appropiation du modèle SOA par les utilisateurs de EasySOA...</p>
                        
                        <p>
                            <ul>
                                <!-- TODO : add links -->
                                <li>Scaffolder proxy
                                    <small> : Take advantage of the services you find, by using them in this secured service scaffolder</<small>
                                </li>
                                <li>Proxy run manager</li>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:7080/easySoa/index.html">FraSCAti Studio</a></li>
                            </ul>
                        </p>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Exemples</h3>
                        <p>
                            <ul>
                                <!-- TODO : add links -->
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/demo-intranet/index.html" target="_blank">Pure Air Flowers</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-registry.accelance.net:8083/demo-intranet/index.html" target="_blank">Intranet</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-pivotal.accelance.net:7080/pivotal/index.html">APV Pivotal</a></li>
                                <li><a href="http://owsi-vm-easysoa-axxx-apv.accelance.net:7080/apv/index.jsp">AXXX dps apv</a></li>
                            </ul>
                        </p>
                    </div>
                </li>
            </ul>
        </div>
        
        <a class="btn btn-primary" href="${Root.path}/indicators/?subprojectId=${subprojectId}&visibility=${visibility}">Old indicators page (to remove when finished)</a>

    </body>

</html>