package fr.axxx.pivotal.client.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import com.axxx.dps.apv.PrecomptePartenaire;
import com.axxx.dps.apv.PrecomptePartenaireService;
import com.axxx.dps.apv.TypeStructure;

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
    public Client createClient(String identifiantClient, String raisonSociale, Integer anciennete, 
            String typeStructure, String numEtVoie, String email, String codePostal, String ville, String pays,
            String tel, String rib, String formeJuridique, String siren, BigDecimal dotGlobAPVN, BigDecimal dontReliquatN1, 
            BigDecimal dontDotN, BigDecimal nbBenefPrevN, BigDecimal montantUtiliseN, BigDecimal nbBenefN){
        EntityManager entityManager = database.get();
        Client client = null;
        try{
            client = new Client(identifiantClient, raisonSociale, siren,  email);
            client.setAnciennete(anciennete);
            client.setTypeStructure(typeStructure);
            client.setNumEtVoie(numEtVoie);
            client.setCodePostal(codePostal);
            client.setVille(ville);
            client.setPays(pays);
            client.setTel(tel);
            client.setRIB(rib);
            client.setFormeJuridique(formeJuridique);
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
    public Client updateClient(String identifiantClient, String raisonSociale, Integer anciennete, 
            String typeStructure, String numEtVoie, String email, String codePostal, String ville, String pays,
            String tel, String rib, String formeJuridique, String siren, BigDecimal dotGlobAPVN, BigDecimal dontReliquatN1, 
            BigDecimal dontDotN, BigDecimal nbBenefPrevN, BigDecimal montantUtiliseN, BigDecimal nbBenefN){
        EntityManager entityManager = database.get();
        Client client = null;
        try{
            client = this.getClient(identifiantClient);
            if(client == null){
                throw new Exception("Unable to find client with the following identifiant : " + identifiantClient);
            }
            client.setRaisonSociale(raisonSociale);
            client.setAnciennete(anciennete);
            client.setTypeStructure(typeStructure);
            client.setNumEtVoie(numEtVoie);
            client.setEmail(email);
            client.setCodePostal(codePostal);
            client.setVille(ville);
            if (pays != null && pays.length() != 0) { // check for call from APV
            	client.setPays(pays);
            }
            client.setTel(tel);
            if (rib != null && rib.length() != 0) { // check for call from APV
            	client.setRIB(rib);
            }
            if (formeJuridique != null && formeJuridique.length() != 0) { // check for call from APV
            	client.setFormeJuridique(formeJuridique);
            }
            client.setSIREN(siren);

            client.setDotGlobAPVN(dotGlobAPVN);
            client.setDontReliquatN1(dontReliquatN1);
            client.setDontDotN(dontDotN);
            client.setNbBenefPrevN(nbBenefPrevN);
            client.setMontantUtiliseN(montantUtiliseN);
            client.setNbBenefN(nbBenefN);
            
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
    
    private void updateClient(Client client) {
        EntityManager entityManager = database.get();
        entityManager.getTransaction().begin();
        entityManager.persist(client);
        entityManager.getTransaction().commit();        
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
    public Client getClient(String identifiantClient){
        try {
            Query query = this.database.get().createQuery("SELECT c FROM Client c WHERE c.identifiantClient = :identifiantClient");
            query.setParameter("identifiantClient", identifiantClient);
            Client client = (Client) query.getSingleResult();
            return client;
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
            LOG.log(Level.SEVERE, "Error trying to get contact client : " + ex.getMessage(), ex);
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
                Client c1 = new Client("AssociationVacances", "Association vacances familles", "32681744200022", "vacances@assovac.fr");
                c1.setAnciennete(0);
                c1.setNumEtVoie("4 PLACE DE NAVARRE");
                c1.setVille("SARCELLES");
                c1.setCodePostal("95200");
                c1.setPays("FR");
                c1.setCreerPrecompteDone(false);
                this.database.get().persist(c1);
                
                // Default value for clients
                Client c2 = new Client("CaisseSecoursFrancais", "Caisse secours francais", "78471981700024", "contact@caissesecours.fr");
                c2.setAnciennete(2);
                c2.setNumEtVoie("19 RUE DE L'ABONDANCE");
                c2.setVille("LYON");
                c2.setCodePostal("69003");
                c2.setPays("FR");
                c2.setCreerPrecompteDone(false);
                this.database.get().persist(c2);

                // Default value for clients
                Client c3 = new Client("FondationSolidarite", "Fondation solidarité", "21950585600019", "contact@fsolidarite.org");
                c3.setAnciennete(1);
                c3.setNumEtVoie("26 AVENUE DE L'OBSERVATOIRE");
                c3.setVille("PARIS");
                c3.setCodePostal("75014");
                c3.setPays("FR");
                c3.setCreerPrecompteDone(false);
                this.database.get().persist(c3);                

                // Default values for ContactClient
                ContactClient contactClient1 = new ContactClient();
                contactClient1.setIdentifiantClient("AssociationVacances");
                contactClient1.setEmail("f.martin@assovac.fr");
                contactClient1.setNomContact("Martin");
                contactClient1.setPrenomContact("Fred");
                contactClient1.setNumEtVoie("4 PLACE DE NAVARRE");
                contactClient1.setVille("SARCELLES");
                contactClient1.setPays("FR");
                contactClient1.setFonctionContact("directeur");
                this.database.get().persist(contactClient1);
                
                ContactClient contactClient2 = new ContactClient();
                contactClient2.setIdentifiantClient("CaisseSecoursFrancais");
                contactClient2.setEmail("jdupont@caissesecours.fr");
                contactClient2.setNomContact("Dupont");
                contactClient2.setPrenomContact("Jacques");
                contactClient2.setNumEtVoie("19 RUE DE L'ABONDANCE");
                contactClient2.setVille("LYON");
                contactClient2.setPays("FR");
                contactClient2.setFonctionContact("tresorier");
                this.database.get().persist(contactClient2);
                
                // Default values for InformationsAPV
                InformationAPV informationAPV = new InformationAPV();
                informationAPV.setIdentifiantClient("AssociationVacances");
                informationAPV.setBilanLibelle(InformationAPV.BILAN_LIBELLE_JEUNES);
                informationAPV.setBilanAnnee(2012);
                informationAPV.setNombre(1200);
                this.database.get().persist(informationAPV);
                
                // Default values for InformationsAPV
                InformationAPV informationAPV2 = new InformationAPV();
                informationAPV2.setIdentifiantClient("AssociationVacances");
                informationAPV2.setBilanLibelle(InformationAPV.BILAN_LIBELLE_SENIORS);
                informationAPV2.setBilanAnnee(2012);
                informationAPV2.setNombre(2640);                
                this.database.get().persist(informationAPV2);
                
                this.database.get().getTransaction().commit();
            } catch (Exception e) {
                this.database.get().getTransaction().rollback();
                LOG.log(Level.SEVERE, "Error trying to create a default client", e);
            }
        }
    }
    
    @Override
    public String creerPrecompte(String identifiantClient) {
        String message = "";
        try{
            Client client = this.getClient(identifiantClient);
            PrecomptePartenaire precomptePartenaire = new PrecomptePartenaire();
            precomptePartenaire.setIdentifiantClientPivotal(identifiantClient);
            precomptePartenaire.setSirenSiret(client.getSIREN());
            precomptePartenaire.setEmail(client.getEmail());
            precomptePartenaire.setVille(client.getVille());            
            precomptePartenaire.setAdresse(client.getNumEtVoie());
            precomptePartenaire.setCp(client.getCodePostal());
            precomptePartenaire.setNomStructure(client.getRaisonSociale());
            precomptePartenaire.setTelephone(client.getTel());
            //precomptePartenaire.setTypeStructure(TypeStructure.valueOf(client.getTypeStructure()));
            // TODO : There is a problem with typestructure here  !!!
            precomptePartenaire.setTypeStructure(TypeStructure.ASSOCIATION_NAT);
            precomptePartenaire.setAnciennete(client.getAnciennete());
            precomptePartenaireService.creerPrecompte(precomptePartenaire); // TODO ville et autres champs requis par PrecomptePartenaire et côté APV
            client.setCreerPrecompteDone(true);
            this.updateClient(client);
        }
        catch(Exception ex){
            LOG.log(Level.SEVERE, "Error trying to create precompte : " + ex.getMessage(), ex);
            message = ex.getMessage();
        }
        return message;
    }

    @Override
    public InformationAPV createOrUpdateInformationApv(String identifiantClient, String bilanLibelle, Integer nombre, Integer bilanAnnee) throws Exception {
        if(!InformationAPV.checkBilanLibelleValue(bilanLibelle)){
            throw new IllegalArgumentException("The value of bilanLibelle must be one of the following values : " 
                    + InformationAPV.BILAN_LIBELLE_ADULTESISOLES + ", " 
                    + InformationAPV.BILAN_LIBELLE_ENFANTS + ", " 
                    + InformationAPV.BILAN_LIBELLE_JEUNES + ", "
                    + InformationAPV.BILAN_LIBELLE_SENIORS);
        }
        // Check if client exists        
        if(getClient(identifiantClient) == null){
            throw new IllegalArgumentException("The value of identifiantClient must match with a registred client in database");
        }
        // Get the information APV to update
        EntityManager entityManager = database.get();
        InformationAPV informationAPV = null;
        informationAPV = this.getInformationAPV(identifiantClient, bilanLibelle, String.valueOf(bilanAnnee));
        if(informationAPV == null){
            informationAPV = new InformationAPV();
        }
        // Fill informationAPV
        try{
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
            throw ex;
        }
        return informationAPV; 
    }

    /**
     * Get the InformationAPV with corresponding id
     * @param id
     * @return The informationAPV or null
     */
    private InformationAPV getInformationAPV(String identifiantClient, String bilanLibelle, String bilanAnnee){
        try {
            Query query = this.database.get().createQuery("SELECT i FROM InformationAPV i WHERE i.identifiantClient = :identifiantclient AND i.bilanLibelle = :bilanlibelle AND i.bilanAnnee = :bilanannee");
            query.setParameter("identifiantclient", identifiantClient);
            query.setParameter("bilanlibelle", bilanLibelle);
            query.setParameter("bilanannee", Integer.valueOf(bilanAnnee));
            InformationAPV informationAPV = (InformationAPV) query.getSingleResult();
            return informationAPV;
        }
        catch(Exception ex){
            LOG.log(Level.SEVERE, "Error trying to get information APV with id : " + ex.getMessage(), ex);
            return null;
        }        
    }
    
    /**
     * Create an InformationAPV in database
     * @param identifiantClient
     * @param bilanLibelle
     * @param nombre
     * @param bilanAnnee
     * @return
     */
    /*private InformationAPV createInformationAPV(String identifiantClient, String bilanLibelle, Integer nombre, Integer bilanAnnee){
        // Create new Information APV
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
    }*/
    
    @Override
    public ContactClient createOrUpdateContactClient(String identifiantClient, String nomContact, String prenomContact, String fonctionContact, String telephone, String email, String numEtVoie,
            String codePostal, String ville, String pays) {
        EntityManager entityManager = database.get();
        ContactClient contactClient = null;
        // Check if client exists
        if(getClient(identifiantClient) == null){
            throw new IllegalArgumentException("The value of identifiantClient must match with a registred client in database");
        }        
        // Get contact client to update*/
        contactClient = this.getContactClient(identifiantClient, fonctionContact);
        if(contactClient == null){
            // Create new contact client
            contactClient = new ContactClient();
        }
        try{
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

    /**
     * Get the InformationAPV with corresponding id
     * @param id
     * @return The informationAPV or null
     */
    private ContactClient getContactClient(String identifiantClient, String fonctionContact){
        try {
            Query query = this.database.get().createQuery("SELECT c FROM ContactClient c WHERE c.identifiantClient = :identifiantclient AND c.fonctionContact = :fonctioncontact");
            query.setParameter("identifiantclient", identifiantClient);
            query.setParameter("fonctioncontact", fonctionContact);            
            ContactClient contactClient = (ContactClient) query.getSingleResult();
            return contactClient;
        }
        catch(Exception ex){
            LOG.log(Level.INFO, "Error trying to get contact client with id = " + identifiantClient + " and fonction contact = " + fonctionContact , ex);
            return null;
        }        
    }    
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Long> getRepartitionTypeStructure() {
        Map<String, Long> map = new HashMap<String, Long>();
        Query query = this.database.get().createQuery("SELECT DISTINCT c.typeStructure FROM Client c");
        List<String> resultList = (List<String>)query.getResultList();
        for(String type : resultList){
            query = this.database.get().createQuery("SELECT count(c) FROM Client c WHERE c.typeStructure = :type");    
            query.setParameter("type", type);
            Long count = (Long)query.getSingleResult();
            map.put(type, count);
        }
        return map;
    }
    
}
