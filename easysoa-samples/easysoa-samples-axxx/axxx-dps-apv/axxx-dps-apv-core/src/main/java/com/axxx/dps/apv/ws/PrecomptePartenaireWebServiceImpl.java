package com.axxx.dps.apv.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.model.TdrTdb;
import com.axxx.dps.apv.service.TdrService;
import com.axxx.dps.apv.util.AxxxConfUtil;


/**
 * TODO doc
 * @author mdutoo
 * 
  targetNamespace="http://www.axxx.com/dps/apv"
  
  <wsdl:portType name="PrecomptePartenaireService">
    ...
  <wsdl:binding name="PrecomptePartenaireServiceImplSoapBinding" type="tns:PrecomptePartenaireService">
    ...

  <wsdl:service name="PrecomptePartenaireServiceImpl">
    <wsdl:port binding="tns:PrecomptePartenaireServiceImplSoapBinding" name="PrecomptePartenairePort">
      <soap:address location="http://localhost:8076/services/PrecomptePartenaireService"/>
    </wsdl:port>
  </wsdl:service>
 *
 */
@WebService(name="PrecomptePartenaire", // used for port, and if no java itf for portType (service interface) ; else defaults to java itf if any, else to java impl
	targetNamespace="http://www.axxx.com/dps/apv", // else defaults to java package ex. http://ws.apv.dps.axxx.com/
	serviceName="PrecomptePartenaireServiceImpl") // service (implementation) (reused for binding ), else default to java impl + "Service"
// wsdl:portType/@name = itfClass.@WebService.name ; else defaults to itfClass.name if any, else implClass.name
// wsdl:service/@name = implClass.@WebService.serviceName ; else defaults to implClass.name + "Service"
// wsdl:service/wsdl:port/@name = implClass.@WebService.name + "Port" ; else defaults to implClass.name + "Port"
//
// and deduced :
// wsdl:binding/@name = wsdl:service/@name + "SoapBinding" ; (thus defaults to implClass.name + "ServiceSoapBinding")
// wsdl:definitions/@name = wsdl:service/@name ; (thus defaults to implClass.name + "Service")
// wsdl:service/wsdl:port/soap:address/@location = from conf (Spring props)
//
// moreover, as usual :
// wsdl:binding/@type = wsdl:portType/@name
// wsdl:service/wsdl:port/@binding = wsdl:binding/@name
// & targetNamespace for all of them
// if impl & itf's targetNamespace differ, impl-generated wsdl has binding & service/port that use portType from imported itf-generated wsdl :
//
@Service("com.axxx.dps.apv.ws.precomptePartenaireWebServiceImpl")
public class PrecomptePartenaireWebServiceImpl implements PrecomptePartenaireWebService {

   	private Log log = LogFactory.getLog(this.getClass());
    
    @Autowired
    private TdrService tdrService;

	@Override
	public void creerPrecompte(PrecomptePartenaire precomptePartenaire) {
	    AxxxConfUtil.getInstance().sleep();
	    
		Tdr newTdr = new Tdr();
		// TODO fields
		newTdr.setAdresse(precomptePartenaire.getAdresse());
		newTdr.setCp(precomptePartenaire.getCp());
		newTdr.setEmail(precomptePartenaire.getEmail());
		newTdr.setIdentifiantClientPivotal(precomptePartenaire.getIdentifiantClientPivotal());
		newTdr.setNomStructure(precomptePartenaire.getNomStructure());
		newTdr.setSirenSiret(precomptePartenaire.getSirenSiret());
		newTdr.setTelephone(precomptePartenaire.getTelephone());
		newTdr.setTypeStructure(precomptePartenaire.getTypeStructure().toString());
		newTdr.setVille(precomptePartenaire.getVille());
		newTdr.setTdrTdb(new TdrTdb());
		newTdr.getTdrTdb().setStatus("created");
        tdrService.create(newTdr);
	}

	
}
