package com.redhat.qe.auto.selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyReader extends Properties {
	
	private static Properties properties;
	

	protected static void loadProperties() {
		properties = new Properties();
		String path = "/"
				+ System.getProperty("harness.environment", "localhost")
				+ "-settings.properties";
		String mydir = System.getProperty("user.dir");
		InputStream in = null;
		try {
			// try class path
			// in = HarnessConfiguration.class.getResourceAsStream(path);
			if (in == null) {
				//FIXME //wes needs to change this.. on hudson side too.
				File fileBVT = new File("/home/rhnuser/automated-testing"
						+ path);			
				File file = new File(mydir + path);
				if (fileBVT.exists()) {
					in = new FileInputStream(fileBVT);
					System.out.println("found BVT properties");
				} else
					in = new FileInputStream(file);
			}
			if (in != null) {
				properties.load(in);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ee) {
					ee.printStackTrace();
				}
			}
		}
	}
	
	public String getProperty(String key) {
		return getProperty(key, "");
	}
	
	public String getProperty(String key, String defaultValue) {
		if (properties == null) {
			loadProperties();
		}
		return properties.getProperty(key, System
				.getProperty(key, defaultValue));
	}
	
	public  int getPropertyAsInt(String key, int defaultValue) {
		int intValue = -1;
		try {
			intValue = Integer.parseInt(getProperty(key, String.valueOf(
					defaultValue).trim()));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return intValue;
	}

}
