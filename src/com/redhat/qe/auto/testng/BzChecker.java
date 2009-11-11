package com.redhat.qe.auto.testng;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;

import testopia.API.Session;
import testopia.API.TestopiaObject;

/**
 * Example code to retrieve a bugzilla bug's status, given its ID.  This is for future use with testng, 
 * so that testng can decide whether to execute a test, based on the group annotation (which may contain
 * a bug id), and the status of that bug.  If the status is ON_QA, for example, it can be tested.
 * @author weissj
 *
 */
public class BzChecker {	
	
	public enum bzState { NEW, ASSIGNED, MODIFIED, ON_DEV, ON_QA, VERIFIED, FAILS_QA, RELEASE_PENDING, POST, CLOSED };
	
	protected static Logger log = Logger.getLogger(BzChecker.class.getName());
	protected Bug bug;
	public BzChecker() {
		/*try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("/home/weissj/workspace/automatjon/jon-2.0/log.properties"));
		}catch(Exception e){
			System.err.println("Unable to read log config file.");
		}*/
	}
	
	public void init() {
		bug = new Bug();
		try {
			bug.connectBZ();
		}catch(Exception e){
			throw new RuntimeException("Could not initialize BzChecker." ,e);
		}
	}
	
	public bzState getBugState(String bugId) {
		Object[] bugs = null;
		try {
			bugs = bug.getBugs("ids", new Object[] {bugId});
		}catch(Exception e){
			throw new RuntimeException("Could not retrieve bug " + bugId + " from bugzilla." ,e);
		}
		
		if (bugs.length ==0) throw new IllegalStateException("No bug found matching ID " + bugId);
		else if (bugs.length > 1) throw new IllegalStateException("Multiple matches found for bug ID " + bugId);
		else {
			Object thisbug = bugs[0];
			Map bmap = (Map)thisbug;
			
			log.finer("Found bug: " + thisbug.toString() );
			Map internals = (Map)bmap.get("internals");
			String status = internals.get("bug_status").toString();
			log.finer("Bug status of " + bugId + " is " + status);
			return bzState.valueOf(status);
		}
	}
	
	public void setBugState(String bugId, bzState state) {
		try {
			bug.update_bug_status(bugId, state);
		}
		catch(Exception e){
			throw new RuntimeException("Could not set bug status " + bugId + " in bugzilla." ,e);
		}
	}
	
	public class Bug extends TestopiaObject{
		private String BZ_URL;
		//private StringAttribute bug_status = newStringAttribute("bug_status", null);
		
		public Bug(){
			listMethod = "Bug.get_bugs";
			System.setProperty("bugzilla.url", "https://bugzilla.redhat.com/bugzilla/xmlrpc.cgi");
		}
		
		protected void connectBZ() throws XmlRpcException, GeneralSecurityException, IOException{
			BZ_URL = System.getProperty("bugzilla.url");
			session = new Session(null, null, new URL(BZ_URL));
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
		
		
		public Map update_bug_status(String bug_id, bzState newState) throws XmlRpcException{
			Map<String,Object> main = new HashMap<String,Object>(); 
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put("bug_status", newState.toString());
			main.put("updates", updates);
			Map map = (Map) this.callXmlrpcMethod("bug.update", main);
			
			System.out.println(map);
			return map;
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		/*Bug myBug = new BzChecker().new Bug();
		//List<>
		myBug.connectBZ();
		List<String> ids = new ArrayList<String>();
		ids.add("497793");
		Object[] bugs = myBug.getBugs("ids",ids);
		for( Object bug: bugs){
			Map bmap = (Map)bug;
			
			log.info("Found bug: " + bug.toString() );
			Map internals = (Map)bmap.get("internals");
			log.info("Bug status is " + internals.get("bug_status"));
		}*/
		BzChecker checker = new BzChecker();
		checker.init();
		String id = "497793";
		log.info("State of " + id + " is " + checker.getBugState(id));
	}

}
