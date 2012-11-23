package fr.axxx.pivotal.client.api;

import java.util.List;

import org.osoa.sca.annotations.Service;

import fr.axxx.pivotal.client.model.Client;

/**
 * Manages client
 */
@Service
public interface ClientService {

	List<Client> listClient();

}
