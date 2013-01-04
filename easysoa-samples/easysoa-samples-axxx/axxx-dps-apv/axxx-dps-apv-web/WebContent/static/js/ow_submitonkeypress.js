/**
 * these javascript functions execute addEventOnEnterKeyPressed when document is loaded.
 * addEventOnEnterKeyPressed takes a form name and a function, it browses the form name element 
 * looking for input type=text to execute someSubmitFunction when the Enter key is pressed.
 * Example of call:
 * 
 jQuery(document).ready(function(){
		addOnloadEventOnEnterKeyPressed(
				addEventOnEnterKeyPressed,
				someFormName,
				someSubmitFunction);
	}); 
 */
function addOnloadEventOnEnterKeyPressed(func, someFormName, someSubmitFunction) {
	try {
		if(typeof window.addEventListener != 'undefined') {
			window.addEventListener('load', func(someFormName,someSubmitFunction), false);
		} else if (typeof document.addEventListener != 'undefined') {
			document.addEventListener('load', func(someFormName,someSubmitFunction), false);
		} else if (typeof window.attachEvent != 'undefined') {
			window.attachEvent('onload', func(someFormName,someSubmitFunction));
		} else {
			if (typeof window.onload == 'function') {
				var oldonload = onload;
				window.onload = function() {
					oldonload();
					func(someFormName,someSubmitFunction);
				};
			} else {
				window.onload = func(someFormName,someSubmitFunction);
			}
		}
	} catch (err) {}
}

function addEventOnEnterKeyPressed(someFormName,someSubmitFunction) {
	
	someForm = document.forms[someFormName]; 
	/*types="";*/
	for (i=0 ; i<someForm.length ; i++){
		
		if (someForm[i].type == 'text' || 
				someForm[i].type == 'radio' || 
					someForm[i].type == 'checkbox' ||  
						someForm[i].type == 'select-one' ||
						someForm[i].type == 'password') { 
			jQuery(someForm[i]).keypress(function(e){
				var keycode;
				if (window.event) {
					keycode = window.event.keyCode;
				} else if (e) { 
					keycode = e.which;
				}
				if(keycode == "13"){ /* user pressed enter !*/
					someSubmitFunction();
					return false; /* returning false will prevent IE to submit form as well. */
				} 
			});
		
		} /*else {
			types+=someForm[i].type+",";
		}*/
		
	}
	
	/*alert(types);*/
}
