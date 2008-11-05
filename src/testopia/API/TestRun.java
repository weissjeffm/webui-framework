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
	private Session session;
	
	//variables used to update the testRun
	private String notes = null;
	private Integer managerID = null;  
	private String summary = null;  
	private String startDate = null;
	private String stopDate = null; 
	private Integer buildID = null;  
	private Integer environmentID = null; 
	private Integer newPlanID = null; 
	
	/**
	 * 
	 * @param buildID int - the new builID
	 */
	public void setBuildID(int buildID) {
		this.buildID = buildID;
	}

	/**
	 * 
	 * @param environmentID int = the new environemnetID
	 */
	public void setEnvironmentID(int environmentID) {
		this.environmentID = environmentID;
	}

	/**
	 * 
	 * @param managerID int - the new managerID
	 */
	public void setManagerID(int managerID) {
		this.managerID = managerID;
	}

	/**
	 * 
	 * @param notes String - the new notes 
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * 
	 * @param startDate String - the new startDate (Format: yyyy-mm-dd hh:mm:ss)
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * 
	 * @param stopDate String - the new stopDate (Format: yyyy-mm-dd hh:mm:ss)
	 */
	public void setStopDate(String stopDate) {
		this.stopDate = stopDate;
	}
	
	/**
	 * 
	 * @param summary String - the new summary 
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	/**
	 * 
	 * @param newPlanID int - the plan that the test run now belongs to
	 */
	public void setPlanID(Integer newPlanID)
	{
		this.newPlanID = newPlanID; 
	}
	
	public void update() throws Exception
	{
		if (runID == null) 
		{
			throw new Exception("runID is null.");
		}
		
		//hashmap to store attributes to be updated
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//add attributes that need to be updated to the hashmap 
		 if(buildID != null)
			 map.put("build_id", buildID);
		 
		 if(environmentID != null)
			 map.put("environment_id", environmentID);		 
		 
		 if (managerID != null)
			 map.put("manager_id", managerID); 
		
		 if(notes != null)
			 map.put("notes", notes); 
		 
		 if(startDate != null)
			 map.put("start_date", startDate);
		 
		 if(stopDate != null)
			 map.put("stop_date", stopDate);
		 
		 if(summary != null)
			 map.put("summary", summary);
		 
		 if(newPlanID != null)
			 map.put("plan_id", newPlanID);
			
		 //update the testRunCase
		 this.callXmlrpcMethod("TestRun.update",
				 				runID,
								map);
			
			
		 notes = null;
		 managerID = null;  
		 summary = null;  
		 startDate = null;
		 stopDate = null; 
		 buildID = null;  
		 environmentID = null; 
		 newPlanID = null;
	}
	
	/**
	 * Gets the attributes of the test run, runID must not be null
	 * @return a hashMap of all the values found. Returns null if there is an error
	 * and the TestRun cannot be returned
	 * @throws Exception 
	 * @throws XmlRpcException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getAttributes() throws Exception, XmlRpcException
	{
		if (runID == null) 
			throw new Exception("runID is null.");
		
		//get the hashmap
		return (HashMap<String, Object>) this.callXmlrpcMethod("TestRun.get",
																runID.intValue());
	}

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
		map.put("build_id", buildID);
		map.put("environment_id", environmentID);
		map.put("manager_id", managerID);
		map.put("plan_id", planID);
		map.put("plan_text_version", planTextVersion);
		map.put("summary", summary);
		
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
	 * @throws Exception
	 * @throws XmlRpcException
	 */
	public Object[] getTestCases() throws Exception, XmlRpcException
	{
		if (runID == null)
			throw new Exception("runID is null.");
			//get the hashmap
		return (Object[])this.callXmlrpcMethod("TestRun.get_test_cases",
												runID.intValue());
	}			
		
	/**
	 * 
	 * @return an array of objects (Object[]) of all the testCaseRuns found. 
	 * Returns null if there is an error and the TestRun cannot be found
	 * @throws Exception
	 */
	public Object[] getTestCaseRuns() throws Exception
	{
		if (runID == null) 
			throw new Exception("runID is null.");
			
		return (Object[])this.callXmlrpcMethod("TestRun.get_test_case_runs",
												runID.intValue());		
			
	}	
}
