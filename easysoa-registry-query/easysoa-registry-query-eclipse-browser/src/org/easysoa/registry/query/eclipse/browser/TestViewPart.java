package org.easysoa.registry.query.eclipse.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * A temporary class allowing to use the {@link EasySOAServicePicker} easily in Eclipse.
 * <p>
 * Open it with "Window > Show view > Other... > TestViewPart"
 * 
 * @author yrodiere
 * 
 */
public class TestViewPart extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		new EasySOAServicePicker(parent, SWT.NONE).pack();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
