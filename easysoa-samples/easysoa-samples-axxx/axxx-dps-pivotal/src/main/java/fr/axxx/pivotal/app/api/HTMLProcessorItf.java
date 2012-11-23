
package fr.axxx.pivotal.app.api;

import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.ComponentService;
import org.eclipse.stp.sca.Composite;
import org.eclipse.stp.sca.JavaImplementation;
import org.eclipse.stp.sca.JavaInterface;
import org.eclipse.stp.sca.PropertyValue;
import org.eclipse.stp.sca.domainmodel.tuscany.HTTPBinding;


/**
 * Produces HTML.
 * Interface to be implemented by a Velocity template (htmlProcessor.vm)
 * which has to be plugged in Java controllers / REST
 * 
 * @author mdutoo
 *
 */
public interface HTMLProcessorItf {

	String getComponentPanel(Component component);
	
	String getComponentMenu();
	
	String getComponentReferencePanel(ComponentReference componentReference);
	
	String getComponentReferenceMenu();
	
	String getComponentServicePanel(ComponentService componentService);
	
	String getComponentServiceMenu();
	
	String getCompositePanel(Composite composite);
	
	String getCompositeMenu();
	
	String getHttpBindingPanel(HTTPBinding binding);
	
	String getHttpBindingMenu();
	
	String getJavaImplementationPanel(JavaImplementation implementation);
	
	String getJavaImplementationMenu();

	String getJavaInterfacePanel(JavaInterface javaInterface);
	
	String getJavaInterfaceMenu();
	
	String getPropertyPanel(PropertyValue propertyValue);
	
	String getPropertyMenu();
	
	String getReferencePanel(org.eclipse.stp.sca.Reference reference, String userId);
	
	String getReferenceMenu();

}
