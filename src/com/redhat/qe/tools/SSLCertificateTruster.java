package com.redhat.qe.tools;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
		
}
