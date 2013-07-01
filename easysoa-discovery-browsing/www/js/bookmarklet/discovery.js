/**
 * Globals & constants
 */

var jQuery, underscore;
var templates = new Object();
var wsdls = new Array();
var wsdlMap = {};
var username = null;
var frameDragged = false;

var LIBS_POLLING_INTERVAL = 20;
//var EASYSOA_WEB = 'http://localhost:8083';
var EASYSOA_WEB = 'http://#{host}:#{port}';

/**
 * Load libs
 */

function loadJS(url) {
	var scriptTag = document.createElement('script');
	scriptTag.type = 'text/javascript';
	scriptTag.src = url;
	document.getElementsByTagName('head')[0].appendChild(scriptTag);
}

function loadCSS(url) {
	var linkTag = document.createElement('link');
	linkTag.rel = 'stylesheet';
	linkTag.type = 'text/css';
	linkTag.href = url;
	document.getElementsByTagName('head')[0].appendChild(linkTag);
}

loadJS(EASYSOA_WEB + '/lib/jquery.js');
loadJS(EASYSOA_WEB + '/lib/underscore.js');
loadCSS(EASYSOA_WEB + '/js/bookmarklet/bookmarklet.css');

/**
 * Core functions
 */

function load() {
	if (window._ && window.jQuery) {
		underscore = window._.noConflict();
		jQuery = window.jQuery.noConflict();
		initTemplates();
		start();
	} else {
		setTimeout(load, LIBS_POLLING_INTERVAL);
	}
}

function start() {
	// Remove any previous result
	var previousResults = jQuery('.easysoa-frame');
	if (previousResults.size() != 0) {
		previousResults.hide('fast', function() {
			previousResults.remove();
			start();
		});
	} else {
		checkIsLoggedIn(
		// If logged in, start WSDL search
		function(aUsername) {
			username = aUsername;
			findWSDLs();
		},
		// Else display login form
		function() {
			runTemplate('login');
		});
	}
}

function checkIsLoggedIn(callbackOnSuccess, callbackOnError) {
	jQuery.ajax({
		url : EASYSOA_WEB + '/userdata',
		dataType : 'jsonp',
		success : function(data) {
			if (data && data.username)  {
				callbackOnSuccess(data.username);
			}
			else {
				callbackOnError();
			}
		},
		error : function() {
			callbackOnError();
		}
	});
}

/**
		success : function(data, textStatus, xhr) {
			if (data.foundLinks) {
				appendWSDLs(data);
			}
			else {
				for (errorKey in data.errors) {
					console.log("EasySOA link parsing ERROR: ", data.errors[errorKey]);
				}
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			console.log("EasySOA jsonp ERROR: ", xhr.responseText);
		}
*/

function findWSDLs() {
	runTemplate('results');
	runServiceFinder(window.location.href);
}

// Scrapes HTML links using remote Nuxeo ServiceFinder
// and its configured strategies.
function runServiceFinder(theUrl, localScrapingLinks, localScrapingIndex) {
	// Send request to Nuxeo
	jQuery.ajax({
		url : EASYSOA_WEB + '/nuxeo/servicefinder/find/' + theUrl, // + '.js',
		dataType : 'jsonp',
		accepts : 'application/javascript',
		crossDomain : true, // default: false for same-domain requests, true for cross-domain requests
		//async = false, // default
        xhrFields: {
            withCredentials: true
        }, // for cross-domain requests
		//scriptCharset
		// error : This handler is not called for cross-domain script and cross-domain JSONP requests.
		success : function(data, textStatus, xhr) {
			if (data.foundLinks) {
				appendWSDLs(data);
			}
			else {
				for (errorKey in data.errors) {
					console.log("EasySOA link parsing ERROR: ", data.errors[errorKey]);
				}
			}

			// local scraping loop using recursive callback
			runLocalScrapingServiceFinder(localScrapingLinks, localScrapingIndex);
		},
		error : function(xhr, textStatus, errorThrown) {
			console.log("EasySOA jsonp ERROR: ", xhr.responseText);
		}
	});
}

