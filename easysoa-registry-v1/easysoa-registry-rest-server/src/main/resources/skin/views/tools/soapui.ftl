<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">
    <#include "/views/EasySOA/urlMacros.ftl">


    <head>
        <title>EasySOA Tools</title>
        <@includeResources/>
    </head>

    <body>

        <div id="header">
            <div id="headerContents">
                <@headerContentsDefault/>
                Soap UI Configuration
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                            <h3>SOAP UI : Export de configuration</h3>
                            <p>
                                <a href="http://www.soapui.org/" target="_blank">SOAP UI</a> (<a href="http://sourceforge.net/projects/soapui/files/">télécharger</a>)
                                est un outil de test de services WSDL pour les développeurs.
                                Il permet de créer et d'exécuter rapidement des tests sur
                                des services SOAP aussi bien d'un point de vue client que d'un point de vue serveur (mocks).
                                <br/>
                                Le registry EasySOA fournit à la demande des configurations SOAPUI permettant de tester un ou plusieurs services déployés (endpoint)
                                d'un même environnement de déploiement, afin de pouvoir tester ou émuler les opérations associées à l'endpoint.
                                <br/>
                                <br/>
                                L'export est accessible :
                                <br/>
                                &nbsp;&nbsp;- dans la <a href="/nuxeo/site/easysoa/services/?subprojectId=/default-domain/Int%C3%A9gration%20DPS%20-%20DCV/Deploiement_v&visibility=deep">
                                    Fiche Service
                                </a>, sous la forme d'un lien placé dans la partie "Outils" de chaque service déployé
                                <br/>
                                <br/>
                                <img src="/nuxeo/site/easysoa/skin/img/doc/soapui/exportSoapUI_FicheService.png" alt="Export de projet SOAP UI, Fiche service" height="60%" width="60%"/>
                                <br/>
                                <br/>
                                &nbsp;&nbsp;- dans l'<a href="/nuxeo/nxpath/default/default-domain/Intégration DPS - DCV/Deploiement/repository/Endpoint/Integration/http://vmapv:7080/apv/services/PrecomptePartenaireService@view_documents">
                                        IHM d'édition collaborative
                                    </a>
                                , sous la forme d'un bouton placé dans la partie supérieure droite de l'écran.
                                Ce bouton n'est affiché que pour les services déployés (endpoints). L'export du projet est un fichier XML qu'il est nécessaire d'enregistrer avant de pouvoir l'ouvrir
                                (car Soap UI n'offre pas la possibilité de l'ouvrir directement depuis le navigateur).
                                <br/>
                                <br/>
                                <img src="/nuxeo/site/easysoa/skin/img/doc/soapui/exportSoapUI.png" alt="Export de projet SOAP UI, IHM d'édition collaborative" height="60%" width="60%"/>
                            </p>
                            <p>
                                Pour ouvrir le projet dans SOAP UI, démarrez SOAP UI puis rendez vous dans le menu 'File' puis dans 'Import Project'.
                                Sélectionnez le fichier projet SOAP UI et ouvrez le.
                                <br/>
                                <br/>
                                <img src="/nuxeo/site/easysoa/skin/img/doc/soapui/exportSoapUI_OpenProject.png" alt="Ouverture de projet SOAP UI" height="60%" width="60%"/>
                            <p/>
                            <p>
                                Le projet contient toutes les operations référencées par l'endpoint.
                                Pour chaque opération il est possible de créer des requêtes de test ou des émulateurs de services (mocks).
                                <br/>
                                <br/>
                                La capture d'écran ci-dessous montre une requête de test sur l'opération creerPrecompte du service PrécomptePartenaireService :
                                <br/>
                                <br/>
                                <img src="/nuxeo/site/easysoa/skin/img/doc/soapui/exportSoapUI_OperationRequest.png" alt="Operation request" height="60%" width="60%"/>
                                <br/>
                                <br/>
                                Pour plus d'information, rendez vous sur la <a href="http://www.soapui.org/Functional-Testing/functional-testing.html">documentation de SOAPUI</a>.
                            </p>
                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>

</html>