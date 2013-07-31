/**
 * 
 */
package org.easysoa.registry.integration;

import java.util.ArrayList;
import java.util.List;

import org.easysoa.registry.rest.OperationResult;
import org.easysoa.registry.rest.RegistryApiSamples;
import org.easysoa.registry.rest.RegistryJsonApi;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;

/**
 * Simple (JSON) registry service mock server implementation
 * 
 * @author jguillemotte
 *
 */
public class RegistryApiServerImpl implements RegistryJsonApi {

    @Override
    public OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception {
        return new OperationResult(true);
    }

    @Override
    public SoaNodeInformations query(String subprojectId,
            String query) throws Exception {
        SoaNodeInformations soaNodeInfos = new SoaNodeInformations();
        List<SoaNodeInformation> list = new ArrayList<SoaNodeInformation>();
        soaNodeInfos.setSoaNodeInformationList(list);
        list.add(buildSoaNodeInformation(null));
        return soaNodeInfos ;
    }

    @Override
    public SoaNodeInformation get(String subprojectId)
            throws Exception {
        return buildSoaNodeInformation(null);
    }

    @Override
    public SoaNodeInformations get(String subprojectId,
            String doctype) throws Exception {
        return query(null, null);
    }

    @Override
    public SoaNodeInformation get(String subprojectId,
            String doctype, String name)
            throws Exception {
        return buildSoaNodeInformation(null);
    }

    @Override
    public OperationResult delete(String subprojectId,
            String doctype, String name)
            throws Exception {
        return new OperationResult(true);
    }

    @Override
    public OperationResult delete(
            String subprojectId,
            String doctype,
            String name,
            String correlatedSubprojectId,
            String correlatedDoctype,
            String correlatedName)
            throws Exception {
        return new OperationResult(true);
    }

    public static SoaNodeInformation lastSoaNodeInformation = null;

    public static SoaNodeInformation buildSoaNodeInformation(String title) {
        SoaNodeInformation soaNodeInfo = RegistryApiSamples.buildSoaNodeInformation1(title);
        lastSoaNodeInformation = soaNodeInfo;
        return soaNodeInfo;
    }

}
