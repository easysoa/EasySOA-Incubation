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
                            <h3>Simple Registry API</h3>
                            <p>
                                La Simple Registry API permet de faire en REST des requêtes simples et couvrant la plupart des besoins sur les objets SOA du modèle,
                                avec un modèle plus simple que la Registry API complète et sans devoir écrire des requêtes NXQL.
                                <br/>
                                <br/>
                                Les opérations disponibles sont les suivantes :
                                <ul>
                                    <li>queryWSDLInterfaces : Retourne les services enregistrés dans le registry EasySOA.</li>
                                    <li>queryEndpoints : Retourne les services déployés.</li>
                                    <li>queryServicesWithEndpoints : Retourne les services ainsi que leurs point de déploiement pour une phase donnée.</li>
                                </ul>
                            </p>

                            <p>
                                Sa définition formelle en JAXRS est ici <a href="https://github.com/easysoa/EasySOA-Incubation/blob/master/easysoa-registry-v1/easysoa-registry-rest-core/src/main/java/org/easysoa/registry/rest/integration/SimpleRegistryService.java" target="_blank">SimpleRegistryService.java</a>
                                <br/>
                                Elle contient aussi sa documentation détaillée, avec des exemples du format requis (JSON, ou bien XML).
                                <br/>
                                Des clients basés sur les moteurs de service Jersey, CXF et FraSCAti sont déjà fournis, voir le projet <a href="https://github.com/easysoa/EasySOA-Incubation/tree/master/easysoa-registry-v1/easysoa-registry-integration-base" target="_blank">Intégration base</a>.
                                <br/>
                                Pour appeler la SimpleRegistryAPI de son registry EasySOA déployé, voir l'exemple des tests unitaires : <a href="https://github.com/easysoa/EasySOA-Incubation/tree/master/easysoa-registry-v1/easysoa-registry-integration-base/src/test/java/org/easysoa/registry/integration" target=_blank">Intégration base tests</a>.
                                <br/>
                                Le Simple Registry API Service est exposé <a href="/nuxeo/site/easysoa/simpleRegistryService/queryEndpoints">ici</a>.
                            </p>

                            <h3>Registry API</h3>
                            <p>
                                La Registry API permet en REST toutes les manipulations sur les objets SOA stockés dans Easysoa
                                (services, implémentations de services, points de déploiement ...), avec toutes les propriétés Nuxeo qu'ils portent.
                                <br/>
                                NB : pour faire uniquement des requêtes simples, utilisez le SimpleRegistryService.
                                <br/>
                                <br/>
                                Voila la liste des opérations supportées par l'API :
                                <ul>
                                    <li>Création : Création d'objets dans le registry EasySOA.</li>
                                    <li>Suppression : Suppression d'objets.</li>
                                    <li>Lecture : Récupération d'objets pour consultation.</li>
                                    <li>Requète : Permet d'éxécuter une requête NXQL dans le registry EasySOA.</li>
                                </ul>
                            </p>

                            <p>
                                Sa définition formelle en JAXRS est ici <a href="https://github.com/easysoa/EasySOA-Incubation/blob/master/easysoa-registry-v1/easysoa-registry-rest-core/src/main/java/org/easysoa/registry/rest/RegistryApi.java"  target="_blank">RegistryApi.java</a>
                                <br/>
                                Elle contient aussi sa documentation détaillée, avec des exemples du format requis (JSON, ou bien XML).
                                <br/>
                                Des clients basés sur les moteurs de service Jersey, CXF et FraSCAti sont déjà fournis, voir le projet XXX <a href="https://github.com/easysoa/EasySOA-Incubation/tree/master/easysoa-registry-v1/easysoa-registry-integration-base" target="_blank">Intégration base</a>.
                                <br/>
                                Pour appeler la RegistryAPI de son registry EasySOA déployé, voir l'exemple des tests unitaires : <a href="https://github.com/easysoa/EasySOA-Incubation/blob/master/easysoa-registry-v1/easysoa-registry-rest-client/src/test/java/org/easysoa/registry/rest/client/RestClientTest.java" target=_blank">RestClientTest.java</a>.
                                <br/>
                                Le Registry API Service est exposé <a href="/nuxeo/site/easysoa/registry">ici</a>.
                            </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>