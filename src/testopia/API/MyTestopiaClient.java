package testopia.API;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
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
}
