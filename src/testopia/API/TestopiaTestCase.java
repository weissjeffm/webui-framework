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

public class TestopiaTestCase extends TestopiaObject{
	
	//inputed values to get a testCase
	private Integer caseID; 
	
	//values for updates 
	//private Integer defaultTesterID = null;
	private IntegerAttribute isAutomated = new IntegerAttribute(null);
	private IntegerAttribute priorityID= new IntegerAttribute(null);
	private IntegerAttribute categoryID= new IntegerAttribute(null);
	private IntegerAttribute canview= new IntegerAttribute(null); 
	private StringAttribute arguments= new StringAttribute(null);
	private StringAttribute alias= new StringAttribute(null); 
	private StringAttribute requirement= new StringAttribute(null);
	private StringAttribute script= new StringAttribute(null); 
	private StringAttribute caseStatusID= new StringAttribute(null);
	private StringAttribute summary= new StringAttribute(null);
	
	/** 
	 * @param userName your bugzilla/testopia userName
	 * @param password your password 
	 * @param url the url of the testopia server
	 * @param caseID - Integer the caseID, you may enter null here if you are creating a test case
	 */
	public TestopiaTestCase(Session session, Integer caseID)
	{
		this.caseID = caseID; 
		this.session = session;
		this.listMethod = "TestCase.list";
	}
	/**
	 * 
	 * @param alias String - the new Alias
	 */	
	public void setAlias(String alias) {
		this.alias.set(alias);
	}

	/**
	 * 
	 * @param arguments String - the new arguments
 	 */
	public void setArguments(String arguments) {
		this.arguments.set(arguments);
	}

	/**
	 * 
	 * @param canview 
	 */
	public void setCanview(boolean canview) {
		this.canview.set(canview? 1: 0);
	}
   
	/**
	 * 
	 * @param caseStatusID String - the new case Status ID
	 */
	public void setCaseStatusID(String caseStatusID) {
		this.caseStatusID.set(caseStatusID);
	}

	/**
	 * 
	 * @param categoryID int - the new categorID
	 */
	public void setCategoryID(int categoryID) {
		this.categoryID.set(categoryID);
	}

	/**
	 * 
	 * @param defaultTesterID int - the new defaultTesterID
	 */
	/*public void setDefaultTesterID(int defaultTesterID) {
		this.defaultTesterID.set(defaultTesterID);
	}*/

	/**
	 * 
	 * @param isAutomated boolean - true if it's to be set automated, 
	 * false otherwise
	 */
	public void setIsAutomated(boolean isAutomated) {
		this.isAutomated.set(isAutomated? 1:0);
	}
	
	/**
	 * 
	 * @param priorityID - int the new priorityID
	 */
	public void setPriorityID(int priorityID) {
		this.priorityID.set(priorityID);
	}
	
	/**
	 * 
	 * @param requirement String - the new requirement 
	 */
	public void setRequirement(String requirement) {
		this.requirement.set(requirement);
	}
	
	/**
	 * 
	 * @param script String - the new script
	 */
	public void setScript(String script) {
		this.script.set(script);
	}

	/**
	 * 
	 * @param summary String - the new summary
	 */
	public void setSummary(String summary) {
		this.summary.set(summary);
	}
	
	/**
	 * Adds a component to the testCase
	 * @param componentID the ID of the component that will be added to the
	 * testCase
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public void addComponent(int componentID)
	throws TestopiaException, XmlRpcException
	{
		if(caseID == null)
			throw new TestopiaException("CaseID cannot be null");
		
		//add the component to the test case
		this.callXmlrpcMethod("TestCase.add_component",
							  caseID,
							  componentID);
	}
	
	/**
	 * Removes a component to the testCase
	 * @param componentID the ID of the component that will be removed from the
	 * testCase
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public void removeComponent(int componentID)
	throws TestopiaException, XmlRpcException
	{
		if(caseID == null)
			throw new TestopiaException("CaseID cannot be null");
		
		//add the component to the test case
		this.callXmlrpcMethod("TestCase.remove_component",
							  caseID,
							  componentID);
	}
	
	/**
	 * Gets the components as an array of hashMaps or null if 
	 * an error occurs
	 * @return an array of component hashMaps or null 
	 * @throws Exception
	 */
	public Object[] getComponents()
	throws TestopiaException, XmlRpcException
	{
		if(caseID == null)
			throw new TestopiaException("CaseID cannot be null");

		return (Object[]) this.callXmlrpcMethod("TestCase.get_components", 
												caseID);	
	}

		
	/**
	 * Updates are not called when the .set is used. You must call update after all your sets
	 * to push the changes over to testopia.
	 * @throws Exception if planID is null 
	 * @throws XmlRpcException
	 * (you made the TestCase with a null caseID and have not created a new test plan)
	 */
	public void update()
	throws TestopiaException, XmlRpcException
	{
		if (caseID == null) 
			throw new TestopiaException("caseID is null.");
		
		//hashmap to store attributes to be updated
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//add attributes that need to be updated to the hashmap 
		if(isAutomated != null)
			map.put("isautomated", isAutomated.get().intValue());
		
		if(priorityID != null)
			map.put("priority_id", priorityID.get().intValue());
		
		if(canview != null)
			map.put("canview", canview.get().intValue());
		
		if(categoryID != null)
			map.put("category_id", categoryID.get());
		
		if(arguments != null)
			map.put("arguments", arguments.get());
		
		if(alias != null)
			map.put("alias", alias.get());
		
		if(requirement != null)
			map.put("requirement", requirement.get());
		
		if(script != null)
			map.put("script", script.get());
		
		if(caseStatusID != null)
			map.put("case_status_id", caseStatusID.get());
		
		if(summary != null)
			map.put("summary", summary.get()); 
		
		//update the testRunCase
		this.callXmlrpcMethod("TestCase.update",
							  caseID,
							  map);
		//make sure multiple updates aren't called 
		isAutomated.clean();
		priorityID.clean();
		categoryID.clean();
		canview.clean();	
		arguments.clean();
		alias.clean();
		requirement.clean();	
		script.clean(); 
		caseStatusID.clean();
		summary.clean();
	}
	
