package com.redhat.qe.auto.selenium;

import java.io.File;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.internal.IConfigurationListener;
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
public class TestNGListener implements IResultListener, ISuiteListener {

	private static Logger log = Logger.getLogger(TestNGListener.class.getName());
	private static IScreenCapture sc = null;
	
	public static void setScreenCaptureUtility(IScreenCapture sc){
		TestNGListener.sc = sc;
	}
	//TestNG's Test Listener methods so Selenium can log and screenshot properly

	public void onFinish(ITestContext context){
		log.fine("=========  TestNG Finishing Test: " + context.getName()+ " ============================================");
		
	}
	
	public void onStart(ITestContext context) {
		
		log.fine("=========  TestNG Starting Test: " + context.getName()+ " =============================================");
	}
	
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		log.log(Level.WARNING, "Test failed (but within success percentage): "+ result.getName(), result.getThrowable());

	}	
	
	public void onTestFailure(ITestResult result) {
		try {
			sc.screenCapture();
		}
		catch(NullPointerException npe){
			log.log(Level.FINE, "Unable to capture screenshot, the capture utility has not been set up yet.");
		}
		catch(Exception e){
			log.log(Level.FINE, "Unable to capture screenshot.", e);
		}
		log.log(Level.SEVERE, "Test failed: "+ result.getName(), result.getThrowable());
	}
	public void onTestSkipped(ITestResult result) {
		if (result.getThrowable() instanceof SkipException){
			log.log(Level.INFO, "========= Skipping test due to SkipException: " 
					+ result.getName()+ " ============================================\n",result.getThrowable());
		}
		else log.fine("========= Skipping test: " + result.getName()+ " ============================================");

	}
	public  void onTestStart(ITestResult result) {
		log.fine("========= Starting test: " + result.getName()+ " ============================================");
		
	}
	public  void onTestSuccess(ITestResult result) {
		Throwable throwable = result.getThrowable();
		if (throwable != null){
			log.log(MyLevel.ACTION, "Expected exception of " + throwable.getClass().getName() + " '" + throwable.getMessage() + "' was in fact thrown." );
		}
		log.fine("========= Test Passed: " + result.getName()+ " ============================================");
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
		log.fine("========= Configuration skipped: " + result.getName()+ " ============================================");
		
	}

	
	public void onConfigurationSuccess(ITestResult result) {
		log.finer("========= Configuration completed: " + result.getName()+ " ============================================");
		
	}

	@Override
	public void onFinish(ISuite suite) {
		log.fine("=========  TestNG Finishing Suite:" + suite.getName()+ " =============================================");
	}

	@Override
	public void onStart(ISuite suite) {
		log.fine("=========  TestNG Starting Suite:" + suite.getName()+ " =============================================");
	}
	
	

}
