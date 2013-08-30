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
                Soap UI Configuration
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                            <h3>Soap UI : Export de configuration</h3>
                            <p>
                                Pour chaque endpoint enregistré dans le registry EasySOA,
                                il est possible de générer une configuration de projet Soap UI afin de pouvoir tester ou émuler les opérations associées à l'endpoint.
                                <br/>
                                <br/>
                                L'export se présente sous la forme d'un bouton placé dans la partie supérieure droite de l'écran.
                                Ce bouton n'est affiché que pour les endpoints. L'export du projet est un fichier XML qu'il est nécéssaire d'enregistrer avant de pouvoir l'ouvrir,
                                Soap UI n'offrant pas la possibilité de l'ouvrir directement depuis le navigateur.
                                <br/>
                                <br/>
                                <img src="/nuxeo/site/easysoa/skin/img/doc/soapui/exportSoapUI.png" alt="Export de projet Soap UI" height="60%" width="60%"/>
                            </p>
                            <p>
                                Pour ouvrir le projet dans Soap UI, demarrez Soap UI puis rendez vous dans le menu 'File' puis dans 'Import Project'.
                                Sélectionnez le fichier projet Soap UI et ouvrez le.
                                <br/>
                                <br/>
                                <img src="/nuxeo/site/easysoa/skin/img/doc/soapui/exportSoapUI_OpenProject.png" alt="Ouverture de projet Soap UI" height="60%" width="60%"/>
                            <p/>
                            <p>
                                Le projet contient toutes les operations référencées par l'endpoint.
                                Pour chaque opération il est possible de créer des requêtes de test ou des émulateurs de services (mocks).
                                <br/>
                                <br/>
                                La capture d'écran ci-dessous montre une réquête de test sur l'opération creerPrecompte du service PrécomptePartenaireService :
                                <br/>
                                <br/>
                                <img src="/nuxeo/site/easysoa/skin/img/doc/soapui/exportSoapUI_OperationRequest.png" alt="Operation request" height="60%" width="60%"/>
                            </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>