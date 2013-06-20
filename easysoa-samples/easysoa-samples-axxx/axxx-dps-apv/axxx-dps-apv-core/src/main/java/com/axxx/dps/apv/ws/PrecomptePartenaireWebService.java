package com.axxx.dps.apv.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;



/**
 * Gère les Précomptes des partenaires sociaux du programme Aide pour
 * les Projets Vacanciers (APV) : permet de
 * <ul>
 *    <li>en créer</li>
 *    <li>les mettre à jour</li>
 *    <li>retourner ceux existants</li>
 * </ul>
 * 
 * @author mdutoo
 */
@WebService(name="PrecomptePartenaireService", // portType (service interface), else defaults to java itf
	targetNamespace="http://www.axxx.com/dps/apv") // defaults to package ex. http://ws.apv.dps.axxx.com/
// wsdl:portType/@name = itfClass.@WebService.name ; else defaults to itfClass.name
//
// and deduced :
// wsdl:definitions/@name = wsdl:portType/@name (thus defaults to itfClass.name)
//
// moreover, as usual :
// & targetNamespace for all of them
public interface PrecomptePartenaireWebService {

	/**
	 * Créer le Précompte de partenaire avec les informations fournies
	 * @param precomptePartenaire le precompte à créer
	 */
	@WebMethod
	public void creerPrecompte(@WebParam(name="precomptePartenaire") PrecomptePartenaire precomptePartenaire);

	/**
     * Créer le Précompte de partenaire avec les informations fournies,
     * ou le met à jour s'il existe déjà et le retourne
	 * @param precomptePartenaire le precompte à créer
	 * @param mettreAJour si faux, ne le met pas à jour s'il existe déjà
	 */
	// with return, without @WebResult
    @WebMethod
	public PrecomptePartenaire creerEtRetournePrecompte(@WebParam(name="precomptePartenaire") PrecomptePartenaire precomptePartenaire, boolean mettreAJour);
    
    /**
     * Renvoie les Precomptes actuels
     * @return les Precomptes actuels
     */
    // without @WebMethod nor args
    public @WebResult(name="PrecomptePartenaires") PrecomptePartenaire[] getPrecomptePartenaires();
}
