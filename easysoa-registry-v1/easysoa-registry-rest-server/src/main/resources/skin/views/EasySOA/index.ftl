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
    </head>

    <body>

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
                        <p>Version de Phase : <#if subprojectId>${subprojectId} <span class="label">visibilité ${visibility}</span><#else>Point de vue global</#if></p>
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
                            <table width="175px">
                                <tr>
                                    <td width="150px">Nombre de services :</td>
                                    <td width="25px">${indicators["InformationService count"].count}</td>
                                </tr>
                                <tr>
                                    <td width="150px">Nombre d'implémentations de services :</td>
                                    <td width="25px">${indicators["ServiceImplementation count"].count}</td>
                                </tr>
                                <tr>
                                    <td width="150px">Nombre d'endpoints :</td>
                                    <td width="25px">${indicators["Endpoint count"].count}</td>
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
                        <a class="btn btn-primary" href="${Root.path}/monitoring/?subprojectId=${subprojectId}&visibility=${visibility}">Plus...</a>
                    </div>
                </li>
                <li class="span6">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h3>Pilotage</h3>
                        <p>Suivi des Phases de production SOA, indicateurs de complétion et gouvernance, (?) édition collaborative du modèle SOA...</p><!-- ou Aide à la prise de décisions ? Registry des services, implementations ?? -->
                        <a class="btn btn-primary" href="#">Plus...</a>
                    </div>
                </li>
            </ul>
        </div>
        
        <a class="btn btn-primary" href="${Root.path}/indicators/?subprojectId=${subprojectId}&visibility=${visibility}">Old indicators page (to remove when finished)</a>

    </body>

</html>