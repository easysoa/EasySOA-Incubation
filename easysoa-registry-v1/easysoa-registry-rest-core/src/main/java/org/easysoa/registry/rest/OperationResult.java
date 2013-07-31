/**
 * EasySOA Registry
 * Copyright 2011-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.rest;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonTypeName;


@XmlRootElement(name = "result")
@JsonTypeName("result")
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

    // for serialization only (it does not work with @JsonAutoDetect or @Property only)
    public void setResult(String result) {
        if (this.result == null) {
            this.result = result;   
        } // can't change an existing result
    }
    public String getResult() {
        return result;
    }

}
