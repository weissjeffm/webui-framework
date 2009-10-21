package com.redhat.qe.auto.bugzilla;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import com.redhat.qe.auto.testng.BzChecker;

public class BugzillaTestNGListener implements IResultListener{
	protected static Logger log = Logger.getLogger(BugzillaTestNGListener.class.getName());
	protected static BzChecker bzChecker = null;

	@Override
	public void onConfigurationFailure(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationSkip(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		if (bzChecker == null) {
			bzChecker = new BzChecker();
			bzChecker.init();
		}
		//if the test is in a group "verifiesBug-xxxxxx" and the bug is in ON_QA, close it
		String[] groups = result.getMethod().getGroups();
		Pattern p = Pattern.compile("verifiesBug-(\\d+)");
		for (String group: groups){
			Matcher m = p.matcher(group);
			if (m.find()){
				String number = m.group(1);
				log.fine("This test verifies bugzilla bug #"+ number);
				BzChecker.bzState state = bzChecker.getBugState(number);
				if (state.equals(BzChecker.bzState.ON_QA)){
					//TODO need to call code here to actually close the bug (doesn't work yet)
					log.warning("Need to verify bug " + number + "!");
					bzChecker.setBugState(number, BzChecker.bzState.VERIFIED);
					log.info("Verified bug " + number);
				}
				else log.warning("Bug " + number + " has been verified, but it is in " + state + " state instead of ON_QA");
			}
			
		}
	}

}
