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
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Allows the user to get an environment from it's ID. It can also create 
 * and update an environment
 * @author anelson
 *
 */
public class Environment extends TestopiaObject{

	private IntegerAttribute productId = newIntegerAttribute("product", null);
	private StringAttribute name = newStringAttribute("name", null);
	private BooleanAttribute isactive = newBooleanAttribute("isactive", null);

	/**
	 * Constructor for Testopia Environment Object
	 * @param session session object to facilitate XMLRPC connection
	 */
	public Environment(Session session, Integer productId, String name)
	{
		this.session = session;
		this.productId.set(productId);
		this.name.set(name);
		this.id = newIntegerAttribute("environment_id", null);
	}


	/**
	 * Updates are not called when the .set is used. You must call update after all your sets
	 * to push the changes over to testopia.
	 * @throws TestopiaException if planID is null 
	 * @throws XmlRpcException
	 * (you made the TestCase with a null caseID and have not created a new test plan)
	 */
	public Map<String,Object> update() throws TestopiaException, XmlRpcException
	{
		//update the testRunCase
		return super.updateById("Environment.update");
	}

	/**
	 * Calls the create method with the attributes as-is (as set via contructors
	 * or setters).  
	 * @return a map of the newly created object
	 * @throws XmlRpcException
	 */
	public Map<String,Object> create() throws XmlRpcException{
		Map map = super.create("Environment.create");		
		return map;
	}



	/**
	 * Returns the environmnet as a HashMap or null if environment can't be found
	 * @param environmentName
	 * @return
	 * @throws XmlRpcException 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getEnvironment(int environmentID)
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
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> listEnvironments(int productId, String environmentName)
	throws XmlRpcException
	{
		if(environmentName != null) return (HashMap<String, Object>)callXmlrpcMethod("Environment.get", productId, environmentName);
		else return (HashMap<String, Object>)callXmlrpcMethod("Environment.get", productId);

	}


	/**
	 * 
	 * @param BuildName the name of the build that the ID will be returned for. 0 Will be 
	 * returned if the build can't be found
	 * @return the ID of the specified product
	 * @throws XmlRpcException 
	 */
	@SuppressWarnings("unchecked")
	public int getEnvironemntIDByName(String environmentName) throws XmlRpcException
	{
		get("Environment.check_environment",environmentName, productId.get());
		return getId();
	}


	public Integer getProductId() {
		return productId.get();
	}


	public void setProductId(Integer productId) {
		this.productId.set(productId);
	}


	public String getName() {
		return name.get();
	}


	public void setName(String name) {
		this.name.set(name);
	}


	public Boolean getIsactive() {
		return isactive.get();
	}


	public void setIsactive(Boolean isactive) {
		this.isactive.set(isactive);
	}
	


}
