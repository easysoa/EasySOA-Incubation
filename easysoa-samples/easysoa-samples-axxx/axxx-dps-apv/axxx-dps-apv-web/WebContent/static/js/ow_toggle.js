/**
 * Toggle subForms of the edited subForm  
 * 
 */

jQuery(document).ready(function(){
	jQuery('.sub-form').each(function(){
		var bodyForm = ('.form-body', this);
					
		jQuery('.subform-name', bodyForm).each(function(){
			
			var nextDiv = jQuery(this).next('div');
			 
			var strong = jQuery('strong', this).addClass('close');
			 
			jQuery(this).click(function(){
				jQuery(strong).toggleClass('open');
				jQuery(nextDiv).toggle('slow');
			})
							
		})	;	
		
	});
});

