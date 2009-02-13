package testopia.API;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
//import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;



public class Session {

	protected String userName;
	protected String password;
	protected URL url;
	protected HttpState httpState = null;
	protected Integer userid;
	XmlRpcClient client;

	public Session(String userName, String password, URL url) {
		this.userName = userName;
		this.password = password;
		this.url = url;
	}

	public Object login()
	throws XmlRpcException, GeneralSecurityException, IOException
	{
		//TrustAllCerts();

		// setup client
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(url);

		client = new XmlRpcClient();
		client.setConfig(config);

		HttpClient httpClient = new HttpClient();
		XmlRpcCommonsTransportFactory fac = new XmlRpcCommonsTransportFactory(
				client);
		fac.setHttpClient(httpClient);
		client.setTransportFactory(fac);
		if (httpState == null)
			httpState = httpClient.getState();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("login", userName);
		map.put("password", password);
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(map);

		HashMap<String, Object>hash = (HashMap<String, Object>)
										client.execute("User.login", params);
		
		this.userid = (Integer) hash.get("id");
		return hash;
		
	}

	/*private void TrustAllCerts()
	throws GeneralSecurityException, IOException
	{
		try{
			// Create a trust manager that does not validate certificate chains
			System.out.println("JHOME="+System.getProperty("java.home"));
			//System.out.println("I have svn upped!!!!");
			ProtocolSocketFactory sf = new EasySSLProtocolSocketFactory();
			Protocol p = new Protocol("https", sf, 443);
			Protocol.registerProtocol("https", p);
		}
		catch(Exception e){
			System.out.println("Couldn't trust all certificates, things may break...");
		}
	}*/

	public XmlRpcClient getClient() {
		return client;
	}

	/**
	 * @return the userid
	 */
	public Integer getUserid() {
		return userid;
	}
}
