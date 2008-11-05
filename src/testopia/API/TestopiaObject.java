package testopia.API;

import java.util.Arrays;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

public abstract class TestopiaObject {

	protected XmlRpcClient client;

	
	protected int callXmlrpcMethod(String methodName, Object... params) throws XmlRpcException{	
		return (Integer) client.execute(methodName,	Arrays.asList(params));	
	}

	
}
