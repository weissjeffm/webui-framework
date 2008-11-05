package testopia.API;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

public class Session {
	
	protected String userName;
	protected String password;
	protected URL url;
	protected HttpState httpState = null;
	
	public Session(String userName, String password, URL url)
	{
		this.userName = userName;
		this.password = password;
		this.url = url; 
	}
	
	public Object login() throws XmlRpcException{
		//setup client
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(url);
		config.setBasicUserName(userName);
		config.setBasicPassword(password);

		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		
		HttpClient httpClient = new HttpClient();
	    XmlRpcCommonsTransportFactory fac = new XmlRpcCommonsTransportFactory(client);
	    fac.setHttpClient(httpClient);
	    client.setTransportFactory(fac);
	    if(httpState == null)
	    	httpState = httpClient.getState();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("login", userName);
		map.put("password", password);
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(map);
		
		return client.execute("User.login", params);

	}

}
