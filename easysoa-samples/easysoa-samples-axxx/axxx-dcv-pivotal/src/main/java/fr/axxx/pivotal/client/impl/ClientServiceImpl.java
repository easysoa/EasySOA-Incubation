package fr.axxx.pivotal.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import fr.axxx.pivotal.app.impl.UserServiceImpl;
import fr.axxx.pivotal.client.api.ClientService;
import fr.axxx.pivotal.client.model.Client;
import fr.axxx.pivotal.persistence.EntityManagerProvider;

/**
 * ClientService implementation on top of JPA 
 */
@Scope("COMPOSITE")
public class ClientServiceImpl implements ClientService {
    
    private final static Logger LOG = Logger.getLogger(ClientServiceImpl.class.getCanonicalName());

    @Reference
    public EntityManagerProvider database;

    @Override
	public List<Client> listClient() {
		return new ArrayList<Client>(0);
	}
	
}
