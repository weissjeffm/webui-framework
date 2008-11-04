/**
 * 
 */
package com.redhat.qe.auto.testopia;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import testopia.API.TestopiaTestCase;
import testopia.API.User;

/**
 * @author jweiss
 *
 */
public class TestopiaTestNGListener implements IResultListener {

	private static final String TESTOPIA_PW = "dog8code";
	private static final String TESTOPIA_USER = "jweiss+jonqa@redhat.com";
	private static final String TESTOPIA_URL = "https://testopia-01.lab.bos.redhat.com/bugzilla/tr_xmlrpc.cgi";
	protected TestProcedureHandler tph = null;
	
	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailure(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
	 */
	@Override
	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult arg0) {
		//get the procedure log from the handler
		String log = "no procedure found!";
		Handler[] handlers = Logger.getLogger("").getHandlers();
		
		if (tph == null) {
			//find the right handler (and save for later)
			for (Handler handler: handlers){
				if (handler instanceof TestProcedureHandler)
					tph = ((TestProcedureHandler)handler);
			}
		}
		log = tph.getLog();
		
		//put it in testopia
		
		//reset the handler
		((TestProcedureHandler)tph).reset();
		

	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationFailure(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationFailure(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationSkip(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSkip(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.testng.internal.IConfigurationListener#onConfigurationSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onConfigurationSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub

	}
	
	public static void main(String args[]) throws Exception{
		TestopiaTestCase tc = new TestopiaTestCase(TESTOPIA_USER, TESTOPIA_PW, new URL(TESTOPIA_URL), null ); 
		User user = new User(TESTOPIA_USER, TESTOPIA_PW, TESTOPIA_USER, new URL(TESTOPIA_URL));
		
		int id = user.getAttributes();
		System.out.println(id);
		//tc.makeTestCase(id, 0, 0, true, 271, "This is a test of the testy test", 0);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("summary", "dfdfg");
		Object[] result = TestopiaTestCase.getList(TESTOPIA_USER, TESTOPIA_PW, new URL(TESTOPIA_URL), values);
		for (Object res: result){
			System.out.println(res.toString());
		}
	}

}
