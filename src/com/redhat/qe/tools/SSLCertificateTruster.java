package com.redhat.qe.tools;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * @author jsefler
 * 
 */
public class SSLCertificateTruster {
	
	/**
	 * This code was taken from an article entitled "Disabling Certificate Validation in an HTTPS Connection"
	 * http://www.exampledepot.com/egs/javax.net.ssl/TrustAll.html
	 */
	public static void trustAllCerts() {
	
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {

			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {

			}
		} };
		
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public static void trustAllCertsForApacheHttp() throws GeneralSecurityException, IOException
	{
		try{
			// Create a trust manager that does not validate certificate chains
			//System.out.println("JHOME="+System.getProperty("java.home"));
			//System.out.println("I have svn upped!!!!");
			ProtocolSocketFactory sf = new EasySSLProtocolSocketFactory();
			Protocol p = new Protocol("https", sf, 443);
			Protocol.registerProtocol("https", p);
		}
		catch(Exception e){
			System.out.println("Couldn't trust all certificates, things may break...");
		}
	}

}
