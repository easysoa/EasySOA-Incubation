package proto.bdaccess;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.NamingException;

import org.junit.Test;
import org.ow2.jasmine.event.beans.JasmineEventEB;
import org.ow2.jasmine.monitoring.eventswitch.beans.JasmineEventSLBRemote;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataBaseEventExtractorTest {

	
	
	
	@Test
	public void getQoSEventsTest() throws ParseException, NamingException, IOException{
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		Date from=formate.parse("2012-07-01_12:00");
		Date t1=formate.parse("2012-07-05_12:00");
	    Date to=formate.parse("2012-07-11_12:00");
	    JasmineEventEB ev1=new JasmineEventEB("qos-event", "server", "source", "probe", 10, t1, "sname");
	    JasmineEventEB[] events = new JasmineEventEB[1];
	    events[0]=ev1;
	    
		JasmineEventSLBRemote mockremote = mock(JasmineEventSLBRemote.class);
		when(mockremote.getEvents("qos-event", null, null, null, null,
				from, to, "timestamp", 0, 10)).thenReturn(events);
		
		DataBaseEventExtractor dbee=new DataBaseEventExtractor(mockremote);
		JasmineEventEB[] response =dbee.getQoSEvents(from, to, 10);
		assertEquals(response[0].getDomain(),"qos-event");
		assertEquals(response[0].getValue(),10);
		
		
	}
	
	

}
