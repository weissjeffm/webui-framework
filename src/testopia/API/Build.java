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
import java.util.Map;

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
	private StringAttribute name = newStringAttribute("name", null);
	private StringAttribute milestone = newStringAttribute("milestone", null);
	private StringAttribute description = newStringAttribute("description", null);
	private BooleanAttribute isactive = newBooleanAttribute("isactive", null);

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
		this.id = newIntegerAttribute("build_id", null);
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
		if (productId == null) 
			throw new TestopiaException("productId is null.");
		//update the testRunCase
		return super.update("Build.update", id);
	}

	/**
	 * Calls the create method with the attributes as-is (as set via contructors
	 * or setters).  
	 * @return a map of the newly created object
	 * @throws XmlRpcException
	 */
	public Map<String,Object> create() throws XmlRpcException{
		Map map = super.create("Build.create");		
		return map;
	}

	public String getName() {
		return name.get();
	}


	public void setName(String name) {
		this.name.set(name);
	}


	public String getMilestone() {
		return milestone.get();
	}


	public void setMilestone(String milestone) {
		this.milestone.set(milestone);
	}


	public String getDescription() {
		return description.get();
	}


	public void setDescription(String description) {
		this.description.set(description);
	}


	public Boolean getIsactive() {
		return isactive.get();
	}


	public void setIsactive(Boolean isactive) {
		this.isactive.set(isactive);
	}


	public Integer getProductId() {
		return productId.get();
	}
	
	/**
	 * 
	 * @param BuildName the name of the build that the ID will be returned for. 0 Will be 
	 * returned if the build can't be found
	 * @return the ID of the specified product
	 * @throws XmlRpcException 
	 */
	public int getBuildIDByName(String buildName) throws XmlRpcException
	{
		get("Build.check_build", buildName, productId.get());
		return getId();
	}

	/**
	 * 
	 * @param id the ID of the build name that will be returned. Null is returned 
	 * if the product can't be found
	 * @return the product name that corresponds the specified product ID
	 * @throws XmlRpcException 
	 */
	@SuppressWarnings("unchecked")
	public String getBuildNameByID(int id) throws XmlRpcException
	{
		get("Build.get", id);
		return getName();
	}
}
