package org.easysoa.registry.systems;

import org.easysoa.registry.test.EasySOAFeature;
import org.nuxeo.runtime.test.runner.Deploy;

/**
 * 
 * @author mkalam-alami
 *
 */
@Deploy({
    "org.easysoa.registry.doctypes.java.core",
    "org.easysoa.registry.defaults"
})
public class EasySOADefaultsFeature extends EasySOAFeature {

}