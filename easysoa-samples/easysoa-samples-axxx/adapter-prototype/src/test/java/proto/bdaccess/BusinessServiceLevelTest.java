package proto.bdaccess;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class BusinessServiceLevelTest {
	
	
	@Test	
	public void isworseTest() throws ParseException{
		
		assertTrue(BusinessServiceLevel.BRONZE.isWorse(BusinessServiceLevel.SILVER));
		assertFalse(BusinessServiceLevel.BRONZE.isWorse(BusinessServiceLevel.VIOLATION));
		assertTrue(BusinessServiceLevel.SILVER.isWorse(BusinessServiceLevel.GOLD));
		assertFalse(BusinessServiceLevel.SILVER.isWorse(BusinessServiceLevel.VIOLATION));
		
		assertFalse(BusinessServiceLevel.GOLD.isWorse(BusinessServiceLevel.VIOLATION));
		assertTrue(BusinessServiceLevel.VIOLATION.isWorse(BusinessServiceLevel.GOLD));
	}
	

}
