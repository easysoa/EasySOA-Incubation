/**
 * Bookmarklet script template
 *
 * rendered by easysoa.js
 * params are : host, port, context, context-escaped, context-display, visibility
 */

/**
 * Globals & constants
 */

console.log("loading bookmarklet");

var jQuery, underscore;
var templates = new Object();
var wsdls = new Array();
var wsdlMap = {};
var username = null;
var frameDragged = false;

var LIBS_POLLING_INTERVAL = 20;
//var EASYSOA_WEB = 'http://localhost:8083';
var EASYSOA_WEB = '#{webDiscoveryUrl}';
var SET_LNG = '#{setLng}';
var TRANSLATION = #{translation};

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
loadJS(EASYSOA_WEB + '/lib/i18next-1.7.1.js'); // .min
loadJS(EASYSOA_WEB + '/lib/underscore.js');
loadCSS(EASYSOA_WEB + '/js/bookmarklet/bookmarklet.css');

// i18next jQuery fn (inspired by i18n.addJqueryFunct())
function addJqueryI18nextFunct(jQuery) {
    // $.t shortcut
	jQuery.t = jQuery.t || i18n.translate;
	var o = i18n.options;

    function parse(ele, key, options) {
        if (key.length === 0) return;

        var attr = 'text';

        if (key.indexOf('[') === 0) {
            var parts = key.split(']');
            key = parts[1];
            attr = parts[0].substr(1, parts[0].length-1);
        }

        if (key.indexOf(';') === key.length-1) {
            key = key.substr(0, key.length-2);
        }

        var optionsToUse;
        if (attr === 'html') {
            optionsToUse = o.defaultValueFromContent ? jQuery.extend({ defaultValue: ele.html() }, options) : options;
            ele.html(jQuery.t(key, optionsToUse));
        } 
        else if (attr === 'text') {
            optionsToUse = o.defaultValueFromContent ? jQuery.extend({ defaultValue: ele.text() }, options) : options;
            ele.text(jQuery.t(key, optionsToUse));
        } else {
            optionsToUse = o.defaultValueFromContent ? jQuery.extend({ defaultValue: ele.attr(attr) }, options) : options;
            ele.attr(attr, jQuery.t(key, optionsToUse));
        }
    }

    function localize(ele, options) {
        var key = ele.attr(o.selectorAttr);
        if (!key && typeof key !== 'undefined' && key !== false) key = ele.text() || ele.val();
        if (!key) return;

        var target = ele
          , targetSelector = ele.data("i18n-target");
        if (targetSelector) {
            target = ele.find(targetSelector) || ele;
        }

        if (!options && o.useDataAttrOptions === true) {
            options = ele.data("i18n-options");
        }
        options = options || {};

        if (key.indexOf(';') >= 0) {
            var keys = key.split(';');

            jQuery.each(keys, function(m, k) {
                if (k !== '') parse(target, k, options);
            });

        } else {
            parse(target, key, options);
        }

        if (o.useDataAttrOptions === true) ele.data("i18n-options", options);
    }

    // fn
    jQuery.fn.i18n = function (options) {
        return this.each(function() {
            // localize element itself
            localize(jQuery(this), options);

            // localize childs
            var elements =  jQuery(this).find('[' + o.selectorAttr + ']');
            elements.each(function() { 
                localize(jQuery(this), options);
            });
        });
    };
}

/**
 * Core functions
 */

