/**
 * EasySOA: JavaScript needed on all pages.
 */
 
 /**
 * Header user bar display.
 * - Needs jQuery to be loaded to work.
 * - Insert <div id="headerUserBar"></div> in your HTML to make this work
 * - Username is then available as "window.username"
 */
$('#headerUserBar').ready(function() {

	var notLoggedInMessage = '<img src="/img/user.png" /> Not logged in (<a href="/login.html">log in</a>)';

	jQuery.ajax({
		url: '/userdata',
		success: function(data, textStatus, jqXHR) {
		    var data = jQuery.parseJSON(jqXHR.responseText);
		    if (data.username) {
		   		$('#headerUserBar').html('<img src="/img/user.png" /> Logged as <span id="username">'
		   				+ data.username + '</span> (<a href="/logout?prev='
		   				+ encodeURIComponent(encodeURIComponent(document.URL.replace(document.location.origin, ''))) + '">logout</a>)');
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

});
