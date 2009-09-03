package com.redhat.qe.auto.testng;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;

import testopia.API.Session;
import testopia.API.TestopiaObject;

public class BzChecker {	
	protected static Logger log = Logger.getLogger(BzChecker.class.getName());
	
	public BzChecker(){
		log.setLevel(Level.FINEST);
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("/home/weissj/workspace/automatjon/jon-2.0/log.properties"));
		}catch(Exception e){
			System.err.println("Unable to read log config file.");
		}
		
	}
	
	public class Bug extends TestopiaObject{
		private String BZ_URL;
		private String BZ_PW;
		private String BZ_USER;
		private String BZ_TESTRUN_PRODUCT;
		private String BZ_TESTRUN_TESTPLAN;

		public Bug(){
			listMethod = "Bug.get_bugs";
			System.setProperty("bugzilla.url", "https://bugzilla.redhat.com/bugzilla/xmlrpc.cgi");
		}
		
		protected void loginBZ() throws XmlRpcException, GeneralSecurityException, IOException{
			BZ_URL = System.getProperty("bugzilla.url");
			BZ_USER = System.getProperty("bugzilla.login");
			BZ_PW = System.getProperty("bugzilla.password");
			BZ_TESTRUN_PRODUCT = System.getProperty("bugzilla.testrun.product");
			BZ_TESTRUN_TESTPLAN = System.getProperty("bugzilla.testrun.testplan");
			log.finer("Logging in to bugzilla as " + BZ_USER);
			session = new Session(BZ_USER, BZ_PW, new URL(BZ_URL));
			try {
				session.init();
			}
			catch(Exception e){
				log.log(Level.FINE, "Couldn't set up bugzilla connection.", e);
			}
		}
		
		/*
		 * Returns a Map containing an Array of Maps.  Within the innermost Maps (which represent bugs), there's another
		 * Map under the key "internals", which has a key "bug_status".  ugh.
		 */
		public Object[] getBugs(Map<String, Object> values) throws XmlRpcException
		{
			//some Testopia objects have no listing mechanism
			if(listMethod == null)
				return null;
			
			Map map = (Map) this.callXmlrpcMethod(listMethod, values);
			return (Object[])map.get("bugs");
			//return result;
		}
	
		public Object[] getBugs(String name, Object value) throws XmlRpcException {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(name, value);
			return getBugs(map);
		}
	}
	
	public static void main(String[] args) throws Exception{
		Bug myBug = new BzChecker().new Bug();
		//List<>
		myBug.loginBZ();
		List<String> ids = new ArrayList<String>();
		ids.add("497793");
		Object[] bugs = myBug.getBugs("ids",ids);
		for( Object bug: bugs){
			//log.info("Is this a Map? " + (bug instanceof Map));
			//log.info("Is this a Hashtable? " + (bug instanceof Hashtable));
			//log.info("Is this a HashMap? " + (bug instanceof HashMap));
			Map bmap = (Map)bug;
			
			log.info("Found bug: " + bug.toString() );
			Map internals = (Map)bmap.get("internals");
			log.info("Bug status is " + internals.get("bug_status"));
		}
	}

}
