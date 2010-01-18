package com.redhat.qe.auto.testng;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.testng.Reporter;

import com.redhat.qe.auto.selenium.LogMessageUtil;

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
				if (param.equals(LogMessageUtil.Style.Banner))
					css_class += " banner";
				if (param.equals(LogMessageUtil.Style.Action))
					css_class += " ACTION";
				if (param.equals(LogMessageUtil.Style.Asserted))
					css_class += " ASSERT";
				if (param.equals(LogMessageUtil.Style.AssertFailed))
					css_class += " ASSERTFAIL";
			}
		//Reporter.log("<div class='" + css_class + "'>"+record.getMessage() + "</div>");
		Reporter.log("<div class='" + css_class + "'>"+tagAllUrls(record.getMessage()) + "</div>");
	}
	
	protected static String tagAllUrls(String msg) {
		//String regex = "(http[s]?://[\\w\\d:/.$~\\-_?=&%#]+)";
		String regex = "((http[s]?|ftp|gopher|telnet|file|notes|ms-help):(//)[\\w\\d:/.$~\\-_?=&%#;]+)"; // ((http[s]?|ftp|gopher|telnet|file|notes|ms-help):(//)[\w\d:/.$~\-_?=&%#;]+)
		return msg.replaceAll(regex, "<a href=$1>$1</a>");
	}


	public static void main(String[] args) {
		String msg = "This is the url (http://foobar.com:7080/page?id=25&id256=%20) page.";
		System.out.println("UNTAGGED: "+msg);
		System.out.println("TAGGED:   "+tagAllUrls(msg));
	}
}
