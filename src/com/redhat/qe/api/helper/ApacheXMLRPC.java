package com.redhat.qe.api.helper;

import java.net.URL;

import tcms.API.Session;

public class ApacheXMLRPC {
	
	protected Session session;
	URL bugURL;
	Object loginHash;
	
	public  ApacheXMLRPC(String login,String password,String url){
		try{
		session = new Session(login, password, new URL(url));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public Object login(String loginMethod,
						String loginKey,
						String login,
						String passKey,
						String password,
						String returnKey
						){

		try{
		//loginHash = session.login("User.login","login",login,"password",password,"id");
		loginHash = session.login(loginMethod,loginKey,login,passKey,password,returnKey);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return loginHash;
	}
	
	public void execute(String methodName, Object... params){	
		try{
		session.getClient().execute(methodName,  params);
		}
		catch(Exception e){
			e.printStackTrace();
		}
			
		
	}
	

	
}
