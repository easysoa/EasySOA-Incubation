package fr.axxx.pivotal;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * Mock implementation of ContactSvcSoap to help test APV calling back to Pivotal.
 * <p/>
 * TODO still deploy it in tests
 * 
 * @author mdutoo
 *
 */
public class ContactSvcSoapMockImpl implements ContactSvcSoap {

	/**
	 * Returns how much Clients there is for each TypeStructure.
	 * <br/>
	 * The mock merely returns empty result
	 */
	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getRepartitionTypeStructureResponse", targetNamespace = "http://pivotal.axxx.fr/", partName = "parameters")
	@WebMethod(action = "http://pivotal.axxx.fr/getRepartitionTypeStructure")
	public GetRepartitionTypeStructureResponse getRepartitionTypeStructure() {
		GetRepartitionTypeStructureResponse res = new GetRepartitionTypeStructureResponse();
		// TODO
		return res;
	}

	/**
	 * Creates or updates the given client.
	 * <br/>
	 * The mock merely returns primary keys (identifiantClient)
	 */
	@Override
	@WebResult(name = "ClientResult", targetNamespace = "http://pivotal.axxx.fr/")
	@RequestWrapper(localName = "Client", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.Client")
	@WebMethod(operationName = "Client", action = "http://pivotal.axxx.fr/Client")
	@ResponseWrapper(localName = "ClientResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.ClientResponse")
	public ArrayOfString client(
			@WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/") String identifiantClient,
			@WebParam(name = "Raison_Sociale", targetNamespace = "http://pivotal.axxx.fr/") String raisonSociale,
			@WebParam(name = "Anciennete", targetNamespace = "http://pivotal.axxx.fr/") Integer anciennete,
			@WebParam(name = "Type_Structure", targetNamespace = "http://pivotal.axxx.fr/") String typeStructure,
			@WebParam(name = "Num_et_voie", targetNamespace = "http://pivotal.axxx.fr/") String numEtVoie,
			@WebParam(name = "Email", targetNamespace = "http://pivotal.axxx.fr/") String email,
			@WebParam(name = "Code_Postal", targetNamespace = "http://pivotal.axxx.fr/") String codePostal,
			@WebParam(name = "Ville", targetNamespace = "http://pivotal.axxx.fr/") String ville,
			@WebParam(name = "Pays", targetNamespace = "http://pivotal.axxx.fr/") String pays,
			@WebParam(name = "Tel", targetNamespace = "http://pivotal.axxx.fr/") String tel,
			@WebParam(name = "RIB", targetNamespace = "http://pivotal.axxx.fr/") String rib,
			@WebParam(name = "Forme_Juridique", targetNamespace = "http://pivotal.axxx.fr/") String formeJuridique,
			@WebParam(name = "SIREN", targetNamespace = "http://pivotal.axxx.fr/") String siren,
			@WebParam(name = "Dot_Glob_APV_N", targetNamespace = "http://pivotal.axxx.fr/") BigDecimal dotGlobAPVN,
			@WebParam(name = "Dont_Reliquat_N_1", targetNamespace = "http://pivotal.axxx.fr/") BigDecimal dontReliquatN1,
			@WebParam(name = "Dont_Dot_N", targetNamespace = "http://pivotal.axxx.fr/") BigDecimal dontDotN,
			@WebParam(name = "Nb_Benef_Prev_N", targetNamespace = "http://pivotal.axxx.fr/") BigDecimal nbBenefPrevN,
			@WebParam(name = "Montant_Utilise_N", targetNamespace = "http://pivotal.axxx.fr/") BigDecimal montantUtiliseN,
			@WebParam(name = "Nb_Benef_N", targetNamespace = "http://pivotal.axxx.fr/") BigDecimal nbBenefN) {
		ArrayOfString res = new ArrayOfString();
		res.string = new ArrayList<String>();
		res.string.add(identifiantClient);
		return res;
	}

	/**
	 * Returns the given existing Client.
	 * <br/>
	 * The mock merely returns primary keys (identifiantClient)
	 * <br/>
	 * <b>TODO The return type is probably wrong !!</b>
	 */
	@Override
	@WebResult(name = "Information_APVResult", targetNamespace = "http://pivotal.axxx.fr/")
	@RequestWrapper(localName = "getClient", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.GetClient")
	@WebMethod(action = "http://pivotal.axxx.fr/getClient")
	@ResponseWrapper(localName = "getClientResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.GetClientResponse")
	public ArrayOfString getClient(
			@WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/") String identifiantClient) {
		ArrayOfString res = new ArrayOfString();
		res.string = new ArrayList<String>();
		res.string.add(identifiantClient);
		return res;
	}

	/**
	 * Creates or updates the Information APV entry of the given client and of the given year and public.
	 * <br/>
	 * The mock merely returns primary keys (identifiantClient, bilanLibelle, bilanAnnee)
	 */
	@Override
	@WebResult(name = "Information_APVResult", targetNamespace = "http://pivotal.axxx.fr/")
	@RequestWrapper(localName = "Information_APV", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.InformationAPV")
	@WebMethod(operationName = "Information_APV", action = "http://pivotal.axxx.fr/Information_APV")
	@ResponseWrapper(localName = "Information_APVResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.InformationAPVResponse")
	public ArrayOfString informationAPV(
			@WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/") String identifiantClient,
			@WebParam(name = "Bilan_Libelle", targetNamespace = "http://pivotal.axxx.fr/") String bilanLibelle,
			@WebParam(name = "Nombre", targetNamespace = "http://pivotal.axxx.fr/") Integer nombre,
			@WebParam(name = "Bilan_Annee", targetNamespace = "http://pivotal.axxx.fr/") Integer bilanAnnee) {
		ArrayOfString res = new ArrayOfString();
		res.string = new ArrayList<String>();
		res.string.add(identifiantClient);
		res.string.add(bilanLibelle);
		res.string.add(String.valueOf(bilanAnnee));
		return res;
	}

	/**
	 * Creates or updates the contact of the given client and of the given type.
	 * <br/>
	 * The mock merely returns primary keys (identifiantClient, nomContact).
	 */
	@Override
	@WebResult(name = "Contact_ClientResult", targetNamespace = "http://pivotal.axxx.fr/")
	@RequestWrapper(localName = "Contact_Client", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.ContactClient")
	@WebMethod(operationName = "Contact_Client", action = "http://pivotal.axxx.fr/Contact_Client")
	@ResponseWrapper(localName = "Contact_ClientResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.ContactClientResponse")
	public ArrayOfString contactClient(
			@WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/") String identifiantClient,
			@WebParam(name = "Nom_Contact", targetNamespace = "http://pivotal.axxx.fr/") String nomContact,
			@WebParam(name = "Prenom_Contact", targetNamespace = "http://pivotal.axxx.fr/") String prenomContact,
			@WebParam(name = "Fonction_Contact", targetNamespace = "http://pivotal.axxx.fr/") String fonctionContact,
			@WebParam(name = "Telephone", targetNamespace = "http://pivotal.axxx.fr/") String telephone,
			@WebParam(name = "Email", targetNamespace = "http://pivotal.axxx.fr/") String email,
			@WebParam(name = "Num_et_voie", targetNamespace = "http://pivotal.axxx.fr/") String numEtVoie,
			@WebParam(name = "Code_postal", targetNamespace = "http://pivotal.axxx.fr/") String codePostal,
			@WebParam(name = "Ville", targetNamespace = "http://pivotal.axxx.fr/") String ville,
			@WebParam(name = "Pays", targetNamespace = "http://pivotal.axxx.fr/") String pays) {
		ArrayOfString res = new ArrayOfString();
		res.string = new ArrayList<String>();
		res.string.add(identifiantClient);
		res.string.add(nomContact);
		return res;
	}

}
