package org.easysoa.registry.rest.marshalling;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class OperationResult {

    private static final String ERROR = "error";

    private static final String SUCCESS = "success";

    private String result;

    private String message;

    private StackTraceElement[] stacktrace;
    
    protected OperationResult() {
        
    }
    
    public OperationResult(boolean success) {
        this.result = success ? SUCCESS : ERROR;
    }
    
    public OperationResult(boolean success, String message, Exception e) {
        this(success);
        this.setException(e);
        message = (message == null) ? "" : message + " : ";
        this.setMessage(message + e.getMessage());
    }
    
    public OperationResult(boolean success, Exception e) {
        this(success, "", e);
    }

    public void setException(Exception e) {
        this.stacktrace = e.getStackTrace();
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @JsonIgnore
    public boolean isSuccessful() {
        return result.equals(SUCCESS);
    } 
    
    public String getMessage() {
        return message;
    }
    
    public StackTraceElement[] getStacktrace() {
        return stacktrace;
    }

}
