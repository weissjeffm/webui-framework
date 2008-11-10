package testopia.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;

import com.redhat.qe.auto.testopia.TestopiaTestNGListener;

public abstract class TestopiaObject {

	protected Session session;
	protected String listMethod;
	protected List<Attribute> attributes = new ArrayList<Attribute>();
	protected static Logger log = Logger.getLogger(TestopiaObject.class.getName());

	protected Object callXmlrpcMethod(String methodName, Object... params) throws XmlRpcException{	
		Object o = (Object) session.getClient().execute(methodName, Arrays.asList(params));	
		//print result for debug purposes
		if (o instanceof Object[]){
			for (Object obj: (Object[])o){
				log.finer("Result of '" + methodName + "' = " + obj.toString());				
			}
		}
		else log.finer("Result of '" + methodName + "' = " + o.toString());
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
			if (attribute.getValue() != null && attribute.isDirty()){
				map.put(attribute.getName(), attribute.getValue());
			}
		}
		return map;
	}
	
	protected void syncAttributes(Map remoteMap){
		for(Attribute attr: attributes){
			String name = attr.getName();
			Object val = remoteMap.get(name);
			if (val == null) 
				try {
					log.warning("Got remote attribute that we don't use locally: " + name + ", " + val.toString());
				}
				catch(NullPointerException npe) {}
			else attr.set(val);
		}
		//FIXME need to check for error before cleaning attributes
		cleanAllAttributes();
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
	protected BooleanAttribute newBooleanAttribute(String name, Boolean value){
		BooleanAttribute ba = new BooleanAttribute(name, value);
		this.attributes.add(ba);
		return ba;
	}
	
	protected void cleanAllAttributes(){
		for(Attribute attribute: attributes)
			attribute.clean();
	}
	
	protected Map<String,Object> update(String methodName, int id) throws XmlRpcException{
		Map<String,Object> outGoingMap =  getDirtyAttributesMap();
		Map<String,Object> map;
		if (outGoingMap.size() > 0)
			map = (Map<String,Object>)this.callXmlrpcMethod(methodName, id, outGoingMap);
		else throw new TestopiaException("There are no locally updated fields to update via xmlrpc!");
		this.syncAttributes(map);
		return map;
	}
	
    protected Map<String,Object> create(String methodName) throws XmlRpcException{		
		Map<String,Object> map = (Map<String,Object>)this.callXmlrpcMethod(methodName, getDirtyAttributesMap());
		this.syncAttributes(map);
		return map;
	}
    
    protected Map<String,Object> get(String methodName, int id) throws XmlRpcException{		
		Map<String,Object> map = (Map<String,Object>)this.callXmlrpcMethod(methodName, id);
		this.syncAttributes(map);
		return map;
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
		public Object getValue(){
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
	
	class BooleanAttribute extends Attribute{
		private BooleanAttribute(String name, Boolean value){
			this.name = name;
			this.value = value;	
		}
		public Boolean get(){
			return (Boolean)value;
		}
		public void set(Boolean s){
			super.set(s);
		}

	}
}
