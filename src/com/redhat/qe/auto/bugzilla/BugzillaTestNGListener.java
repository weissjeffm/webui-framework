package com.redhat.qe.auto.bugzilla;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcException;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.internal.IResultListener;

import com.redhat.qe.auto.testng.BlockedByBzBug;
import com.redhat.qe.auto.testng.BzBugDependency;
import com.redhat.qe.auto.testng.BzChecker;
import com.redhat.qe.auto.testopia.AbstractTestProcedureHandler;
import com.redhat.qe.auto.testopia.TestProcedureHandler;

public class BugzillaTestNGListener implements IResultListener{
	private static final String AUTO_VERIFIED = "AutoVerified";
	private static final String BLOCKED_BY_BUG = "blockedByBug";
	private static final String VERIFIES_BUG = "verifiesBug";
	protected static Logger log = Logger.getLogger(BugzillaTestNGListener.class.getName());
	protected static BzChecker bzChecker = null;
	protected static Map<Object[], BzBugDependency> bzTests = new HashMap<Object[], BzBugDependency>();
	
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
		/*
		 * if the test is in a group "blockedByBug-xxxxxx" and the bug is not in
		 * ON_QA, VERIFIED, RELEASE_PENDING, POST, CLOSED then skip it 
		 */
		if (bzChecker == null) {
			bzChecker = new BzChecker();
			bzChecker.init();
		}
		String[] groups = result.getMethod().getGroups();
		
		Pattern p = Pattern.compile("[" + VERIFIES_BUG + "|" + BLOCKED_BY_BUG +"]-(\\d+)");
		for (String group: groups){
			Matcher m = p.matcher(group);
			if (m.find()){
				String number = m.group(1);
				lookupBugAndSkipIfOpen(number);
			}			
		}
		//if nothing found, check the param list (if there is one) for certain types
		Object[] params = result.getParameters();
		
		if (params.length > 0 && params[0] instanceof BzBugDependency){
			BzBugDependency bbb = (BzBugDependency)params[0];
			lookupBugAndSkipIfOpen(bbb.getBugId());
			//if we get here, we need to extract items into the list of params
			log.finer("Extracting parameters: " + Arrays.deepToString(bbb.getParameters()));
			result.setParameters(bbb.getParameters());
			/*
			 * save the bug number in a hashtable here, otherwise the info is lost
			 * and we won't know that if the test passes, we can unblock this bug,
			 * unless we have that bug ID after the test is run
			 */
			bzTests.put(result.getParameters(), bbb); 
		}
	}

	protected void lookupBugAndSkipIfOpen(String number){
		BzChecker.bzState state; 
		try {
			state = bzChecker.getBugState(number);
		}catch(XmlRpcException xre) {
			log.log(Level.WARNING, "Could not determine the state of bug " + number + ". Assuming test needs to be run.", xre);
			return;
		}
		if (! (state.equals(BzChecker.bzState.ON_QA) ||
				state.equals(BzChecker.bzState.VERIFIED) ||
				state.equals(BzChecker.bzState.RELEASE_PENDING) ||
				state.equals(BzChecker.bzState.POST) ||
				state.equals(BzChecker.bzState.CLOSED))){
			// the bug is not ready to retest
			throw new SkipException("This test is blocked by "+state.toString()+" Bugzilla bug "+number+".  (https://bugzilla.redhat.com/show_bug.cgi?id="+number+")");
		}
	}
	
	@Override
	public void onTestSuccess(ITestResult result) {
		if (bzChecker == null) {
			bzChecker = new BzChecker();
			bzChecker.init();
		}
		//FIXME this method needs some work
		
		//if the test is in a group "verifiesBug-xxxxxx" and the bug is in ON_QA, close it
		String[] groups = result.getMethod().getGroups();
		Pattern p = Pattern.compile("[" + VERIFIES_BUG + "|" + BLOCKED_BY_BUG +"]-(\\d+)");
		for (String group: groups){
			Matcher m = p.matcher(group);
			if (m.find()){
				String number = m.group(1);
				BzChecker.bzState state;
				try {
					state = bzChecker.getBugState(number);
				}
				catch(XmlRpcException xre) {
					log.log(Level.WARNING, "Could not determine the state of bug " + number + ". It may need to be closed if is hasn't been already.", xre);
					break;
				}
				if (group.startsWith(VERIFIES_BUG)) {
					log.fine("This test verifies bugzilla bug #"+ number);
					if (state.equals(BzChecker.bzState.ON_QA)){
						//TODO need to call code here to actually close the bug (doesn't work yet)
						log.warning("Need to verify bug " + number + "!");
						/*
						 * not ready to start auto-closing yet
						 * bzChecker.setBugState(number, BzChecker.bzState.VERIFIED);
						 * log.info("Verified bug " + number);
						 **/
						verifyComment(number, result);
					}
					else log.warning("Bug " + number + " has been verified, but it is in " + state + " state instead of ON_QA");
				}
				else { //blockedByBug
					log.warning("Test is now unblocked by bug " + number + ".");
				}
			}
		}
		BzBugDependency blockedOrVerifiedBy = bzTests.get(result.getParameters());
		if (blockedOrVerifiedBy != null){
			log.warning("Test is now unblocked by bug " + blockedOrVerifiedBy.getBugId() + ".");			
			if (blockedOrVerifiedBy.getType().equals(BzBugDependency.Type.Verifies)){
				verifyComment(blockedOrVerifiedBy.getBugId(), result);
			}
		}
	}
	
	protected void verifyComment(String bugNumber, ITestResult result){
		//first check if this has been tested already.  if so, do nothing
		try {
			if (bzChecker.getBugField(bugNumber, "keywords").toString().indexOf(AUTO_VERIFIED) != -1 ) {
				log.info("Bug " + bugNumber + " already has the AutoVerified keyword.");
				return;
			}
		} catch(Exception e) {
			log.log(Level.WARNING, "Could not determine if bug " + bugNumber + " has been marked AutoVerified yet.",e);
		}
		StringBuffer sb = new StringBuffer();
		bzChecker.login(System.getProperty("bugzilla.login"), System.getProperty("bugzilla.password"));
		sb.append("Verified by Automated Test " + result.getName() + " parameters: (" + Arrays.deepToString(result.getParameters()) + ")\n");
		String log = AbstractTestProcedureHandler.getActiveLog();
		if (log != null)
		sb.append("Automation log:\n");
		sb.append(AbstractTestProcedureHandler.getActiveLog());
		bzChecker.addKeywords(bugNumber, AUTO_VERIFIED);
		bzChecker.addComment(bugNumber, sb.toString());
		bzTests.remove(result.getParameters());
	}
	
	public static void main (String... args) {
		Pattern p = Pattern.compile("[verifiesBug|blockedByBug]-(\\d+)");
		Matcher m = p.matcher("blockedByBug-12354542");
		m.find();
		System.out.println(m.group(1));
	}

}
