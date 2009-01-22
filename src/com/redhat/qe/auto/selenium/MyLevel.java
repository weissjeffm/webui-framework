package com.redhat.qe.auto.selenium;

import java.util.logging.Level;

public class MyLevel extends Level {

	// custom logging level for java logging, to log clicks

	static final long serialVersionUID = 3945372834L;
	// Create the new level
	public static final Level ACTION = new MyLevel("ACTION", Level.INFO.intValue() + 1);

	public MyLevel(String name, int value) {
		super(name, value);
	}


}
