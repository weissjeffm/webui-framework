package com.redhat.qe.auto.testng;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogMessageUtil {
	public enum Style {Banner, Action, Asserted, AssertFailed, StartTest};
	
	public static LogRecord info() {
		return new LogRecord(Level.INFO, "");
	}
	public static LogRecord action() { 
		LogRecord action =  new LogRecord(Level.INFO, "");
		action.setParameters(new Object[] {Style.Action});
		return action;
	}
	public static LogRecord fine() {
		return new LogRecord(Level.FINE, "");
	}

	
}
