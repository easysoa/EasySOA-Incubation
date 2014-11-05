// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var base64 = require('./lib/base64').base64;

/**
 * Misc functions
 *
 * Author: Marwane Kalam-Alami
 */

exports.encodeAuthorization = function(username, password) {
    if (username !== null && password !== null) {
        return "Basic " + base64.encode(username + ':' + password);
    }
    else {
        return null;
    }
};

exports.strToRegexp = function(strings) {
	if (!(strings instanceof Array)) {
		strings = [ strings ];
	}
	var result = [];
	for (var key in strings) {
		result.push(new RegExp(strings[key]));
	}
	return result;
};

/**
 * Format the phase received from the nuxeo registry service for display in the UI
 * TODO i18n using i18next-node and reusing i18next js translations
 */
exports.formatPhaseForDisplay = function(context){
    
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
};