package proto.bdaccess;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.junit.Test;
import org.ow2.jasmine.event.beans.JasmineEventEB;

public class AdapterServiceRestTest {
	

    @Test
    public void easysoaRestTest() throws Exception{
        // demo setup
        //String pivotalHost = "localhost";
        //String apvHost = "localhost";
        //String registryHost = "localhost";
        String pivotalHost = "owsi-vm-easysoa-axxx-pivotal.accelance.net";
        String apvHost = "owsi-vm-easysoa-axxx-pivotal.accelance.net";
        String registryHost = "owsi-vm-easysoa-axxx-registry.accelance.net";
        
        String deploiementSubprojectId = "/default-domain/Intégration DPS - DCV/Deploiement_v";
        SoaNodeId tdrWebServiceProdEndpointSoaId = new SoaNodeId(deploiementSubprojectId, Endpoint.DOCTYPE, "Prod:http://" + apvHost + ":7080/apv/services/PrecomptePartenaireService");//TODO host
        SoaNodeId checkAddressProdEndpointSoaId = new SoaNodeId(deploiementSubprojectId, Endpoint.DOCTYPE, "Prod:" + fromEnc("iuuq;00xxx/ebub.rvbmjuz.tfswjdf/dpn0npdlxtr5220tfswjdft0JoufsobujpobmQptubmWbmjebujpo/JoufsobujpobmQptubmWbmjebujpoIuuqTpbq22Foeqpjou0"));//checkAddressProdEndpoint
        SoaNodeId informationAPVProdEndpointSoaId = new SoaNodeId(deploiementSubprojectId, Endpoint.DOCTYPE, "Prod:http://" + pivotalHost + ":7080/WS/ContactSvc.asmx");//TODO host // 18000
        
        // conf
        ExportREST exportREST = new ExportREST();
        //exportREST.setPassword("s0a"); // on VM

        // init
        exportREST.initClients("http://" + registryHost + ":8080/nuxeo/site",
                "Administrator", "s0a");
        ///String tdrWebServiceProdEndpointNuxeoId = exportREST.getIdRef(tdrWebServiceProdEndpointSoaId); // alt, does not work in jasmine
        String tdrWebServiceProdEndpointNuxeoId = exportREST.getRegistryApi().get(
                tdrWebServiceProdEndpointSoaId.getSubprojectId(),
                tdrWebServiceProdEndpointSoaId.getType(),
                tdrWebServiceProdEndpointSoaId.getName()).getUuid();
        
        // data
        Date dateFrom = new Date(0);
        Date dateTo = new Date(); // now
        String endPointId = tdrWebServiceProdEndpointNuxeoId;
        String slaOrOlaName = "SLA Dde_Cré_Précompte";
        String level = "silver";
        
        boolean res = exportREST.exportData(dateFrom, dateTo, endPointId, slaOrOlaName, level);
        assertEquals(true, res);
    }
	
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
		
		// REST call :
		
		adapt.calculAndExportIndicatorsFromTo();
	}


    protected static String toEnc(String s) {
        char[] sChars = s.toCharArray();
        for (int i = 0; i < sChars.length ; i++) {
            sChars[i] = (char) ((sChars[i] + 1) % 256);
        }
        return new String(sChars);
    }
    protected static String fromEnc(String s) {
        char[] sChars = s.toCharArray();
        for (int i = 0; i < sChars.length ; i++) {
            sChars[i] = (char) ((sChars[i] - 1) % 256);
        }
        return new String(sChars);
    }
    
}
