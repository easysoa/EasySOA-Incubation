/**
 * EasySOA Samples - AXXX
 * Copyright 2011-2012 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package com.axxx.dps.apv.ws;

import java.io.IOException;
import java.util.List;

import javax.xml.soap.SOAPException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.axxx.dps.apv.ws.PrecomptePartenaire;
import com.axxx.dps.apv.ws.PrecomptePartenaireWebService;

import fr.axxx.pivotal.ContactSvcSoap;
import fr.axxx.pivotal.GetRepartitionTypeStructureResponse;
import fr.axxx.pivotal.TypeStructureCount;

/**
 * Some tests require a running Pivotal on localhost ; those tests are disabled by default.
 * 
 * @author mdutoo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:axxx-dps-apv-test-context.xml" })
public class PrecomptePartenaireWebServiceTest {

    /**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(PrecomptePartenaireWebServiceTest.class.getClass());
    
    @Autowired
    @Qualifier("com.axxx.dps.apv.ws.PrecomptePartenaireWebServiceTestClient")
    private PrecomptePartenaireWebService precomptePartenaireWebService;

    @Autowired
    @Qualifier("com.ancv.form.ws.esbContactSvcClient")
    private ContactSvcSoap pivotalContactService;

	public void setProxyLocator(PrecomptePartenaireWebService precomptePartenaireWebService) {
        this.precomptePartenaireWebService = precomptePartenaireWebService;
    }	
	
	/**
	 * 
	 * @throws InterruptedException 
	 */
	@BeforeClass
	public static void setUp() throws InterruptedException {
		
	}

    @Test
	public void testCreerPrecompte() {
        Assert.assertNotNull(this.precomptePartenaireWebService);
    	PrecomptePartenaire precomptePartenaire = new PrecomptePartenaire();
    	precomptePartenaire.setIdentifiantClientPivotal("0x000E0006A00900C0");
    	precomptePartenaire.setNomStructure("ANECD");
    	precomptePartenaire.setTypeStructure(TypeStructure.ASSOCIATION_NAT);
    	precomptePartenaire.setAdresse("Carl-Metz-Str. 3");
    	precomptePartenaire.setCp("76185");
    	precomptePartenaire.setVille("Karlsruhe");
    	precomptePartenaire.setApeNaf("512E");
    	precomptePartenaire.setSirenSiret("");
		this.precomptePartenaireWebService.creerPrecompte(precomptePartenaire);
	}

    /**
     * Requires a running Pivotal on localhost
     */
    //@Test
    public void testCallInformationAPV() {
        Assert.assertNotNull(this.pivotalContactService);
        this.pivotalContactService.informationAPV("AssociationVacances", "jeunes", 3, 2012);
    }

    /**
     * Requires a running Pivotal on localhost
     */
    //@Test
    public void testGetRepartitionTypeStructure() {
        Assert.assertNotNull(this.pivotalContactService);
        GetRepartitionTypeStructureResponse repartitionTypeStructureRes =
                this.pivotalContactService.getRepartitionTypeStructure();
        Assert.assertNotNull(repartitionTypeStructureRes);
        Assert.assertNotNull(repartitionTypeStructureRes.getGetClientResult());
        List<TypeStructureCount> typeStructureCounts = repartitionTypeStructureRes.getGetClientResult().getTypeStructureCount();
        Assert.assertNotNull(typeStructureCounts);
        
        TypeStructureCount associationNatTypeStructureCount = null;
        for (TypeStructureCount typeStructureCount : typeStructureCounts) {
            if (typeStructureCount.getTypeStructure().equals("Association nat.")) {
                associationNatTypeStructureCount = typeStructureCount;
            }
        }
        Assert.assertNotNull(associationNatTypeStructureCount);
        Assert.assertTrue(associationNatTypeStructureCount.getClientCount() > 0);
    }
	
	/**
	 * Wait for an user action to stop the test 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	//@Test
	public final void testWaitUntilRead() throws Exception{
		logger.info("PrecomptePartenaireWebServiceImpl test started, wait for user action to stop");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("PrecomptePartenaireWebServiceImpl test stopped.");
	}	
}
