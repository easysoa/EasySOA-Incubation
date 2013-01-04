

function preloadingAttachOnloadEvent(func) {
	if(typeof window.addEventListener != 'undefined') {
		window.addEventListener('load', func, false);
	} else if (typeof document.addEventListener != 'undefined') {
		document.addEventListener('load', func, false);
	} else if (typeof window.attachEvent != 'undefined') {
		window.attachEvent('onload', func);
	} else {
		if (typeof window.onload == 'function') {
			var oldonload = onload;
			window.onload = function() {
				oldonload();
				func();
			};
		} else {
			window.onload = func;
		}
	}
}

function addCallToShowLoadInProgressMessage() {
	
	var links = document.getElementsByTagName("A");

	for (var i=0; i<links.length; i++) {
		
		/* Only add onclick's call to method "ShowLoadInProgressMessage" 
		 * on links that will send user away from the page: 
		 * That is, links that have an HREF defined
		 * and that do not start with either
		 * 		-	"#"
		 * 		- 	current location href + "#" 
		 * 		- 	or "javascript:"
		 * 		- 	or "mailto:"
		 * 		- 	or class names that contains "no-prealoading-msg"
		 * and whose target are not "_blank" or _top.
		 */
		if (links[i].target != '_blank' && links[i].target != 'top' && links[i].target != 'new'
				&& links[i].href.length > 0 
				&& !links[i].href.match(new RegExp("^#","g"))
				&& !links[i].href.match(new RegExp("^"+document.location.href.replace("?","\\?")+"#","g"))
				&& !links[i].href.match(new RegExp("^javascript:","g"))
				&& !links[i].href.match(new RegExp("^mailto:","g"))
				&& !links[i].className.match(new RegExp("no-prealoading-msg","g"))) {
			jQuery(links[i]).click(function(){
				/*alert('click!');*/
				showLoadInProgressMessage();
			});
		}
		
	}
	
	/* NOTE; Works only on forms that are submitted through a input type="submit" 
	 * 	Meaning, it won't work with a document
	 * 	I suspect it works also if form is submitted lile jQuery('#myFormId').submit()
	 * 	but I had not tested it yet 
	 */
	var allForms = document.getElementsByTagName("FORM");
	
	for (var i=0; i<allForms.length; i++) {
		
		jQuery(allForms[i]).submit(function() {
			showLoadInProgressMessage();
		});
	}
	
}

preloadingAttachOnloadEvent(addCallToShowLoadInProgressMessage);

function showLoadInProgressMessage() {

	// todo put this in method param
	
	//var display=true;
	
	 Ext.MessageBox.show({
         msg: 'Requête en cours, merci de patienter...',
         progressText: 'En cours...',
         width:300,
         wait:true,
         waitConfig: {interval:200},
         icon:'ext-mb-download', //custom class 
         animEl: 'mb7'
     });
	/*
	var divBg = document.getElementById('ow-load-in-progress-wrapper');
	var divMsgBox = document.getElementById('ow-load-in-progress');
	*/
	/* When screen is longer than screen size, center in middle */
	
	/* for debug screen vars, un-comment below until alert(resolutions) */
	/*var resolutions =
		"WINDOW :" + "\n" +
		"window.screenTop = " + window.screenTop + "\n" +
		"window.screenLeft = " + window.screenLeft+ "\n" +
		"window.innerHeight = " + window.innerHeight + "\n" +
		"window.innerWidth = " + window.innerWidth   + "\n" +
		"window.outerHeight = " + window.outerHeight + "\n" +
		"window.outerWidth = " + window.outerWidth + "\n" +
		"window.screenX = " + window.screenX + "\n" +
		"window.screenY = " + window.screenY+ "\n" +
		"window.scrollMaxX = " + window.scrollMaxX + "\n" +
		"window.scrollMaxY = " + window.scrollMaxY+ "\n" +
		"window.scrollX = " + window.scrollX + "\n" +
		"window.scrollY = " + window.scrollY+ "\n" +
		"SCREEN :" + "\n" +
		"screen.top = " + screen.top + "\n" +
		"screen.left = " + screen.left + "\n" +
		"screen.width = " + screen.width + "\n" +
		"screen.height = " + screen.height + "\n" +
		"DOCUMENT :" + "\n" +
		"document.height = " + document.height + "\n" +
		"document.width = " + document.width + "\n" +
		"DOCUMENT.BODY :" + "\n" +
		"document.body.clientTop = " + document.body.clientTop + "\n" +
		"document.body.clientLeft = " + document.body.clientLeft+ "\n" +
		"document.body.clientHeight = " + document.body.clientHeight + "\n" +
		"document.body.clientWidth = " + document.body.clientWidth + "\n" +
		"document.body.offsetTop = " + document.body.offsetTop + "\n" +
		"document.body.offsetLeft = " + document.body.offsetLeft + "\n" +
		"document.body.offsetHeight = " + document.body.offsetHeight + "\n" +
		"document.body.offsetWidth = " + document.body.offsetWidth + "\n" +
		"document.body.scrollTop = " + document.body.scrollTop + "\n" +
		"document.body.scrollLeft = " + document.body.scrollLeft + "\n" +
		"document.body.scrollHeight = " + document.body.scrollHeight + "\n" +
		"document.body.scrollWidth = " + document.body.scrollWidth + "\n" +
		"(IE)document.documentElement.scrollTop = " + document.documentElement.scrollTop + "\n" +
		"(IE)document.body.parentNode.scrollTop = " + document.body.parentNode.scrollTop + "\n" +
		"(IE)document.body.scrollTop = " + document.body.scrollTop + "\n" +
		 "";

	alert(resolutions);*/
	/*
	divBg.style.width = document.body.clientWidth + 'px';
	divBg.style.height = document.body.clientHeight + 'px';
	*/
	/* The top position of the message box is calculated 
	 * by adding the scrollPosition to the height of the screen divided by 2
	 * So we need to find these values for each browser  
	 * */
	/*
	if (window.scrollY >= 0 ){ // FF
		
		divMsgBox.style.top = ((window.innerHeight / 2) + window.scrollY) + 'px';
		
	} else if (document.documentElement.scrollTop >= 0 ) { // IE
		
		if (document.documentElement.scrollTop > 0) {
			
			divMsgBox.style.top = (((screen.height / 2) + document.documentElement.scrollTop) - 150) + 'px';
		
		} else {
			
			divMsgBox.style.top = (((screen.height / 2) + document.documentElement.scrollTop) - 150) + 'px';
			
		}
		
	} else if (document.body.parentNode.scrollTop >= 0 ) { // IE - NOT TESTED
		
		if (document.documentElement.scrollTop > 0) {
			
			divMsgBox.style.top = (((screen.height / 2) + document.body.parentNode.scrollTop) - 150) + 'px';
			
		} else {
			
			divMsgBox.style.top = ((screen.height / 2) + document.body.parentNode.scrollTop) + 'px';
			
		}
	
	} else if (document.body.scrollTop >= 0 ){ // IE/Macintosh - NOT TESTED
		
		divMsgBox.style.top = ((window.innerHeight / 2) + document.body.scrollTop) + 'px';	
		
	}
	
	
	divBg.style.display='block';*/
	
}


