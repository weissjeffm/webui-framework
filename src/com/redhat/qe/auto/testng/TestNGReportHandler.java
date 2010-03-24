package com.redhat.qe.auto.testng;

import java.util.logging.Handler;
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
		String css_class = record.getLevel().toString();
		if (record.getParameters() != null)
			for (Object param: record.getParameters()){
				if (param.equals(LogMessageUtil.Style.Banner))
					css_class += " banner";
				if (param.equals(LogMessageUtil.Style.StartTest))
					css_class += " startTest";
				if (param.equals(LogMessageUtil.Style.Action))
					css_class += " ACTION";
				if (param.equals(LogMessageUtil.Style.Asserted))
					css_class += " ASSERT";
				if (param.equals(LogMessageUtil.Style.AssertFailed))
					css_class += " ASSERTFAIL";
			}
		//Reporter.log("<div class='" + css_class + "'>"+record.getMessage() + "</div>");
		Reporter.log("<div class='" + css_class + "'>"+tagAllUrls(addLineBreaks(escapeAllTags(record.getMessage()))) + "</div>");
	}
	
	/**
	 * Search msg for all embedded url strings (e.g. http://www.redhat.com) and return a modified
	 * msg with html links wrapped around the msg to make it clickable when viewed within a browser.
	 * @param msg
	 * @return
	 * @author jsefler
	 */
	protected static String tagAllUrls(String msg) {
		//String regex = "(http[s]?://[\\w\\d:/.$~\\-_?=&%#]+)";
		String regex = "((http[s]?|ftp|gopher|telnet|file|notes|ms-help):(//)[\\w\\d:/.$~\\-_?=&%#;]+)"; // ((http[s]?|ftp|gopher|telnet|file|notes|ms-help):(//)[\w\d:/.$~\-_?=&%#;]+)
		return msg.replaceAll(regex, "<a href=$1>$1</a>");
	}
	
	/**
	 * Search msg for all embedded html tags and escape their less than and greater than characters.
	 * @param msg
	 * @return - msg with all of its original html tags escaped.
	 * @author jsefler 
	 */
	protected static String escapeAllTags(String msg) {
		String regex = "<([^>]+)>";
		return msg.replaceAll(regex, "&lt;$1&gt;");
	}
	
	protected static String addLineBreaks(String msg) {
		return msg.replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>");
	}

	public static void main(String[] args) {
		String msg = "This is the url (http://foobar.com:7080/page?id=25&id256=%20) page.";
		System.out.println("UNTAGGED: "+msg);
		System.out.println("  TAGGED: "+tagAllUrls(msg));
		
		String msg2 = "<div>ssh root@rlx-0-04 grep -E '\"<password>dog8code</password><foo><-hi>\"' <> /tmp/foo_-ds.xml </div> <foo> <bar/>  run.sh foo < /tmp; cat foo >> /tmp";
		System.out.println("UNESCAPED: "+msg2);
		System.out.println("  ESCAPED:   "+escapeAllTags(msg2));

	}
}
