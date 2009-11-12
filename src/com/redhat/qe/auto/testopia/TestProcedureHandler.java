package com.redhat.qe.auto.testopia;

import java.util.logging.LogRecord;

import com.redhat.qe.auto.selenium.MyLevel;

/**
 * @author jweiss
 * Class to take in-memory log statements of selenium actions,
 * format them and return them as a big formatted string.
 *
 */
public class TestProcedureHandler extends AbstractTestProcedureHandler {

	protected StringBuffer sb = new StringBuffer();

	public TestProcedureHandler() {
 		setLevel(MyLevel.INFO);
		setFormatter(new TestProcedureFormatter());	
    }
	
	@Override
	public void publish(LogRecord record) {
		
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
	
	public void reset(){
		sb = new StringBuffer();
	}

}
