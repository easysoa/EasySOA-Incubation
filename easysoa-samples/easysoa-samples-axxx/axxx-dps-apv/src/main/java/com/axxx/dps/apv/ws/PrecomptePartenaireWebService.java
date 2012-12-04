package com.axxx.dps.apv.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;



/**
 * TODO doc
 * @author mdutoo
 *
 */
@WebService(name="PrecomptePartenaireService", // used for imported WSDL, else default to java itf
	targetNamespace="http://www.axxx.com/dps/apv") // default to package ex. http://ws.apv.dps.axxx.com/
public interface PrecomptePartenaireWebService {

	/**
	 * TODO doc
	 * @param precomptePartenaire
	 */
	@WebMethod
	public void creerPrecompte(@WebParam(name="precomptePartenaire") PrecomptePartenaire precomptePartenaire);

}
