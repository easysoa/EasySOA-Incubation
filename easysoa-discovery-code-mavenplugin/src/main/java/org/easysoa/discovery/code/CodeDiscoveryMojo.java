package org.easysoa.discovery.code;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.easysoa.discovery.code.handler.JaxRSSourcesHandler;
import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.discovery.code.handler.SourcesHandler;
import org.easysoa.registry.rest.OperationResult;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.types.Deliverable;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Allows to discover services information by parsing the sources of a project.
 * All discoveries are then sent to Nuxeo through its REST API. 
 * 
 * @goal discover
 */
public class CodeDiscoveryMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="http://localhost:8080/nuxeo/site"
     */
    private String nuxeoSitesUrl;
    
    /**
     * @parameter default-value="Administrator"
     */
    private String username;
    
    /**
     * @parameter default-value="Administrator"
     */
    private String password;
    
    /**
     * @parameter default-value="/default-domain/MyProject/Realisation_v"
     */
    private String subproject;
    
    /**
     * @parameter
     */
    private String application;

    /**
     * The current SCM sources version (commit hash)
     * 
     * @parameter expression="${buildNumber}"
     */
    private String buildNumber;
    
    /**
     * May be set to false to always create InformationServices out of Java interfaces
     * (i.e. source code is the "master" controlling the InformationService layer).
     * Otherwise (by default), interfaces are only created if an existing
     * matching one is not found (i.e. the "master" controlling the InformationService
     * layer is not source code but ex. manual Specifications subproject in the registry).
     * 
     * @parameter expression="${easysoa-discovery-code.matchInterfacesFirst}" default-value="true"
     */
    private boolean matchInterfacesFirst;

    /**
     * May be set to false to altogether disable discovering InformationServices
     * out of Java interfaces.
     * Set by default.
     * 
     * @parameter expression="${easysoa-discovery-code.discoverInterfaces}" default-value="true"
     */
    private boolean discoverInterfaces;

    /**
     * May be set to false to disable discovery of Java service implementations
     * ex. when initializing an EasySOA project's Specifications subproject with
     * InformationServices out of existing Java API code (then discoverInterfaces will be set).
     * Set by default.
     * 
     * @parameter expression="${easysoa-discovery-code.discoverImplementations}" default-value="true"
     */
    private boolean discoverImplementations;
    
    /**
     * The regex to match by group:artifact id where interfaces have to be
     * looked up. By default look up interfaces in top-level pom project group
     * (if no parent, means to project's group id).
     * 
     * @parameter
     */
    private String interfaceLookupMavenIdRegex = null;

	private Map<String, SourcesHandler> availableHandlers = new HashMap<String, SourcesHandler>();

    /** Cached compiled pattern of interfaceLookupMavenIdRegex, inited in getter */
    private Pattern interfaceLookupMavenIdRegexPattern = null;

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        try {
            if ("pom".equals(this.getMavenProject().getArtifact().getType())) {
                log.info("Skipping project " + this.getMavenProject().getArtifact().getId() + " (pom so no source)");
                return;
            }

            if (interfaceLookupMavenIdRegex == null) {
                // By default look up interfaces in top-level pom project group
                // (so if no parent, by default look up interfaces in the same
                // group)
                MavenProject topLevelMavenProject = getTopLevelMavenProject(this.getMavenProject());
                interfaceLookupMavenIdRegex = topLevelMavenProject.getArtifact().getGroupId() + ":.*";
            }
            try {
                this.interfaceLookupMavenIdRegexPattern = Pattern.compile(this.interfaceLookupMavenIdRegex);
                // compiling the regex to match by group:artifact id where
                // interfaces have to be looked up
            } catch (Throwable error) {
                log.info("Error compiling pattern " + this.interfaceLookupMavenIdRegex
                        + ", should be a valid regex, aborting.");
                log.debug(error);
                return;
            }

        // Init registry client
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setCredentials(username, password);
        clientBuilder.setNuxeoSitesUrl(nuxeoSitesUrl);
        RegistryApi registryApi = clientBuilder.constructRegistryApi();
        
        // Init handlers
        this.availableHandlers.put("JAX-WS", new JaxWSSourcesHandler(this));
        this.availableHandlers.put("JAX-RS", new JaxRSSourcesHandler(this));
        
        MavenDeliverableInformation mavenDeliverable = new MavenDeliverableInformation(
                this.getSubproject(), project.getGroupId() + ":" + project.getArtifactId());
        mavenDeliverable.setTitle(project.getName());
        String commit;
        if (buildNumber == null || buildNumber.length() < 10) {
            commit = "UNKNOWN (ERROR GETTING COMMIT)"; // BUG Talend's crozas
        } else {
            commit = buildNumber.substring(0, 10);
        }
        mavenDeliverable.setVersion(project.getVersion() + " (commit " + commit + ")");
        if (application != null && !application.trim().isEmpty()) {
            mavenDeliverable.setApplication(application);
        }
        try {
            mavenDeliverable.setProperty(Deliverable.XPATH_LOCATION, project.getBasedir().toURI().toURL().toString());
        } catch (MalformedURLException e) {
            log.error("Failed to convert project location to URL", e);
        }
        try {
            // List dependencies
            List<?> dependencies = project.getDependencies();
            List<String> formattedDependencies = new ArrayList<String>();
            for (Object objectDependency : dependencies) {
                Dependency d = (Dependency) objectDependency;
                formattedDependencies.add(d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion());
            }
            mavenDeliverable.setProperty(Deliverable.XPATH_DEPENDENCIES, (Serializable) formattedDependencies);
        } catch (MalformedURLException e) {
            log.error("Failed to convert project location to URL", e);
        }
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        discoveredNodes.add(mavenDeliverable);

        // Configure parser
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(project.getBasedir());
        
        // Iterate through classes to find WSes
        JavaSource[] sources = builder.getSources();
        CodeDiscoveryRegistryClient registryClient = new CodeDiscoveryRegistryClient(registryApi);
        for (SourcesHandler handler : availableHandlers.values()) {
            discoveredNodes.addAll(handler.handleSources(sources, mavenDeliverable, registryClient, log));
        }
        
        // Build and send discovery request
        try {
            log.info("Sending found SoaNodes to " + nuxeoSitesUrl);
            if (discoveredNodes.size() > 0) {
                for (SoaNodeInformation soaNode : discoveredNodes) {
                    log.info("> " + soaNode.getSoaNodeId().toString());
                    OperationResult result = registryApi.post(soaNode);
                    if (!result.isSuccessful()) {
                        IOException ioException = new IOException(result.getMessage());
                        ioException.setStackTrace(result.getStacktrace());
                    	throw ioException;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to send discovery request", e);
        }
        
        }
        catch (ClientHandlerException e) {
            log.error("Failed to connect to Nuxeo", e);
        }
        catch (UniformInterfaceException e) {
            log.error("Failed to connect to Nuxeo", e);
        }
        catch (Exception e) {
            throw new MojoExecutionException("Failed to discover SOA documents in code", e);
        }
        
    }
    
    public boolean isMatchInterfacesFirst() {
        return matchInterfacesFirst;
    }

    public boolean isDiscoverInterfaces() {
        return discoverInterfaces;
    }

    public boolean isDiscoverImplementations() {
        return discoverImplementations;
    }

	public MavenProject getMavenProject() {
        return project;
    }

    public boolean shouldLookupInterfaces(Artifact dependency) {
        if (this.interfaceLookupMavenIdRegexPattern == null) {
            return true;
        }

        return this.interfaceLookupMavenIdRegexPattern.matcher(dependency.getId()).matches();
    }

    private MavenProject getTopLevelMavenProject(MavenProject mavenProject) {
        MavenProject parentMavenProject = mavenProject.getParent();
        while (parentMavenProject != null && parentMavenProject.getModules().contains(mavenProject.getArtifactId())) { // ex.
                                                                                                                       // "axxx-dps-apv-core"
            mavenProject = parentMavenProject;
            parentMavenProject = mavenProject.getParent();
        }
        return mavenProject;
    }
    
    
    // protected setters are only for testing purpose

    public MavenProject getProject() {
        return project;
    }

    protected void setProject(MavenProject project) {
        this.project = project;
    }

    public String getNuxeoSitesUrl() {
        return nuxeoSitesUrl;
    }

    protected void setNuxeoSitesUrl(String nuxeoSitesUrl) {
        this.nuxeoSitesUrl = nuxeoSitesUrl;
    }

    public String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    public String getSubproject() {
        return subproject;
    }

    public void setSubproject(String subproject) {
        this.subproject = subproject;
    }

    public String getApplication() {
        return application;
    }

    protected void setApplication(String application) {
        this.application = application;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    protected void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getInterfaceLookupMavenIdRegex() {
        return interfaceLookupMavenIdRegex;
    }

    protected void setInterfaceLookupMavenIdRegex(String interfaceLookupMavenIdRegex) {
        this.interfaceLookupMavenIdRegex = interfaceLookupMavenIdRegex;
    }

    protected void setMatchInterfacesFirst(boolean matchInterfacesFirst) {
        this.matchInterfacesFirst = matchInterfacesFirst;
    }

    protected void setDiscoverInterfaces(boolean discoverInterfaces) {
        this.discoverInterfaces = discoverInterfaces;
    }

    protected void setDiscoverImplementations(boolean discoverImplementations) {
        this.discoverImplementations = discoverImplementations;
    }

}