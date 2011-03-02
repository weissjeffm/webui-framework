package com.redhat.qe.auto.testng;

public abstract class BzBugDependency {
	protected String[] bugIds = null;
	protected Type type = null;
	
	public enum Type {BlockedBy("Blocked by bugzilla bug"), 
					Verifies("Verifies bugzilla bug");
		private String desc = null;
		Type(String desc){
			this.desc = desc;
		}
		public String getDescription(){
			return desc;
		}
	};
	
	public String[] getBugIds() {
		return bugIds;
	}
		
	public Type getType() {
		return type;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(" **" + type.getDescription());
		for (String bugId: bugIds )
			sb.append(" " + bugId);
		return sb.toString();
	}

}
