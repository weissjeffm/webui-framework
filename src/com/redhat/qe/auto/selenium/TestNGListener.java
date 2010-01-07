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
		log.log(Level.FINE, "Finished TestNG Script: " + context.getName(), LogMessageStyle.Banner);
		System.out.println();
	}
	
	public void onStart(ITestContext context) {
		System.out.println();
		log.log(Level.FINE, "Starting TestNG Script: " + context.getName(), LogMessageStyle.Banner);
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
		Throwable err = result.getThrowable();
		Level level = Level.SEVERE;
		if (err != null && err instanceof AssertionError)
			level = MyLevel.ASSERTFAIL;
		log.log(level, "Test failed: "+ result.getName(), err);
	}
	
	public void onTestSkipped(ITestResult result) {
		if (result.getThrowable() instanceof SkipException){
			LogRecord r= new LogRecord(Level.INFO,  "Skipping test " + result.getName() + ": " + result.getThrowable().getMessage());
			r.setParameters(new Object[]{LogMessageStyle.Banner});
			log.log(r);
		}
		else {
			log.log(Level.FINE, "Skipping Test: " + result.getName(), LogMessageStyle.Banner);
		}
	}
	
	public  void onTestStart(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		System.out.println();

		log.log(Level.FINE, "Starting Test: " + result.getName(), LogMessageStyle.Banner);
	}
	
	public  void onTestSuccess(ITestResult result) {
		Throwable throwable = result.getThrowable();
		if (throwable != null){
			log.log(MyLevel.ASSERT, "Expected exception of " + throwable.getClass().getName() + " '" + throwable.getMessage() + "' was in fact thrown." );
		}
		String params = "";
		if (result.getParameters() != null && result.getParameters().length > 0)
				params = "(" + Arrays.deepToString(result.getParameters()) + ")";
		log.log(Level.FINE, String.format("Test Passed: %s%s", result.getName(), params), LogMessageStyle.Banner);
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
		log.log(Level.INFO, "Configuration skipped: " + result.getName(), LogMessageStyle.Banner);
	}

	
	public void onConfigurationSuccess(ITestResult result) {
		log.log(Level.FINE, "Configuration completed: " + result.getName(), LogMessageStyle.Banner);
	}

	@Override
	public void onFinish(ISuite suite) {
		log.log(Level.FINE, "Finishing TestNG Suite:" + suite.getName(), LogMessageStyle.Banner);
	}

	@Override
	public void onStart(ISuite suite) {
		log.log(Level.FINE, "Starting TestNG Suite:" +suite.getName(), LogMessageStyle.Banner);
	}
	
	protected void screencap(ITestResult result) throws Exception{
		if (sc instanceof ITestNGScreenCapture){
			((ITestNGScreenCapture) sc).testNGScreenCapture(result);
		}
		else sc.screenCapture();
	}

}
