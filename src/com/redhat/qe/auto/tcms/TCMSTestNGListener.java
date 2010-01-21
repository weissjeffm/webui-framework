package com.redhat.qe.auto.tcms;

import java.util.logging.Logger;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

public class TCMSTestNGListener implements IResultListener{
	protected static Logger log = Logger.getLogger(TCMSTestNGListener.class.getName());

	@Override
	public void onConfigurationFailure(ITestResult arg0) {
	}

	@Override
	public void onConfigurationSkip(ITestResult arg0) {
	}

	@Override
	public void onConfigurationSuccess(ITestResult arg0) {
	}

	@Override
	public void onFinish(ITestContext arg0) {
	}

	@Override
	public void onStart(ITestContext arg0) {
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
	}

	@Override
	public void onTestFailure(ITestResult arg0) {
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
	}

	@Override
	public void onTestStart(ITestResult result) {
		ImplementsTCMS tcmsInfo = 
			result.getMethod().getMethod().getAnnotation(ImplementsTCMS.class);
		if(tcmsInfo != null){
			if (tcmsInfo.tcms().toLowerCase().contains("nitrate"))
				log.info("Executing testcase: https://tcms.engineering.redhat.com/case/"+
						tcmsInfo.id()+"/");
		}
	}
	
	@Override
	public void onTestSuccess(ITestResult result) {
	}
}
