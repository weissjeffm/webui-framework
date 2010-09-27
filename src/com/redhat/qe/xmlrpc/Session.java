package com.redhat.qe.xmlrpc;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
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
			
				//org.apache.http.client.HttpClient httpClient = new org.apache.http.client.HttpClient();
				HttpClient  httpClient = new HttpClient();
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

