package org.easysoa.registry.rest.client.types.java;

import org.easysoa.registry.rest.client.types.DeliverableInformation;
import org.easysoa.registry.types.java.MavenDeliverable;

public class MavenDeliverableInformation extends DeliverableInformation implements MavenDeliverable {

    public MavenDeliverableInformation(String subprojectId, String groupId, String artifactId) throws Exception {
        this(subprojectId, groupId + ":" + artifactId);
    }
    
    public MavenDeliverableInformation(String subprojectId, String name) throws Exception {
        super(subprojectId, name);
        this.setNature(MavenDeliverable.NATURE);
    }

    @Override
    public String getGroupId() {
        // Identifiers format:
        // [GROUPID]:[ARTIFACTID]
        String[] tokens = getSoaName().split(":");
        if (tokens.length == 2) {
            return tokens[0];
        }
        else {
            return null;
        }
    }

    @Override
    public String getArtifactId() {
        // Identifiers format:
        // [GROUPID]:[ARTIFACTID]
        String[] tokens = getSoaName().split(":");
        if (tokens.length == 2) {
            return tokens[1];
        }
        else {
            return null;
        }
    }
    
 }
