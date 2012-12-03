package fr.axxx.pivotal.client.api;

import java.util.List;

import org.osoa.sca.annotations.Service;

import fr.axxx.pivotal.client.model.Client;
import fr.axxx.pivotal.client.model.ContactClient;
import fr.axxx.pivotal.client.model.InformationAPV;

/**
 * Manages client
 */
@Service
public interface ClientService {

    /**
     * Returns all clients
     * @return
     */
	List<Client> listClient();

	/**
	 * Create a new client
	 * @param identifiantClient
	 * @param raisonSociale
	 * @param siren
	 * @param email
	 * @return The new client
	 */
    Client createClient(String identifiantClient, String raisonSociale, String siren, String email);

    /**
     * Remove a client
     * @param identifiantClient
     */
    void removeClient(String identifiantClient);

    /**
     * Get a client
     * @param identifiantClient
     * @return The corresponding client
     */
    Client getClient(String identifiantClient);

    /**
     * 
     * @param identifiantClient
     * @return
     */
    List<InformationAPV> getInformationAPV(String identifiantClient);    
    
    /***
     * 
     * @param identifiantClient
     * @return
     */
    List<ContactClient> getContactClient(String identifiantClient);    
    
    /**
     * Update a client
     * @param identifiantClient
     * @param raisonSociale
     * @param siren
     * @param email
     * @return The updated client
     */
    Client updateClient(String identifiantClient, String raisonSociale, String siren, String email);
	
    /**
     * Call the web service creerPrecompte
     * @param identifiantClient
     * @param raisonSociale
     * @param siren
     * @param email
     * @throws Exception If a problem occurs
     */
    String creerPrecompte(String identifiantClient) throws Exception;

    /**
     * 
     * @param identifiantClient
     * @param bilanLibelle
     * @param nombre
     * @param bilanAnnee
     * @return
     */
    InformationAPV createInformationApv(String identifiantClient, String bilanLibelle, Integer nombre, Integer bilanAnnee);

    /**
     * 
     * @param identifiantClient
     * @param nomContact
     * @param prenomContact
     * @param fonctionContact
     * @param telephone
     * @param email
     * @param numEtVoie
     * @param codePostal
     * @param ville
     * @param pays
     * @return
     */
    ContactClient createContactClient(String identifiantClient, String nomContact, String prenomContact, String fonctionContact, String telephone, String email, String numEtVoie, String codePostal,
            String ville, String pays);
    
}
