/**
 * Format the phase received from the nuxeo registry service for display in the UI
 */
function formatPhaseForDisplay(context){
    
    var contextDisplay = "";
    if(context != "" && typeof context != "undefined"){
        var phaseShortId = context.replace('/default-domain/', '');
        var phaseShortIdVersionIndex = phaseShortId.lastIndexOf('_v');
        var version;
        if(phaseShortId.match(/_v$/)){
            version = i18n.t("index.current");
        } else {
            version = phaseShortId.substring(phaseShortIdVersionIndex + 2, phaseShortId.length);
        }        
        phaseShortId = phaseShortId.substring(0, phaseShortIdVersionIndex).replace('/', ' / ');
        contextDisplay = phaseShortId + " (" + i18n.t("index.version") + " " + version + ")";
    }
    return contextDisplay;
    
}