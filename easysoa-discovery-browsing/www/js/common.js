/**
 * EasySOA: JavaScript needed on all pages.
 */
 
 /**
 * Header user bar display.
 * - Needs jQuery to be loaded and i18next to be inited to work.
 * - To make this work : insert <div id="headerUserBar"></div> in your HTML,
 * and call initCommon() at init (AFTER i18next init) ex. in Backbone initializer function
 * or in a jQuery event handler such as $('#headerUserBar').ready(initCommon)
 * - Username is then available as "window.username"
 */

// i18n'ing mere local .html links ex. /examples.html
function initLinksI18n() {
	$('a.i18n').each(function() {
		this.href += "?setLng=" + i18n.lng();
	});
}

function initCommon() {
	initLinksI18n();
	
	var notLoggedInMessage = '<img src="/img/user.png" />' + i18n.t("common.Not logged in")
	    + ' (<a href="/login.html">' + i18n.t("common.log in") + '</a>)';

	jQuery.ajax({
		url: '/userdata',
		success: function(data, textStatus, jqXHR) {
		    var data = jQuery.parseJSON(jqXHR.responseText);
		    if (data.username) {
		   		$('#headerUserBar').html('<img src="/img/user.png" /> ' + i18n.t("common.Logged as") + ' <span id="username">'
		   				+ data.username + '</span> (<a href="/logout?prev='
		   				+ encodeURIComponent(encodeURIComponent(document.URL.replace(document.location.origin, '')))
		   				+ '">' + i18n.t("common.logout") + '</a>)');
		   		window.username = data.username;
		    }
		    else {
		    	$('#headerUserBar').html(notLoggedInMessage);
		    }
		    $('#headerUserBar').show();
		},
		error: function(data) {
		   	$('#headerUserBar').html(notLoggedInMessage);
		    $('#headerUserBar').show();
		}
	});

}
