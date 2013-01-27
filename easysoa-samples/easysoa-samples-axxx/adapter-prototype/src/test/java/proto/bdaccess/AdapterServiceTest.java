package proto.bdaccess;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ow2.jasmine.event.beans.JasmineEventEB;

public class AdapterServiceTest {
	
	
	
	@Test
	public void calculIndicatorsTest() throws ParseException{
		
		List<BusinessSLA>businessSLA = new ArrayList<BusinessSLA>();
		BusinessSLA temp = new BusinessSLA(70, 70, 70,
				new HoursPeriod(0, 1440), new HoursPeriod(420, 1320),
				new HoursPeriod(600, 1080), "smarttravel.com", "booktrip", "slaName");
		businessSLA.add(temp);
		AdapterService adapt= new AdapterService(businessSLA);
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		Date date=formate.parse("2012-07-01_12:00");
		JasmineEventEB e=new JasmineEventEB("qos-event", "", "", "misc:type=unknown:qosevent.smarttravel.com.booktrip.ResponseTime", "43",date, "");
		
		JasmineEventEB[] events=new JasmineEventEB[1];
		events[0]=e;
		
		String res=adapt.calculIndicators(events);
		assertEquals(res,"booktrip@smarttravel.com is GOLD level\n");
	}

}
