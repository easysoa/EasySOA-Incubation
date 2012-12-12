/**
 * 
 */
package fr.axxx.pivotal;

import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import fr.axxx.pivotal.client.api.ClientService;
import fr.axxx.pivotal.client.model.Client;
import fr.axxx.pivotal.client.model.InformationAPV;
import fr.axxx.pivotal.client.model.ContactClient;

/**
 * @author jguillemotte
 *
 */
@Scope("COMPOSITE")
public class ContactSvcSoapImpl implements ContactSvcSoap {

    private final static Logger LOG = Logger.getLogger(ContactSvcSoapImpl.class.getCanonicalName());    
    
    @Reference
    private ClientService clientService;
    
    @Override
    public ArrayOfString client(String identifiantClient, String raisonSociale, Integer anciennete, String typeStructure, String numEtVoie, String email, String codePostal, String ville, String pays,
            String tel, String rib, String formeJuridique, String siren, BigDecimal dotGlobAPVN, BigDecimal dontReliquatN1, BigDecimal dontDotN, BigDecimal nbBenefPrevN, BigDecimal montantUtiliseN,
            BigDecimal nbBenefN) {
        Client client = clientService.updateClient(identifiantClient, raisonSociale, 
                anciennete, typeStructure, numEtVoie, email, codePostal, 
                ville, pays, tel, rib, formeJuridique, siren);
        return mapClientToArrayOfString(client);
    }

    @Override
    public ArrayOfString informationAPV(String identifiantClient, String bilanLibelle, Integer nombre, Integer bilanAnnee) {
        ArrayOfString arrayOfString = new ArrayOfString();
        InformationAPV informationApv;
        try {
            informationApv = clientService.createOrUpdateInformationApv(identifiantClient, bilanLibelle, nombre, bilanAnnee);
            arrayOfString.getString().add(String.valueOf(informationApv.getId()));
            arrayOfString.getString().add(informationApv.getIdentifiantClient());
            arrayOfString.getString().add(informationApv.getBilanLibelle());
            arrayOfString.getString().add(String.valueOf(informationApv.getNombre()));
            arrayOfString.getString().add(String.valueOf(informationApv.getBilanAnnee()));            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error trying to create or update InformationAPV : " + ex.getMessage(), ex);
        }
        return arrayOfString;
    }

    @Override
    public ArrayOfString contactClient(String identifiantClient, String nomContact, String prenomContact, String fonctionContact, String telephone, String email, String numEtVoie, String codePostal,
            String ville, String pays) {
        ArrayOfString arrayOfString = new ArrayOfString();
        ContactClient contactClient;
        try {
            contactClient = clientService.createOrUpdateContactClient(identifiantClient, nomContact, prenomContact, fonctionContact, telephone, email, numEtVoie, codePostal, ville, pays);
            arrayOfString.getString().add(String.valueOf(contactClient.getId()));
            arrayOfString.getString().add(contactClient.getIdentifiantClient());
            arrayOfString.getString().add(contactClient.getNomContact());
            arrayOfString.getString().add(contactClient.getPrenomContact());
            arrayOfString.getString().add(contactClient.getFonctionContact());
            arrayOfString.getString().add(contactClient.getTelephone());
            arrayOfString.getString().add(contactClient.getEmail());
            arrayOfString.getString().add(contactClient.getComplement());
            arrayOfString.getString().add(contactClient.getNumEtVoie());
            arrayOfString.getString().add(contactClient.getCodePostal());
            arrayOfString.getString().add(contactClient.getVille());
            arrayOfString.getString().add(contactClient.getPays());            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error trying to create or update ContactClient : " + ex.getMessage(), ex);
        }

        return arrayOfString;
    }

    @Override
    public GetRepartitionTypeStructureResponse getRepartitionTypeStructure() {
        GetRepartitionTypeStructureResponse response = new GetRepartitionTypeStructureResponse();
        ArrayOfTypeStructureCount array = new ArrayOfTypeStructureCount(); 
        Map<String, Long> result = clientService.getRepartitionTypeStructure();
        for(String key : result.keySet()){
            TypeStructureCount type = new TypeStructureCount();
            type.setTypeStructure(key);
            type.setClientCount(result.get(key));
            array.getTypeStructureCount().add(type); 
        }
        response.setGetClientResult(array);
        return response;
    }

    @Override
    public ArrayOfString getClient(String identifiantClient) {
        ArrayOfString arrayOfString = new ArrayOfString();
        Client client = clientService.getClient(identifiantClient);
        arrayOfString.getString().add(client.getIdentifiantClient());
        return mapClientToArrayOfString(client);
    }

    /**
     * Map a client in an ArrayOfString
     * @param client The client to map
     * @return An ArrayOfString containing the client informations
     */
    private ArrayOfString mapClientToArrayOfString(Client client){
        ArrayOfString arrayOfString = new ArrayOfString();
        arrayOfString.getString().add(client.getIdentifiantClient());
        arrayOfString.getString().add(client.getRaisonSociale());
        arrayOfString.getString().add(client.getTypeStructure());
        arrayOfString.getString().add(client.getNumEtVoie());
        arrayOfString.getString().add(client.getEmail());
        arrayOfString.getString().add(client.getCodePostal());
        arrayOfString.getString().add(client.getVille());
        arrayOfString.getString().add(client.getPays());
        arrayOfString.getString().add(client.getTel());
        arrayOfString.getString().add(client.getRIB());
        arrayOfString.getString().add(client.getFormeJuridique());
        arrayOfString.getString().add(client.getSIREN());
        arrayOfString.getString().add(String.valueOf(client.getDotGlobAPVN()));
        arrayOfString.getString().add(String.valueOf(client.getDontReliquatN1()));
        arrayOfString.getString().add(String.valueOf(client.getDontDotN()));
        arrayOfString.getString().add(String.valueOf(client.getNbBenefN()));
        arrayOfString.getString().add(String.valueOf(client.getMontantUtiliseN()));
        arrayOfString.getString().add(String.valueOf(client.getNbBenefPrevN()));
        return arrayOfString;
    }
    
}
