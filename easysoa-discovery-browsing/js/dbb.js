// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var httpProxy = require('http-proxy');
var http = require('http');
var socketio = require('socket.io');
var url = require('url');

var settings = require('./settings');
var proxy = require('./proxy');
var nuxeo = require('./nuxeo');
var utils = require('./utils');

var clientAddressToSessionMap; // must be set to auth's by easysoa.js'
exports.setClientAddressToSessionMap = function(map) {
    clientAddressToSessionMap = map;
}

/**
 * Discovery by Browsing component, responsible for communicating with the DBB client.
 *
 * Author: Marwane Kalam-Alami
 */

//============= Initialize ==============

SERVICE_FINDER_IGNORE_REGEXPS = utils.strToRegexp(settings.SERVICE_FINDER_IGNORE);

// =============== Model ================

var clientWellConfigured = false; // XXX: Is common to everybody (should be part of a session)
var io = null;
var foundWSDLs = [];

returnFoundWSDLsToDbbProxyUi = function(data) {
  if (typeof data == 'string') {
    data = JSON.parse(data);
  } 
  
	if (data.foundLinks) {
		try {
			for (name in data.foundLinks) {
				var newWSDL = {
					applicationName : data.applicationName,
					serviceName : name,
					url : data.foundLinks[name]
				};
				console.log('Found:', newWSDL.serviceName, '(' + newWSDL.url + ')');
				foundWSDLs[newWSDL.url] = JSON.stringify(newWSDL);
			    if (io != null) {
			        io.sockets.emit('wsdl', JSON.stringify(newWSDL));
			    }
			}
		}
		catch (error) {
			console.log('ERROR: While saving a WSDL: ', error);
		}
	}
};

// ================ I/O =================

exports.configure = function(app, webServer) {
	// Router configuration
	app.get('/dbb/clear', clearWSDLs);
	app.get('/dbb/send', sendWSDL);

	// SocketIO init
	io = socketio.listen(webServer);
	io.set('transports', ['htmlfile', 'jsonp-polling']); // Default: websocket, htmlfile, xhr-polling, jsonp-polling
	io.set('log level', 2);
	io.sockets.on('connection', initSocketIOConnection);
};

function getNuxeoSession(request) {
    // NB. strategies to find Nuxeo session :
    // 1. by per-user proxy instance ; but none yet for now, will have to be done in FStudio on-demand HTTProxy LATER
    // 2. by client OS i.e. IP :
    var clientAddress = request.connection.remoteAddress;
    return clientAddressToSessionMap[clientAddress];
}

exports.handleProxyRequest = function(request, response) {
	
	// Confirms that the proxy is indeed seeing requests coming
	confirmClientConfiguration();
	
	// Run service finder
	if (!isIgnoredUrl(request.url)) {
		//findWSDLs(request.url);
                var nuxeoSession = getNuxeoSession(request);
                if(nuxeoSession){
                    findWSDLsAndReturnToDbbProxyUi(nuxeoSession, request.url);                    
                } else {
                    // Send back an error
                    console.log("ERROR:", "Not logged in");
                    //redirectToLoginForm(request, response);
                    response.writeHead(500);
                    response.end("You are not logged in !");
                }
	}
	
};


// ============= Controller =============

initSocketIOConnection = function(socket) {
    // Notify that Nuxeo is ready
    if (nuxeo.isReady()) {
      socket.emit('ready');
    }
    if (clientWellConfigured) {
      socket.emit('proxyack');
    }
    // Send stored WSDLs
    for (key in foundWSDLs) {
      socket.emit('wsdl', foundWSDLs[key]);
    }
};

confirmClientConfiguration = function() {
	clientWellConfigured = true;
};

isIgnoredUrl = function(url) {
	for (key in SERVICE_FINDER_IGNORE_REGEXPS) {
		var regexp = SERVICE_FINDER_IGNORE_REGEXPS[key];
		if (regexp.test(url)) {
			return true;
		}
	}
	return false;
};

//findWSDLsAndReturnToDbbProxyUi = function(url) {
findWSDLsAndReturnToDbbProxyUi = function(session, url) {
	nuxeo.runRestRequest(
		//{username: 'Administrator', password: 'Administrator'}, // Session TODO better, I guess that's because findWSDLs must be able to be called in JSONP
                session,
		settings.EASYSOA_SERVICE_FINDER_PATH + '/find/' + url,
		'GET',
		null,
		null,
		function(data, error) {
			try {
				returnFoundWSDLsToDbbProxyUi(JSON.parse(data));
			}
			catch (error) {
				console.log("ERROR: While finding WSDLs:", error);
			}
		}
	);
};

sendWSDL = function(request, response, next) {
  delete request.query.token; // filter random query addition to avoid browser cache
	nuxeo.runRestRequest(
		request.session,
		settings.EASYSOA_DISCOVERY_PATH,
		'POST',
		{'Content-Type':'application/json'},
		request.query,
		function(data, error) {
			if (data) {
				response.end('ok');
			}
			else {
				response.end(error);
			}
		}
	);
};

clearWSDLs = function(request, response, next) {
	foundWSDLs = [];
	response.end('ok');
};
