package org.easysoa.registry.types;

import java.security.InvalidParameterException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class OperationInformation {

    private final String name;
    private String parameters;
    private String documentation;
    
    public OperationInformation(String name, String parameters, String documentation) {
        this.name = name;
        this.parameters = parameters;
        this.documentation = documentation;
    }
   
    public String getName() {
        return name;
    }
   
    public String getParameters() {
        return parameters;
    }

    public String getDocumentation() {
        return documentation;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o instanceof OperationInformation) {
    		return name.equals(((OperationInformation) o).name);
    	}
    	return false;
    }

	public void mergeWith(OperationInformation operation) throws InvalidParameterException {
		if (!operation.name.equals(this.name)) {
			throw new InvalidParameterException("Can't merge operations whose names don't match");
		}
		if (operation.parameters != null) {
			this.parameters = operation.parameters;
		}
		if (operation.documentation != null) {
			if (this.documentation != null) {
				this.documentation += "\n\nAdditional documentation:\n" + operation.documentation;
			}
			else {
				this.documentation = operation.documentation;
			}
		}
		
	}
    
}
