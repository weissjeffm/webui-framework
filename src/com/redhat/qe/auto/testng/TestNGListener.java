package com.redhat.qe.auto.testng;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
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

	protected static Logger log = Logger.getLogger(TestNGListener.class.getName());

	
	//Override TestNG's Test Listener methods for logging purposes

	@Override
	public void onFinish(ITestContext context){
		log.log(Level.INFO, "Finished TestNG Script: " + context.getName(), LogMessageUtil.Style.Banner);
	}
	
	@Override
	public void onStart(ITestContext context) {
		log.log(Level.INFO, "Starting TestNG Script: " + context.getName(), LogMessageUtil.Style.Banner);
	}
	
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		log.log(Level.WARNING, "Test Failed (but within success percentage): "+ result.getName(), result.getThrowable());
	}	
	
	@Override
	public void onTestFailure(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		Throwable err = result.getThrowable();
		LogRecord logRecord = new LogRecord(Level.SEVERE, "Test Failed: "+ result.getName());
		logRecord.setThrown(err);
		if (err != null && err instanceof AssertionError)
			logRecord.setParameters(new Object[] {LogMessageUtil.Style.AssertFailed});
		log.log(logRecord);
	}
	
	@Override
	public void onTestSkipped(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		if (result.getThrowable() != null){
			LogRecord r= new LogRecord(Level.INFO,  "Skipping test " + result.getName() + ": " + result.getThrowable().getMessage());
			r.setParameters(new Object[]{LogMessageUtil.Style.Banner});
			log.log(r);
		}
		else {
			log.log(Level.INFO, "Skipping Test " + result.getName() + ":  Unsatisfied dependency", LogMessageUtil.Style.Banner);
		}
	}
	
	@Override
	public  void onTestStart(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		LogRecord r= new LogRecord(Level.INFO, String.format("Starting Test: %s%s", result.getName(), getParameters(result)));
		r.setParameters(new Object[]{LogMessageUtil.Style.Banner, LogMessageUtil.Style.StartTest});
		log.log(r);
				
	}
	
	@Override
	public  void onTestSuccess(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		Throwable throwable = result.getThrowable();
		if (throwable != null){
			log.log(Level.INFO, "Expected exception of " + throwable.getClass().getName() + " '" + throwable.getMessage() + "' was in fact thrown." , LogMessageUtil.Style.Asserted);
		}
		log.log(Level.INFO, String.format("Test Passed: %s%s", result.getName(), getParameters(result)), LogMessageUtil.Style.Banner);
	}

	@Override
	public void onConfigurationFailure(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		log.log(Level.SEVERE, "Configuration Failed: " + result.getName(), result.getThrowable());
	}
	
	@Override
	public void onConfigurationSkip(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		if (result.getThrowable() != null){
			LogRecord r= new LogRecord(Level.INFO,  "Skipping configuration " + result.getName() + ": " + result.getThrowable().getMessage());
			r.setParameters(new Object[]{LogMessageUtil.Style.Banner});
			log.log(r);
		}
		else {
			log.log(Level.INFO, "Skipping configuration " + result.getName() + ":  Unsatisfied dependency", LogMessageUtil.Style.Banner);
		}
	}
	
	@Override
	public void onConfigurationSuccess(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		log.log(Level.FINE, "Configuration completed: " + result.getTestClass().getName() + "." + result.getName() , LogMessageUtil.Style.Banner);
	}

	@Override
	public void onFinish(ISuite suite) {
		log.log(Level.INFO, "Finishing TestNG Suite:" + suite.getName(), LogMessageUtil.Style.Banner);
	}

	@Override
	public void onStart(ISuite suite) {
		log.log(Level.INFO, "Starting TestNG Suite:" +suite.getName(), LogMessageUtil.Style.Banner);
	}

	
	public String getParameters(ITestResult result) {
		String params = "";
		Object[] parameters = result.getParameters();
		if (parameters != null && parameters.length > 0)
			params = "(" + Arrays.deepToString(parameters) + ")";
		return params;
	}
}
