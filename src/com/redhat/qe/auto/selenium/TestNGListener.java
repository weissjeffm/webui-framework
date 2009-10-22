package com.redhat.qe.auto.selenium;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
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
		log.fine("========= Finished TestNG Script: " + context.getName());
		System.out.println();
	}
	
	public void onStart(ITestContext context) {
		System.out.println();
		log.fine("========= Starting TestNG Script: " + context.getName());
	}
	
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		log.log(Level.WARNING, "Test failed (but within success percentage): "+ result.getName(), result.getThrowable());
	}	
	
	public void onTestFailure(ITestResult result) {
		try {
			screencap(result);
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
			log.log(Level.INFO, "========= Skipping test due to SkipException: " + result.getName(),result.getThrowable());
		}
		else {
			log.fine("========= Skipping Test: " + result.getName());
		}
	}
	
	public  void onTestStart(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		System.out.println();
		log.fine("========= Starting Test: " + result.getName());
	}
	
	public  void onTestSuccess(ITestResult result) {
		Throwable throwable = result.getThrowable();
		if (throwable != null){
			log.log(MyLevel.ACTION, "Expected exception of " + throwable.getClass().getName() + " '" + throwable.getMessage() + "' was in fact thrown." );
		}
		log.fine("========= Test Passed: " + result.getName());
	}

	
	public void onConfigurationFailure(ITestResult result) {
		try {
			screencap(result);
		}
		catch(Exception e){
			log.log(Level.WARNING, "Unable to capture screenshot.", e);
		}
		log.log(Level.SEVERE, "Configuration Failed: " + result.getName(), result.getThrowable());
	}

	
	public void onConfigurationSkip(ITestResult result) {
		System.out.println();
		log.fine("========= Configuration skipped: " + result.getName());
	}

	
	public void onConfigurationSuccess(ITestResult result) {
		log.finer("========= Configuration completed: " + result.getName());
	}

	@Override
	public void onFinish(ISuite suite) {
		log.fine("========= Finishing TestNG Suite:" + suite.getName());
	}

	@Override
	public void onStart(ISuite suite) {
		System.out.println();
		log.fine("========= Starting TestNG Suite:" + suite.getName());
	}
	
	protected void screencap(ITestResult result) throws Exception{
		if (sc instanceof ITestNGScreenCapture){
			((ITestNGScreenCapture) sc).testNGScreenCapture(result);
		}
		else sc.screenCapture();
	}

}
