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
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Creates a test plan object, that allows the user to create, change and get test plan values
 * @author anelson
 *
 */
public class TestPlan extends TestopiaObject{
	
	protected static final String LIST_METHOD = "TestPlan.filter";


	private static final String PLAN_ID = "plan";


	//inputed values to update a testPlan 
	private IntegerAttribute productId = newIntegerAttribute("product", null);  	
	private IntegerAttribute type = newIntegerAttribute("type", null);  	
	private BooleanAttribute isactive = newBooleanAttribute("isactive", null);  		 
	private StringAttribute name = newStringAttribute("name", null);  	
	private StringAttribute defaultProductVersion = newStringAttribute("default_product_version", null);  	
	private StringAttribute version = newStringAttribute("version", null);  	

	
	/**
	 * 
	 * @param userName your bugzilla/testopia userName
	 * @param password your password 
	 * @param url the url of the testopia server
	 * @param planID - Integer the planID, you may enter null here if you are creating a test plan
	 */
	public TestPlan(Session session, Integer planID)
	{
		this.session = session;
		this.id = newIntegerAttribute(PLAN_ID, planID);

		this.listMethod = LIST_METHOD;
	}
	
	public TestPlan(Session session, String plan) throws XmlRpcException
	{
		this.session = session;
		this.listMethod = LIST_METHOD;
		this.id = newIntegerAttribute(PLAN_ID, null);

		getPlanIDByName(plan);

	}
	
	public TestPlan(Session session, Integer product, String plan, String version) throws XmlRpcException
	{
		this.session = session;
		this.listMethod = LIST_METHOD;
		this.id = newIntegerAttribute(PLAN_ID, null);
		this.productId.set(product);
		this.defaultProductVersion.set(version);
		this.name.set(plan);
		getPlanIdByCurrentAttributes();

	}
	
	public int getPlanIdByCurrentAttributes() throws XmlRpcException{
		Object[] results = this.getList(getAttributesMap());
		if (results.length > 1) throw new RuntimeException("Multiple matches on testplan name='" + name + "'.  Please use a more specific query.");
		if (results.length == 0) throw new RuntimeException("No matches on testplan with attributes " + getAttributesMap());
		//for (Object result: results) log.info("Found test plan:" + result.toString());
		syncAttributes((Map)results[0]);
		return getId();
	}
	

	
	public int getPlanIDByName(String name) throws XmlRpcException{
		Object[] results = this.getList("name", name);
		if (results.length > 1) throw new RuntimeException("Multiple matches on testplan name='" + name + "'.  Please use a more specific query.");
		
		//for (Object result: results) log.info("Found test plan:" + result.toString());
		syncAttributes((Map)results[0]);
		return getId();
	}
	

	

	
	/**
	 * 
	 * @param defaultProductVersion String - the default product version the test plan will be changed to
	 */
	public void setDefaultProductVersion(String defaultProductVersion)
	{
		this.defaultProductVersion.set(defaultProductVersion); 
	}
	
	public String getDefaultProductVersion(){
		return this.defaultProductVersion.get();
	}

	/**
	 * 
	 * @param isActive boolean - change if the test plan is active or not
	 */
	public void setIsActive(boolean isActive)
	{
		isactive.set(isActive);
		
	}
	
	/**
	 * 
	 * @param name String - the new name of the test plan 
	 */
	public void setName(String name)
	{
		this.name.set(name); 		
	}
	
	/**
	 * 
	 * @param productID int - the new product ID of the test plan 
	 */
	public void setProductID(int productID)
	{
		this.productId.set(productID); 
	}
	
	/**
	 * 
	 * @param typeID int - the new type of the test plan
	 */
	public void setTypeID(int typeID)
	{
		this.type.set(typeID); 
	}
	
	
	/**
	 * 
	 * @return a hashMap of all the values found. Returns null if there is an error
	 * and the TestPlan cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCategories()
	throws TestopiaException, XmlRpcException
	{
		if (id.get() == null) 
			throw new TestopiaException("planID is null.");
		
		//get the hashmap
		return (HashMap<String, Object>)this.callXmlrpcMethod("TestPlan.get",
															  id.get());
	}
	
	/**
	 * 
	 * @return an array of objects (Object[]) of all the values found for the builds. 
	 * Returns null if there is an error and the TestPlan cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public Object[] getBuilds()
	throws TestopiaException, XmlRpcException
	{
		if (id.get() == null) 
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_builds",
												id.get());
	}
	
	/**
	 * 
	 * @return an array of objects (Object[]) of all the components found. 
	 * Returns null if there is an error and the TestPlan cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public Object[] getComponents()
	throws TestopiaException, XmlRpcException
	{
		if (id.get() == null) 
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_components",
												id.get());
	}
	
	/**
	 * 
	 * @return an array of objects (Object[]) of all the testcases found. 
	 * Returns null if there is an error and the TestPlan cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public Object[] getTestCases()
	throws TestopiaException, XmlRpcException
	{
		if (id.get() == null) 
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_test_cases",
												id.get());
	}
	
	/**
	 * 
	 * @return an array of objects (Object[]) of all the test runs found. 
	 * Returns null if there is an error and the TestPlan cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public Object[] getTestRuns()
	throws TestopiaException, XmlRpcException
	{
		if (id.get() == null)
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_test_runs",
												id.get());
	}

}
