package com.axxx.dps.apv.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;


@WebService(name="ProjetService", // service interface name (portType), else defaults to java interface
	targetNamespace="http://www.axxx.com/dps/apv", // else defaults to java package ex. http://ws.apv.dps.axxx.com/
	serviceName="ProjetServiceImpl") // service implementation name (used for binding & port), else default to java impl
@Service("com.axxx.dps.apv.ws.projetWebServiceImpl")
public class ProjetWebServiceImpl implements ProjetWebService {

   	private Log log = LogFactory.getLog(this.getClass());
   	
	@Override
	public boolean publish(String projetId) {
		log.debug("publish start");
		return false;
	}

}
