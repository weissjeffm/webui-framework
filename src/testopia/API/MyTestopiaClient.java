package testopia.API;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;


public class MyTestopiaClient {

	
	/*public static void main(String[] args) throws Exception{
	    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	    config.setServerURL(new URL("url"));
	    config.setBasicUserName("userName");
	    config.setBasicPassword("password");

	    final XmlRpcClient client = new XmlRpcClient();
	    client.setConfig(config);

	    final HttpClient httpClient = new HttpClient();
	    final XmlRpcCommonsTransportFactory fac = new XmlRpcCommonsTransportFactory(client);
	    fac.setHttpClient(httpClient);
	    client.setTransportFactory(fac);
	    final HttpState httpState = httpClient.getState();
	    

	}*/
	
	/*private int getNextTestRunID(String caseID) throws XmlRpcException{
		Object[] params = new Object[]{caseID};
		Object[] result;
		
		try{
			result = (Object[]) client.execute("Bugzilla.Testopia.Webservice.TestCase.get_case_run_history", params);
		}
		catch (XmlRpcException e){
			log.info("Encountered an error while gathering testrun IDs!");
			throw e;
		}
		
		int highest = -1;
		for (int i=0;i<result.length;i++){
			Hashtable elem = (Hashtable) result[i];
			int id = (Integer) elem.get("run_id");
			if (id > highest)
			   highest = id;
		}
		return (highest + 1);
	}*/
}
