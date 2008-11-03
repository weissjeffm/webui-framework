package com.redhat.qe.auto.selenium;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author jweiss
 * Class to take in-memory log statements of selenium actions,
 * format them and return them as a big formatted string.
 *
 */
public class TestProcedureHandler extends Handler {

	protected StringBuffer sb = new StringBuffer();
	
	public TestProcedureHandler() {
 	
		setLevel(MyLevel.ACTION);
		setFormatter(new TestProcedureFormatter());
	
    }
	
	@Override
	public void publish(LogRecord record) {
		// TODO Auto-generated method stub
		if (isLoggable(record)) 
			sb.append(getFormatter().format(record));
	}
	
	

	@Override
	public void close() throws SecurityException {
		
	}

	@Override
	public void flush() {
		
	}

	public String getLog(){
		return sb.toString();
	}
}
