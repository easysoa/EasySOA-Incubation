
package org.easysoa.registry.utils;

/**
 *
 * @author jguillemotte
 */
public enum ContextVisibility {
    
    // Context enum values
    STRICT("strict"),
    DEPTH("depth");
    
    // Enum value
    private String value;

    // Constructor
    private ContextVisibility(String value) {
        this.value = value;
    }
    
    /**
     * Return the enum value
     * @return The enum value
     */
    public String getValue(){
        return this.value;
    }
    
}
