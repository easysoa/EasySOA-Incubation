package org.easysoa.registry.types.adapters.java;

import java.util.List;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.SubprojectServiceImpl;
import org.easysoa.registry.types.SubprojectNode;
import org.easysoa.registry.types.adapters.ServiceConsumptionAdapter;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.easysoa.registry.types.java.JavaServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.runtime.api.Framework;


/**
 * 
 * @author mkalam-alami
 *
 */
public class JavaServiceConsumptionAdapter extends ServiceConsumptionAdapter implements JavaServiceConsumption {
    
    public JavaServiceConsumptionAdapter(DocumentModel documentModel)
            throws PropertyException, InvalidDoctypeException, ClientException {
        super(documentModel);
    }

    @Override
    public String getDoctype() {
        return JavaServiceConsumption.DOCTYPE;
    }

    @Override
    public String getConsumedInterface() throws Exception {
        return (String) documentModel.getPropertyValue(XPATH_CONSUMEDINTERFACE);
    }

	@Override
	public String getConsumerClass() throws Exception {
        return (String) documentModel.getPropertyValue(XPATH_CONSUMERCLASS);
	}

	@Override
	public String getConsumedInterfaceLocation() throws Exception {
        return (String) documentModel.getPropertyValue(XPATH_CONSUMEDINTERFACELOCATION);
	}

    @Override
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        CoreSession documentManager = documentModel.getCoreSession();
        DocumentModelList consumableServiceImplModels = documentService.query(documentManager,
        		DocumentService.NXQL_SELECT_FROM + JavaServiceImplementation.DOCTYPE
                + DocumentService.NXQL_AND + XPATH_WSDL_PORTTYPE_NAME + " = '" + getProperty(XPATH_WSDL_PORTTYPE_NAME) + "'"
                + DocumentService.NXQL_AND + SubprojectServiceImpl.buildCriteriaSeenFromSubproject(this.documentModel),
                true, false);
        // NB. matching on wsdl:wsdlPortTypeName rather than on
        // javasi:implementedInterface=javasc:consumedInterface, because the remote provider's
        // java interface is not necessarily (rarely) the same as the this consumer's !
        return documentService.createSoaNodeIds(consumableServiceImplModels.toArray(new DocumentModel[]{}));
    }
    
}
