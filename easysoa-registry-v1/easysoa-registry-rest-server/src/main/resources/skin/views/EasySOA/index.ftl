<!DOCTYPE html>

<html>
    
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
            
            <br/>
            
            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Cartographie des services</h3>
                        <p>Découverte de services, IHM de consultation du modèle SOA, qualité ...</p>
                        
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de services :</td>
                                    <td width="20%">${indicators["InformationService count"].count}</td>
                                </tr>
                                <tr>
                                    <td width="80%">Nombre d'implémentations de services :</td>
                                    <td width="20%">${indicators["ServiceImplementation count"].count}</td>
                                </tr>
                                <tr>
                                    <td width="80%">Nombre d'endpoints :</td>
                                    <td width="20%">${indicators["Endpoint count"].count}</td>
                                </tr>
                            </table>
                        </p>
                        
                        <a class="btn btn-primary" href="${Root.path}/services/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>                        
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Réconciliation technique / métier</h3><!-- ou (mise en) correspondance, métier / technique ? -->
                        <p>Matching Dashboard, (?) gestion des versions</p><!-- "gestion des versions" ?? -->

                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de Placeholders (implémentations inconnues d'endpoints) :</td>
                                    <td width="20%">${indicators["Placeholders count"].count}</td>
                                </tr>
                            </table>
                        </p>                        
                        
                        <a class="btn btn-primary" href="${Root.path}/dashboard/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>
                    </div>
                </li>
            </ul>
            
            <br/>

            <ul class="thumbnails">
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Usage des services</h3>
                        <p>Statistiques et indicateurs à l'exécution, (?) d'appropiation du modèle SOA par les utilisateurs de EasySOA...</p>
                        
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
                        <p>Suivi des Phases de production SOA, indicateurs de complétion et gouvernance, (?) édition collaborative du modèle SOA...</p><!-- ou Aide à la prise de décisions ? Registry des services, implementations ?? -->
                        
                        <p>
                            <table class="table table-bordered" width="100%">
                                <tr>
                                    <td width="80%">Nombre de services n'ayant aucun tag utilisateur :</td>
                                    <td width="20%">${indicators["Services without at least one user tag"].count}</td>
                                </tr>
                            </table>
                        </p>                        
                        
                        <a class="btn btn-primary" href="#">Plus...</a>
                    </div>
                </li>
            </ul>
        </div>
        
        <a class="btn btn-primary" href="${Root.path}/indicators/?subprojectId=${subprojectId}&visibility=${visibility}">Old indicators page (to remove when finished)</a>

    </body>

</html>