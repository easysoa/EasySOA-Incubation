// EasySOA Web
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var url = require('url');

var settings = require('./settings');
var nuxeo = require('./nuxeo');
var proxy = require('./proxy');

/**
 * EasySOA light web services
 * 
 * Author: Marwane Kalam-Alami
 */

// =========== Initialization ===========
SCAFFOLDING_SERVER_PARSED_URL = url.parse(settings.SCAFFOLDING_SERVER_URL);

// ================ I/O =================

exports.configure = function(app) {
	// Router configuration
	app.get('/light/serviceList', serviceList);
	app.get('/scaffoldingProxy', forwardToScaffolder);
};

forwardToScaffolder = function(request, response, next) {
	proxy.forwardTo(request, response, SCAFFOLDING_SERVER_PARSED_URL.hostname,
			SCAFFOLDING_SERVER_PARSED_URL.port);
};

// ============= Controller =============

serviceList = function(request, response, next) {
	fetchServiceList(request.session, function(data, error) {
		var responseData = new Object();
		responseData.success = (data !== false);
		responseData.data = data || error;
		response.write(JSON.stringify(responseData));
		response.end();
	});
};

fetchServiceList = function(session, callback) {
	try {
		nuxeo.runAutomationDocumentQuery(
			session,
			"SELECT * FROM Service WHERE ecm:currentLifeCycleState <> 'deleted'", // TODO v1 InformationService +Phase, -proxy
			'dublincore, servicedef', // TODO v1 spnode, soanode, informationservice, (wsdlinfo, restinfo, platform, architecturecomponent) (possibly also lookup impls ??)
			function(data) {
				try {
					data = JSON.parse(data);
					result = new Array();
					for ( var documentIndex in data.entries) {
						document = data.entries[documentIndex];
						entry = new Object();
						entry.title = document.properties['dc:title'];
						entry.url = document.properties['serv:url']; // TODO v1 endp:url
						if (document.properties['serv:fileUrl'] === null) { // TODO v1
						  document.properties['serv:fileUrl'] = entry.url + '?wsdl';
						}
						entry.lightUrl = '/scaffoldingProxy/?wsdlUrl=' + document.properties['serv:fileUrl'];
						result.push(entry);
					}
					callback(result);
				} catch (error) {
					callback(false, error);
				}
			});
	} catch (error) {
		console.error("[ERROR] Failed to fetch service list: " + error.stack);
		callback(false, error);
	}
};
