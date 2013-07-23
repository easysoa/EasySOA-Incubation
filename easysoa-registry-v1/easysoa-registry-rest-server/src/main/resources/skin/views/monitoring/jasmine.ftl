<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">

    <#include "/views/EasySOA/macros.ftl">

    <head>
        <title>EasySOA Monitoring</title>
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
                EasySOA - Monitoring JASMINe
            </div>
        </div>
        <br/>
        <div class="container" id="container">
            <ul class="thumbnails">
                <li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">
                        <h2>Démarrage rapide</h2>
                        <p>
                            Le monitoring JASMINe permet de visualiser et de configurer les données des indicateurs techniques.
                            <br/>
                            Pour cela, plusieurs widgets :
                            <ul>
                                <li><a href="#probeManagerWidget">Probe manager</a> permettant de configurer les sondes.</li>
                                <li><a href="#MonitoringWidget">Monitoring</a> permattant de visualiser les données.</li>
                                <li><a href="#NotifiationEditorWidget">Notification editor</a> permettant de configurer les notifications et alertes.</li>
                            </ul>
                            <br/>
                            <a class="btn" href="${jasmine_url}" target="_blank">Ouvrir l'outil JASMINe</a>
                        </p>

                        <hr/>

                        <a id="probeManagerWidget"></a>
                        <h3>Widget Probe manager</h3>
                        <p>
                            Ce module permet de visualiser les sondes et indicateurs qui sont définis dans le fichier de configuration du module JasmineProbe.
                            Il permet également de démarrer, arrêter et de définir de nouvelles sondes (ainsi que des indicateurs, outputs et targets).
                            <br/>
                            <h4>Par exemple :</h4>
                            <br/>
                            L'exemple AXXX pivotal est constitué de :
                            <br/>
                            <br/>
                            3 sondes :
                            <ul>
                                <li>qos_probe : Sonde de type QoS Event (collector QoS Event développé pour EasySOA) permettant de mesurer les temps de latence et de réponse du service.</li>
                                <li>cxfServices : Sonde permettant d'afficher l'état et des statistiques sur le web service "PrecomptePartenaireService".</li>
                                <li>poll-cpusun-pivotal : Sonde permettant d'afficher le taux d'utilisation CPU sur la plate-forme AXXX Pivotal.</li>
                            </ul>
                            <br/>
                            1 target JMX
                            <ul>
                                <li>pivotal : utilisée par les sondes "cxfServices" et "poll-cpu-pivotal".</li>
                            </ul>
                            <br/>
                            Plusieurs indicateurs dont:
                            <ul>
                                <li>qos : de type QoS Event utilisé dans qos_probe.</li>
                                <li>PrecomptePartenaireServiceStatus, PrecomptePartenaireServiceStats : de type jmx permettant de déterminer l'état du service et des compteurs statistiques (nb invocations, temps de réponse moyenne) concernant le service "PrecomptePartenaireService", en utilisant des MBeans cxf (en effet PrecomptePartenaireService est un WEB service cxf dans Talend).</li>
                                <li>Des indicateurs jmx et des indicateurs de calcul afin de déterminer différents paramètres concernant l'utilisation CPU de la target pivotal. Par exemple, l'indicateur "currentCpuLoad" permet de connaître la charge CPU de pivotal.</li>
                            </ul>
                            <br/>
                            2 output:
                            <ul>
                                <li>stdio : permet de visualiser des valeurs des indicateurs dans les widgets.</li>
                                <li>mule : permet de publier les valeurs collectées dans le BUS de données de JASMINe. Ces valeurs sont enregistrées dans la BD du serveur JASMINe.</li>
                            </ul>
                            <br/>
                            <br/>
                            <img src="/nuxeo/site/easysoa/skin/img/doc/jasmine/probeManager.png" alt="Probe manager" height="80%" width="80%"/>
                        </p>

                        <a id="MonitoringWidget"></a>
                        <h3>Widget Monitoring</h3>
                        <p>
                            Ce module permet de visualiser par des graphes les valeurs qui arrivent dans le BUS du serveur JASMINe (live monitoring) ou rejouer des données de la base de données JASMINe.
                            <br/>
                            <!--
                            - Le 1er mode (live monitoring) peut être utilisé pour visualiser les valeurs des indicateurs CPU collectées par la probe poll-cpusun-pivotal.
                            - Le 2ème mode (replay from DB) pour visualiser les valeurs de indicateurs QoS produites par la probe qos_probe.
                            -->
                            <h4>Par exemple :</h4>
                            <br/>
                            Pour obtenir des graphes il faut charger un fichier de configuration qui indique quels sont les valeurs représentées par les courbes (series) du/des graphs:
                            <ul>
                                <li>qos.xml pour le graph des indicateurs QoS</li>
                                <li>karaf_cpu.xml pour le graph des indicateurs de charge CPU</li>
                            </ul>
                            <br/>
                            <ol>
                                <li>qos.xml
                                    <ul>
                                        <li>Charger le fichier (bouton Apply)</li>
                                        <li>Dans la fenêtre ainsi ouverte (label qos), dérouler le menu afin de passer le monitoring "Stopped" à "Replay Database". On peut laisser la période par défaut</li>
                                        <li>Appuyer sur le bouton Start</li>
                                    </ul>
                                    2 courbes sont affichées : "creerPrecompre SOA Latency" et "creerPrecompre Response Time" correspondant aux valeurs de QoS de l'opération "creerPrecompte" du service "PrecomptePartenaireService".
                                </li>
                                <br/>
                                <li>karaf_cpu.xml
                                    <ul>
                                        <li>Dans le ProbeManager, démarrer la sonde poll-cpu-pivotal en effectuant d'abord la sélection de cette probe (utiliser l'icone Edit -la loupe-). Dans la fenêtre ouverte, appuyer sur le boutton Start. On peut voir ainsi les valeurs collectées dans "Probe results".
                                        <li>Retourner dans Monitoring pour charger le graph karaf_cpu.xml</li>
                                        <li>Passer de l'état "Stopped" à "Live Monitoring" puis appuyer sur Start</li>
                                    </ul>
                                    La courbe "CPU Load" montre une charge comprise habituellement entre 2 et 3 %.
                                </li>
                            </ol>
                            <br/>
                            <br/>
                            <img src="/nuxeo/site/easysoa/skin/img/doc/jasmine/graphNotif.png" alt="Graphique avec notifications" height="80%" width="80%"/>
                        </p>

                        <a id="NotifiationEditorWidget"></a>
                        <h3>Widget Notification editor</h3>
                        <p>
                            Ce widget permet la définition et l'activation des régles (Drools) pour détecter des valeurs seuil et générer en cas de détection une notification.
                            <br/>
                            <h4>Par exemple :</h4>
                            <br/>
                            Nous allons utiliser une règle qui est prédefine et activée par configuration : "Insert Jasmine Event in JMS Topic".
                            Une notification sera émise lorsque la valeur de "Current CPU Load" dépassera un seuil (2.7 dans notre cas d"exemple).
                            <!--On peut voir et modifier cette valeur en utilisant la fonction "Edit" (on devrait désactiver, modifier la valeur dans Edit, puis Save et Compile), revenir ensuite dans le Rules dashboard, selectionner et réactiver avec Activate (ne pas oublier de faire Appply)-->
                            <br/>
                            <br/>
                            Les notifications émises apparaissent dans le widget "Notification Board" mais aussi dans le graph de monitoring.
                            <br/>
                            <br/>
                            <img src="/nuxeo/site/easysoa/skin/img/doc/jasmine/notificationBoard.png" alt="Notification board" height="80%" width="80%"/>
                        </p>

                    </div>
                </li>

                <@displayReturnToIndexButtonBar/>
            </ul>
        </div>
    </body>
</html>
