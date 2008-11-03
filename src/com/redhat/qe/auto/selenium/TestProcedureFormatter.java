package com.redhat.qe.auto.selenium;

import java.util.logging.LogRecord;

public class TestProcedureFormatter extends LogFormatter {

	@Override
	public String format(LogRecord record) {
		if (!record.getLevel().equals(MyLevel.ACTION)) return super.format(record);
		else {
			String throwable = "";
			if (record.getThrown() != null) throwable = throwableToString(record.getThrown())  + "\n";
			return record.getMessage() + "\n" + throwable;
		}
	}
	
}
