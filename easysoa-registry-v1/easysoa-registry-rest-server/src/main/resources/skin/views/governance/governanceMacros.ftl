<#macro displayPhaseMonitoringDiagram indicators>
    <script type="text/javascript">
        // Function for bargraph rendering
        $(function() {
            //var data = [ ["Spécifications", 75], ["Réalisation", 54], ["Déploiement", 28] ];
            /*var data = [
                {label:'Plus de 75%', color:'green', data: [[1, 78]]},
                {label:'De 50 à 75%', color:'orange', data: [[2, 55]]},
                {label:'Moins de 50%', color:'red', data: [[3, 20]]},
            ];*/
            var data = [
                {label:'${indicators["specificationsProgress"].percentage} %', color:'green', data: [[1, ${indicators["specificationsProgress"].percentage}]]}
                ,{label:'${indicators["realisationProgress"].percentage} %', color:'orange', data: [[2, ${indicators["realisationProgress"].percentage}]]}
                ,{label:'${indicators["deploiementProgress"].percentage} %', color:'red', data: [[3, ${indicators["deploiementProgress"].percentage}]]}
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
                    ticks: [
                        [1,'${Root.msg("Specifications")}']
                        ,[2,'${Root.msg("Development")}']
                        ,[3,'${Root.msg("Deployment")}']
                    ]
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
</#macro>