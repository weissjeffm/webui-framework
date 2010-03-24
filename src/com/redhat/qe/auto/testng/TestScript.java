package com.redhat.qe.auto.testng;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.redhat.qe.tools.compare.CollectionSorter;

public abstract class TestScript {

	protected static boolean initialized = false;
	protected static Logger log = Logger.getLogger(TestScript.class.getName());
	protected static final String defaultAutomationPropertiesFile=System.getenv("HOME")+"/automation.properties"; 
	protected static final String defaultLogPropertiesFile=System.getProperty("user.home")+ "/log.properties"; 
	
	public TestScript() {
		if (initialized) return; //only need to run this stuff once per jvm
		
		String propFile ="";
			
		//load log properties
		try{
			Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
			
			
			propFile = System.getProperty("log.propertiesfile", defaultLogPropertiesFile);
			if (! new File(propFile).exists()) {
				log.fine("No log.propertiesfile specified, nor found in HOME dir, trying to use default in project.");
				propFile = "log.properties";
			}
			else{
				log.info("Found log properties file: "+propFile);
			}

			LogManager.getLogManager().readConfiguration(new FileInputStream(propFile));
			log.fine("Loaded logger configuration from log.propertiesfile: "+propFile);

		} catch(Exception e){
			e.printStackTrace();
			log.log(Level.SEVERE, "Could not load log properties from "+propFile, e);
		}
		
		//load automation properties
		try{
			propFile = (System.getProperty("automation.propertiesfile"));
			
			if(propFile == null || propFile.length() == 0){
				log.info("System property automation.propertiesfile is not set.  Defaulting to "+ defaultAutomationPropertiesFile);
				propFile = defaultAutomationPropertiesFile;
			}
			Properties p = new Properties();
			p.load(new FileInputStream(propFile));
			for (Object key: p.keySet()){
				System.setProperty((String)key, p.getProperty((String)(key)));
			}
			log.fine("Loaded automation properties from automation.propertiesfile: "+propFile);
			
			// default automation.dir to user.dir
			if(System.getProperty("automation.dir") == null){
				System.setProperty("automation.dir", System.getProperty("user.dir"));
			}

		} catch(Exception e){
			e.printStackTrace();
			log.log(Level.SEVERE, "Could not load automation properties from "+propFile, e);
		}
	
		
		// echo all the system properties
		Set<String> keySet = System.getProperties().stringPropertyNames();
		List<String> keyList = CollectionSorter.asSortedList(keySet);
		for (Object key: keyList){
			String value = System.getProperty((String) key);
			if (key.toString().toLowerCase().contains("password"))
				value = "********";
			log.finer("Property("+key+")= "+ value);
		}
		
		initialized = true;
		
	}
		


}
