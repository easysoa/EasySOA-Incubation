package org.easysoa.registry.types;

import java.security.InvalidParameterException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class OperationInformation {
    
    /* constants to be used to serialize it (to nuxeo model prop, or even json...) */
    public static final String OPERATION_NAME = "operationName";
    public static final String OPERATION_PARAMETERS = "operationParameters";
    public static final String OPERATION_RETURN_PARAMETERS = "operationReturnParameters";
    public static final String OPERATION_DOCUMENTATION = "operationDocumentation";
    public static final String OPERATION_IN_CONTENT_TYPE = "operationInContentType";
    public static final String OPERATION_OUT_CONTENT_TYPE = "operationOutContentType";

    private final String name;
    private String parameters;
    private String returnParameters;
    private String documentation;
    private String inContentType;
    private String outContentType;
    //private String methodSignature;
    
    public OperationInformation(String name,
            String parameters, String returnParameters,
            String documentation,
            String inContentType, String outContentType) {
        this.name = name;
        this.parameters = parameters;
        this.returnParameters = returnParameters;
        this.documentation = documentation;
        this.inContentType = inContentType;
        this.outContentType = outContentType;
    }
   
    public String getName() {
        return name;
    }
   
    public String getParameters() {
        return parameters;
    }
   
    public String getReturnParameters() {
        return returnParameters;
    }

    public String getDocumentation() {
        return documentation;
    }

    public String getInContentType() {
        return inContentType;
    }

    public String getOutContentType() {
        return outContentType;
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
        if (operation.returnParameters != null) {
            this.returnParameters = operation.returnParameters;
        }
		if (operation.documentation != null) {
			if (this.documentation != null) {
				this.documentation += "\n\nAdditional documentation:\n" + operation.documentation;
			}
			else {
				this.documentation = operation.documentation;
			}
		}
        if (operation.inContentType != null) {
            this.inContentType = operation.inContentType;
        }
        if (operation.outContentType != null) {
            this.outContentType = operation.outContentType;
        }
		
	}
    
}