	/**
	 * 
	 * @param authorID the bugzilla/testopia ID of the author 
	 * @param caseStatusID 
	 * @param categoryID
	 * @param isAutomated
	 * @param planID the ID of the plan the testCase will be added to 
	 * @param summary string - the summary of the testCase. Null allowed
	 * @param priorityID Integer - the priority of the testCase (0-5). Null allowed
	 * @return
	 * @throws XmlRpcException
	 */
	public int makeTestCase(int authorID, int caseStatusID, int categoryID,
			boolean isAutomated, int planID, String summary,Integer priorityID)
	throws XmlRpcException
	{
				
		int isAutomatedInt; 
		
		//convert to integer of 1 if isAutomated is true (1 == true)
		if(isAutomated)
			isAutomatedInt = 1; 
		
		//else convert to 0 for false (0 == false)
		else 
			isAutomatedInt = 0; 

		//set the values for the test case
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("author_id", authorID);
		map.put("case_status_id", caseStatusID);
		map.put("category_id", categoryID);
		map.put("isautomated", isAutomatedInt);
		map.put("plan_id", planID);
		
		//add the optional values if they are not null
		if(summary != null)
			map.put("summary", summary);
		
		if(priorityID != null)
			map.put("priority_id", priorityID.intValue());
		
		//update the test case
		Map result = (Map)this.callXmlrpcMethod("TestCase.create",
													map);
			
		caseID = (Integer)result.get("case_id"); 
		//System.out.println(result);	
		return caseID;
	}
	
	public int makeTestCase(String author, String caseStatus, String category, 
			String product, String plan, String summary,String priority) throws XmlRpcException{
		User user = new User(session, author);
		
		int user_id = user.getAttributes();

		
		TestPlan tp = new TestPlan(session, null);
		
		Object[] results = tp.getList("name", plan);
		//for (Object result: results) System.out.println("Found test plan:" + result.toString());
		int tp_id = (Integer)((Map)results[0]).get("plan_id");
		System.out.println("Found acceptance testplan id: " + tp_id);
		
		Product prod = new Product(session);
		int cat_id = prod.getCategoryIDByName(category, product);
		System.out.println("Found category id: " + cat_id);
		
		TestopiaTestCase tc2 = new TestopiaTestCase(session,null);
		
		int pri_id = tc2.getPriorityIdByName(priority);
		int stat_id = tc2.getStatusIdByName(caseStatus);
		return makeTestCase(user_id, stat_id, cat_id, true, tp_id, summary, pri_id);
}
	
	
	/**
	 * Gets the attributes of the test case, caseID must not be null
	 * @return a hashMap of all the values found. Returns null if there is an error
	 * and the TestCase cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getAttributes()
	throws TestopiaException, XmlRpcException
	{
		if (caseID == null)
			throw new TestopiaException("caseID is null.");
		
		//get the hashmap
		return (HashMap<String, Object>) this.callXmlrpcMethod("TestCase.get",
																caseID.intValue());	
	}
	
	@Deprecated
	public int getCategoryIdByName(String categoryName)
	throws XmlRpcException
	{
		//get the result
		return (Integer) this.callXmlrpcMethod("TestCase.lookup_category_id_by_name",
												  categoryName);	
	}
	
	public int getPriorityIdByName(String categoryName) throws XmlRpcException
	{
		//get the result
		return (Integer) this.callXmlrpcMethod("TestCase.lookup_priority_id_by_name",
												  categoryName);	
	}
	
	public int getStatusIdByName(String categoryName) throws XmlRpcException
	{
		//get the result
		return (Integer) this.callXmlrpcMethod("TestCase.lookup_status_id_by_name",
												  categoryName);	
	}
	
	 /**
	  * Uses Deprecated API -Use Product class for this
	  * @param categoryName the name of the category that the ID will be returned for. This will search within the
	  * test plans that this test case belongs to and return the first category with a matching name. 0 Will be 
	  * returned if the category can't be found
	  * @return the ID of the specified product
	  * @throws XmlRpcException
	  */
	@Deprecated
	public int getBuildIDByName(String categoryName)
	throws XmlRpcException
	 {
		//get the result
		return (Integer)this.callXmlrpcMethod("TestCase.lookup_category_id_by_name",
											  categoryName);
	 }
	public Integer getIsAutomated() {
		return isAutomated.get();
	}
	public Integer getPriorityID() {
		return priorityID.get();
	}
	public Integer getCategoryID() {
		return categoryID.get();
	}
	public Integer getCanview() {
		return canview.get();
	}
	public String getArguments() {
		return arguments.get();
	}
	public String getAlias() {
		return alias.get();
	}
	public String getRequirement() {
		return requirement.get();
	}
	public String getScript() {
		return script.get();
	}
	public String getCaseStatusID() {
		return caseStatusID.get();
	}
	public String getSummary() {
		return summary.get();
	}
}
