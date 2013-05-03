// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var http = require('http');
var url = require('url');

var base64 = require('./lib/base64').base64;
var settings = require('./settings');
var proxy = require('./proxy');
var utils = require('./utils');

/**
 * Nuxeo component, allowing to perform requests to the registry.
 *
 * Author: Marwane Kalam-Alami
 */


//=============== Model ================

NUXEO_REST_PARSED_URL = url.parse(settings.NUXEO_REST_URL);
EASYSOA_ROOT_URL_PARSED = url.parse(settings.EASYSOA_ROOT_URL);

var ready = false;

//================ I/O =================

exports.configure = function(app) {
	app.get('/nuxeo/registry/post*', forwardBookmarkletPosts);
	app.get('/nuxeo/registry*', forwardToNuxeo);
	app.get('/nuxeo/servicefinder*', forwardToNuxeo);
	app.all('/nuxeo/dashboard*', forwardToNuxeo);
};

forwardToNuxeo = function(request, response, next) {
	var parsedUrl = url.parse(request.url);
	proxy.forwardTo(request, response,
			EASYSOA_ROOT_URL_PARSED.hostname,
			EASYSOA_ROOT_URL_PARSED.port,
			EASYSOA_ROOT_URL_PARSED.path + parsedUrl.path.replace('/nuxeo', ''));
};

forwardBookmarkletPosts = function(request, response, next) {
        var forwardedRequestHeaders = request.headers; // TODO if pb, clone
    var forwardedBody = request.query; // TODO if pb, clone
    forwardedRequestHeaders['content-type'] = 'application/json';
    //delete forwardedBody['_']; // (timestamp ??) from jquery's $ajax' 'script' & 'jsonp' dataType
        
    if (forwardedBody.callback) {
        var callback = forwardedBody.callback;
        delete forwardedBody.callback;
        delete forwardedBody['_']; // (timestamp ??) from jquery's $ajax' 'script' & 'jsonp' dataType
    }

    runRestRequest(request.session, 'easysoa/registry',
        'POST', forwardedRequestHeaders, forwardedBody,
        function(responseData) {
            response.setHeader("Content-Type", "application/json");
            //response.setHeader("Access-Control-Allow-Origin", "*"); // doesn't work because error "XMLHttpRequest cannot load [url]. Cannot
            // use wildcard in Access-Control-Allow-Origin when credentials flag is true.", see http://stackoverflow.com/questions/3506208/jquery-ajax-cross-domain http://caniuse.com/#feat=cors
            //response.writeHead(200, {'Content-Length': responseData.length});
            //response.write("{}"); // responseData
            if (callback) {
                responseData = callback + '(' + responseData + ")";
            }
            response.write(responseData); // responseData
            response.end();
        }
  );
    
};


exports.runRestRequest = runRestRequest = function(session, path, method, headers, body, callback) {
  // Normalize optional params
  var method = method || 'GET';
  var headers = headers || {};
  var callback = callback || (function() {});
  var bodyString = '';
  if (body) {
    if (typeof body != 'string') {
      bodyString = JSON.stringify(body);
    }
    else {
      bodyString = body;
    }
  }
  
  // Set headers
  if (session && session.username) {
	  headers['Authorization'] = utils.encodeAuthorization(session.username, session.password);
	  if (bodyString) {
	    headers['Content-Length'] = bodyString.length;
	  }
  }
  
  var requestOptions = {
	  'port' : NUXEO_REST_PARSED_URL.port,
	  'method' : method,
	  'host' : NUXEO_REST_PARSED_URL.hostname,
	  'path' : NUXEO_REST_PARSED_URL.path + '/' + path,
	  'headers' : headers
  };
  
  var nxRequest = http.request(requestOptions, function(response) {
    var responseData = '';
    response.on('data', function(data) {
      responseData += data;
    });
      response.on('end', function() {
      callback(responseData);
		});
  });
  
  nxRequest.on('error', function(data) {
    callback(false, data);
  });
  
  nxRequest.end(bodyString);

};

exports.runAutomationRequest = runAutomationRequest = function(session, operation, params, headers, callback, method) {
	
  // Normalize optional params (callback is also opt.)
  operation = operation || '';
  params = params || '';
  headers = headers || {};
  method = method || 'POST';
      
  headers['Content-Type'] = 'application/json+nxrequest';
  headers['Accept'] = 'application/json+nxentity, */*';
  body = {
	context: {},
	params: params
  };

  runRestRequest(session, 'automation/' + operation,
		  method || 'POST', headers, body, callback);
};

exports.runAutomationDocumentQuery = function(session, query, schemasToInclude, callback) {
	if (session.username != null) {
		runAutomationRequest(
			session,
			'Document.Query',
			{'query': query},
			(schemasToInclude) ? {'X-NXDocumentProperties': 'dublincore, servicedef'} : null, // TODO v1 spnode, soanode, informationservice, (wsdlinfo, restinfo, platform, architecturecomponent) (possibly also lookup impls ??)
			callback
		);
	}
	else {
		callback(false, "Invalid username");
	}
};


//============= Controller =============

exports.isReady = function() {
	return ready;
};

exports.areCredentialsValid = areCredentialsValid = function(username, password, callback) {
	runAutomationRequest({username: username, password: password},
		null, null, null,
		function(data) {
			callback(data[0] == "{"); // If login succeeded, we get the automation doc
		},
		method='GET'
	);
};

exports.startConnectionChecker = function() {
	console.log('Checking if Nuxeo is ready...');
	checkConnection();
};

checkConnection = function() {
	areCredentialsValid('Administrator', 'Administrator', function(valid) { // TODO : credential hard coded here ??
		if (valid) {
			console.log('Nuxeo is ready.');
			ready = true;
		}
		else {
			console.log("...");
			setTimeout(checkConnection, 2000);
		}
	});
};
