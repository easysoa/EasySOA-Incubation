package fr.axxx.pivotal.client.api;

import java.util.List;

import org.osoa.sca.annotations.Service;

import fr.axxx.pivotal.client.model.Client;

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
     * Update a client
     * @param identifiantClient
     * @param raisonSociale
     * @param siren
     * @param email
     * @return The updated client
     */
    Client updateClient(String identifiantClient, String raisonSociale, String siren, String email);
	
}
