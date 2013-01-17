package org.easysoa.registry.types;

import java.io.Serializable;

public interface Document {

    public static final String XPATH_NAME = "ecm:name";

    public static final String XPATH_TITLE = "dc:title";

    String getName() throws Exception;

    String getTitle() throws Exception;

    void setTitle(String title) throws Exception;

    Object getProperty(String xpath) throws Exception;

    void setProperty(String xpath, Serializable value) throws Exception;

    String getSubprojectId() throws Exception;
}
