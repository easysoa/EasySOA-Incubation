package proto.bdaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ow2.jasmine.event.beans.JasmineEventEB;


/**
 * 
 */
@Component(name = "bdaccess")
@Instantiate
@Provides
public class AdapterService {

	private Date dateFrom;
	private Date dateTo;
	
	private List<BusinessSLA> businessSLA;

	private ExportREST exportREST= new ExportREST();
	
	/**
	 * Constructor.
	 */
	public AdapterService() {
	}
	
	/**
	 * constructor for test purpose
	 * @param businessSLA
	 */
	public AdapterService(List<BusinessSLA> businessSLA) {
		this.businessSLA = businessSLA;
	}
	
	/**
	 * start of the osgi bundle.
	 * Read the json config file.
	 */
	@Validate
	public void start() {
		businessSLA = new ArrayList<BusinessSLA>();
		/*
		 * BusinessSLA temp = new BusinessSLA(70, 70, 70, new HoursPeriod(0,
		 * 1440), new HoursPeriod(420, 1320), new HoursPeriod(600, 1080),
		 * "smarttravel.com", "meteo_service"); businessSLA.add(temp); temp =
		 * new BusinessSLA(70, 60, 50, new HoursPeriod(0, 1440), new
		 * HoursPeriod(420, 1320), new HoursPeriod(600, 1080),
		 * "smarttravel.com", "exchg_rate_service"); businessSLA.add(temp); temp
		 * = new BusinessSLA(70, 60, 50, new HoursPeriod(0, 1440), new
		 * HoursPeriod(420, 1320), new HoursPeriod(600, 1080),
		 * "smarttravel.com", "translation_service"); businessSLA.add(temp);
		 */
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		
		try {
			System.out.println("Processing jasmine QoS events data");

			String path = System.getenv("JONAS_BASE")
					+ System.getProperty("file.separator") + "conf"
					+ System.getProperty("file.separator")
					+ "easysoa-adapter.json";
			
			InputStream inputStream;
			JSONObject config;
			
			try {
				inputStream = new FileInputStream(path);
				
				JSONTokener tokener = new JSONTokener(new InputStreamReader(
						inputStream));
				
				config = new JSONObject(tokener);
				
				dateFrom = formate.parse(config.getString("from"));
				dateTo = formate.parse(config.getString("to"));
				
				JSONArray sla = config.getJSONArray("BusinessSLA");

				for (int i = 0; i < sla.length(); i++) {
					businessSLA.add(readSLA(sla.getJSONObject(i)));
				}
				inputStream.close();
				
				exportData(dateFrom, dateTo);
				
				calculIndicatorsFromTo(dateFrom, dateTo);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calculTodayIndicators();

	}
/**
 * read all the sla json object and make a java object of it.
 * 
 * @param obj
 * @return
 * @throws JSONException
 */
	private BusinessSLA readSLA(JSONObject obj) throws JSONException {
		int maxGold = obj.getInt("maxGold");
		int maxSilver = obj.getInt("maxSilver");
		
		int maxBronze = obj.getInt("maxBronze");
		
		HoursPeriod goldPeriod = new HoursPeriod(obj
				.getJSONObject("goldPeriod").getInt("beginning"), obj
				.getJSONObject("goldPeriod").getInt("end"));
		HoursPeriod silverPeriod = new HoursPeriod(obj.getJSONObject(
				"silverPeriod").getInt("beginning"), obj.getJSONObject(
				"silverPeriod").getInt("end"));
		
		HoursPeriod bronzePeriod = new HoursPeriod(obj.getJSONObject(
				"bronzePeriod").getInt("beginning"), obj.getJSONObject(
				"bronzePeriod").getInt("end"));
		
		String provider = obj.getString("provider");
		String service = obj.getString("service");

		// read new field: SlaOrOlaName
		String slaOrOlaName= obj.getString("slaOrOlaName");
		
		BusinessSLA temp = new BusinessSLA(maxGold, maxSilver, maxBronze,
				goldPeriod, silverPeriod, bronzePeriod, provider, service, slaOrOlaName);

		return temp;
	}

	/**
	 * Export the QoS data between date1 and date2 from jasmine database to a file.
	 * 
	 * @param date1
	 * @param date2
	 */
	public void exportData(Date date1, Date date2) {
		
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-sss");
		String date = formate.format(new Date(System.currentTimeMillis()));
		
		// creating file path
		String path = System.getenv("JONAS_BASE")
				+ System.getProperty("file.separator") + "logs"
				+ System.getProperty("file.separator") + "qos-event-" + date;
		
		try {
			File f = new File(path);
			
			// open the file
			PrintWriter pw = new PrintWriter(new FileWriter(f), true);
			
			// print the header
			pw.println("Qos events from " + date1 + " to " + date2);

			DataBaseEventExtractor db = new DataBaseEventExtractor();
			
			// extracting data from database
			JasmineEventEB[] events = db.getQoSEvents(date1, date2, 1200);
			
			// print every data
			for (JasmineEventEB ev : events) {
				pw.println(ev.getTimestamp() + " " + ev.getProbe() + " : "
						+ ev.getValue());
			}
			// close the file
			pw.flush();

			pw.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NamingException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		}
	}


	/**
	 * compute today indicator with the database and store them in a file.
	 */
	public void calculTodayIndicators() {

		try {
			// get today date
			Date today = new Date();
			
			// calcul today start
			Calendar cal = Calendar.getInstance();
			
			Date todayStart = new Date(
					today.getTime()
							- ((cal.get(Calendar.HOUR_OF_DAY) * 60 * 60
							+ cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND)) * 1000));
			
			// extract today qos events from the database
			DataBaseEventExtractor db = new DataBaseEventExtractor();

			JasmineEventEB[] events = db.getQoSEvents(todayStart, today, 12000);
			
			// calculate the indicators from the events
			String res = calculIndicators(events);

			// Store the result, creating file name
			SimpleDateFormat formate = new SimpleDateFormat(
					"yyyy-MM-dd_HH-mm-ss");
				String date = formate.format(new Date(System.currentTimeMillis()));
				String path = System.getenv("JONAS_BASE")
					+ System.getProperty("file.separator") + "logs"
					+ System.getProperty("file.separator") + "service-level-"
					+ date;
				
			// open the file
			File f = new File(path);

			PrintWriter pw = new PrintWriter(new FileWriter(f), true);
			// print result in the file
			pw.println(res);
			// close the file
			pw.flush();

			pw.close();
		} catch (NamingException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Compute the indicator between from to to with the database and store them in a file.
	 * 
	 * @param from
	 * @param to
	 */
	public void calculIndicatorsFromTo(Date from, Date to) {
		try {
			DataBaseEventExtractor db = new DataBaseEventExtractor();
			JasmineEventEB[] events = db.getQoSEvents(from, to, 12000);
			String res = calculIndicators(events);

			// Store the result
			SimpleDateFormat formate = new SimpleDateFormat(
					"yyyy-MM-dd_HH-mm-ss");
			String fromS = formate.format(from);
			String toS = formate.format(to);
			
			String path = System.getenv("JONAS_BASE")
					+ System.getProperty("file.separator") + "logs"
					+ System.getProperty("file.separator")
					+ "service-level-from-" + fromS + "-to-" + toS;

			File f = new File(path);

			PrintWriter pw = new PrintWriter(new FileWriter(f), true);
			pw.println(res);
			pw.flush();

			pw.close();

		} catch (NamingException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Export the QoS data between date1 and date2 from jasmine database to EasySOA via a REST call.
	 * 
	 */
	public void calculAndExportIndicatorsFromTo() {
		try {
			// extract data from the DB Jasmine
			DataBaseEventExtractor db = new DataBaseEventExtractor();
			JasmineEventEB[] events = db.getQoSEvents(dateFrom, dateTo, 12000);
			
			// calculate biz SLA indicators of services based on event coming from Jasmine
			List<ServiceBizSLAResult> listServiceBizSLAResult= calculIndicatorsService(events);

			// the REST url target base of EasySOA
			String urlTarget="todo:";
			
			// export each biz SLA level service to esaySOA (REST call)
			for (ServiceBizSLAResult servBizSLAResult : listServiceBizSLAResult) {				
				// for each service, export the data indicators
				// exportData(Date dateFrom, Date dateTo, String endPointId, String slaOrOlaName, String level) 
				exportREST.exportData(dateFrom, dateTo , servBizSLAResult.getBusinessSLA().getProvider(), servBizSLAResult.getBusinessSLA().getSlaOrOlaName(), servBizSLAResult.getBusinessServiceLevel().toString());				
			}	
			
		} catch (IOException e) {

			e.printStackTrace();
		} catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	/**
	 * compute indicators for each service in the events.
	 * 
	 * @param events
	 * @return
	 */
	public String calculIndicators(JasmineEventEB[] events) {
		// if no events return nothing
		if (events == null)
			return "";
		
		// a hashmap to store the result
		Hashtable<String, BusinessServiceLevel> serviceLevels = new Hashtable<String, BusinessServiceLevel>();
		
		// for each events
		for (JasmineEventEB ev : events) {
			// parsing the database line
			String probe = ev.getProbe();
			String[] evProp = probe.split("\\.");
			String provider = evProp[1] + "." + evProp[2];
			String service = evProp[3];
			String identifier = service + "@" + provider;
			String dataType = evProp[4];
			
			// if it s a response time event
			if (dataType.equals("ResponseTime")) {
				// finding corresponding BusinessSLA
				Iterator<BusinessSLA> it = businessSLA.iterator();
				BusinessSLA sla = null;
				int k = 0;
				while (it.hasNext()
						&& (!(sla = it.next()).getProvider().equals(provider) || !sla
								.getService().equals(service))) {
					k++;
				}
				
				// there is a SLA
				if (k < businessSLA.size()) {
					// Checking SLA
					BusinessServiceLevel level = sla.computeServiceLevel(
							Long.parseLong((String) ev.getValue()),
							ev.getTimestamp());
					// update SL of the service if it worse than actual
					BusinessServiceLevel oldLevel = serviceLevels
							.get(identifier);
					if (oldLevel == null || level.isWorse(oldLevel)) {
						serviceLevels.put(identifier, level);
					}
				}
			}
		}
		
		// building response string
		StringBuffer res = new StringBuffer();
		Enumeration<String> serviceIdentifiers = serviceLevels.keys();
		
		while (serviceIdentifiers.hasMoreElements()) {
			String serviceIdentifier = serviceIdentifiers.nextElement();
			BusinessServiceLevel level = serviceLevels.get(serviceIdentifier);
			res.append(serviceIdentifier + " is " + level + " level");
			res.append("\n");
		}
		return res.toString();
	}
	
	/**
	 * compute indicators for each service in the events.
	 * 
	 * return list of serviceBizSLA and level calculate (from jasmine events).
	 * 
	 * @param events
	 * @return
	 */
	public List<ServiceBizSLAResult> calculIndicatorsService(JasmineEventEB[] events) {
		// to store SLA result for services
		List<ServiceBizSLAResult> listServiceBizSLAResult= new ArrayList<ServiceBizSLAResult>();
		
		// if no events return nothing
		if (events == null)
			return listServiceBizSLAResult;
				
		// a hashmap to store the result
		Hashtable<String, BusinessServiceLevel> serviceLevels = new Hashtable<String, BusinessServiceLevel>();
		
		// for each events
		for (JasmineEventEB ev : events) {
			// parsing the database line
			String probe = ev.getProbe();
			String[] evProp = probe.split("\\.");
			String provider = evProp[1] + "." + evProp[2];
			String service = evProp[3];
			String identifier = service + "@" + provider;
			String dataType = evProp[4];
			
			// if its a response time event
			if (dataType.equals("ResponseTime")) {
				// finding corresponding BusinessSLA
				Iterator<BusinessSLA> it = businessSLA.iterator();
				BusinessSLA sla = null;
				int k = 0;
				while (it.hasNext() && 
						(!(sla = it.next()).getProvider().equals(provider) || !sla.getService().equals(service))) {
					k++;
				}
				
				// there is a SLA
				if (k < businessSLA.size()) {
					// Checking SLA
					BusinessServiceLevel level = sla.computeServiceLevel(
							Long.parseLong((String) ev.getValue()),
							ev.getTimestamp());
					// update SL of the service if it worse than actual
					BusinessServiceLevel oldLevel = serviceLevels.get(identifier);
					
					if (oldLevel == null || level.isWorse(oldLevel)) {
						serviceLevels.put(identifier, level);
						
						// save the result: the level indicator calculate and the service biz SLA
						ServiceBizSLAResult serviceBizSLAResult = new ServiceBizSLAResult(sla, level);
					}
				}
			}
		}
		return listServiceBizSLAResult;
	}
	

	/**
	 * things done when the osgi service stop
	 */
	@Invalidate
	public void stop() {

		System.out.println("stop");

	}
	
}
