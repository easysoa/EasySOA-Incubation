$(function() {

	window.socket = null;
	
	window.AppView = Backbone.View.extend({
	
	    error: function(msg) {
              console.error(msg);
              SubmitForm.view.failure(msg);
	    },
	    
		/**
		 * Application initialization
		 */
		initialize: function() {
			
			// i18n using i18next.js , with backbone see http://pedromadias.wordpress.com/2013/01/27/backbone-js-and-i18n/
			$.i18n.init({
				debug: true,
				fallbackLng: 'en_US' // else default fallback language is dev
			}, function() {
				$(document.body).i18n();
				initIndex();
				
			App.descriptorsView = new DescriptorsView;
			App.frameView = new FrameView;
			App.navBarView = new NavbarView;
			App.submitFormView = new SubmitFormView;
			App.serviceListView = new ServiceListView;
			
        socket = io.connect();
      
        // Show proxy warning after a delay
        setTimeout(function() {
        	nothingProxiedDiv = $('#nothingProxied');
        	if (nothingProxiedDiv != null) {
        		nothingProxiedDiv.show(300);
        	}
        }, 2000);
      
        socket.on('proxyack', function(data) {
              $('#nothingProxied').remove();
        });
        socket.on('error', function(data) {
              App.error(data);
        });
        socket.on('wsdl', function(data) {
              try {
                  data = jQuery.parseJSON(data);
                  if (!Descriptors.contains(data)) {
                    Descriptors.add(data);
                  }
              }
              catch (error) {
                App.error(i18n.t("app.Error while handling 'wsdl' message") + ": "+error);
              }
        });
        socket.on('ready', function(data) {
        	App.submitFormView.info(i18n.t("app.Ready"));
        });

			});  
		}
         
	});
	
	window.App = new AppView;
	
});
