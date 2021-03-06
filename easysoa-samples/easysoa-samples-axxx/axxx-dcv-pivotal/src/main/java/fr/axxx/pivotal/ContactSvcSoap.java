package fr.axxx.pivotal;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.4.2
 * 2012-12-11T18:54:45.313+01:00
 * Generated source version: 2.4.2
 * 
 */
@WebService(targetNamespace = "http://pivotal.axxx.fr/", name = "ContactSvcSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface ContactSvcSoap {

    /**
     * Get repartition TypeStructure
     */
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "getRepartitionTypeStructureResponse", targetNamespace = "http://pivotal.axxx.fr/", partName = "parameters")
    @WebMethod(action = "http://pivotal.axxx.fr/getRepartitionTypeStructure")
    public GetRepartitionTypeStructureResponse getRepartitionTypeStructure();

    /**
     * Client.
     */
    @WebResult(name = "ClientResult", targetNamespace = "http://pivotal.axxx.fr/")
    @RequestWrapper(localName = "Client", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.Client")
    @WebMethod(operationName = "Client", action = "http://pivotal.axxx.fr/Client")
    @ResponseWrapper(localName = "ClientResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.ClientResponse")
    public fr.axxx.pivotal.ArrayOfString client(
        @WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String identifiantClient,
        @WebParam(name = "Raison_Sociale", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String raisonSociale,
        @WebParam(name = "Anciennete", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.Integer anciennete,
        @WebParam(name = "Type_Structure", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String typeStructure,
        @WebParam(name = "Num_et_voie", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String numEtVoie,
        @WebParam(name = "Email", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String email,
        @WebParam(name = "Code_Postal", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String codePostal,
        @WebParam(name = "Ville", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String ville,
        @WebParam(name = "Pays", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String pays,
        @WebParam(name = "Tel", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String tel,
        @WebParam(name = "RIB", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String rib,
        @WebParam(name = "Forme_Juridique", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String formeJuridique,
        @WebParam(name = "SIREN", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String siren,
        @WebParam(name = "Dot_Glob_APV_N", targetNamespace = "http://pivotal.axxx.fr/")
        java.math.BigDecimal dotGlobAPVN,
        @WebParam(name = "Dont_Reliquat_N_1", targetNamespace = "http://pivotal.axxx.fr/")
        java.math.BigDecimal dontReliquatN1,
        @WebParam(name = "Dont_Dot_N", targetNamespace = "http://pivotal.axxx.fr/")
        java.math.BigDecimal dontDotN,
        @WebParam(name = "Nb_Benef_Prev_N", targetNamespace = "http://pivotal.axxx.fr/")
        java.math.BigDecimal nbBenefPrevN,
        @WebParam(name = "Montant_Utilise_N", targetNamespace = "http://pivotal.axxx.fr/")
        java.math.BigDecimal montantUtiliseN,
        @WebParam(name = "Nb_Benef_N", targetNamespace = "http://pivotal.axxx.fr/")
        java.math.BigDecimal nbBenefN
    );

    /**
     * Information APV.
     */
    @WebResult(name = "Information_APVResult", targetNamespace = "http://pivotal.axxx.fr/")
    @RequestWrapper(localName = "Information_APV", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.InformationAPV")
    @WebMethod(operationName = "Information_APV", action = "http://pivotal.axxx.fr/Information_APV")
    @ResponseWrapper(localName = "Information_APVResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.InformationAPVResponse")
    public fr.axxx.pivotal.ArrayOfString informationAPV(
        @WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String identifiantClient,
        @WebParam(name = "Bilan_Libelle", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String bilanLibelle,
        @WebParam(name = "Nombre", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.Integer nombre,
        @WebParam(name = "Bilan_Annee", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.Integer bilanAnnee
    );

    /**
     * Get Client.
     */
    @WebResult(name = "Information_APVResult", targetNamespace = "http://pivotal.axxx.fr/")
    @RequestWrapper(localName = "getClient", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.GetClient")
    @WebMethod(action = "http://pivotal.axxx.fr/getClient")
    @ResponseWrapper(localName = "getClientResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.GetClientResponse")
    public fr.axxx.pivotal.ArrayOfString getClient(
        @WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String identifiantClient
    );

    /**
     * Contact client.
     */
    @WebResult(name = "Contact_ClientResult", targetNamespace = "http://pivotal.axxx.fr/")
    @RequestWrapper(localName = "Contact_Client", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.ContactClient")
    @WebMethod(operationName = "Contact_Client", action = "http://pivotal.axxx.fr/Contact_Client")
    @ResponseWrapper(localName = "Contact_ClientResponse", targetNamespace = "http://pivotal.axxx.fr/", className = "fr.axxx.pivotal.ContactClientResponse")
    public fr.axxx.pivotal.ArrayOfString contactClient(
        @WebParam(name = "Identifiant_Client", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String identifiantClient,
        @WebParam(name = "Nom_Contact", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String nomContact,
        @WebParam(name = "Prenom_Contact", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String prenomContact,
        @WebParam(name = "Fonction_Contact", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String fonctionContact,
        @WebParam(name = "Telephone", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String telephone,
        @WebParam(name = "Email", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String email,
        @WebParam(name = "Num_et_voie", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String numEtVoie,
        @WebParam(name = "Code_postal", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String codePostal,
        @WebParam(name = "Ville", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String ville,
        @WebParam(name = "Pays", targetNamespace = "http://pivotal.axxx.fr/")
        java.lang.String pays
    );
}
