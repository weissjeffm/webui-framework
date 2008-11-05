 /*
  * The contents of this file are subject to the Mozilla Public
  * License Version 1.1 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.mozilla.org/MPL/
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * The Original Code is the Bugzilla Testopia Java API.
  *
  * The Initial Developer of the Original Code is Andrew Nelson.
  * Portions created by Andrew Nelson are Copyright (C) 2006
  * Novell. All Rights Reserved.
  *
  * Contributor(s): Andrew Nelson <anelson@novell.com>
  *
  */
package testopia.API;

import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Allows the user to get a hashmap of the component values by inputing the component ID
 * @author anelson
 *
 */
public class Component {
		private Session session; 
		
		 
		 /**
		  * 
		  * @param userName - your testopia/bugzilla username
		  * @param password - the password for your account 
		  * @param url - the url of the testopia server
		  */
		 public Component(Session session)
		 {
			 this.session = session;
		 }
		 
			/**
			 * 
			 * @param id the ID of the component that will be returned. Null is returned 
			 * if the component can't be found
			 * @return the product name that corresponds the specified product ID
			 */
			@SuppressWarnings("unchecked")
			public HashMap<String, Object> getComponentByID(int id)
			 {
				 try 
					{
						XmlRpcClient client = session.getClient();

						ArrayList<Object> params = new ArrayList<Object>();
						
						//set up params, to identify the build
						params.add(id);
						
						//get the result
						HashMap<String, Object> result = (HashMap<String, Object>)client.execute("Component.get", params);
						
						//System.out.println(result);
						
						return result;
					
						
					}			
					
					catch (Exception e)
					{
						e.printStackTrace();
						return null;
					}
			 }
}
