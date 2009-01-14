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

public class TestCase extends TestopiaObject{
		
	//values for updates 
	//private Integer defaultTesterID = null;
	private StringAttribute priority = newStringAttribute("priority", null);
	private IntegerAttribute categoryID= newIntegerAttribute("category", null);
	private StringAttribute arguments= newStringAttribute("arguments", null);
	private StringAttribute alias= newStringAttribute("alias", null); 
	private StringAttribute requirement= newStringAttribute("requirement", null);
	private StringAttribute script= newStringAttribute("script", null); 
	private StringAttribute caseStatusID= newStringAttribute("case_status_id", null);
	private StringAttribute summary= newStringAttribute("summary", null);
	private StringAttribute action= newStringAttribute("action", null);
	private StringAttribute status= newStringAttribute("status", null);
	private BooleanAttribute isAutomated = newBooleanAttribute("isautomated", null);
	private StringAttribute plans= newStringAttribute("plans", null);
	
	/** 
	 * @param userName your bugzilla/testopia userName
	 * @param password your password 
	 * @param url the url of the testopia server
	 * @param caseID - Integer the caseID, you may enter null here if you are creating a test case
	 */
	public TestCase(Session session, Integer caseID)
	{
		this.id = newIntegerAttribute("case_id", caseID);
		this.session = session;
		this.listMethod = "TestCase.list";

	}
	
	public TestCase(Session session, String caseAlias) throws XmlRpcException
	{
		this.session = session;
		this.listMethod = "TestCase.list";
		this.id = newIntegerAttribute("case_id", null);

		get("TestCase.get", caseAlias);
		
	}
	
	public TestCase(Session session, String status, Integer categoryId, String priority, String summary, Integer plan){
		this.session = session;
		this.status.set(status);
		this.listMethod = "TestCase.list";
		this.categoryID.set(categoryId);
		this.priority.set(priority);
		this.summary.set(summary);
		this.plans.set(Integer.toString(plan));
		this.id = newIntegerAttribute("case_id", null);

	}
	
	public TestCase(Session session, String status, String category, String priority, String summary, String plan, String product) throws XmlRpcException{
		this.session = session;
		this.listMethod = "TestCase.list";
		this.status.set(status);
		this.id = newIntegerAttribute("case_id", null);

		this.categoryID.set(new Product(session).getCategoryIDByName(category, product));

		this.priority.set(priority);
		this.summary.set(summary);
		this.plans.set(Integer.toString(new TestPlan(session,plan).getId()));
	}

	public TestCase(Session session, String status, String category, String priority, String summary, String plan, String product, String version) throws XmlRpcException{
		this.session = session;
		this.listMethod = "TestCase.list";
		this.status.set(status);
		this.id = newIntegerAttribute("case_id", null);

		Product prod = new Product(session, product);
		this.categoryID.set(prod.getCategoryIDByName(category, product));

		this.priority.set(priority);
		this.summary.set(summary);
		this.plans.set(Integer.toString(new TestPlan(session, prod.getId(), plan, version).getId()));
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
	public void setIsAutomated(Boolean isAutomated) {
		this.isAutomated.set(isAutomated);
	}
	
	/**
	 * 
	 * @param priorityID - int the new priorityID
	 */
	public void setPriorityID(String priorityID) {
		this.priority.set(priorityID);
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
		if(id.get() == null)
			throw new TestopiaException("CaseID cannot be null");
		
		//add the component to the test case
		this.callXmlrpcMethod("TestCase.add_component",
							  id.get(),
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
		if(id.get() == null)
			throw new TestopiaException("CaseID cannot be null");
		
		//add the component to the test case
		this.callXmlrpcMethod("TestCase.remove_component",
							  id.get(),
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
		if(id.get() == null)
			throw new TestopiaException("CaseID cannot be null");

		return (Object[]) this.callXmlrpcMethod("TestCase.get_components", 
												id.get());	
	}
	
	/**
	 * Adds a testplan to the testCase
	 * @param componentID the ID of the component that will be added to the
	 * testCase
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public void addTestPlan(int testPlanID)
	throws TestopiaException, XmlRpcException
	{
		if(id.get() == null)
			throw new TestopiaException("CaseID cannot be null");

		this.callXmlrpcMethod("TestCase.link_plan", 
							  id.get(),
							  testPlanID);	
	}
	
	/**
	 * Removes an associated testplan applied to the testCase
	 * @param componentID the ID of the component that will be removed from the
	 * testCase
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public void removeTestPlan(int testPlanID)
	throws TestopiaException, XmlRpcException
	{
		if(id.get() == null)
			throw new TestopiaException("CaseID cannot be null");

		this.callXmlrpcMethod("TestCase.unlink_plan", 
							  id.get(),
							  testPlanID);	
	}
	/**
	 * Gets the test plans as an array of hashMaps or null if 
	 * an error occurs
	 * @return an array of test plan hashMaps or null 
	 * @throws Exception
	 */
	public Object[] getTestPlans()
	throws TestopiaException, XmlRpcException
	{
		if(id.get() == null)
			throw new TestopiaException("CaseID cannot be null");

		return (Object[]) this.callXmlrpcMethod("TestCase.get_plans", 
												id.get());	
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
		if (id.get() == null) 
			throw new TestopiaException("caseID is null.");
		//update the testRunCase
		return super.update("TestCase.update");
	}
	
	public Integer storeText() throws TestopiaException, XmlRpcException
	{
		if (id.get() == null) 
			throw new TestopiaException("caseID is null.");
		//update the testRunCase
		return (Integer)super.callXmlrpcMethod("TestCase.store_text", getId(), getAction(), "", "", "");
	}
	
	/**
	 * Calls the create method with the attributes as-is (as set via contructors
	 * or setters).  
	 * @return a map of the newly created object
	 * @throws XmlRpcException
	 */
	public Map<String,Object> create() throws XmlRpcException{
		Map map = super.create("TestCase.create");		
		return map;
	}
	
	
	/**
	 * Gets the attributes of the test case, caseID must not be null
	 * @return a hashMap of all the values found. Returns null if there is an error
	 * and the TestCase cannot be returned
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public Map<String, Object> getAttributes() throws TestopiaException, XmlRpcException
	{
		if (id.get() == null)
			throw new TestopiaException("caseID is null.");
		
		//get the hashmap
		return get("TestCase.get", id.get());	
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
	public Boolean getIsAutomated() {
		return isAutomated.get();
	}
	public String getPriorityID() {
		return priority.get();
	}
	public Integer getCategoryID() {
		return categoryID.get();
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
	public String getAction() {
		return action.get();
	}
	public void setAction(String action) {
		this.action.set(action);
	}

	public String getPlan() {
		return plans.get();
	}

	public void setPlan(Integer plans) {
		this.plans.set(Integer.toString(plans));
	}
}
