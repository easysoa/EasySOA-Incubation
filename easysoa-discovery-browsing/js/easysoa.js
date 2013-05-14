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
var networkIPAddress;

// Gets network (non local 0.0.0.0) IP
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

app.configure(function(){

    // Set the network IP address
    getNetworkIP(function (error, ip) {
        console.log(ip);
        networkIPAddress = ip;
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
    var jsFile = fs.readFileSync("../www/js/bookmarklet/bookmarklet-min.js","utf8");
    //jsFile = jsFile.replace("#{host}", webServer.address().address); // this method webServer.address().address is useless, return always local loopback 0.0.0.0
    jsFile = jsFile.replace("#{host}", networkIPAddress);
    jsFile = jsFile.replace("#{port}", webServer.address().port);
    jsFile = jsFile.replace("#{context}", context);
    res.writeHead(200);
    res.end(jsFile);
  });

  // To set dynamically the host and port in the discovery.js file  
  app.get('/js/bookmarklet/discovery.js*', function(req, res) {
    console.log("[DEBUG] ", "passing in discovery.js template function");
    var context = unescape(req.param('subprojectId', ''));
    var jsFile = fs.readFileSync("../www/js/bookmarklet/discovery.js","utf8");
    //jsFile = jsFile.replace("#{host}", webServer.address().address); // this method webServer.address().address is useless, return always local loopback 0.0.0.0
    jsFile = jsFile.replace("#{host}", networkIPAddress);
    jsFile = jsFile.replace("#{port}", webServer.address().port);
    jsFile = jsFile.replace("#{context}", context);
    res.writeHead(200);
    console.log("[DEBUG] ", "End of discovery.js template function");
    res.end(jsFile);
  });

    
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
  proxyComponent.handleProxyRequest(request, response);
  
});
proxyServer.listen(settings.PROXY_PORT);

