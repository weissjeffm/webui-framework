package testopia.API;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

public abstract class TestopiaObject {

	protected Session session;
	protected String listMethod;
	
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
	
	abstract class Attribute {
		Object attr = null;
		boolean dirty = true;
		public boolean isDirty(){
			return dirty;
		}
		public void clean(){
			dirty=false;
		}
		public void set(Object s){
			attr = s;
			dirty = true;
		}
	}
	
	class StringAttribute extends Attribute{
		public StringAttribute(String s){
			attr = s;
		}
		public String get(){
			return (String)attr;
		}
		public String toString(){
			return (String)attr;
		}
		
	}
	
	class IntegerAttribute extends Attribute{
		public IntegerAttribute(Integer s){
			attr = s;
		}
		public Integer get(){
			return (Integer)attr;
		}
	}
}
