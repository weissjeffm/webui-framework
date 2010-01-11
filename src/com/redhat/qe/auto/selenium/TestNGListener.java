package com.redhat.qe.auto.selenium;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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

	
	//Override TestNG's Test Listener methods so Selenium can log and screenshot properly

	public void onFinish(ITestContext context){
		log.log(Level.FINE, "Finished TestNG Script: " + context.getName(), LogMessageUtil.Style.Banner);
		System.out.println();
	}
	
	public void onStart(ITestContext context) {
		System.out.println();
		log.log(Level.FINE, "Starting TestNG Script: " + context.getName(), LogMessageUtil.Style.Banner);
	}
	
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		log.log(Level.WARNING, "Test failed (but within success percentage): "+ result.getName(), result.getThrowable());
	}	
	
	public void onTestFailure(ITestResult result) {
		Throwable err = result.getThrowable();
		LogRecord logRecord = new LogRecord(Level.SEVERE, "Test failed: "+ result.getName());
		logRecord.setThrown(err);
		if (err != null && err instanceof AssertionError)
			logRecord.setParameters(new Object[] {LogMessageUtil.Style.AssertFailed});
		log.log(logRecord);
		try {
			screencap(result);
		}
		catch(NullPointerException npe){
			log.log(Level.FINE, "Unable to capture screenshot, the capture utility has not been set up yet.");
		}
		catch(Exception e){
			log.log(Level.FINE, "Unable to capture screenshot.", e);
		}

	}
	
	public void onTestSkipped(ITestResult result) {
		if (result.getThrowable() instanceof SkipException){
			LogRecord r= new LogRecord(Level.INFO,  "Skipping test " + result.getName() + ": " + result.getThrowable().getMessage());
			r.setParameters(new Object[]{LogMessageUtil.Style.Banner});
			log.log(r);
		}
		else {
			log.log(Level.FINE, "Skipping Test: " + result.getName(), LogMessageUtil.Style.Banner);
		}
	}
	
	public  void onTestStart(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		System.out.println();
		log.log(Level.FINE, String.format("Starting Test: %s%s", result.getName(), getParameters(result)), LogMessageUtil.Style.Banner);
	}
	
	public  void onTestSuccess(ITestResult result) {
		Throwable throwable = result.getThrowable();
		if (throwable != null){
			log.log(Level.INFO, "Expected exception of " + throwable.getClass().getName() + " '" + throwable.getMessage() + "' was in fact thrown." , LogMessageUtil.Style.Asserted);
		}
		log.log(Level.FINE, String.format("Test Passed: %s%s", result.getName(), getParameters(result)), LogMessageUtil.Style.Banner);
	}

	public String getParameters(ITestResult result) {
		String params = "";
		Object[] parameters = result.getParameters();
		if (parameters != null && parameters.length > 0)
			params = "(" + Arrays.deepToString(parameters) + ")";
		return params;
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
		log.log(Level.INFO, "Configuration skipped: " + result.getName(), LogMessageUtil.Style.Banner);
	}

	
	public void onConfigurationSuccess(ITestResult result) {
		log.log(Level.FINE, "Configuration completed: " + result.getName(), LogMessageUtil.Style.Banner);
	}

	@Override
	public void onFinish(ISuite suite) {
		log.log(Level.FINE, "Finishing TestNG Suite:" + suite.getName(), LogMessageUtil.Style.Banner);
	}

	@Override
	public void onStart(ISuite suite) {
		log.log(Level.FINE, "Starting TestNG Suite:" +suite.getName(), LogMessageUtil.Style.Banner);
	}
	
	protected void screencap(ITestResult result) throws Exception{
		if (sc instanceof ITestNGScreenCapture){
			((ITestNGScreenCapture) sc).testNGScreenCapture(result);
		}
		else sc.screenCapture();
	}

}
