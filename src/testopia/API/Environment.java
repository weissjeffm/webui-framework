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
  * 				Jason Sabin <jsabin@novell.com>
  *
  */
package testopia.API;

import java.util.HashMap;
import org.apache.xmlrpc.XmlRpcException;

/**
 * Allows the user to get an environment from it's ID. It can also create 
 * and update an environment
 * @author anelson
 *
 */
public class Environment extends TestopiaObject{
	 
	 /**
	  * Constructor for Testopia Environment Object
	  * @param session session object to facilitate XMLRPC connection
	  */
	 public Environment(Session session)
	 {
		 this.session = session;
	 }
	 
	 
	 /**
	  * Creates a new environment and returns the environmentID, 0 is returned 
	  * if an error occurs
	  * @param name name of environment
	  * @param productID product ID integer that environment is tied to
	  * @param isActive boolean stating whether environment is active or not
	 * @throws XmlRpcException 
	  */
	 public int makeEnvironment(String name, int productID, boolean isActive)
	 throws XmlRpcException
	 { 
		 //Check if the environment already exists. Will return a null if the environment does not exist
		 HashMap<String, Object> environmentTest = listEnvironments(productID, name);
		 
		 if(environmentTest == null){
			 //environment does not exist so we need to create a new environment
		 
			 HashMap<String, Object> map = new HashMap<String, Object>();
			 
			 //1 for true, 0 for false
			 if(isActive)
				 map.put("isactive", 1);
			 else
				 map.put("isactive", 0);
			 
			 map.put("name", name);
			 map.put("product_id", productID);	
			//get the result
			return (Integer) this.callXmlrpcMethod("Environment.create", map);
		 }
		 else{
			 //Build already exists
			 System.out.println("-->Build "+name+" already exists will not create build");
			 //Set the id correctly before returning
			 String envIDString = environmentTest.get("environment_id").toString();
			 return Integer.parseInt(envIDString);
		 }
	 }
	 
	 /**
	  * Updates the environment on testopia with the specified parameters
	  * @param name string - the name of the build. Can be null
	  * @param milestone string - the milestone. Can be null
	  * @param isactive Boolean - if the build is active. Can be null
 	  * @param description String - description of the build. Can be null
	  * @param buildID int - the buildID
	 * @throws XmlRpcException 
	  */
	 public void updateEnvironment(String name, Boolean isactive, 
			 Integer productID, int environmentID) throws XmlRpcException
	 {
		 //put values into map if they are not null 
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(name != null)
			 map.put("name", name);
		if(productID != null)
			 map.put("product_id", productID);
		if(isactive != null)
		{
			 //put 1 into map if true
			 if(isactive)
				 map.put("isactive", 1);
		 	 //else put false
			 else 
				 map.put("isactive", 0);
		}
		//get the result
		this.callXmlrpcMethod("Environment.update",environmentID,
												   map);
	 }
	 
	 /**
	  * Returns the environmnet as a HashMap or null if environment can't be found
	  * @param environmentName
	  * @return
	 * @throws XmlRpcException 
	  */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getEnvirnoment(int environmentID)
	throws XmlRpcException
	 {
		return (HashMap<String, Object>)this.callXmlrpcMethod("Environment.get", environmentID);
	 }
	 
	 /**
	  * 
	  * @param productName - the name of the product that the 
	  * @param environmentName
	  * @return
	 * @throws XmlRpcException 
	  */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> listEnvironments(String productName, String environmentName)
	throws XmlRpcException
	{
		//set up params, to identify the environment
		if(productName != null)	
		{
			Product product = new Product(session);
			int productId = product.getProductIDByName(productName);
			if(environmentName != null)
				return (HashMap<String, Object>)this.callXmlrpcMethod("Environment.get",
																	  productId,
																	  environmentName);
			else
				return (HashMap<String, Object>)this.callXmlrpcMethod("Environment.get",
						  											  productId);
		}
		if(environmentName != null){
			if(productName != null){
				Product product = new Product(session);
				int productId = product.getProductIDByName(productName);
				return (HashMap<String, Object>)this.callXmlrpcMethod("Environment.get",
						  											  productId,
						  											  environmentName);
			}
			else
				return (HashMap<String, Object>)this.callXmlrpcMethod("Environment.get",
						  											  environmentName);
		}
		return null;
	}

	 /**
	  * 
	  * @param productId - the product id 
	  * @param environmentName
	  * @return
	 * @throws XmlRpcException 
	  */
	 public HashMap<String, Object> listEnvironments(int productId, String environmentName) throws XmlRpcException
	 {
		 if(environmentName != null) return (HashMap<String, Object>)callXmlrpcMethod("Environment.get", productId, environmentName);
		 else return (HashMap<String, Object>)callXmlrpcMethod("Environment.get", productId);
		 
	 }
}
