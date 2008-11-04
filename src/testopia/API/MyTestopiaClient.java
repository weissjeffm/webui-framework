package testopia.API;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;

import sun.net.www.http.HttpClient;

public class MyTestopiaClient {

	
	public static void main(String[] args) throws Exception{
	    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	    config.setServerURL(new URL("url"));
	    config.setBasicUserName("userName");
	    config.setBasicPassword("password");

	    final XmlRpcClient client = new XmlRpcClient();
	    client.setConfig(config);

		 XmlRpcTransportFactory factory = new XmlRpcTransportFactory() {
			 public XmlRpcTransport getTransport(){ 
				 return new XmlRpcSunHttpTransport(client){
			 
		            private URLConnection conn;
		            protected URLConnection newURLConnection(URL pURL) throws IOException {
		                conn = super.newURLConnection(pURL);
		                return conn;
		            }
		            protected void initHttpHeaders(XmlRpcRequest pRequest) {
		                try {
							super.initHttpHeaders(pRequest);
						} catch (XmlRpcClientException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                setCookies(conn);
		            }
		            protected void close() throws XmlRpcClientException {
		                getCookies(conn);
		            }
		            private void setCookies(URLConnection pConn) {
		                // Implement me ...
		            }
		            private void getCookies(URLConnection pConn) {
		                // Implement me ...
		            }
				 };
			 }
	    };

		
	    
	  /*  final HttpClient httpClient = new HttpClient();
	    final XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(client);
	    factory.setHttpClient(httpClient);
	    client.setTransportFactory(factory);
	    final HttpState httpState = client.getState();

	    return client;*/

	}
}
