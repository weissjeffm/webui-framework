package testopia.API;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

public abstract class TestopiaObject {

	protected XmlRpcClient client;
	protected String listMethod;
	
	protected Object callXmlrpcMethod(String methodName, Object... params) throws XmlRpcException{	
		return (Object) client.execute(methodName, Arrays.asList(params));	
	}

	/**
	 * Generic method designed to obtain a list of objects that match parameters
	 * supplies in provided HashMap object
	 * @param values a HashMap with the parameters that will be searched for;
	 * if you supply the pair {"plan_id": 5}, plan_id #5 will be returned. Any combination
	 * of attributes can be entered and the result will be all matches that fit 
	 * the input values
	 * @return list of matching objects
	 */
	public Object[] getList(HashMap<String, Object> values)
	{
		//some Testopia objects have no listing mechanism
		if(listMethod == null)
			return null;
		try 
		{
			Object[] result = (Object[]) this.callXmlrpcMethod(listMethod, values);
			return result;
		}			
		
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
