package com.redhat.qe.auto.testng;


public class VerifiesBzBug extends BzBugDependency {

	public VerifiesBzBug(String bugId, Object... params) {
		super();
		this.bugId = bugId;
		this.params = params;
		this.type = Type.Verifies; 
	}
}
