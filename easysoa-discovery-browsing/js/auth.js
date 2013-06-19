// EasySOA Web
// Copyright (c) 2011 -2012 Open Wide and others
//
// MIT licensed
//
// Contact : easysoa-dev@googlegroups.com

var settings = require('./settings');
var nuxeo = require('./nuxeo');
var utils = require('./utils');

/**
 * Authentication components, service both the authentication service & filter.
 *
 * Author: Marwane Kalam-Alami
 */

//========== Initialization ============

JSONP_HEADERS = { 'Content-Type' : 'application/javascript' };
LOGIN_FORM_PATH = '/login.html';

var noAuthNeeded = settings.NO_AUTH_NEEDED;

var clientAddressToSessionMap = {};
exports.clientAddressToSessionMap = clientAddressToSessionMap;

noAuthNeeded.push(LOGIN_FORM_PATH);
noAuthNeeded.push('/login');
noAuthNeeded.push('/userdata');
NO_AUTH_NEEDED_REGEXP = utils.strToRegexp(noAuthNeeded);

//================ I/O =================

exports.configure = function(app) {
	// Authentication filter
	app.use(authFilter);

	// Router configuration
	app.get('/login', login);
	app.post('/login', login);
	app.get('/logout', logout);
	app.get('/userdata', getUserdata);
};

authFilter = function(request, response, next) {
	if (isRequestAuthorized(request)) {
		next();
	} else {
		redirectToLoginForm(request, response);
	}
};

// ============= Controller =============

login = function(request, response, next) {
	var params = (request.body && request.body.username) ? request.body : request.query;
	if (params && params.username && params.password) {
		if (nuxeo.isReady()) {
			try {
				nuxeo.areCredentialsValid(
					params.username,
					params.password,
					function(isValid) {
						if (isValid) {
							// Create session
							request.session.username = params.username;
							// XXX: Could store the Base64 hash instead
							request.session.password = params.password;
                                                        // add ip/session in map
                                                        if(request.headers['x-easysoa-orig-ip']){
                                                            clientAddressToSessionMap[request.headers['x-easysoa-orig-ip']] = request.session; // Connexion with proxy
                                                        } else {
                                                            clientAddressToSessionMap[request.connection.remoteAddress] = request.session; // Connexion without proxy
                                                        }

							console.log("[INFO] Session created for: " + params.username);
                                                        console.log("[DEBUG] subprojectId: " + request.query.subprojectId);
                                                        console.log("[DEBUG] visibility: " + request.query.visibility);
							if (params.callback) {
                                                                console.log("[DEBUG] callback : " + params.callback);
								response.writeHead(200, JSONP_HEADERS);
								response.end(params.callback + '({result: "ok"})');
							} else {
                                                                console.log("[DEBUG] prev : " + params.prev + " - decoded : " + decodeURIComponent(params.prev));
								response.redirect(decodeURIComponent(params.prev));
							}
						} else {
							redirectToLoginForm(request, response, true);
						}
					});
			} catch (error) {
				console.log("ERROR:", error);
				redirectToLoginForm(request, response);
			}
		} else {
			redirectToLoginForm(request, response, false, true);
		}
	} else {
		// No credentials provided
		if (params.callback) {
			response.writeHead(400, JSONP_HEADERS);
			response.end(params.callback + '(No credentials provided)');
		} else {
			redirectToLoginForm(request, response, true);
		}
	}
};

logout = function(request, response, next) {
	var username = request.session.username;
	request.session.destroy();

        // remove ip/session
        if(request.headers['x-easysoa-orig-ip']){
            delete clientAddressToSessionMap[request.headers['x-easysoa-orig-ip']]; // Connexion with proxy
        } else {
            delete clientAddressToSessionMap[request.connection.remoteAddress]; // Connexion without proxy
        }

	console.log("[INFO] Session destroyed for: " + username);
	if (request.query && request.query.callback) {
		response.end(request.query.callback + '({result: "ok"})');
	} else {
		response.redirect(request.url);
	}
};

getUserdata = function(request, response, next) {
	if (request.session && request.session.username) {
		var responseData = {};
		responseData.username = request.session.username;
		if (request.query && request.query.callback) { // JSONP support
			response.write(request.query.callback + '('
					+ JSON.stringify(responseData) + ')');
		} else {
			response.write(JSON.stringify(responseData));
		}
	} else {
		if (request.query && request.query.callback) { // JSONP support
			response.write(request.query.callback + '()');
		} else {
			response.writeHead(403);
		}
	}
	response.end();
};

isRequestAuthorized = function(request) {
	if (isAuthenticated(request)) {
		return true;
	} else {
		for ( var key in NO_AUTH_NEEDED_REGEXP) {
			var regex = NO_AUTH_NEEDED_REGEXP[key];
			if (regex.test(request.url)) {
				return true;
			}
		}
	}
	return false;
};

isAuthenticated = function(request) {
	return request.session && request.session.username;
};

redirectToLoginForm = function(request, response, error, nuxeoNotReady) {

	request.body = request.body || {};
	request.query = request.query || {};

	if (request.query.callback) {
		if (nuxeoNotReady) {
			response.writeHead(500, JSONP_HEADERS);
			response.end(request.query.callback
					+ '({result: "error", error: "Internal error: Nuxeo not started"})');
		} else {
			response.writeHead(403, JSONP_HEADERS);
			response.end(request.query.callback
					+ '({result: "error", error: "Forbidden"})');
		}
	} else {
		var prevPage = request.body.prev || request.query.prev || encodeURIComponent(request.url);
		var destinationUrl = LOGIN_FORM_PATH + '?'
				//+ ((prevPage) ? 'prev=' + prevPage + '&' : '')
                                + ((prevPage) ? 'prev=' + prevPage : '')
				+ ((error) ? 'error=true&' : '')
				+ ((nuxeoNotReady) ? 'nuxeoNotReady=true' : '');
		response.redirect(destinationUrl);
	}
};
