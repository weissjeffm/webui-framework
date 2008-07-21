package com.redhat.qe.auto.selenium;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

/**
 * This class listens for TestNG events, and logs them using the 
 * standard java logging facility.
 * 
 * In order to use this listener, the class name must be provided to testNG
 * using the -listener option (or specified as the attribute 'listener'
 * in an ant call to testng).
 * @author jweiss
 *
 */
public class TestNGListener implements IResultListener {

	private static Logger log = Logger.getLogger(TestNGListener.class.getName());
	private static IScreenCapture sc = null;
	
	public static void setScreenCaptureUtility(IScreenCapture sc){
		TestNGListener.sc = sc;
	}
	//TestNG's Test Listener methods so Selenium can log and screenshot properly

	public void onFinish(ITestContext context){
		log.info("TestNG Finishing: " + context.getName());
		
	}
	
	public void onStart(ITestContext context) {
		log.info("TestNG Starting: " + context.getName());
	}
	
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		log.log(Level.WARNING, "Test failed (but within success percentage): "+ result.getName(), result.getThrowable());

	}	
	
	public void onTestFailure(ITestResult result) {
		try {
			sc.screenCapture();
		}
		catch(Exception e){
			log.log(Level.WARNING, "Unable to capture screenshot.", e);
		}
		log.log(Level.SEVERE, "Test failed: "+ result.getName(), result.getThrowable());
	}
	public void onTestSkipped(ITestResult result) {
		log.info("Skipping test: " + result.getName());

	}
	public  void onTestStart(ITestResult result) {
		log.info("Starting test: " + result.getName());
		
	}
	public  void onTestSuccess(ITestResult result) {
		log.info("Test Passed: " + result.getName());
		
	}

	
	public void onConfigurationFailure(ITestResult result) {
		try {
			sc.screenCapture();
		}
		catch(Exception e){
			log.log(Level.WARNING, "Unable to capture screenshot.", e);
		}
		log.log(Level.SEVERE, "Configuration Failed: " + result.getName(), result.getThrowable());
		
	}

	
	public void onConfigurationSkip(ITestResult result) {
		log.fine("Configuration skipped: " + result.getName());
		
	}

	
	public void onConfigurationSuccess(ITestResult result) {
		log.fine("Configuration passed: " + result.getName());
		
	}
	
	

}