function load() {
	if (window._ && window.jQuery) {
		underscore = window._.noConflict();
		jQuery = window.jQuery.noConflict();
		
		// i18n using i18next.js
		i18n.init({
			customLoad: function(lngValue, nsValue, options, loadComplete) {
				if (SET_LNG === lngValue) {
					loadComplete(null, TRANSLATION);
				} else {
					loadComplete("Unsupported language " + SET_LNG, null);
				}
			},
			lng: SET_LNG, debug: true, setJqueryExt: false, fallbackLng: 'en_US' }); // else default fallback language is dev
		// DONT load using resGetPath: EASYSOA_WEB + '/locales/__lng__/__ns__.json' else CORS failure
		// DONT useLocalStorage: true because means only caching
		/*console.log("window.localStorage" + window.localStorage);
		if (window.localStorage) {
 	    	window.localStorage.setItem("res_" + SET_LNG, JSON.stringify(TRANSLATION));
		}*/
		addJqueryI18nextFunct(jQuery);
		
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
			$resultsDiv.append(templates['wsdl'](newWSDL));
			$resultsDiv.append(templates['afterWsdls'](null));
			$resultsDiv.i18n();
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

        var context = jQuery('#easysoa-context').attr('value');

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

    var registryServerName = "#{registryServerName}";
    var serverDiv = "";
    if("" !== registryServerName){
       serverDiv = '<div class="easysoa-doc"><span data-i18n="notify.Server">Serveur</span>: ' + registryServerName + '</div>';
    }

	templates['results'] = underscore.template(
	'<div class="easysoa-frame" id="easysoa-tpl-results">\
	  <div class="easysoa-exit" onclick="exit()"></div>\
	  <div class="easysoa-title" data-i18n="notify.Found WSDLs">NOT TRANSLATED Found WSDLs:</div>\
	  <span class="easysoa-doc" data-i18n="notify.clickToSubmit">(click on those you want to submit)</span>\
    </div>');

	templates['login'] = underscore.template(
	'<div class="easysoa-frame" id="easysoa-tpl-login">\
	  <div class="easysoa-exit" onclick="exit()"></div>\
	  <div class="easysoa-title" data-i18n="notify.Login">Login:</div>\
	  <span class="easysoa-form-label" data-i18n="notify.User">User</span> <input type="text" id="easysoa-username" value="Administrator" /><br />\
      <span class="easysoa-form-label" data-i18n="notify.Password">Password</span> <input type="password" id="easysoa-password" value="Administrator" /><br />\
      <input type="submit" id="easysoa-submit" />\
	  <div id="easysoa-login-error"></div>\
    </div>');

	templates['wsdl'] = underscore.template(
	'<div class="easysoa-wsdl-result" onclick="sendWSDL(this)" id="<%= id %>">\
      <span class="easysoa-wsdl-name"><%= serviceName %></span><br />\
	  <a href="<%= serviceURL %>" onclick="return false;" class="easysoa-wsdl-link"><%= serviceURL %></a>\
    </div>');

	templates['afterWsdls'] = underscore.template(
	'<div class="easysoa-doc"><span data-i18n="notify.Environment name">Environment name:</span> <input type="text" id="easysoa-environment" value="Production" /></div>' +
        serverDiv +
        '<div class="easysoa-doc"><span data-i18n="notify.Phase">Phase:</span> <%= unescape(\'#{context-display}\') %><input type="hidden" id="easysoa-context" value="<%= unescape(\'#{context-escaped}\') %>"/></div>' +
        '<div class="easysoa-doc"><a href="#{nuxeoUrl}/site/easysoa/dashboard/?subprojectId=<%= unescape(\'#{context}\') %>&visibility=#{visibility}" target="_blank" data-i18n="[title]index.onceRegistered;index.matchingDashboard" title="Une fois enregistrés, vous pouvez regler la correspondance entre les services et leurs implémentations et définitions dans l\'outil de réconciliation">Mise en correspondance</a></div>'
	);
	// TODO i18n context-display in node.js easysoa.js using i18next-node and reusing i18next js translations
	// NB. the more in depth following code also works for display :
	// '<% if (\'é\'.length==2) print(unescape(\'#{context}\')); else print(decodeURIComponent(\'#{context}\')); %>'
	// i.e. if not utf8 unescape (which actually works also in the second case), else if utf8 decode
	// see also http://benjol.blogspot.fr/2007/01/detect-bad-encoding-javascript-html-utf.html
}

function runTemplate(name, data) {
	var renderedTemplate = templates[name](data);
	jQuery('body').append(renderedTemplate);
	jQuery('body').i18n();
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
