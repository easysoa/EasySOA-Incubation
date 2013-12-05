// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var httpProxy = require('http-proxy');
var url = require('url');
var base64 = require('./lib/base64').base64;
var utils = require('./utils');

/**
 * Basic HTTP proxy component, using 'node-http-proxy'.
 *
 * Author: Marwane Kalam-Alami
 */

// Proxy Initialization
var proxy = new httpProxy.RoutingProxy();

proxy.on('proxyError', function(error, request, result) {
    result.end("<h1>Error "+error.errno+"</h1>" +
    		   "<p>"+error.message+"</p>");
});

// Proxy handler

// if null host & port, defaults to request URL's (default proxy behaviour)
exports.forwardTo = forwardTo = function(request, response, host, port, path) {
	var parsedUrl = null;
	if (host == null || port == null) {
		parsedUrl = url.parse(request.url);
	}
	if (path != null) {
		request.url = path;
	}
	if (request.session && request.session.username) {
		request.headers['Authorization'] = utils.encodeAuthorization(request.session.username, request.session.password);
	}
	try {
		///console.log("proxying to " + (host || parsedUrl.hostname) + ":" + (port || parsedUrl.port || 80) + " for " + request.url);
	  proxy.proxyRequest(request, response, {
	      'host': host || parsedUrl.hostname,
	      'port': port || parsedUrl.port || 80 // default http url port
	  });
	} catch (error) {
	  response.writeHead(500);
	  response.end('Failed to proxy request: ' + e);
	  console.log(error);
	}
};

exports.handleProxyRequest = function(request, response, settings) {
	var parsedUrl = url.parse(request.url);
	var host, port;
	if (settings.clientHost // else not yet inited, can't check if easysoa itself
			&& settings.FORWARD_PROXY_HOST && parsedUrl.hostname != settings.clientHost) {
		host = settings.FORWARD_PROXY_HOST;
		port = settings.FORWARD_PROXY_PORT || 80;
	} else {
		// no forward proxy, or easysoa target
		host = parsedUrl.hostname;
		port = parsedUrl.port || 80;
	}
	forwardTo(request, response, host, port, parsedUrl.path);
};


