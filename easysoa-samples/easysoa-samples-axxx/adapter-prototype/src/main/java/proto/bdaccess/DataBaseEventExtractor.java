/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
 * Contact: jasmine@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id: DataBaseEventExtractor.java 9583 2012-01-11 11:48:56Z durieuxp $
 * --------------------------------------------------------------------------
 */
package proto.bdaccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.rmi.RMISecurityManager;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ow2.jasmine.event.beans.JasmineEventEB;
import org.ow2.jasmine.monitoring.eventswitch.beans.JasmineEventSLBRemote;
import org.ow2.jasmine.monitoring.eventswitch.connectors.EJB3Connector;

/**
 * Class for the extraction of the data form a database
 * 
 * @author Christophe Merisier
 */
public class DataBaseEventExtractor {

	private boolean configdone = false;
	private Hashtable<String, String> options;
	private JasmineEventSLBRemote remote = null;

	private static Map<String, String> keys = null;
	static {
		keys = new TreeMap<String, String>();
		for (Field f : Context.class.getFields()) {
			try {
				if (f.getType().equals(java.lang.String.class)) {
					keys.put(f.getName(), (String) f.get(null));
				}
			} catch (Exception e) {
				// Nothing
			}
		}
	}
	
	/**
	 * basic constructor
	 */
	public DataBaseEventExtractor(){
		
	}
	/**
	 * constructor for test
	 * @param remote
	 */
	public DataBaseEventExtractor(JasmineEventSLBRemote remote){
		this.remote=remote;
	}
	
	
	/**
	 * Get the first event of the JASMINe Database
	 * 
	 * @return
	 * @throws NamingException
	 * @throws ParseException
	 * @throws IOException
	 */
	/*public JasmineEventEB getFirstEvent() throws NamingException,
			ParseException, IOException {
		if (!configdone) {
			setConfig();
		}
		// The classloader thing is here to solve a osgi problem 
		ClassLoader saved = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				AdapterService.class.getClassLoader());

		JasmineEventEB[] events = null;
		try {
			events = getBean().getEvents(null, null, null, null, "timestamp",
					0, 1);

		} finally {
			Thread.currentThread().setContextClassLoader(saved);
		}
		return events[0];
	}*/

	/**
	 * Get the events responding to the filter.
	 * 
	 * @param servers
	 * @param probes
	 * @param from
	 * @param to
	 * @param nbrecords
	 * @return
	 * @throws NamingException
	 * @throws ParseException
	 * @throws IOException
	 */
	public JasmineEventEB[] getEvents(final Collection<String> servers,
			final Collection<String> probes, final Date from, final Date to,
			final int nbrecords) throws NamingException, ParseException,
			IOException {

		if (!configdone) {
			setConfig();
		}
		// ask the bean to extract events from the database
		JasmineEventEB[] events = getBean().getEvents(servers, probes, from,
				to, "timestamp", 0, nbrecords);

		return events;
	}

	/**
	 * Get nbrecords of QoS event with a timestamp between from and to
	 * 
	 * @param from
	 * @param to
	 * @param nbrecords
	 * @return
	 * @throws NamingException
	 * @throws ParseException
	 * @throws IOException
	 */
	public JasmineEventEB[] getQoSEvents(final Date from, final Date to,
			final int nbrecords) throws NamingException, ParseException,
			IOException {

		if (!configdone) {
			setConfig();
		}
		
		// The classloader thing is here to solve a osgi problem 
		ClassLoader saved = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				AdapterService.class.getClassLoader());

		JasmineEventEB[] events = null;
		try {
			// ask the bean to extract events from the database
			events = getBean().getEvents("qos-event", null, null, null, null,
					from, to, "timestamp", 0, nbrecords);

		} finally {
			Thread.currentThread().setContextClassLoader(saved);
		}

		return events;
	}

	/**
	 * set configuration needed to connect to the bean
	 */
	private void setConfig() {
		options = new Hashtable<String, String>();
		options.put("Context.INITIAL_CONTEXT_FACTORY",
				"org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory");
		options.put("Wrapper.LOOKUP_BEAN", "db-ejb/event");

		configdone = true;
	}

	/**
	 * Establish connection with JASMINe database
	 * 
	 * @throws NamingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void connect() throws NamingException, IOException {
		/*
		 * if (configuration == null) { throw new
		 * RuntimeException("This EJB3Connector has not been initialized"); }
		 */
		if (Boolean.parseBoolean(options.get("Wrapper.INIT_SECURITY_MANAGER"))
				&& System.getSecurityManager() == null) {
			if (System.getProperty("java.security.policy") == null) {
				File temp = File
						.createTempFile("java.security.policy.", ".tmp");
				FileOutputStream s = new FileOutputStream(temp);
				s.write("grant { permission java.security.AllPermission; };"
						.getBytes());
				s.close();
				System.setProperty("java.security.policy", temp.toString());
			}
			System.setSecurityManager(new RMISecurityManager());
		}
		Hashtable<String, Object> env = new Hashtable<String, Object>(
				options.size() - 1);
		for (String k : keys.keySet()) {
			String value = options.get("Context." + k);
			if (value != null) {
				env.put(keys.get(k), value);
			}
		}
		
		// The classloader thing is here to solve a osgi problem 
		ClassLoader saved = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				AdapterService.class.getClassLoader());

		Context initialContext = null;
		try {
			initialContext = new InitialContext(env);
			remote = (JasmineEventSLBRemote) initialContext.lookup(options
					.get("Wrapper.LOOKUP_BEAN"));
		} finally {
			Thread.currentThread().setContextClassLoader(saved);
		}

	}
/**
 * initialize the remote object
 * @return
 * @throws NamingException
 * @throws IOException
 */
	private final JasmineEventSLBRemote getBean() throws NamingException,
			IOException {
		if (remote == null) {
			connect();
		}
		return remote;
	}

	/**
	 * finish the connection
	 */
	public void finished() {
		remote = null;
	}
}
