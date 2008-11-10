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
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Creates a test plan object, that allows the user to create, change and get test plan values
 * @author anelson
 *
 */
public class TestPlan extends TestopiaObject{
	
	private static final String PLAN_ID = "plan_id";


	//inputed values to update a testPlan 
	private int authorID; 	
	private String defaultProductVersion;  	
	private String creation_date;
	private int isactive; 	
	private String name; 		
	private int productID;  	
	private int typeID;  	
	 
	//booleans used to trigger if a value has been set
	private boolean isSetAuthorID = false; 	
	private boolean isSetDefaultProductVersion = false;  	
	private boolean isSetcreation_date = false;
	private boolean isSetIsactive = false; 	
	private boolean isSetName = false; 		
	private boolean isSetProductID = false;  	
	private boolean isSetTypeID = false;
	
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
		this.id = planID;
		this.listMethod = "TestPlan.list";
	}
	
	public TestPlan(Session session, String plan) throws XmlRpcException
	{
		this.session = session;
		this.listMethod = "TestPlan.list";
		this.id = getPlanIDByName(plan);
	}
	
	/**
	 * 
	 * @param authorID the bugzilla/testopia ID of the author 
	 * @param productID the bugzilla/testopia ID of the product 
	 * @param defaultProductVersion 
	 * @param typeID
	 * @param name the name of the test plan
	 * @return the ID of the test plan
	 * @throws XmlRpcException 
	 */
	public int makeTestPlan(String authorID, String productID, String defaultProductVersion,
			String typeID, String name)
	throws XmlRpcException
	{	
		//set the values for the test plan
		HashMap<String, Object> map = new HashMap();
		map.put("author_id", authorID);
		map.put("product_id", productID);
		map.put("default_product_version", defaultProductVersion);
		map.put("type_id", typeID);
		map.put("name", name);
		
		//update the testRunCase
		int result = (Integer)this.callXmlrpcMethod("TestPlan.create",
													map);
			
		id = result; 
			
		return result;
			
	}
	
	public int getPlanIDByName(String name) throws XmlRpcException{
		Object[] results = this.getList("name", name);
		//for (Object result: results) log.info("Found test plan:" + result.toString());
		return (Integer)((Map)results[0]).get(PLAN_ID);
	}
	
	/**
	 * Updates are not called when the .set is used. You must call update after all your sets
	 * to push the changes over to testopia.
	 * @throws Exception if planID is null 
	 * (you made the TestPlan with a null planID and have not created a new test plan)
	 */
	public void update()
	throws TestopiaException, XmlRpcException
	{
		if (id == null) 
			throw new TestopiaException("planID is null.");
		
		//hashmap to store attributes to be updated
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//add attributes that need to be updated to the hashmap 
		 if(isSetAuthorID == true)
			 map.put("author_id", authorID);
		 
		 if(isSetDefaultProductVersion == true)
			 map.put("default_product_version", defaultProductVersion);
		 
		 if (isSetcreation_date == true)
			 map.put("creation_date", creation_date); 
		
		 if(isSetIsactive == true)
			 map.put("isactive", isactive); 
		 
		 if(isSetName == true)
			 map.put("name", name);
		 
		 if(isSetProductID == true)
			 map.put("product_id", productID);
		 
		 if(isSetTypeID == true)
			 map.put("type_id", typeID);
		
		//update the testRunCase
		this.callXmlrpcMethod("TestPlan.update",
							  id,
							  map);
		
		//make sure multiple updates aren't called, for one set
		isSetAuthorID = false;  	
		isSetDefaultProductVersion = false;	
		isSetcreation_date = false; 
		isSetIsactive = false;  	
		isSetName = false;  		
		isSetProductID = false;   	
		isSetTypeID = false;
	}
	
	/**
	 * 
	 * @param authorID int - the bugzilla authorID that the TestPlan will be changed to
	 */
	public void setAuthorID(int authorID)
	{
		this.isSetAuthorID = true;
		this.authorID = authorID; 
	}
	
	/**
	 * 
	 * @param defaultProductVersion String - the default product version the test plan will be changed to
	 */
	public void setDefaultProductVersion(String defaultProductVersion)
	{
		this.isSetDefaultProductVersion = true;
		this.defaultProductVersion = defaultProductVersion; 
	}
	
	/**
	 * 
	 * @param creationDate String - the creation date the test plan will be changed to (Format: yyyy-mm-dd hh:mm:ss)
	 */
	public void setCreationDate(String creationDate)
	{
		this.isSetcreation_date = true; 
		this.creation_date = creationDate; 
	}
	
	/**
	 * 
	 * @param isActive boolean - change if the test plan is active or not
	 */
	public void setIsActive(boolean isActive)
	{
		this.isSetIsactive = true; 
		
		//convert to integer of 1 if isActive is true (1 == true)
		if(isActive)
			this.isactive = 1; 
		
		//else convert to 0 for false (0 == false)
		else 
			this.isactive = 0; 
		
	}
	
	/**
	 * 
	 * @param name String - the new name of the test plan 
	 */
	public void setName(String name)
	{
		this.isSetName = true;
		this.name = name; 		
	}
	
	/**
	 * 
	 * @param productID int - the new product ID of the test plan 
	 */
	public void setProductID(int productID)
	{
		this.isSetProductID = true; 
		this.productID = productID; 
	}
	
	/**
	 * 
	 * @param typeID int - the new type of the test plan
	 */
	public void setTypeID(int typeID)
	{
		this.isSetTypeID = true; 
		this.typeID = typeID; 
	}
	
	/**
	 * Gets the attributes of the test plan, planID must not be null
	 * @return a hashMap of all the values found. Returns null if there is an error
	 * and the TestPlan cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getAttributes()
	throws TestopiaException, XmlRpcException
	{
		if (id == null) 
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (HashMap<String, Object>) this.callXmlrpcMethod("TestPlan.get",
											   				   id.intValue());
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
		if (id == null) 
			throw new TestopiaException("planID is null.");
		
		//get the hashmap
		return (HashMap<String, Object>)this.callXmlrpcMethod("TestPlan.get",
															  id.intValue());
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
		if (id == null) 
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_builds",
												id.intValue());
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
		if (id == null) 
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_components",
												id.intValue());
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
		if (id == null) 
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_test_cases",
												id.intValue());
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
		if (id == null)
			throw new TestopiaException("planID is null.");
			
		//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestPlan.get_test_runs",
												id.intValue());
	}

}
