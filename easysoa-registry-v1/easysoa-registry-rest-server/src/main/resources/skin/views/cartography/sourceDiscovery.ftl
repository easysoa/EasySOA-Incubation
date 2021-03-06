<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Cartographie</title>
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
                EasySOA - Découverte sources
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                            <h3>Le plugin Maven de découverte Easysoa</h3>
                            <p>
                            La découverte sources permet d'extraire automatiquement les services, implémentations et consommations depuis le code source (Pour l'instant en Java) en l'analysant lors de la compilation.
                            <br/>
                            <!--The only thing to do is to add and configure the easysoa discovery code maven plugin in your project pom.xml file.-->
                            Pour le mettre en oeuvre, il faut ajouter et configurer le plugin Easysoa découverte source dans le fichier pom.xml de votre projet.
                            <br/>
                            Il y a plusieurs paramètres à configurer :
                            <ul>
                                <li>nuxeoSitesUrl : L'addresse du système Easysoa Nuxeo</li>
                                <li>username : L'utilisateur devant être utilisé pour la connection</li>
                                <li>password : Le mot de passe devant être utilisé pour la connection</li>
                                <li>subproject : La phase ou enregistrer les informations découvertes</li>
                                <li>application : L'application ou enregistrer les informations découvertes</li>
                            </ul>
                            </p>
                            <h3>Code source</h3>
                            Le code source est disponible <a href="https://github.com/easysoa/EasySOA-Incubation/tree/master/easysoa-discovery-code-mavenplugin">ici</a>.

                            <H3>Exemple de configuration :</h3>
                            Le code ci dessous est à ajouter au fichier pom.xml de votre projet.
                            <pre class="prettyprint">
...
&lt;!-- EasySOA Code discovery plugin -->
&lt;plugin/&gt;
  &lt;groupId/&gt;org.easysoa.discovery.code&lt;/groupId/&gt;
    &lt;artifactId/&gt;easysoa-discovery-code-mavenplugin&lt;/artifactId/&gt;
    &lt;version/&gt;1.0-SNAPSHOT&lt;/version/&gt;
    &lt;executions/&gt;
      &lt;execution/&gt;
        &lt;id>test&lt;/id/&gt;
        &lt;phase/&gt;compile&lt;/phase/&gt;
        &lt;goals/&gt;
          &lt;goal/&gt;discover&lt;/goal/&gt;
        &lt;/goals/&gt;
        &lt;configuration/&gt;
          &lt;nuxeoSitesUrl/&gt;http://localhost:8080/nuxeo/site&lt;/nuxeoSitesUrl/&gt;
          &lt;username/&gt;Administrator&lt;/username/&gt;
          &lt;password/&gt;Administrator&lt;/password/&gt;
          &lt;application/&gt;/default-domain/Intégration DPS - DCV/Realisation_v&lt;/application/&gt;
          &lt;subproject/&gt;AXXX DPS APV&lt;/subproject/&gt;
        &lt;/configuration/&gt;
      &lt;/execution/&gt;
    &lt;/executions/&gt;
&lt;/plugin/&gt;
...
                            </pre>

                            <a href="https://github.com/easysoa/EasySOA-Incubation/blob/master/easysoa-samples/easysoa-samples-axxx/axxx-dps-apv/pom.xml">Ici</a> vous pouvez trouver un exemple complet de fichier pom.xml.

                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
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