package com.redhat.qe.auto.selenium;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A Formatter for java logging, to print nice timestamped 
 * lines to stdout/stderr.
 * @author jweiss
 *
 */
public class ConsoleLogFormatter extends Formatter {

	private static final DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm:ss.SSS");
	
	
	@Override
	public String format(LogRecord record) {
		String date = sdf.format(new Date(record.getMillis()));
		String throwable = "";
		String message = record.getMessage();
		if (record.getThrown() != null) throwable = throwableToString(record.getThrown())  + "\n";
		if (record.getParameters() != null)
			for (Object param: record.getParameters()){
				if (param.equals(LogMessageUtil.Style.Banner))
					message = "======= " + message;
			}
		
		return date + " - " + record.getLevel() + ": " + message + " (" + record.getSourceClassName() + "." 
		+ record.getSourceMethodName() + ")\n" + throwable;
	}

	
	protected String throwableToString(Throwable t){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}

