package testopia.API;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.parser.NullParser;
import org.apache.xmlrpc.parser.TypeParser;
import org.apache.xmlrpc.serializer.NullSerializer;

import sun.net.www.http.HttpClient;







public class Session {

	protected String userName;
	protected String password;
	protected URL url;
	//protected HttpState httpState = null;
	protected Integer userid;
	XmlRpcClient client;

	public Session(String userName, String password, URL url) {
		this.userName = userName;
		this.password = password;
		this.url = url;
	}

	public void init() throws XmlRpcException, GeneralSecurityException, IOException{
		TrustAllCerts();

		// setup client
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(url);

		client = new XmlRpcClient();
		client.setConfig(config);

		//org.apache.http.client.HttpClient httpClient = new org.apache.http.client.HttpClient();
		DefaultHttpClient  httpClient = new DefaultHttpClient();
		XmlRpcCommonsTransportFactory fac = new XmlRpcCommonsTransportFactory(
				client);

		
		/*NegotiateSchemeFactory nsf = new NegotiateSchemeFactory();
		httpClient.getAuthSchemes().register(AuthPolicy.SPNEGO, nsf);*/
		
		/*fac.setHttpClient(httpClient);
		client.setTransportFactory(fac);
		if (httpState == null)
			httpState = httpClient.getState();*/
		
		fac.setHttpClient(httpClient);
		
		httpClient.getState().setCredentials(new AuthScope(url.getHost(), 443, null),
				new UsernamePasswordCredentials(userName, password));
		client.setTransportFactory(fac);
		client.setTypeFactory(new MyTypeFactory(client));

	}
	
	public Object login()
	throws XmlRpcException, GeneralSecurityException, IOException
	{
		return login("Auth.login","login",userName,"password",password,"id");
	}
	
	
	public Object login(String loginMethod,String loginKey, String login, String passKey, String password,String returnKey)
	throws XmlRpcException, GeneralSecurityException, IOException
	{
		init();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(loginKey, login);
		map.put(passKey, password);
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(map);

		HashMap<String, Object>hash = (HashMap<String, Object>)
										client.execute(loginMethod, params);
		
		this.userid = (Integer) hash.get(returnKey);
		return hash;
		
	}

	private void TrustAllCerts()
	throws GeneralSecurityException, IOException
	{
		try{
			Security.addProvider(new MyProvider());
			Security.setProperty(
		            "ssl.TrustManagerFactory.algorithm",
		            "TrustAllCertificates");
			// Create a trust manager that does not validate certificate chains
			//System.out.println("JHOME="+System.getProperty("java.home"));
			//System.out.println("I have svn upped!!!!");
			/*ProtocolSocketFactory sf = new EasySSLProtocolSocketFactory();
			Protocol p = new Protocol("https", sf, 443);
			Protocol.registerProtocol("https", p);*/
		}
		catch(Exception e){
			System.out.println("Couldn't trust all certificates, things may break...");
		}
	}

	public XmlRpcClient getClient() {
		return client;
	}

	/**
	 * @return the userid
	 */
	public Integer getUserid() {
		return userid;
	}
	
	 /* The following code disables certificate checking.
	    * Use the Security.addProvider and Security.setProperty
	    * calls to enable it */
	   public static class MyProvider extends Provider {
	      public MyProvider() {
	         super( "MyProvider", 1.0, "Trust certificates" );
	         put( "TrustManagerFactory.TrustAllCertificates",
	            MyTrustManagerFactory.class.getName() );
	      }
	      protected static class MyTrustManagerFactory
	            extends TrustManagerFactorySpi {
	         public MyTrustManagerFactory() {}
	         protected void engineInit( KeyStore keystore ) {}
	         protected void engineInit(
	            ManagerFactoryParameters mgrparams ) {}
	         protected TrustManager[] engineGetTrustManagers() {
	            return new TrustManager[] {
	               new MyX509TrustManager()
	            };
	         }
	      }
	      protected static class MyX509TrustManager
	            implements X509TrustManager {
	         public void checkClientTrusted(
	            X509Certificate[] chain, String authType) {}
	         public void checkServerTrusted(
	            X509Certificate[] chain, String authType) {}
	         public X509Certificate[] getAcceptedIssuers() {
	            return null;
	         }
	      }
	   }

	public class MyTypeFactory extends TypeFactoryImpl {

	    public MyTypeFactory(XmlRpcController pController) {
	        super(pController);
	    }

	    @Override
	    public TypeParser getParser(XmlRpcStreamConfig pConfig,
	      NamespaceContextImpl pContext, String pURI, String pLocalName) {

	        if ("".equals(pURI) && NullSerializer.NIL_TAG.equals(pLocalName)) {
	            return new NullParser();
	        } else {
	            return super.getParser(pConfig, pContext, pURI, pLocalName);
	        }
	    }
	}
	
	public static void main (String... args) throws Exception{
		Session session = new Session("jweiss", "113!!#Two", new URL("https://tcms.engineering.redhat.com/xmlrpc/"));
		session.login();
	}

}
