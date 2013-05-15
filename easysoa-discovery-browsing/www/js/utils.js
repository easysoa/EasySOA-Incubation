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
            version = "en cours";
        } else {
            version = phaseShortId.substring(phaseShortIdVersionIndex + 2, phaseShortId.length);
        }        
        phaseShortId = phaseShortId.substring(0, phaseShortIdVersionIndex).replace('/', ' / ');
        contextDisplay = phaseShortId + " (version " + version + ")";
    }
    return contextDisplay;
    
}