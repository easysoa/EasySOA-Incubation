// EasySOA Web
//
// Copyright (c) 2011-2012 Open Wide and others
//
// MIT licensed
//
// Contact : easysoa-dev@googlegroups.com

require('longjohn');
var http = require('http');
var express = require('express');//, cookieSessions = require('./cookie-sessions');
var fs = require('fs');
var net = require('net');
var settings = require('./settings');
var authComponent = require('./auth');
var proxyComponent = require('./proxy');
var dbbComponent = require('./dbb');
var lightComponent = require('./light');
var nuxeoComponent = require('./nuxeo');
var utils = require('./utils');

//var settings.networkIPAddress;
//var settings.clientHost;

dbbComponent.setClientAddressToSessionMap(authComponent.clientAddressToSessionMap);

/**
 * Application entry point.
 * Serves both an EasySOA Web server & a discovery by browsing proxy
 *
 * Author: Marwane Kalam-Alami
 */

/*
 * Set up web server
 */
var app = express();
var webServer = http.createServer(app);

// Gets network (non local 0.0.0.0) IP
// TODO BETTER USE request.headers.host ex. 192.168.2.163:8083 OK!!!
// Works by opening a socket with google
// NB. alternatives :
// * os.networkInterfaces() BUT doesn't work on Windows (in dec 2012)
// * execute & parse output of programs like ifconfig & ipconfig
// http://stackoverflow.com/questions/3653065/get-local-ip-address-in-node-js
exports.getNetworkIP = getNetworkIP = function(callback) {

    var socket = net.createConnection(80, 'www.google.com');
    socket.on('connect', function() {
        callback(undefined, socket.address().address);
        socket.end();
    });
    socket.on('error', function(e) {
        callback(e, 'error');
    });
}
exports.getClientHost = getClientHost = function(request) {
	// TODO handle proxying ex. by using custom header,
	// see X-EasySOA-Orig-IP at bottom or Nuxeo's VirtualHostHelper
	var reqHostPort = request.headers.host;
    var colonIndex = reqHostPort.indexOf(':');
    var reqHost = reqHostPort.substring(0, colonIndex);
    ///console.log(reqHost);
    return reqHost;
}

app.configure(function(){

    // Set the network IP address
    getNetworkIP(function (error, ip) {
        console.log(ip);
        settings.networkIPAddress = ip;
        if (error) {
            console.log('error:', error);
        }
    });

  // Request formatting
  app.use(express.cookieParser());
  app.use(express.session({ secret: 'easysoa-web' }));
  app.use(express.bodyParser());

  // Components routing & middleware configuration
  authComponent.configure(app);
  dbbComponent.configure(app, webServer);
  lightComponent.configure(app);
  nuxeoComponent.configure(app);

  // Router
  app.use(app.router);

  // Static file server
  app.use(express.favicon(settings.WWW_PATH + '/favicon.ico'));
  app.use(express.static(settings.WWW_PATH));
  app.use(express.directory(settings.WWW_PATH));

  // To set dynamically the host and port in the bookmarklet template to generate the bookmarklet script
  app.get('/js/bookmarklet/bookmarklet-min.js', function(req, res) {
    var context = unescape(req.param('subprojectId', ''));
    var visibility = unescape(req.param('visibility', ''));
    var registryServerName = unescape(req.param('registryServerName', ''));
    var setLng = unescape(req.param('setLng', ''));
    settings.clientHost = getClientHost(req)
    var jsFile = fs.readFileSync("../www/js/bookmarklet/bookmarklet-min.js","utf8");
    //jsFile = jsFile.replace("#{host}", webServer.address().address); // this method webServer.address().address is useless, return always local loopback 0.0.0.0
    jsFile = jsFile.replace("#{host}", settings.clientHost); // better than networkIPAddress because works offline
    jsFile = jsFile.replace("#{port}", webServer.address().port);
    jsFile = jsFile.replace("#{context}", context);
    jsFile = jsFile.replace("#{visibility}", visibility);
    jsFile = jsFile.replace("#{registryServerName}", registryServerName);
    jsFile = jsFile.replace("#{setLng}", setLng);
    res.writeHead(200);
    res.end(jsFile);
  });

  // To set dynamically the host and port in the discovery.js file
  app.get('/js/bookmarklet/discovery.js*', function(req, res) {
    console.log("[DEBUG] ", "passing in discovery.js template function");
    var context = unescape(req.param('subprojectId', ''));
    var visibility = unescape(req.param('visibility', ''));
    var registryServerName = unescape(req.param('registryServerName', ''));
    var setLng = unescape(req.param('setLng', ''));
    if (!setLng || setLng === "" || setLng === "undefined") {
    	setLng = "en"; // default language for bookmarklet
    }
    ///console.log("[DEBUG] ", "setLng", setLng);
    var translation = fs.readFileSync("../www/locales/" + setLng + "/translation.json","utf8");
    if (!translation) {
    	translation = "{}";
    }
    ///console.log("[DEBUG] ", "translation", translation);
    settings.clientHost = getClientHost(req)
    var jsFile = fs.readFileSync("../www/js/bookmarklet/discovery.js","utf8");

    // replacing variables of our poor man's template script :
    // NB. /g regex flag allows to do a replaceAll, see http://stackoverflow.com/questions/1144783/replacing-all-occurrences-of-a-string-in-javascript
    //jsFile = jsFile.replace("#{host}", webServer.address().address); // this method webServer.address().address is useless, return always local loopback 0.0.0.0
    jsFile = jsFile.replace(/#{webDiscoveryUrl}/g, "http://" + settings.clientHost+ ":" + webServer.address().port);// better than networkIPAddress because works offline
    jsFile = jsFile.replace(/#{context-escaped}/g, escape(context)); // For input field, escape required to avoid encoding problem
    jsFile = jsFile.replace(/#{context-display}/g, escape(utils.formatPhaseForDisplay(context))); // For display, idem
    // 3 following replace for matching dashboard link
    jsFile = jsFile.replace(/#{nuxeoUrl}/g, NUXEO_URL);
    jsFile = jsFile.replace(/#{context}/g, context); // Escape not required in this case (link to matching)
    jsFile = jsFile.replace(/#{visibility}/g, visibility);
    jsFile = jsFile.replace(/#{registryServerName}/g, registryServerName);
    jsFile = jsFile.replace(/#{setLng}/g, setLng);
    jsFile = jsFile.replace(/#{translation}/g, translation);

    res.writeHead(200);
    console.log("[DEBUG] ", "End of discovery.js template function");
    ///console.log("[DEBUG] ", "jsFile", jsFile);
    res.end(jsFile);
  });

  // To set dynamically the nuxeo url and easysoa root url in index.html
  /**app.get('/index.html', function(req, res) {
    var jsFile = fs.readFileSync("../www/index.html","utf8");
    jsFile = jsFile.replace("#{easysoaRootUrl}", EASYSOA_ROOT_URL);
    jsFile = jsFile.replace("#{nuxeoUrl}", NUXEO_URL);
    res.writeHead(200);
    res.end(jsFile);
  });*/

});
webServer.listen(settings.WEB_PORT);

nuxeoComponent.startConnectionChecker();

/*
 * Set up proxy server
 */
var proxyServer = http.createServer(function(request, response) {

  // Service finder
  dbbComponent.handleProxyRequest(request, response);

  request.headers['X-EasySOA-Orig-IP'] = request.connection.remoteAddress;

  // Proxy
  proxyComponent.handleProxyRequest(request, response, settings);

});
proxyServer.listen(settings.PROXY_PORT);

