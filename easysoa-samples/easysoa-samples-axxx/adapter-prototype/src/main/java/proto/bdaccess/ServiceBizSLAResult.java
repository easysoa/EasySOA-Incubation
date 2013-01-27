package proto.bdaccess;

/**
 * Service business SLA result,
 *  
 *						The level indicator calculate and the service biz SLA
 */
public class ServiceBizSLAResult {
	private BusinessSLA  businessSLA;
	
	
	/**
	 * The level indicator calculate (after found in jasmine).
	 */
	private BusinessServiceLevel levelResult;
	
	
	/**
	 * Constructor.
	 */
	public ServiceBizSLAResult(BusinessSLA  bizSLA, BusinessServiceLevel level)
	{
		businessSLA= bizSLA;
		levelResult=level;
	}
	
	public BusinessSLA getBusinessSLA()
	{
		return businessSLA;
	}
	public BusinessServiceLevel getBusinessServiceLevel(){
		return levelResult;
	}
}
