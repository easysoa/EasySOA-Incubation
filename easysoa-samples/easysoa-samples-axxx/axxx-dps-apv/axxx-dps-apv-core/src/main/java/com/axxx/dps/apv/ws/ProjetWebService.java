package com.axxx.dps.apv.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Manages the lifecycle of APV Web "Projets" (requests for funded holidays)
 * 
 * @author mdutoo
 *
 */
@WebService(name="ProjetService", // used for imported WSDL, else default to java itf
	targetNamespace="http://www.axxx.com/dps/apv") // default to package ex. http://ws.apv.dps.axxx.com/
public interface ProjetWebService {
   
	/**
	 * Publishes the given projet to business reporting and DCV Pivotal CRM
	 * 
	 * @param projetId the projet to publish
	 * @return true if successful, false otherwise
	 */
	@WebMethod @WebResult(name="success", targetNamespace="http://www.axxx.com/dps/apv")
    public boolean publish(@WebParam(name="projetId") String projetId);

}
