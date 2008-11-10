/**
 * 
 */
package com.redhat.qe.auto.testopia;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import testopia.API.Build;
import testopia.API.Environment;
import testopia.API.Product;
import testopia.API.Session;
import testopia.API.TestCaseRun;
import testopia.API.TestPlan;
import testopia.API.TestRun;
import testopia.API.TestopiaException;

import com.redhat.qe.auto.selenium.LogFormatter;

/**
 * @author jweiss
 *
 */
public class TestopiaTestNGListener implements IResultListener {

	private static final String TESTOPIA_PW = "2$(w^*@&J";
	private static final String TESTOPIA_USER = "jweiss@redhat.com";
	private static final String TESTOPIA_URL = "https://testopia-01.lab.bos.redhat.com/bugzilla/tr_xmlrpc.cgi";
	protected TestProcedureHandler tph = null;
	protected static Logger log = Logger.getLogger(TestopiaTestNGListener.class.getName());
	protected static TestRun testrun;
	protected static Product product;
	protected static Build build;
	protected static Environment environment;
	protected static TestPlan testplan;
	protected static Session session;
	
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
		//create new test run
		String testname = "Run of suite " + arg0.getSuite().getName();
		try {
			loginTestopia();
			retrieveContext();
			testrun = new TestRun(session, 
					testplan.getId(),
					environment.getId(), 
					build.getId(), 
					session.getUserid(), 
					testname);

		} catch(Exception e){
			log.severe("Could not log in to testopia!  Aborting!");
			TestopiaException te=new TestopiaException("Failed to log in to testopia");
			te.initCause(e);
			throw te;
		}
		
		
		
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
		//create new testcaserun
		String testname = arg0.getMethod().getMethodName();
		TestCaseRun testcaserun = new TestCaseRun(session, testrun.getId());

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult result) {
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
		String testName = result.getName();
		
		
		//reset the handler
		((TestProcedureHandler)tph).reset();
		
		//also add the test run
		

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
	
	//FIXME this is just temporary for testing
	private static void setLogConfig(){
		Logger.getLogger("").setLevel(Level.ALL);
		Logger.getLogger("").getHandlers()[0].setFormatter(new LogFormatter());
		Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
		log.info("Hello");
	}
	
	protected void loginTestopia() throws XmlRpcException, GeneralSecurityException, IOException{
		setLogConfig();
		log.finer("Testing log setting.");
		session = new Session(TESTOPIA_USER, TESTOPIA_PW, new URL(TESTOPIA_URL));
		session.login();

	}
	
	protected void retrieveContext() throws XmlRpcException{
		product = new Product(session);
		testplan = new TestPlan(session, "Acceptance");
		build = new Build(session, product.getId());
		Integer buildID = build.getBuildIDByName("2.2 CR1");
		environment = new Environment(session, product.getId(), null);
		Integer envId = environment.getEnvironemntIDByName("Windows+Postgres");
		/*HashMap<String,Object> trinst= (HashMap<String, Object>) tr.create();
		TestCaseRun tcr = new TestCaseRun(session,
										  (Integer)trinst.get("run_id"),
										  2948,
										  buildID,
										  envId);
		tcr.create();*/

	}
	public static void main(String args[]) throws Exception{
		setLogConfig();
		log.finer("Testing log setting.");
		Session session = new Session(TESTOPIA_USER, TESTOPIA_PW, new URL(TESTOPIA_URL));
		session.login();
		/*//tc.makeTestCase(id, 0, 0, true, 271, "This is a test of the testy test", 0);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("summary", "dfdfg");
		Object[] result = new TestopiaTestCase(session, 0).getList(values);
		for (Object res: result){
			System.out.println(res.toString());
		}
		TestCaseRun tcr = new TestCaseRun(session, 2935, 1, 1, 1, 1);
		tcr.makeTestCaseRun(1, 1);
		tcr.setNotes("RICK ASTLEY");
		tcr.setStatus(2);
		tcr.update();*/
		
		/*TestCase tc2 = new TestCase(session, "PROPOSED", "--default--", "P1", "what up dude", "Acceptance", "JBoss ON");
		tc2.setIsAutomated(true);
		tc2.create();
		tc2.setPriorityID("P2");
		tc2.update();
		tc2.update();*/
		
		
		//TestRun tcr = new TestRun(session, 2948, "2.2 CR1", "Windows + Postgres" );
		
		//tcr.create();
		
		//TestCaseRun tcr = new TestCaseRun(session, 2935, )
		Product prod = new Product(session);
		Integer prodId = prod.getProductIDByName("JBoss ON");
		TestPlan tp = new TestPlan(session, "Acceptance");
		Integer plan = tp.getId();
		Build bu = new Build(session, prodId);
		Integer build = bu.getBuildIDByName("2.2 CR1");
		Environment env = new Environment(session, prodId, null);
		Integer envId = env.getEnvironemntIDByName("Windows+Postgres");
		TestRun tr = new TestRun(session, plan, envId, build, session.getUserid(), "Test");
		HashMap<String,Object> trinst= (HashMap<String, Object>) tr.create();
		TestCaseRun tcr = new TestCaseRun(session,
										  (Integer)trinst.get("run_id"),
										  2948,
										  build,
										  envId);
		tcr.create();
	}

}
