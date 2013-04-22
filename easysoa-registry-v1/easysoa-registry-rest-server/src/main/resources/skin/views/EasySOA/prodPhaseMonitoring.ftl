<!DOCTYPE html>

<html xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <head>
        <title>EasySOA Gouvernance</title>
        <meta charset="utf-8"/>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/jquery._js"></script><!-- XXX No idea why (temporary 5.7-SNAPSHOT bug?), but Nuxeo returns the path of the script instead of the script itself when it is in .js -->
        
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/flot/jquery.flot._js"></script>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/flot/jquery.flot.categories._js"></script>
        
        <link href="/nuxeo/site/easysoa/skin/css/prettify.css" type="text/css" rel="stylesheet"/>
        <script type="text/javascript" src="/nuxeo/site/easysoa/skin/js/prettify/prettify._js"></script>
        
        <!-- Bootstrap default style and scripts -->
        <link href="/nuxeo/site/easysoa/skin/css/bootstrap.css" rel="stylesheet" media="screen"/>
        <script src="/nuxeo/site/easysoa/skin/js/bootstrap._js"></script>
        <!-- custom style and scripts -->
	<link rel="stylesheet" type="text/css" href="/nuxeo/site/easysoa/skin/css/base.css" media="all"/>
	<link rel="shortcut icon" type="image/png" href="/nuxeo/site/easysoa/skin/favicon.ico"/> 
    </head>
    
    <body>
        <#include "/views/EasySOA/macros.ftl">
        
        <div id="header">
            <div id="headerContents">
                <div id="logoLink">&nbsp;</div>
                <div id="headerUserBar"><@displayUserInfo Root.currentUser/></div>
                EasySOA - Suivi des phases
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
                        <h2>Suivi des phases</h2>
                        <p>
                        Ce graphique montre le degré d'avancement (en pourcentage) de chaque phase :
                        </p>
                        <!-- Display a graph with phases state in % -->
                        <table style="border: 0">
                            <tr>
                                <td style="width: 20%"><div id="legendholder"></div></td>
                                <td style="width: 80%"><div id="phaseGraph" style="width:600px;height:400px"></div></td>
                            </tr>
                        </table>
                    </div>
                </li>
                
                <!--<li class="span12">
                    <div class="thumbnail">
                        <img data-src="holder.js/300x200" alt="">            
                        <a class="btn" href="${Root.path}/../?subprojectId=${subprojectId}&visibility=${visibility}">Retour à l'accueil</a>
                    </div>
                </li>-->
                <@displayReturnToIndexButtonBar/>                
            </ul>
        </div>
    </body>
    
    <script type="text/javascript">
        // Function for bargraph rendering
        $(function() {
            //var data = [ ["Spécifications", 75], ["Réalisation", 54], ["Déploiement", 28] ];
            
            var data = [
                {label:'Plus de 75%', color:'green', data: [[1, 78]]},
                {label:'De 50 à 75%', color:'orange', data: [[2, 55]]},
                {label:'Moins de 50%', color:'red', data: [[3, 20]]},
            ];
            
            $.plot("#phaseGraph", data, {
                series: {
                    bars: {
                        show: true,
                        barWidth: 0.8,
                        align: "center"
                    }
                },
                xaxis: {
                    ticks: [[1,'Spécifications'],[2,'Réalisation'],[3,'Déploiement']]
                    //mode: "categories",
                    //tickLength: 0
                },
                yaxis: {
                    max: 100,
                    tickSize: 10
                },
                legend: { 
                    show: true,
                    container: $("#legendholder")
                }
            });

            // Add the Flot version string to the footer
            //$("#footer").prepend("Flot " + $.plot.version + " &ndash; ");
	});    
    </script>
    
</html>
