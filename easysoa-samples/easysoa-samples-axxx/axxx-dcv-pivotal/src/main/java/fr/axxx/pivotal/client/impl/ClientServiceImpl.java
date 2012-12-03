package fr.axxx.pivotal.client.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import com.axxx.dps.apv.PrecomptePartenaire;
import com.axxx.dps.apv.PrecomptePartenaireService;
import fr.axxx.pivotal.client.api.ClientService;
import fr.axxx.pivotal.client.model.Client;
import fr.axxx.pivotal.client.model.ContactClient;
import fr.axxx.pivotal.client.model.InformationAPV;
import fr.axxx.pivotal.persistence.EntityManagerProvider;

/**
 * ClientService implementation on top of JPA 
 */
@Scope("COMPOSITE")
public class ClientServiceImpl implements ClientService {
    
    private final static Logger LOG = Logger.getLogger(ClientServiceImpl.class.getCanonicalName());

    @Reference
    public EntityManagerProvider database;
    
    @Reference
    public PrecomptePartenaireService precomptePartenaireService;
    
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
            LOG.log(Level.SEVERE, "Error trying to create client : " + ex.getMessage(), ex);
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
            LOG.log(Level.SEVERE, "Error trying to update client : " + ex.getMessage(), ex);
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
            LOG.log(Level.SEVERE, "Error trying to remove client : " + ex.getMessage(), ex);
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
            LOG.log(Level.SEVERE, "Error trying to get client : " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * @see ClientService#getInformationAPV(String)
     */    
    @Override
    @SuppressWarnings("unchecked")
    public List<InformationAPV> getInformationAPV(String identifiantClient){
        try {
            Query query = this.database.get().createQuery("SELECT i FROM InformationAPV i WHERE i.identifiantClient = :identifiantClient");
            query.setParameter("identifiantClient", identifiantClient);
            List<InformationAPV> InformationAPVs = (List<InformationAPV>) query.getResultList();
            return InformationAPVs;
        }
        catch(Exception ex){
            LOG.log(Level.SEVERE, "Error trying to get information APV : " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * @see ClientService#getContactClient(String)
     */    
    @Override
    @SuppressWarnings("unchecked")
    public List<ContactClient> getContactClient(String identifiantClient){
        try {
            Query query = this.database.get().createQuery("SELECT c FROM ContactClient c WHERE c.identifiantClient = :identifiantClient");
            query.setParameter("identifiantClient", identifiantClient);
            List<ContactClient> contactClients = (List<ContactClient>) query.getResultList();
            return contactClients;
        }
        catch(Exception ex){
            LOG.log(Level.SEVERE, "Error trying to get contatc client : " + ex.getMessage(), ex);
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
                
                // Default value for clients
                Client c1 = new Client("AssociationVacances", "Association vacances", "1254365", "vacances@assovac.fr");
                this.database.get().persist(c1);
                
                // Default values for ContactClient
                ContactClient contactClient = new ContactClient();
                contactClient.setIdentifiantClient("AssociationVacances");
                contactClient.setEmail("f.martin@assovac.fr");
                contactClient.setNomContact("Martin");
                contactClient.setNumEtVoie("120 AVENUE DU GENERAL LECLERC");
                contactClient.setVille("Paris");
                contactClient.setPays("France");
                
                // Default values for InformationsAPV
                InformationAPV informationAPV = new InformationAPV();
                informationAPV.setIdentifiantClient("AssociationVacances");
                informationAPV.setBilanLibelle("Bilan");
                informationAPV.setBilanAnnee(2012);
                informationAPV.setNombre(1200);
                
                this.database.get().getTransaction().commit();
            } catch (Exception e) {
                this.database.get().getTransaction().rollback();
                LOG.log(Level.SEVERE, "Error trying to create a default client", e);
            }
        }
    }
    
    @Override
    public String creerPrecompte(String identifiantClient) throws Exception {
        Client client = this.getClient(identifiantClient);
        PrecomptePartenaire precomptePartenaire = new PrecomptePartenaire();
        precomptePartenaire.setIdentifiantClientPivotal(identifiantClient);
        precomptePartenaire.setSirenSiret(client.getSIREN());
        precomptePartenaire.setEmail(client.getEmail());
        precomptePartenaireService.creerPrecompte(precomptePartenaire);
        return "Précompte succesfully created";
    }

    @Override
    public InformationAPV createInformationApv(String identifiantClient, String bilanLibelle, Integer nombre, Integer bilanAnnee) {
        EntityManager entityManager = database.get();
        InformationAPV informationAPV = null;
        try{
            informationAPV = new InformationAPV();
            informationAPV.setIdentifiantClient(identifiantClient);
            informationAPV.setBilanLibelle(bilanLibelle);
            informationAPV.setNombre(nombre);
            informationAPV.setBilanAnnee(bilanAnnee);
            entityManager.getTransaction().begin();
            entityManager.persist(informationAPV);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.log(Level.SEVERE, "Error trying to create client: " + ex.getMessage(), ex);
            return null;
        }
        return informationAPV;
    }

    @Override
    public ContactClient createContactClient(String identifiantClient, String nomContact, String prenomContact, String fonctionContact, String telephone, String email, String numEtVoie,
            String codePostal, String ville, String pays) {
        EntityManager entityManager = database.get();
        ContactClient contactClient = null;
        try{
            contactClient = new ContactClient();
            contactClient.setIdentifiantClient(identifiantClient);
            contactClient.setNomContact(nomContact);
            contactClient.setPrenomContact(prenomContact);
            contactClient.setFonctionContact(fonctionContact);
            contactClient.setTelephone(telephone);
            contactClient.setEmail(email);
            contactClient.setNumEtVoie(numEtVoie);
            contactClient.setCodePostal(codePostal);
            contactClient.setVille(ville);
            contactClient.setPays(pays);
            entityManager.getTransaction().begin();
            entityManager.persist(contactClient);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            LOG.log(Level.SEVERE, "Error trying to create client: " + ex.getMessage(), ex);
            return null;
        }
        return contactClient;
    }
    
}
