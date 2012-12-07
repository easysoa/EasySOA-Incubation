package com.axxx.dps.apv.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;


/**
 * TODO doc
 * @author mdutoo
 *
 */
@WebService(name="PrecomptePartenaire", // service interface name (portType), else defaults to java interface
	targetNamespace="http://www.axxx.com/dps/apv", // else defaults to java package ex. http://ws.apv.dps.axxx.com/
	serviceName="PrecomptePartenaireServiceImpl") // service implementation name (used for binding & port), else default to java impl
@Service("com.axxx.dps.apv.ws.precomptePartenaireWebServiceImpl")
public class PrecomptePartenaireWebServiceImpl implements PrecomptePartenaireWebService {

   	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void creerPrecompte(PrecomptePartenaire precomptePartenaire) {
		log.debug("creerPrecompte start");
	}

	
}
