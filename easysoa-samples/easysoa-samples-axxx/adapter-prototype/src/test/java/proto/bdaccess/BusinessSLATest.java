package proto.bdaccess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BusinessSLATest {
	
	@Test	
	public void computeServiceLevelTestViolation() throws ParseException{
		BusinessSLA sla= new BusinessSLA(70, 70, 70,
				new HoursPeriod(0, 1440), new HoursPeriod(420, 1320),
				new HoursPeriod(600, 1080), "smarttravel.com", "meteo_service","slaName");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		int responseTime=75;
		Date timeStamp=format.parse("2012-08-21_10:01");
		BusinessServiceLevel level=sla.computeServiceLevel(responseTime, timeStamp);
		
		assertEquals(BusinessServiceLevel.VIOLATION, level);
		
		
	}
	
	@Test	
	public void computeServiceLevelTestBronze() throws ParseException{
		BusinessSLA sla= new BusinessSLA(70, 80, 85,
				new HoursPeriod(0, 1440), new HoursPeriod(420, 1320),
				new HoursPeriod(600, 1080), "smarttravel.com", "meteo_service","slaName");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		int responseTime=84;
		Date timeStamp=format.parse("2012-08-21_12:01");
		BusinessServiceLevel level=sla.computeServiceLevel(responseTime, timeStamp);
		
		assertEquals(BusinessServiceLevel.BRONZE, level);
		
		
	}
	
	@Test	
	public void computeServiceLevelTestSilver() throws ParseException{
		BusinessSLA sla= new BusinessSLA(70, 80, 85,
				new HoursPeriod(0, 1440), new HoursPeriod(420, 1320),
				new HoursPeriod(600, 1080), "smarttravel.com", "meteo_service","slaName");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		int responseTime=75;
		Date timeStamp=format.parse("2012-08-21_07:01");
		BusinessServiceLevel level=sla.computeServiceLevel(responseTime, timeStamp);
		
		assertEquals(BusinessServiceLevel.SILVER, level);
		
		
	}
	
	@Test	
	public void computeServiceLevelTestGold() throws ParseException{
		BusinessSLA sla= new BusinessSLA(70, 70, 70,
				new HoursPeriod(0, 1440), new HoursPeriod(420, 1320),
				new HoursPeriod(600, 1080), "smarttravel.com", "meteo_service","slaName");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		int responseTime=69;
		Date timeStamp=format.parse("2012-08-21_04:01");
		BusinessServiceLevel level=sla.computeServiceLevel(responseTime, timeStamp);
		
		assertEquals(BusinessServiceLevel.GOLD, level);
		
		
	}
	@Test	
	public void getTest() throws ParseException{
		BusinessSLA sla= new BusinessSLA(70, 85, 90,
				new HoursPeriod(0, 1440), new HoursPeriod(420, 1320),
				new HoursPeriod(600, 1080), "smarttravel.com", "meteo_service","slaName");
		
		
		
		assertEquals(sla.getGoldPeriod(),new HoursPeriod(0, 1440));
		assertEquals(sla.getSilverPeriod(),new HoursPeriod(420, 1320));
		assertEquals(sla.getBronzePeriod(),new HoursPeriod(600, 1080));
		assertEquals(sla.getMaxGold(), 70);
		assertEquals(sla.getMaxSilver(), 85);
		assertEquals(sla.getMaxBronze(), 90);
		assertEquals(sla.getProvider(), "smarttravel.com");
		assertEquals(sla.getService(), "meteo_service");
		
		
		
	}
	
	@Test	
	public void setTest() throws ParseException{
		BusinessSLA sla= new BusinessSLA();
		sla.setGoldPeriod(new HoursPeriod(0, 1440));
		sla.setSilverPeriod(new HoursPeriod(420, 1320));
		sla.setBronzePeriod(new HoursPeriod(600, 1080));
		sla.setMaxGold(70);
		sla.setMaxSilver(85);
		sla.setMaxBronze(90);
		sla.setProvider("smarttravel.com");
		sla.setService("meteo_service");
		
		
		
		
		
		assertEquals(sla.getGoldPeriod(),new HoursPeriod(0, 1440));
		assertEquals(sla.getSilverPeriod(),new HoursPeriod(420, 1320));
		assertEquals(sla.getBronzePeriod(),new HoursPeriod(600, 1080));
		assertEquals(sla.getMaxGold(), 70);
		assertEquals(sla.getMaxSilver(), 85);
		assertEquals(sla.getMaxBronze(), 90);
		assertEquals(sla.getProvider(), "smarttravel.com");
		assertEquals(sla.getService(), "meteo_service");
		
		
		
	}
	
	
	

}
