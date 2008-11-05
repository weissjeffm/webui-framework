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
import java.util.ArrayList;

import org.apache.xmlrpc.client.XmlRpcClient;

/**
 * 
 * @author anelson, bstice
 * Retrives the user ID for the inputed email
 */
public class User {
	 
	
	private String user; 
	private Session session;
	
	 
	 /**
	  * 
	  * @param userName - your testopia/bugzilla username
	  * @param password - the password for your account 
	  * @param login - the user you want attributes returned for
	  * @param url - the url of the testopia server
	  */
	 public User(Session session, String login)
	 {
		 this.user = login;
		 this.session = session;
	 }
	
	 
	 /**
	 * @return the user_id for the specified login. Returns 0 if there is
	 *         an error and the user ID cannot be returned
	 */
	 public int getAttributes()
		{
			try 
			{
				XmlRpcClient client = session.getClient();

				ArrayList<Object> params = new ArrayList<Object>();
				
				//set up params, to identify the user
				params.add(user);
				
				//update the testRunCase
				int result = (Integer)client.execute("User.lookup_id_by_login",params);
				
				//System.out.println(result);
				
				return result;
			
				
			}			
			
			catch (Exception e)
			{
				e.printStackTrace();
				return 0;
			}
		}
}
