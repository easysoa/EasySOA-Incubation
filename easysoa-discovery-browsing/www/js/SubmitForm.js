$(function() {

	window.SubmitFormModel = Backbone.Model.extend({

		HTTP: "http://",
		
		initialize: function() {
			this.set({
				"url": "",
				"applicationName": "",
				"serviceName": ""
			});
		},
		
		getURL: function() {
			var url = this.get("url");
			return (url.indexOf(this.HTTP) != -1) ? url : this.HTTP+url;
		},
		
		getApplicationName: function() {
			return this.get("applicationName");
		},
		
		getServiceName: function() {
			return this.get("serviceName");
		},
		
		submit: function() {
			if (this.getURL() != '') {
          this.view.info("Sending request...");
          var environmentName = $('#environmentSelect option').filter(":selected").attr('value');
          var context = $('#easysoa-context').attr('value');
          
          var url = this.getURL();
			    jQuery.ajax({
			        url: '/dbb/send?token=' + Math.random(), // avoids caching
			        data: {
			            'id': {
                                      'subprojectId' : context,
			              'type': 'Endpoint',
			              'name': environmentName + ':' + url
			            },
			            'properties': {
                                      'spnode:subproject':context,
			              'endp:url': url,
			              'env:environment': environmentName,
			              'dc:title': environmentName + ': ' + $('#submitService').attr('value') //FIXME Better property sync with getServiceName()
			              // TODO v1 LATER component / platform, probe
			            }
			          },
			        success: function (data) {
			            if (data == 'ok') {
                     SubmitForm.view.success();
                  }
                  else {
                     SubmitForm.view.failure(data);
                  }
                },
              error: function (data) {
                  SubmitForm.view.failure(data);
                }
			      });
			}
		},
		
		select: function(descriptor) {
			this.set({
				"url": descriptor.url,
				"serviceName": descriptor.serviceName,
				"applicationName": descriptor.applicationName
			});
		}
		
	});

	window.SubmitForm = new SubmitFormModel;
		
	window.SubmitFormView = Backbone.View.extend({
		
		el: $('#menuSubmitForm'),

		selectedWSDL: $('#submitSelectedWSDL'),
		submitService: $('#submitService'),
		submitApp: $('#submitApp'),
		messageInfo: $('#messageInfo'),
		messageSuccess: $('#messageSuccess'),
		messageFailure: $('#messageFailure'),
		environmentSelect: $('#environmentSelect'),
		
		events: {
			"click #submit": "submit",
			"click #newEnvironment": "newEnvironment"
		},
		
		initialize: function() {
			_.bindAll(this, 'render', 'newEnvironment');
			SubmitForm.view = this;
			SubmitForm.bind('change', this.render);
			
      // Init environment list
      this.environmentSelect.append('<option value="Production">Production</option>');
      var view = this;
      $.getJSON('/nuxeo/servicefinder/environments',
        function(data, textStatus, jqXHR) {
            var environments = jQuery.parseJSON(data);
            for (i in environments) {
              if (environments[i] != 'Production') {
                view.environmentSelect.append('<option value="' + environments[i] + '">' + environments[i] + '</option>');
              }
            }
        });
		},
	
		render: function() {
			this.selectedWSDL.html(SubmitForm.getURL());
			this.submitApp.attr("value", SubmitForm.getApplicationName());
			this.submitService.attr("value", SubmitForm.getServiceName());
			return this;
		},

		submit: function() {
			SubmitForm.submit();
		},
		
		hideAllMessages: function() {
			this.messageInfo.hide();
			this.messageSuccess.hide();
			this.messageFailure.hide();
		},
		
		info: function(msg) {
			this.hideAllMessages();
			this.messageInfo.html(msg);
			this.messageInfo.fadeIn(500, function() {
					setTimeout(function() {
						$('#messageInfo').fadeOut(500);
					}, 3000);
				});
		},
		
		success: function() {
			this.hideAllMessages();
			this.messageSuccess.html('<img src="img/ok.png" height="15" /> WSDL registered');
			this.messageSuccess.fadeIn(500, function() {
					setTimeout(function() {
						$('#messageSuccess').fadeOut(500);
					}, 3000);
				});
		},
		
		failure: function(error) {
			this.hideAllMessages();
			this.messageFailure.html(error);
			this.messageFailure.fadeIn(500, function() {
					setTimeout(function() {
						$('#messageFailure').fadeOut(500);
					}, 5000);
				});
		},
		
		newEnvironment: function() {
		  var newEnvName = prompt('New environment name');
		  if (newEnvName != null) {
		    this.environmentSelect.append('<option value="' + newEnvName + '">' 
		      + newEnvName + '</option>');
		    $('option[value='+newEnvName+']', this.environmentSelect).attr('selected', 'selected');
		  }
		}
		
	});
	
});
