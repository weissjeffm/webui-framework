package com.redhat.qe.auto.tcms;

import java.util.logging.Handler;
import java.util.logging.Logger;

public abstract class AbstractTestProcedureHandler extends Handler implements ITestProcedureHandler{
	protected static Logger log = Logger.getLogger(AbstractTestProcedureHandler.class.getName());

	
	public static ITestProcedureHandler getActiveHandler(){
		Handler[] handlers = Logger.getLogger("").getHandlers();
		//find the right handler (and save for later)
		for (Handler handler: handlers){
			log.finer("Handlers = " + handler.getClass().getName());
			if (handler.getClass().getName().contains("TestProcedureHandler")) {
				//log.finer("Class cl:" + TestProcedureHandler.class.getClassLoader().toString() + ". var cl" + tph.getClass().getClassLoader().toString());
				return ((ITestProcedureHandler)handler);
			}
			
		}
		return null;
		
	}
	public static String getActiveLog() {
		ITestProcedureHandler tph = TestProcedureHandler.getActiveHandler();
		return tph == null? null : tph.getLog();
	}
	
	public static void resetActiveLog() {
		ITestProcedureHandler tph = TestProcedureHandler.getActiveHandler();
		if (tph != null) tph.reset();
	}
}
