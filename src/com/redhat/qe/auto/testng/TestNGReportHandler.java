package com.redhat.qe.auto.testng;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.testng.Reporter;

import com.redhat.qe.auto.selenium.LogMessageStyle;

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
		String css_class = record.getLevel().toString();
		if (record.getParameters() != null)
			for (Object param: record.getParameters()){
				if (param.equals(LogMessageStyle.Banner))
					css_class += " banner";
			}
		Reporter.log("<div class='" + css_class + "'>"+record.getMessage() + "</div>");
	}


}
