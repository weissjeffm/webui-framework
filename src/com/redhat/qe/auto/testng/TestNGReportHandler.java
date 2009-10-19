package com.redhat.qe.auto.testng;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.testng.Reporter;

public class TestNGReportHandler extends Handler {

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void publish(LogRecord record) {
		Reporter.log("<span class='" + record.getLevel().toString() + "'>"+record.getMessage() + "</span>");
	}


}
