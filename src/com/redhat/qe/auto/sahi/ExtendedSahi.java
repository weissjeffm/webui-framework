package com.redhat.qe.auto.sahi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.config.Configuration;

/**
 * This class extends the Browser functionality.  It 
 * provides logging of UI actions (via java standard logging),
 * and some convenience methods.
 * @author dgao
 * @author jkandasa (Jeeva Kandasamy)
 */
public class ExtendedSahi extends Browser {
	private static Logger _logger = Logger.getLogger(ExtendedSahi.class.getName());

	public ExtendedSahi(String browserPath, String browserProcessName, String browserOpt, String sahiDir, String userDataDir) {
		super(browserPath, browserProcessName, browserOpt);
		Configuration.initJava(sahiDir, userDataDir);
	}
	
	//This method is used to select drop down on GWT web (Example- RHQ 4.x)
	public void selectComboBoxDivRow(Browser browser, String comboBoxIdentifier, String optionToSelect){
		//browser.focus(browser.div(comboBoxIdentifier));
		browser.xy(browser.div(comboBoxIdentifier), 3, 3).click();
		//browser.row(optionToSelect).focus();
		browser.xy(browser.row(optionToSelect), 3, 3).click();
		_logger.log(Level.INFO, "Selected the element ["+optionToSelect+"]");
	}
	
	//Getting array value from String
	public String[] getCommaToArray(String commaValue){
		return commaValue.split(",");
	}

	//String to key value map
	public HashMap<String, String> getKeyValueMap(String keyValuesString){
		HashMap<String, String> keyValueMap = new HashMap<String, String>();
		if(keyValuesString == null){
			return keyValueMap;
		}		
		String[] keyValuesArray = keyValuesString.split(",");
		for(String keyValue: keyValuesArray){
			String[] keyVal = keyValue.split("=");
			if(keyVal.length < 2 ){
				keyValueMap.put(keyVal[0].trim(), "");
			}else{
				keyValueMap.put(keyVal[0].trim(), keyVal[1].trim());
			}			
		}
		return keyValueMap;		
	}
	
	//String to collection of hash map
	public LinkedList<HashMap<String, String>> getKeyValueMapList(String keyValuesString){
		LinkedList<HashMap<String, String>> list = new LinkedList<HashMap<String,String>>();
		HashMap<String, String> keyValueMap = new HashMap<String, String>();
		String[] keyValuesArray = keyValuesString.split(",");
		for(String keyValue: keyValuesArray){
			String[] keyVal = keyValue.split("=");
			if(keyVal.length < 2 ){
				keyValueMap.put(keyVal[0].trim(), "");
			}else{
				keyValueMap.put(keyVal[0].trim(), keyVal[1].trim());
			}	
			list.addLast((HashMap<String, String>) keyValueMap.clone());
		}
		return list;		
	}
	
	//Wait until the element get present or timeout, which one is lesser
	public boolean waitForElementDivExists(Browser browser, String element, int waitTimeMilliSeconds){
		return waitForElementExists(browser, browser.div(element), "Div: "+element, waitTimeMilliSeconds);
	}

	public boolean waitForElementRowExists(Browser browser, String element, int waitTimeMilliSeconds){
		return waitForElementExists(browser, browser.row(element), "Row: "+element, waitTimeMilliSeconds);
	}
	
	public boolean waitForElementDivVisible(Browser browser, String element, int waitTimeMilliSeconds){
		return waitForElementVisible(browser, browser.row(element), "Div: "+element, waitTimeMilliSeconds);
	}
	
	public boolean waitForElementRowVisible(Browser browser, String element, int waitTimeMilliSeconds){
		return waitForElementVisible(browser, browser.row(element), "Row: "+element, waitTimeMilliSeconds);
	}

	public boolean waitForElementExists(Browser browser, ElementStub elementStub, String element, int waitTimeMilliSeconds){
		_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(waitTimeMilliSeconds/1000)+" Second(s)...");
		while(waitTimeMilliSeconds >=  0){
			if(elementStub.exists()){
				_logger.info("Element ["+element+"] exists.");
				return true;
			}else{
				browser.waitFor(500);
				waitTimeMilliSeconds -= 500;
				if((waitTimeMilliSeconds%(1000*5)) == 0){
					_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(waitTimeMilliSeconds/1000)+" Second(s)...");
				}
			}
		}		
		_logger.warning("Failed to get the element! ["+element+"]");
		return false;
	}

	public boolean waitForElementVisible(Browser browser, ElementStub elementStub, String element, int waitTimeMilliSeconds){
		_logger.finer("Waiting for the element: ["+element+"], Remaining wait time: "+(waitTimeMilliSeconds/1000)+" Second(s)...");
		while(waitTimeMilliSeconds >=  0){
			if(elementStub.isVisible()){
				_logger.info("Element ["+element+"] is visable");
				return true;
			}else{
				browser.waitFor(500);
				waitTimeMilliSeconds -= 500;
				if((waitTimeMilliSeconds%(1000*5)) == 0){
					_logger.finer("Waiting for the element: ["+element+"], Remaining wait time: "+(waitTimeMilliSeconds/1000)+" Second(s)...");
				}
			}
		}		
		_logger.warning("Failed to get the element! ["+element+"]");
		return false;
	}
}
