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

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Allows the user to get a buildID from it's name, or it's name from the buildID. 
 * It can also create and update a build
 * @author anelson
 * @author weissj
 *
 */
public class Build extends TestopiaObject{
	 private IntegerAttribute productId = newIntegerAttribute("product", null);
	
	 /**
	  * 
	  * @param userName - your testopia/bugzilla username
	  * @param password - the password for your account 
	  * @param login - the user you want attributes returned for
	  * @param url - the url of the testopia server
	  */
	 public Build(Session session, Integer productId)
	 {
		 this.session = session;
		 this.productId.set(productId);
	 }
	 
	 
	 /**
	  * Creates a new build and returns the buildID, 0 is returned if an error occurs
	  * @param name
	  * @param productID
	 * @throws XmlRpcException 
	  */
	 public int makeBuild(String name, int productID, Boolean isactive, String milestone) throws XmlRpcException
	 { 
		 //Check if the build already exists. Will return a 0 if the build does not exist
		 int buildTest = getBuildIDByName(name);
		 
		 if(buildTest == 0){
			 //Build does not exist so we need to create a new build
		 
			 HashMap<String, Object> map = new HashMap<String, Object>();
			 map.put("name", name);
			 map.put("product_id", productID);
			 map.put("milestone", milestone);
			 
			 //1 for true, 0 for false
			 if(isactive)
				 map.put("isactive", 1);
			 else
				 map.put("isactive", 0);
			 
			 //get the result
			 return (Integer) this.callXmlrpcMethod("Build.create", map);
		 }
		 else{
			 //Build already exists
			 log.info("Build "+name+" already exists will not create build");
			 //Make sure we don't forget to set the buildID
			 return buildTest;
		 }
	 }
	 
	 /**
	  * Updates builds on testopia with the specified parameters
	  * @param name string - the name of the build. Can be null
	  * @param milestone string - the milestone. Can be null
	  * @param isactive Boolean - if the build is active. Can be null
 	  * @param description String - description of the build. Can be null
	  * @param buildID int - the buildID
	 * @throws XmlRpcException 
	  */
	 public void updateBuild(String name, String milestone, Boolean isactive, 
			 String description, int buildID) throws XmlRpcException
	 {
		 //put values into map if they are not null 
		 HashMap<String, Object> map = new HashMap<String, Object>();
		 if(name != null)
			 map.put("name", name);
		 if(milestone != null)
			 map.put("milestone", milestone);
		 if(isactive != null)
		 {
			 //put 1 into map if true
			 if(isactive)
				 map.put("isactive", 1);
		 	//else put false
			 else 
				 map.put("isactive", 0);
		 }
		 
		 if(description != null)
			 map.put("description", description);
		 
		 this.callXmlrpcMethod("Build.update", buildID, map);
	 }
	 
	 /**
	  * 
	  * @param BuildName the name of the build that the ID will be returned for. 0 Will be 
	  * returned if the build can't be found
	  * @return the ID of the specified product
	 * @throws XmlRpcException 
	  */
	@SuppressWarnings("unchecked")
	public int getBuildIDByName(String buildName) throws XmlRpcException
	 {
		Object[] params = new Object[]{buildName, productId.get()};
		HashMap<String, Object> ret = (HashMap<String, Object>)
										this.callXmlrpcMethod("Build.check_build",
															  params);
		return (Integer)ret.get("build_id");
	 }
	 
	/**
	 * 
	 * @param id the ID of the build name that will be returned. Null is returned 
	 * if the product can't be found
	 * @return the product name that corresponds the specified product ID
	 * @throws XmlRpcException 
	 */
	 public String getBuildNameByID(int id) throws XmlRpcException
	 {
		//get the result
		return (String)this.callXmlrpcMethod("Build.lookup_id_by_name", id);
	 }
}