function appendWSDLs(data) {
	var $resultsDiv = jQuery('#easysoa-tpl-results');
	var wsdlEntryTpl = templates['wsdl'];
	for (key in data.foundLinks) {
		var newWSDL = {
			id: wsdls.length,
			applicationName: data.applicationName,
			serviceName: key,
			serviceURL: data.foundLinks[key]
		};

		// avoiding to display twice the same service (happens when local scraping
		// sends a link to this same HTML page, therefore containing the same WSDL URLs)
		if (!wsdlMap[newWSDL.serviceURL]) {
			wsdls.push(newWSDL);
			wsdlMap[newWSDL.serviceURL] = newWSDL;
			$resultsDiv.append(wsdlEntryTpl(newWSDL));
			$resultsDiv.append(templates['afterWsdls'](null));
		}
	}
}

// HACK Local scraping to filter WSDLs and send them to Nuxeo,
// in case it couldn't get access to the page
// NB. scraping sequentially using recursive callbacks rather than "for" loop
// to avoid messing with condition !wsdlMap[linkUrl] below
function runLocalScrapingServiceFinder(links, i) {
	if (!links) {
		// loop init
		var links = jQuery('a');
		var i = 0;
	}

	// loop end condition
	if (i < links.length) {

		// loop iteration code
		var linkUrl = jQuery(links[i]).prop('href'); // And not jQuery(this).attr('href') otherwise only a relative link
        if (linkUrl
        		&& (linkUrl.substr(-4) == 'wsdl' || linkUrl.substr(-4) == 'WSDL')
        		&& !wsdlMap[linkUrl]) {
        	// next iteration through async runServiceFinder
			runServiceFinder(linkUrl, links, i + 1);
		} else {
			// next iteration directly
			runLocalScrapingServiceFinder(links, i + 1);
		}
	}
}

function sendWSDL(domElement) {
	var $domElement = jQuery(domElement);
	var wsdlToSend = wsdls[$domElement.attr('id')];

	  var environmentName = jQuery('#easysoa-environment').attr('value');
	  var endpointUrl = wsdlToSend.serviceURL.replace('?wsdl', '').replace('?WSDL', '');

        var context = unescape(jQuery('#easysoa-context').attr('value'));

		jQuery.ajax({
			url : EASYSOA_WEB + '/nuxeo/registry/post',
			dataType : 'jsonp',
			//dataType : 'json', // result is sent to success callback (but sends Origin header,
                        // and if not same domaine origin, there will be no response body, see http://en.wikipedia.org/wiki/Same_origin_policy )
			//dataType : 'script', // result is evaluated but not sent to success callback
                        //accepts : 'application/javascript',
                        //crossDomain : true, // default: false for same-domain requests, true for cross-domain requests
		//async = false, // default
        xhrFields: {
            withCredentials: true
        }, // for cross-domain requests (REQUIRED for sendWSDL to /nuxeo/registry/post)
			data : {
        'id': {
          'subprojectId' : context,
          'type': 'Endpoint',
          'name': environmentName + ':' + endpointUrl // TODO Environment should not be hardcoded
        },
        'properties': {
          'spnode:subproject':context,
          'endp:url': endpointUrl,
          'env:environment': environmentName,
          'dc:title': environmentName + ' - ' + endpointUrl, // rather than wsdlToSend.serviceName because not discriminant enough
          'dc:description': environmentName + ' - ' + wsdlToSend.serviceName,
          'rdi:url' : wsdlToSend.serviceURL
          // TODO v1 LATER component / platform, probe
        }
      },
			success : function(data, textStatus, xhr) {
				if (data.result == "success") {
					jQuery(domElement).css({ 'background-color': '#CD5', 'opacity' : 1 });
				}
				else {
					console.warn("EasySOA ERROR: ", data.result);
					jQuery(domElement).css({ 'background-color': '#C77', 'opacity' : 1 });
				}
			},
                        error : function(xhr, textStatus, errorThrown) {
                            console.warn("EasySOA ERROR: Request failure - ", xhr.responseText);
			}
		});

	$domElement.animate({opacity:0.5}, 'fast', function() {
	});
}

