package org.easysoa.registry.rest.integration;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Endpoint state service interface.
 * Meant to be called by operational SOA monitoring platforms,
 * such as Bull's OW2 Jasmine on top of Talend ESB and its SOA SLA rules.
 * 
 * Architecture : see https://github.com/easysoa/EasySOA/wiki/Easysoa-integration-architecture---monitoring-platform
 * 
 * TODO jguillemotte
 * 
 * implementation :
 *  * meant to be on top of Nuxeo Directory (actually SQL, with the right indexes)
 * * dates are ISO8601 (in java use the right SimpleDateFormat), see http://stackoverflow.com/questions/3558458/recommended-date-format-for-rest-api  
 * 
 * @author mdutoo, jguillemotte
 *
 */
@Path("easysoa/endpointStateService")
public interface EndpointStateService {

	/**
	 * Creates (OPT or udpdates) each of the given indicator, for the given endpointId, levelName and timestamp.
	 * NB. endpointId et slaOrOlaName sont à récupérer du modèle EasySOA des Specifications et mettre dans la configuration de la plateforme de monitoring
	 *  (d'abord manuellement puis récupérés automatiquement au démarrage étant donné l'id du sous-projet de déploiement versionné) 
	 * @param SlaOrOlaIndicators : array of SlaOrOlaIndicator
	 * {endpointId, slaOrOlaName, timestamp, serviceLevelHealth=gold/silver/bronze, serviceLevelViolation=true/false})
	 * where endpointId is the nuxeo id of the endpoint
	 * @throws Exception
	 */
	public void updateSlaOlaIndicators(SlaOrOlaIndicator[] SlaOrOlaIndicators) throws Exception;
	
	/**
	 * Returns level indicators, in the given period (default : daily)
	 * TODO add criteria as required by UI :
	 * * endpointId & slaOrOlaName
	 * * or wider : at least environment and subprojectId (or only global environment) ; possibly componentId... 
	 * OPT paginated navigation
     * @param periodStart : if null day start, if both null returns all in the current day
     * @param periodEnd : if null now, if both null returns all in the current day
     * @param pageSize OPT pagination : number of indicators per page
     * @param pageStart OPT pagination : index of the first indicator to return (starts with 0)
	 * @return SlaOrOlaIndicators array of SlaOrOlaIndicator
	 */
	public SlaOrOlaIndicator[] getSlaOrOlaIndicators(Date periodStart, Date periodEnd, int pageSize, int pageStart);
	
}
