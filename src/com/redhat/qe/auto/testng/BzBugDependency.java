package com.redhat.qe.auto.testng;

public abstract class BzBugDependency {
	protected String bugId = null;
	protected Object[] params = null;
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
	
	public String getBugId() {
		return bugId;
	}
	
	public Object[] getParameters() {
		return params;
	}
	
	public Type getType() {
		return type;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (Object param: params){
			if (param==null) param = "null";
			sb.append(param.toString() + "," );
		}
		sb.append(" **" + type.getDescription() + " " + bugId);
		return sb.toString();
	}

}
