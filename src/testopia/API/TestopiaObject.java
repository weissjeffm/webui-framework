package testopia.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

public abstract class TestopiaObject {

	protected Session session;
	protected String listMethod;
	protected List<Attribute> attributes = new ArrayList<Attribute>();
	
	protected Object callXmlrpcMethod(String methodName, Object... params) throws XmlRpcException{	
		Object o = (Object) session.getClient().execute(methodName, Arrays.asList(params));	
		//print result for debug purposes
		if (o instanceof Object[]){
			for (Object obj: (Object[])o){
				System.out.println("Debug: result of '" + methodName + "' = " + obj.toString());				
			}
		}
		else System.out.println("Debug: result of '" + methodName + "' = " + o.toString());
		return o;
	}

	/**
	 * Generic method designed to obtain a list of objects that match parameters
	 * supplies in provided HashMap object
	 * @param values a Map with the parameters that will be searched for;
	 * if you supply the pair {"plan_id": 5}, plan_id #5 will be returned. Any combination
	 * of attributes can be entered and the result will be all matches that fit 
	 * the input values
	 * @return list of matching objects
	 */
	public Object[] getList(Map<String, Object> values) throws XmlRpcException
	{
		//some Testopia objects have no listing mechanism
		if(listMethod == null)
			return null;
		
		Object[] result = (Object[]) this.callXmlrpcMethod(listMethod, values);
		return result;
	}
	
	/**
	 * A simpler method to search by a single parameter that doesn't require creation of a Map object
	 * @param name - the name of the attribute to search on 
	 * @param value - the value to search for
	 * @return - list of matching objects
	 * @throws XmlRpcException
	 */
	public Object[] getList(String name, Object value) throws XmlRpcException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(name, value);
		return getList(map);
	}
	
	protected Map<String,Object> getDirtyAttributesMap(){
		Map<String,Object> map = new HashMap<String,Object>();
		for(Attribute attribute: attributes){
			if (attribute.get() != null && attribute.isDirty()){
				map.put(attribute.getName(), attribute.get());
			}
		}
		return map;
	}
	
	protected void syncAttributes(Map remoteMap){
		for(Attribute attr: attributes){
			String name = attr.getName();
			Object val = remoteMap.get(name);
			if (val == null) 
				System.out.println("Warning, Got remote attribute that we don't use locally: " + name + ", " + val.toString());
			else attr.set(val);
		}
	}
	/**
	 * protected method to create a new string attribute to use in 
	 * Testopia objects
	 * @param s the string attribute
	 * @return
	 */
	protected StringAttribute newStringAttribute(String name, String value){
		StringAttribute sa = new StringAttribute(name, value);
		this.attributes.add(sa);
		return sa;
	}
	
	protected IntegerAttribute newIntegerAttribute(String name, Integer value){
		IntegerAttribute ia = new IntegerAttribute(name, value);
		this.attributes.add(ia);
		return ia;
	}
	
	protected void cleanAllAttributes(){
		for(Attribute attribute: attributes)
			attribute.clean();
	}
	
	abstract class Attribute {
		String name = null;
		Object value = null;
		boolean dirty = true;
		
		public boolean isDirty(){
			return dirty;
		}
		public void clean(){
			dirty=false;
		}
		public Object get(){
			return value;
		}
		private void set(Object s){
			value = s;
			dirty = true;
		}
		public String getName(){
			return name;
		}
	}
	
	class StringAttribute extends Attribute{
		private StringAttribute(String name, String value){
			this.name = name;
			this.value = value;
		}
		public String get(){
			return (String)value;
		}
		public void set(String s){
			super.set(s);
		}
		public String toString(){
			return (String)value;
		}
		
	}
	
	class IntegerAttribute extends Attribute{
		private IntegerAttribute(String name, Integer value){
			this.name = name;
			this.value = value;	
		}
		public Integer get(){
			return (Integer)value;
		}
		public void set(Integer s){
			super.set(s);
		}

	}
}
