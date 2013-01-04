package com.axxx.dps.apv.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;



/**
 * TODO doc
 * @author mdutoo
 *
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
	 * TODO doc
	 * @param precomptePartenaire
	 */
	@WebMethod
	public void creerPrecompte(@WebParam(name="precomptePartenaire") PrecomptePartenaire precomptePartenaire);

}
