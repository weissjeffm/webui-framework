package com.redhat.qe.xmlrpc;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.auth.NegotiateScheme;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpParams;
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

import com.redhat.qe.tools.SSLCertificateTruster;

public class Session {

	protected String userName;
	protected String password;
	protected URL url;
	protected XmlRpcClient client;

	public Session(String userName, String password, URL url) {
		this.userName = userName;
		this.password = password;
		this.url = url;
	}

	public void init() throws XmlRpcException, GeneralSecurityException,
			IOException {
				SSLCertificateTruster.trustAllCertsForApacheXMLRPC();
			
				// setup client
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				config.setServerURL(url);
			
				client = new XmlRpcClient();
				client.setConfig(config);
				XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(client);
			    client.setTransportFactory(factory);
			    factory.setHttpClient(new HttpClient());
			    client.setTypeFactory(new MyTypeFactory(client));
			    			    
			    factory.getHttpClient().getState().setCredentials(
			    		new AuthScope(url.getHost(), 443, null), new UsernamePasswordCredentials(userName, password));
			    // register the auth scheme
		        AuthPolicy.registerAuthScheme("Negotiate", NegotiateScheme.class);

		        // include the scheme in the AuthPolicy.AUTH_SCHEME_PRIORITY preference
		        List<String> schemes = new ArrayList<String>();
		        schemes.add(AuthPolicy.BASIC);
		        schemes.add("Negotiate");

		        HttpParams params = DefaultHttpParams.getDefaultParams();        
		        params.setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, schemes);
		        
		        Credentials use_jaas_creds = new UsernamePasswordCredentials(userName, password);
		        factory.getHttpClient().getState().setCredentials(
		            new AuthScope(null, -1, null),
		            use_jaas_creds);

			}

	public XmlRpcClient getClient() {
		return client;
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

}

