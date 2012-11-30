package fr.axxx.pivotal.client.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
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
    @SuppressWarnings("unchecked")    
	public List<Client> listClient() {
        try {
            Query query = this.database.get().createQuery("SELECT c FROM Client c");
            List<Client> clients = (List<Client>) query.getResultList();
            return clients;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
            return null;
        }		
	}

    /**
     * @see ClientService#createClient(String, String, String, String)
     */
    @Override
    public Client createClient(String identifiantClient, String raisonSociale, String siren, String email){
        EntityManager entityManager = database.get();
        Client client = null;
        try{
            client = new Client(identifiantClient, raisonSociale, siren,  email);
            entityManager.getTransaction().begin();
            entityManager.persist(client);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.log(Level.SEVERE, "Error trying to create client: " + ex.getMessage(), ex);
            return null;
        }
        return client;
    }    

    /**
     * @see ClientService#updateClient(String, String, String, String)
     */
    @Override
    public Client updateClient(String identifiantClient, String raisonSociale, String siren, String email){
        EntityManager entityManager = database.get();
        Client client = null;
        try{
            client = this.getClient(identifiantClient);
            client.setRaisonSociale(raisonSociale);
            client.setSIREN(siren);
            client.setEmail(email);
            entityManager.getTransaction().begin();
            entityManager.persist(client);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.log(Level.SEVERE, "Error trying to update client: " + ex.getMessage(), ex);
            return null;
        }
        return client;        
    }
    
    /**
     * @see ClientService#removeClient(String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void removeClient(String identifiantClient){
        EntityManager entityManager = database.get();
        try{
            Query query = this.database.get().createQuery("SELECT c FROM Client c WHERE c.identifiantClient = :identifiantClient");
            query.setParameter("identifiantClient", identifiantClient);
            List<Client> clients = (List<Client>) query.getResultList();            
            entityManager.getTransaction().begin();
            for(Client client : clients){
                entityManager.remove(client);
            }
            entityManager.getTransaction().commit();            
        }
        catch(Exception ex){
            entityManager.getTransaction().rollback();
            LOG.log(Level.SEVERE, "Error trying to remove client: " + ex.getMessage(), ex);
        }
    }

    /**
     * @see ClientService#getClient(String)
     */    
    @Override
    @SuppressWarnings("unchecked")
    public Client getClient(String identifiantClient){
        try {
            Query query = this.database.get().createQuery("SELECT c FROM Client c WHERE c.identifiantClient = :identifiantClient");
            query.setParameter("identifiantClient", identifiantClient);
            List<Client> clients = (List<Client>) query.getResultList();
            return clients.get(0);
        }
        catch(Exception ex){
            LOG.log(Level.SEVERE, "Error trying to get client: " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * Fill default values for clients 
     */
    @Init
    public void fillDefaults() {
        List<Client> allClients = this.listClient();
        if  (allClients == null || allClients.isEmpty()) {
            LOG.log(Level.WARNING, "Filling Clients...");
            try {
                this.database.get().getTransaction().begin();
                // TODO Set correct values for client
                Client c1 = new Client("testCLient", "testClient", "1254365", "test@test.fr");
                this.database.get().persist(c1);
                //Client c2 = new Client("admin", "Administrator", DataUtils.crypt("admin"), "admin@axxx.fr");
                //this.database.get().persist(c2);
                
                this.database.get().getTransaction().commit();
            } catch (Exception e) {
                this.database.get().getTransaction().rollback();
                LOG.log(Level.SEVERE, "Error trying to create a default client", e);
            }
        }
    }
    
}