function exit() {
	jQuery('.easysoa-frame').hide('fast');
	jQuery.ajax({
		url : EASYSOA_WEB + '/logout',
		dataType : 'jsonp'
	});
}

/**
 * HTML templates
 */

function initTemplates() {

	templates['results'] = underscore.template(
	'<div class="easysoa-frame" id="easysoa-tpl-results">\
	  <div class="easysoa-exit" onclick="exit()"></div>\
	  <div class="easysoa-title">Found WSDLs:</div>\
	  <span class="easysoa-doc">(click on those you want to submit)</span>\
    </div>');

	templates['login'] = underscore.template(
	'<div class="easysoa-frame" id="easysoa-tpl-login">\
	  <div class="easysoa-exit" onclick="exit()"></div>\
	  <div class="easysoa-title">Login:</div>\
	  <div class="easysoa-form-label">User </div><input type="text" id="easysoa-username" value="Administrator" /><br />\
      <div class="easysoa-form-label">Password </div><input type="password" id="easysoa-password" value="Administrator" /><br />\
      <input type="submit" id="easysoa-submit" />\
	  <div id="easysoa-login-error"></div>\
    </div>');

	templates['wsdl'] = underscore.template(
	'<div class="easysoa-wsdl-result" onclick="sendWSDL(this)" id="<%= id %>">\
      <span class="easysoa-wsdl-name"><%= serviceName %></span><br />\
	  <a href="<%= serviceURL %>" onclick="return false;" class="easysoa-wsdl-link"><%= serviceURL %></a>\
    </div>');

	templates['afterWsdls'] = underscore.template(
	'<div class="easysoa-doc">Environment name: <input type="text" id="easysoa-environment" value="Production" /></div>' +
        '<div class="easysoa-doc">Phase: #{context-display}<input type="hidden" id="easysoa-context" value="#{context}"/></div>' +
        '<div class="easysoa-doc"><a href="#{nuxeoUrl}/site/easysoa/dashboard/?subprojectId=#{context}&visibility=#{visibility}" target="_blank" title="Une fois enregistrés, vous pouvez regler la correspondance entre les services et leurs implémentations et définitions dans l\'outil de réconciliation">Mise en correspondance</a></div>'
	);
}

function runTemplate(name, data) {
	jQuery('body').append(templates[name](data));
	jQuery('#easysoa-tpl-' + name).show('fast');

	// Make frames draggables
	if (name == 'login' || name == 'results') {
		jQuery('.easysoa-title').mousedown(function() {
			frameDragged = true;
		});
		jQuery(window).mousemove(function(e) {
			if (frameDragged) {
				var frame = jQuery('.easysoa-frame');
				frame.css({
					left: e.clientX - (frame.width() / 2),
					top: e.clientY - 20
				});
			}
		});
		jQuery(window).mouseup(function() {
			frameDragged = false;
		});
	}

	// Login template handler
	if (name == 'login') {
		jQuery('#easysoa-submit').click(function() {
			jQuery.ajax({
				url : EASYSOA_WEB + '/login',
				data : {
					username : jQuery('#easysoa-username').attr('value'),
					password : jQuery('#easysoa-password').attr('value')
				},
				dataType : 'jsonp',
				success : function(data) {
					if (data.result == "ok") {
						start();
					}
					else {
						var errorTag = jQuery('#easysoa-login-error');
						errorTag.hide('fast');
						errorTag.html('ERROR: ' + data.error);
						errorTag.show('fast');
					}
				},
				error : function() {
					var errorTag = jQuery('#easysoa-login-error');
					errorTag.hide('fast');
					errorTag.html('ERROR: Failed to run login request');
					errorTag.show('fast');
				}
			});
		});
	}
}

/**
 * Bootstrap
 */

setTimeout(load, LIBS_POLLING_INTERVAL);
