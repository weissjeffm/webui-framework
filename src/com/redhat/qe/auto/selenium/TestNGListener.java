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

import com.redhat.qe.auto.testng.LogMessageUtil;


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
public class TestNGListener extends com.redhat.qe.auto.testng.TestNGListener implements IResultListener, ISuiteListener {

	private static IScreenCapture sc = null;
	
	public static void setScreenCaptureUtility(IScreenCapture sc){
		TestNGListener.sc = sc;
	}

	
	//Override TestNG's Test Listener methods so Selenium can log and screenshot properly

	@Override
	public void onTestFailure(ITestResult result) {
		super.onTestFailure(result);
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
	
	@Override
	public void onConfigurationFailure(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		try {
			screencap(result);
		}
		catch(Exception e){
			log.log(Level.WARNING, "Unable to capture screenshot.", e);
		}
		log.log(Level.SEVERE, "Configuration Failed: " + result.getName(), result.getThrowable());
	}

	
	protected void screencap(ITestResult result) throws Exception{
		if (sc==null) {
			log.log(Level.WARNING, "No ScreenCaptureUtility has been set.", LogMessageUtil.Style.Banner);
			return;
		}
		if (sc instanceof ITestNGScreenCapture){
			((ITestNGScreenCapture) sc).testNGScreenCapture(result);
		}
		else sc.screenCapture();
	}

}
