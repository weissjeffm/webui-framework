package com.redhat.qe.auto.tcms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class TestProcedureFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		/*if (!record.getLevel().equals(MyLevel.ACTION)) return super.format(record);
		else { */
			String throwable = "";
			if (record.getThrown() != null) throwable = throwableToString(record.getThrown())  + "\n";
			return record.getMessage() + "<br>\n" + throwable;
		//}
	}
	
	protected String throwableToString(Throwable t){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	
}
