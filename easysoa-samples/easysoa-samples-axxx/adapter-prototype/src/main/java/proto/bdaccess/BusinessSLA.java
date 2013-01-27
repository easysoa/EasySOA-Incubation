package proto.bdaccess;

import java.util.Date;
/**
 * a class modelising a service business sla.
 * @author Florent
 *
 */
public class BusinessSLA {

	//max that response time should attain in gold period
	private int maxGold;
	//max that response time should attain in Silver period
	private int maxSilver;
	//max that response time should attain in Bronze period
	private int maxBronze;
	
	//period of time for gold level
	private HoursPeriod goldPeriod;
	//period of time for silver level
	private HoursPeriod silverPeriod;
	//period of time for bronze level
	private HoursPeriod bronzePeriod;
	
	//the provider of the service
	private String provider;
	//the name of the service
	private String service;
	
	// SLA or OLA name
	private String slaOrOlaName;
	
	/**
	 * Constructor that initialize all SLA (or OLA) value.
	 * 
	 * @param maxGold
	 * @param maxSilver
	 * @param maxBronze
	 * @param goldPeriod
	 * @param silverPeriod
	 * @param bronzePeriod
	 * @param provider
	 * @param service
	 * @param slaOrOlaName
	 */
	public BusinessSLA(int maxGold, int maxSilver, int maxBronze,
			HoursPeriod goldPeriod, HoursPeriod silverPeriod,
			HoursPeriod bronzePeriod, String provider, String service,String slaOrOlaName) {
		super();
		this.maxGold = maxGold;
		this.maxSilver = maxSilver;
		this.maxBronze = maxBronze;
		this.goldPeriod = goldPeriod;
		this.silverPeriod = silverPeriod;
		this.bronzePeriod = bronzePeriod;
		this.provider = provider;
		this.service = service;
		this.slaOrOlaName= slaOrOlaName;
	}
	/**
	 * constructor initializing no value
	 */
	public BusinessSLA(){
		
	}
	/**
	 * this methode take a response time value 
	 * and a timestamp when this value was monitored.
	 * then compute the service level 
	 * according to the value of the attribute of the current sla
	 * @param responseTime
	 * @param timeStamp
	 * @return
	 */
	public BusinessServiceLevel computeServiceLevel(long responseTime,Date timeStamp){
		//assume that the service is at it best shape
		BusinessServiceLevel sl=BusinessServiceLevel.GOLD;
		
		//check if its gold
		if(goldPeriod.inPeriod(timeStamp)&&responseTime>maxGold){
			//it s not so its silver
			sl=BusinessServiceLevel.SILVER;
			//check if it s worse than silver
			if(silverPeriod.inPeriod(timeStamp)&&responseTime>maxSilver){
				//it's not even silver level so it s bronze
				sl=BusinessServiceLevel.BRONZE;
				//lets checks if there is a violation of the sla
				if(bronzePeriod.inPeriod(timeStamp)&&responseTime>maxBronze){
					//violation of the sla
					sl=BusinessServiceLevel.VIOLATION;
				}
			}				
		}
		
		return sl;
	}
	
	/**
	 * return the maxgold attribute of the currente sla.
	 * max that response time should attain in gold period
	 * @return
	 */
	public int getMaxGold() {
		return maxGold;
	}
	
	/**
	 * set the maxgold attribute of the current sla
	 * max that response time should attain in gold period
	 * @param maxGold
	 */
	public void setMaxGold(int maxGold) {
		this.maxGold = maxGold;
	}
	/**
	 * return the maxsilver attribute of the current sla.
	 * max that response time should attain in Silver period
	 * @return
	 */
	public int getMaxSilver() {
		return maxSilver;
	}
	/**
	 * set the maxsilver attribute of the current sla.
	 *  max that response time should attain in Silver period
	 * @param maxSilver
	 */
	public void setMaxSilver(int maxSilver) {
		this.maxSilver = maxSilver;
	}
	/**
	 * return the maxBronze attribute of the current sla.
	 * max that response time should attain in Bronze period
	 * @return
	 */
	public int getMaxBronze() {
		return maxBronze;
	}
	/**
	 * set the maxBronze attribute of the current sla.
	 *  max that response time should attain in Bronze period
	 * @param maxBronze
	 */
	public void setMaxBronze(int maxBronze) {
		this.maxBronze = maxBronze;
	}
	/**
	 * return the goldperiod attribute of the current sla.
	 * period of time of the day for gold level
	 * @return
	 */
	public HoursPeriod getGoldPeriod() {
		return goldPeriod;
	}
	/**
	 * set the goldperiod attribute of the current sla.
	 * period of time of the day for gold level
	 * @param goldPeriod
	 */
	public void setGoldPeriod(HoursPeriod goldPeriod) {
		this.goldPeriod = goldPeriod;
	}
	/**
	 * return the SilverPeriod attribute of the current sla.
	 * period of time of the day for silver level
	 * @return
	 */
	public HoursPeriod getSilverPeriod() {
		return silverPeriod;
	}
	/**
	 * set the SilverPeriod attribute of the current sla.
	 * period of time of the day for silver level
	 * @param silverPeriod
	 */
	public void setSilverPeriod(HoursPeriod silverPeriod) {
		this.silverPeriod = silverPeriod;
	}
	
	/**
	 * return the bronzePeriod attribute of the current sla.
	 * period of time of the day for bronze level
	 * @return
	 */
	public HoursPeriod getBronzePeriod() {
		return bronzePeriod;
	}
	/**
	 * set the bronzePeriod attribute of the current sla.
	 * period of time of the day for bronze level
	 * @param bronzePeriod
	 */
	public void setBronzePeriod(HoursPeriod bronzePeriod) {
		this.bronzePeriod = bronzePeriod;
	}
	
	/**
	 * return the provider of the service of the current sla
	 * @return
	 */
	public String getProvider() {
		return provider;
	}
	/**
	 * set the provider of the service of the current sla
	 * @param provider
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}
	/**
	 * return the service of the current sla
	 * @return
	 */
	public String getService() {
		return service;
	}
	/**
	 * set the service of the current sla
	 * @param service
	 */
	public void setService(String service) {
		this.service = service;
	}
	
	public String getSlaOrOlaName(){
		return slaOrOlaName;
	}
	
	
	
	
}
