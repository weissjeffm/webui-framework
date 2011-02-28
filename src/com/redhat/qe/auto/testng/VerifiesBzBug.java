package com.redhat.qe.auto.testng;


public class VerifiesBzBug extends BzBugDependency {

	public VerifiesBzBug(String bugId, Object... params) {
		this(new String[] {bugId}, params);
	}

	
	public VerifiesBzBug(String[] bugIds, Object... params) {
		super();
		this.bugIds = bugIds;
		this.params = params;
		this.type = Type.Verifies; 
	}
}
