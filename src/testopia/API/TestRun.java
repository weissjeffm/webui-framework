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

public class TestRun extends TestopiaObject{
	//inputed values to get a testRun
	private Integer runID;
	
	//variables used to update the testRun
	private StringAttribute notes;
	private IntegerAttribute managerID;  
	private StringAttribute summary;  
	private StringAttribute startDate;
	private StringAttribute stopDate; 
	private IntegerAttribute buildID;  
	private IntegerAttribute environmentID; 
	private IntegerAttribute newPlanID; 
	
	/**
	 * 
	 * @param userName your bugzilla/testopia userName
	 * @param password your password 
	 * @param url the url of the testopia server
	 * @param runID - Integer the runID, you may enter null here if you are creating a test run
	 */
	public TestRun(Session session, Integer runID)
	{
		this.session = session;
		this.runID = runID; 
		this.listMethod = "TestRun.list";
		
		this.notes = this.newStringAttribute(null);
		this.managerID = this.newIntegerAttribute(null);
		this.summary = this.newStringAttribute(null);
		this.startDate = this.newStringAttribute(null);
		this.stopDate = this.newStringAttribute(null);
		this.buildID = this.newIntegerAttribute(null);
		this.environmentID = this.newIntegerAttribute(null);
		this.cleanAllAttributes();
	}
	
	/**
	 * Updates all attributes of this TestRun object via XMLRPC
	 * @throws TestopiaException
	 * @throws XmlRpcException
	 */
	public void update()
	throws TestopiaException, XmlRpcException
	{
		if (runID == null) 
			throw new TestopiaException("runID is null.");
		
		//hashmap to store attributes to be updated
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//add attributes that need to be updated to the hashmap 
		 if(this.buildID.isDirty()){
			 map.put("build_id", this.buildID.get());
			 //this.buildID.clean();
		 }
		 if(this.environmentID.isDirty()){
			 map.put("environment_id", this.environmentID.get());		 
			 //this.environmentID.clean();
		 }
		 if(this.managerID.isDirty()){
			 map.put("manager_id", this.managerID.get()); 
			 //this.managerID.clean();
		 }
		 if(this.notes.isDirty()){
			 map.put("notes", this.notes.get());
			 //this.notes.clean();
		 }
		 if(this.startDate.isDirty()){
			 map.put("start_date", this.startDate.get());
			 //this.startDate.clean();
		 }
		 if(this.stopDate.isDirty()){
			 map.put("stop_date", this.stopDate.get());
			 //this.stopDate.clean();
		 }
		 if(this.summary.isDirty()){
			 map.put("summary", this.summary.get());
			 //this.summary.clean();
		 }
		 if(this.newPlanID.isDirty()){
			 map.put("plan_id", this.newPlanID.get());
			 //this.newPlanID.clean();
		 }
		 if(map.size() > 0)
			 //then update the testRunCase
			 this.callXmlrpcMethod("TestRun.update",
					 				runID,
					 				map);
		 
		 this.cleanAllAttributes();
	}
	
	/**
	 * Gets the attributes of the test run, runID must not be null
	 * @return a hashMap of all the values found. Returns null if there is an error
	 * and the TestRun cannot be returned
	 * @throws TestopiaException 
	 * @throws XmlRpcException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getAttributes()
	throws TestopiaException, XmlRpcException
	{
		if (runID == null) 
			throw new TestopiaException("runID is null.");
		
		//get the hashmap
		return (HashMap<String, Object>) this.callXmlrpcMethod("TestRun.get",
																runID.intValue());
	}
	
	/**
	 * 
	 * @param buildID
	 * @param environmentID
	 * @param managerID
	 * @param planID int - the ID of the plan the run will be added to 
	 * @param planTextVersion
	 * @param summary String - text summary of the run
	 * @return the ID of the test run
	 * @throws XmlRpcException
	 */
	public int makeTestRun(int buildID, int environmentID, int managerID, int planID,
			int planTextVersion, String summary)
	throws XmlRpcException
	{
		//set the values for the test plan
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("build_id", this.buildID.get());
		map.put("environment_id", this.environmentID.get());
		map.put("manager_id", this.managerID.get());
		map.put("plan_id", planID);
		map.put("plan_text_version", planTextVersion);
		map.put("summary", this.summary.get());
		
		//update the testRunCase
		int result = (Integer)this.callXmlrpcMethod("TestRun.create",
													map);
			
		runID = result; 	
		return result;		
	}
	
	/**
	 * 
	 * @return an array of objects (Object[]) of all the testcases found. 
	 * Returns null if there is an error and the TestRun cannot be returned
	 * @throws TestopiaException
	 * @throws XmlRpcException
	 */
	public Object[] getTestCases()
	throws TestopiaException, XmlRpcException
	{
		if (runID == null)
			throw new TestopiaException("runID is null.");
		
		return (Object[])this.callXmlrpcMethod("TestRun.get_test_cases",
												runID.intValue());
	}			
		
	/**
	 * 
	 * @return an array of objects (Object[]) of all the testCaseRuns found. 
	 * Returns null if there is an error and the TestRun cannot be found
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public Object[] getTestCaseRuns()
	throws TestopiaException, XmlRpcException
	{
		if (runID == null) 
			throw new TestopiaException("runID is null.");
			
		return (Object[])this.callXmlrpcMethod("TestRun.get_test_case_runs",
												runID.intValue());
	}
	
	/**
	 * @return the runID
	 */
	public Integer getRunID() {
		return runID;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes.get();
	}

	/**
	 * @return the managerID
	 */
	public Integer getManagerID() {
		return managerID.get();
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary.get();
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate.get();
	}

	/**
	 * @return the stopDate
	 */
	public String getStopDate() {
		return stopDate.get();
	}

	/**
	 * @return the buildID
	 */
	public Integer getBuildID() {
		return buildID.get();
	}

	/**
	 * @return the environmentID
	 */
	public Integer getEnvironmentID() {
		return environmentID.get();
	}

	/**
	 * @return the newPlanID
	 */
	public Integer getNewPlanID() {
		return newPlanID.get();
	}
	
	/**
	 * 
	 * @param buildID int - the new builID
	 */
	public void setBuildID(int buildID) {
		this.buildID.set(buildID);
	}

	/**
	 * 
	 * @param environmentID int = the new environemnetID
	 */
	public void setEnvironmentID(int environmentID) {
		this.environmentID.set(environmentID);
	}

	/**
	 * 
	 * @param managerID int - the new managerID
	 */
	public void setManagerID(int managerID) {
		this.managerID.set(managerID);
	}

	/**
	 * 
	 * @param notes String - the new notes 
	 */
	public void setNotes(String notes) {
		this.notes.set(notes);
	}

	/**
	 * 
	 * @param startDate String - the new startDate (Format: yyyy-mm-dd hh:mm:ss)
	 */
	public void setStartDate(String startDate) {
		this.startDate.set(startDate);
	}

	/**
	 * 
	 * @param stopDate String - the new stopDate (Format: yyyy-mm-dd hh:mm:ss)
	 */
	public void setStopDate(String stopDate) {
		this.stopDate.set(stopDate);
	}
	
	/**
	 * 
	 * @param summary String - the new summary 
	 */
	public void setSummary(String summary) {
		this.summary.set(summary);
	}
	
	/**
	 * 
	 * @param newPlanID int - the plan that the test run now belongs to
	 */
	public void setPlanID(Integer newPlanID)
	{
		this.newPlanID.set(newPlanID); 
	}
}
