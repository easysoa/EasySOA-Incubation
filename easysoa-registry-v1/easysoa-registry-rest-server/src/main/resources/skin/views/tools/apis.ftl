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
                EasySOA - API's
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                            <h3>Registry API</h3>
                            <p>
                                L'API REST registry peut être utilisée pour effectuer les opérations de base sur les objets stockés dans Easysoa (services, implémentations de services, points de déploiement ...).
                                <br/>
                                Voila la liste des opérations supportées par l'API :
                                <ul>
                                    <li>Création : Création d'objets dans le registry EasySOA.</li>
                                    <li>Suppression : Suppression d'objets.</li>
                                    <li>Lecture : Récupération d'objets pour consultation.</li>
                                    <li>Requète : Permet d'éxécuter une requete NXQL dans le registry EasySOA.</li>
                                </ul>
                            </p>
                            <h3>Simple registry API</h3>
                            <p>
                                L'API registry simple est une version simplifiée de l'API REST registry.
                                Elle offre des opérations prédéfinies puissantes permettant de récuperer des objets ayant des relations complexes sans devoir ecrire des requetes NXQL.
                                <br/>
                                Les opérations disponibles sont les suivantes :
                            <ul>
                                <li>queryWSDLInterfaces : Retourne les services enregistrés dans le registry EasySOA.</li>
                                <li>queryEndpoints : Retourne les services déployés.</li>
                                <li>queryServicesWithEndpoints : Retourne les services ainsi que leurs point de déploiement pour une phase donnée.</li>
                            </ul>

                            </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>