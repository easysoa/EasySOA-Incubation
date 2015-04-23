/** Compress me as is to bookmarklet-min.js with UglifyJS (http://marijnhaverbeke.nl/uglifyjs) */

function loadJS(url) {
	var newScript = document.createElement("script");
	newScript.type = "text/javascript";
	newScript.src = url;
	document.getElementsByTagName("head")[0].appendChild(newScript);
}

//loadJS("//localhost:8083/js/bookmarklet/discovery.js");
var jsUrl = "http://#{host}:#{port}/js/bookmarklet/discovery.js?" + escape("subprojectId=#{context}&visibility=#{visibility}&registryServerName=#{registryServerName}&setLng=#{setLng}");
console.log("Trying to load bookmarklet from URL", jsUrl);
// NB. WARNING should leave protocol out so it can adapt to the visited page's,
// but requires serving easysoa also in https
try {
	loadJS(jsUrl);
} catch (e) {
	alert("Unknown error while loading URL", jsURL, e);
}


setTimeout(function() {
	if (!window.EASYSOA_WEB) {
		alert("Error: EasySOA seems unreachable.\n\nDetails:\nFailed to load URL " + jsUrl);
	}
}, 500);

void(0);
