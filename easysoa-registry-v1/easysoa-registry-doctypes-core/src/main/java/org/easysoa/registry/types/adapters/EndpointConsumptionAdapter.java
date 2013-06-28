package org.easysoa.registry.types.adapters;

import java.util.LinkedList;
import java.util.List;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.utils.RelationsHelper;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.runtime.api.Framework;


/**
 *
 * @author mkalam-alami
 *
 */
public class EndpointConsumptionAdapter extends ServiceConsumptionAdapter implements EndpointConsumption {

    private final CoreSession documentManager;

    public EndpointConsumptionAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
        this.documentManager = documentModel.getCoreSession();
    }

    @Override
    public String getDoctype() {
        return EndpointConsumption.DOCTYPE;
    }

    /**
     * @obsolete works using Relations that are never set,
     * so use rather DocumentService getSoaNodeChildren & getSoaNodeParents
     * on Endpoint(Consumption).
     * TODO LATER rewrite it, for now let it be for DoctypesTest.testEndpointConsumptionRelations()
     */
    @Override
    public SoaNodeId getConsumedEndpoint() throws Exception {
        DocumentModelList outgoingRelations = RelationsHelper.getOutgoingRelations(documentManager, documentModel, PREDICATE_CONSUMES);
        if (outgoingRelations != null && outgoingRelations.size() > 0) {
            DocumentService documentService = Framework.getService(DocumentService.class);
            return documentService.createSoaNodeId(outgoingRelations.get(0));
        }
        else {
            return null;
        }
    }

    /**
     * @obsolete works using Relations that are never set,
     * so use rather DocumentService getSoaNodeChildren & getSoaNodeParents
     * on Endpoint(Consumption).
     * TODO LATER rewrite it, for now let it be for DoctypesTest.testEndpointConsumptionRelations()
     */
    @Override
    public void setConsumedEndpoint(SoaNodeId consumedEndpoint) throws Exception {
        RelationsHelper.deleteOutgoingRelations(documentManager, documentModel, PREDICATE_CONSUMES);
        DocumentService documentService = Framework.getService(DocumentService.class);
        if (consumedEndpoint != null) {
            DocumentModel consumedEndpointModel = documentService.create(documentManager, consumedEndpoint);
            RelationsHelper.createRelation(documentManager, documentModel, PREDICATE_CONSUMES, consumedEndpointModel);
        }
    }

    @Override
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception {
        List<SoaNodeId> consumableServiceImpls = new LinkedList<SoaNodeId>();
        DocumentService documentService = Framework.getService(DocumentService.class);
        // NB. not using getConsumedEndpoint(), which works using Relations that are never set
        //DocumentModel foundEndpoint = documentService.findSoaNode(documentManager, getConsumedEndpoint());
        List<DocumentModel> consumedEndpointRes = documentService.getSoaNodeChildren(documentModel, Endpoint.DOCTYPE);
        if (consumedEndpointRes.isEmpty()) {
        	return consumableServiceImpls;
        }
        DocumentModel foundEndpoint = consumedEndpointRes.get(0); // EndpointConsumption has only one consumed endpoint
        DocumentModel endpointImpl = documentService.getParentServiceImplementation(foundEndpoint);
        if (endpointImpl != null) {
            consumableServiceImpls.add(documentService.createSoaNodeId(endpointImpl));
        } // else no impl, ex. endpoint not matched to it
        return consumableServiceImpls;
    }

    @Override
    public String getHost() throws PropertyException, ClientException {
        return (String) documentModel.getPropertyValue(XPATH_CONSUMER_HOST);
    }

}
