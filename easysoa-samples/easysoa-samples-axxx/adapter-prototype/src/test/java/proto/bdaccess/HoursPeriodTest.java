package proto.bdaccess;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class HoursPeriodTest {
	
	
	@Test	
	public void equalsTest() throws ParseException{
		HoursPeriod p1=new HoursPeriod(0, 1440);
		HoursPeriod p2=new HoursPeriod(0, 1440);
		HoursPeriod p3=new HoursPeriod(600, 1080);
		HoursPeriod p4=new HoursPeriod(0, 1080);
		
		assertFalse(p1.equals(p3));
		assertFalse(p3.equals(p1));
		assertFalse(p4.equals(p1));
		assertFalse(p1.equals(p4));
		assertTrue(p1.equals(p2));
		assertFalse(p1.equals("string"));
		
		
	}
	
	@Test	
	public void inPeriodTest() throws ParseException{
		HoursPeriod p1=new HoursPeriod(600, 1080);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		
		Date date1=format.parse("2012-08-21_04:01");
		Date date2=format.parse("2012-08-21_10:01");
		assertFalse(p1.inPeriod(date1));
		
		assertTrue(p1.inPeriod(date2));
		
		
		
	}
	@Test	
	public void toStringTest() throws ParseException{
		HoursPeriod p1=new HoursPeriod(600, 1080);
		assertEquals("begining:600 ending:1080", p1.toString());
	}
	
	

}
