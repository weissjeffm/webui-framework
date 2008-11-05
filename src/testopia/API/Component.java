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

import java.util.HashMap;
import org.apache.xmlrpc.XmlRpcException;

/**
 * Allows the user to get a hashmap of the component values by inputing the component ID
 * @author anelson
 *
 */

public class Component extends TestopiaObject{
		/**
		 * Constructor for Testopia Component Object
		 * @param session session object to facilitate XMLRPC connection
		 */
		 public Component(Session session)
		 {
			 this.session = session;
		 }
		 
		/**
		 * Returns components that match supplied ID number
		 * @param id the ID of the component that will be returned. Null is returned 
		 * if the component can't be found
		 * @return the product name that corresponds the specified product ID
		 */
		@SuppressWarnings("unchecked")
		public HashMap<String, Object> getComponentByID(int id) throws XmlRpcException
		{
			return (HashMap<String, Object>)
				this.callXmlrpcMethod("Component.get", id);
		}
}
