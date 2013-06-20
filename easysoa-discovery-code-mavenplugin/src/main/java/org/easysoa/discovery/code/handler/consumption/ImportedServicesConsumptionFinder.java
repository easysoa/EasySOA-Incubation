package org.easysoa.discovery.code.handler.consumption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.model.JavaServiceConsumptionInformation;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.types.ServiceConsumption;
import org.easysoa.registry.types.java.MavenDeliverable;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

public class ImportedServicesConsumptionFinder implements ServiceConsumptionFinder {

    /** whether not to find test consumptions (default true) */
    private boolean filterSources = true;
    
    public ImportedServicesConsumptionFinder() {
        
    }
    
    /**
     * Default value: true
     * @param filterSources
     */
    public void setFilterSources(boolean filterSources) {
        this.filterSources = filterSources;
    }
    
    @Override
    public List<JavaServiceConsumptionInformation> find(JavaSource javaSource, MavenDeliverable mavenDeliverable,
            Map<String, JavaServiceInterfaceInformation> serviceInterfaces) throws Exception {
        List<JavaServiceConsumptionInformation> foundConsumptions = new ArrayList<JavaServiceConsumptionInformation>();
        for (JavaClass c : javaSource.getClasses()) {
            if (!filterSources || !ParsingUtils.isTestClass(c)) {
                foundConsumptions.addAll(find(c, mavenDeliverable, serviceInterfaces));
            }
        }
        return foundConsumptions;
    }

    public List<JavaServiceConsumptionInformation> find(JavaClass c,
            MavenDeliverable mavenDeliverable, Map<String, JavaServiceInterfaceInformation> serviceInterfaces) throws Exception {
        List<JavaServiceConsumptionInformation> foundConsumptions = new ArrayList<JavaServiceConsumptionInformation>();
        
        // Explore imports
        for (String importedClassName : c.getSource().getImports()) {
            Type importedClassType = new Type(importedClassName);
            for (String serviceInterface : serviceInterfaces.keySet()) {
                if (importedClassType.getFullyQualifiedName().equals(serviceInterface)) {
                    JavaServiceConsumptionInformation scInfo = new JavaServiceConsumptionInformation(
                            c.getFullyQualifiedName(), serviceInterfaces.get(serviceInterface));
                    if (c.getComment() != null) {
	                    scInfo.setProperty(ServiceConsumption.XPATH_DOCUMENTATION,
	                   	        "Class documentation: " + c.getComment());
                    }
                    scInfo.setProperty(ServiceConsumption.XPATH_ISTEST, ParsingUtils.isTestClass(c));
                    foundConsumptions.add(scInfo);
                }
            }
        }
        
        // TODO Also explore package
        
        return foundConsumptions;
    }

}
