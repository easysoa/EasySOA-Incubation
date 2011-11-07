// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #1 
 * Description: Create Service Scaffolder Client for a given existing service endpoint
 * Context : Light
 * Author: Marwane Kalam-Alami
 */

var api = require('./api.js');

// Make the user choose a service

var user = "Sophie";
var envFilter = [ "sandbox", "dev" ]; // "sandbox" is a sandboxed version of "staging" i.e. actual, existing services
var serviceEndpointToScaffold = api.selectServiceEndpointInUI(envFilter); // user also navigates or filters

// Create environment

var testEnv = api.createEnvironment("Light", user, "PureAirFlowers"); // on default business architecture

api.addExternalServiceEndpoint(testEnv, serviceEndpointToScaffold);

var scaffolderClient = api.createScaffolderClient(testEnv, serviceEndpointToScaffold);
var scaffolderClientEndpoint = api.addServiceImpl(testEnv, scaffolderClient);

// Launch scaffolder

console.log("Setting up environment "+testEnv.name);
if (api.start(testEnv)) { // starts scaffolder
    api.display(scaffolderClientEndpoint);
    console.log("Done.");
} else {
    console.error("Fail.");
}

// Exports for further scenarios

exports.user = user;
exports.serviceEndpointToScaffold = serviceEndpointToScaffold;
exports.testEnv = testEnv;
exports.scaffolderClient = scaffolderClient;
exports.scaffolderClientEndpoint = scaffolderClientEndpoint;