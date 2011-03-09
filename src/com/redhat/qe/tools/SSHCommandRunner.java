package com.redhat.qe.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.redhat.qe.auto.testng.LogMessageUtil;
import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;

public class SSHCommandRunner implements Runnable {

	protected Connection connection;
	protected String user = null;
	

	protected Session session;
	protected InputStream out;
	protected static Logger log = Logger.getLogger(SSHCommandRunner.class.getName());


	protected InputStream err;
	protected String s_out = null;
	protected String s_err = null;
	protected boolean kill = false;
	protected String command = null;
	protected Object lock = new Object();
	

	public SSHCommandRunner(Connection connection,
			String command) {
		super();
		this.connection = connection;
		this.command = command;
	}
	
	
	public SSHCommandRunner(String server,
			String user,
			File sshPemFile,
			String passphrase,
			String command) throws IOException{
		super();
		Connection newConn = new Connection(server);
		newConn.connect();
		if (!newConn.authenticateWithPublicKey(user, sshPemFile, passphrase)) {
			throw new RuntimeException("Could not log in to " + newConn.getHostname() + " with the given credentials ("+user+").");
		}

		this.connection = newConn;
		this.user = user;
		this.command = command;
	}

	public SSHCommandRunner(String server,
			String user,
			String passphrase,
			File sshPemFile,
			String pemPassphrase,
			String command) throws IOException{
		super();
		Connection newConn = new Connection(server);
		newConn.connect();
		try {
			newConn.authenticateWithPublicKey(user, sshPemFile, pemPassphrase);
		}
		catch (IOException e) {
			//e.printStackTrace();
			newConn = new Connection(server);
			newConn.connect();
			if (!newConn.authenticateWithPassword(user, passphrase)) {
				throw new RuntimeException("Could not log in to " + newConn.getHostname() + " with the given credentials ("+user+").");
			}
		}

		this.connection = newConn;
		this.user = user;
		this.command = command;
	}

	public SSHCommandRunner(String server,
			String user,
			String sshPemFile,
			String passphrase,
			String command) throws IOException{
		this(server, user, new File(sshPemFile), passphrase, command);
	}

	public SSHCommandRunner(String server,
			String user,
			String passphrase,
			String sshPemFile,
			String pemPassphrase,
			String command) throws IOException{
		this(server, user, passphrase, new File(sshPemFile), pemPassphrase, command);
	}

	
	public void run(LogRecord logRecord) {
		try {
			if (logRecord == null) logRecord = LogMessageUtil.fine();
			
			/*
			 * Sync'd block prevents other threads from getting the streams before they've been set up here.
			 */
			synchronized (lock) {
//				log.info("SSH: Running '"+this.command+"' on '"+this.connection.getHostname()+"'");
				String message = "ssh "+ connection.getHostname()+ " " + command;
				if (this.user!=null) message = "ssh "+ user +"@"+ connection.getHostname()+" "+ command;
				logRecord.setMessage(message);
				log.log(logRecord);
				// sshSession.requestDumbPTY();
				session = connection.openSession();
				//session.startShell();
				session.execCommand(command);
				out = new StreamGobbler(session.getStdout());
				err = new StreamGobbler(session.getStderr());
			}

			

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void run() {
		run(LogMessageUtil.action());
	}
	
	public Integer waitFor(){
		return waitForWithTimeout(null);
	}
	
	/**
	 * @param timeoutMS - time out, in milliseconds
	 * @return null if command was interrupted or timedout, the command return code otherwise
	 */
	public Integer waitForWithTimeout(Long timeoutMS){
		/*getStderr();
		getStdout();*/
		//causes problem when another thread is reading the 'live' output.
		
		int res = 0;
		boolean timedOut = false;
		int cond = ChannelCondition.EXIT_STATUS | ChannelCondition.EOF;
		long startTime = System.currentTimeMillis();
		while (!kill && 
				((res & cond) != cond)){
			if (timeoutMS != null && System.currentTimeMillis() - startTime > timeoutMS) {
				timedOut = true;
				break;
			}
			res = session.waitForCondition(cond, 1000);
		}
		Integer exitCode = null;
		if (! (kill || timedOut))
			exitCode = getExitCode();
				
		session.close();

		kill=false;
		return exitCode;
	}
	
	public boolean isDone(){
		if (session == null)
			return false;
		return (getExitCode() != 0);
	}

	protected String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
	
	
	public SSHCommandResult getSSHCommandResult() {
		return new SSHCommandResult(getExitCode(),getStdout(),getStderr());
	}
	
	
	public Integer getExitCode() {
		return session.getExitStatus();
	}

	
	/**
	 * Consumes entire stdout stream of the command, this will block until the stream is closed.
	 * @return entire contents of stdout stream
	 */
	public String getStdout() {
		synchronized (lock) {
			if (s_out == null) s_out = convertStreamToString(out);
			return s_out;
		}
	}

	/**
	 * Consumes entire stderr stream of the command, this will block until the stream is closed.
	 * @return entire contents of stderr stream
	 */
	public String getStderr() {
		synchronized (lock) {
			if (s_err == null) s_err = convertStreamToString(err);
			return s_err;
		}
	}
	
	public void setCommand(String command) {
		reset();
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void runCommand(String command){
		runCommand(command,LogMessageUtil.fine());
	}
	
	public void runCommand(String command, LogRecord logRecord){
		reset();
		this.command = command;
		run(logRecord);
	}
	
	public SSHCommandResult runCommandAndWait(String command){
		return runCommandAndWait(command,null,LogMessageUtil.fine(), false);
	}
	
	public SSHCommandResult runCommandAndWait(String command, boolean liveLogOutput){
		return runCommandAndWait(command,null,LogMessageUtil.fine(), liveLogOutput);
	}
	
	public SSHCommandResult runCommandAndWait(String command, Long timeoutMS){
		return runCommandAndWait(command,timeoutMS,LogMessageUtil.fine(), false);
	}
	
	public SSHCommandResult runCommandAndWait(String command, LogRecord logRecord){
		return runCommandAndWait(command,null,logRecord, false);
	}
	
	/**
	 * @param command - the remote command to run
	 * @param timeoutMS - abort if command doesn't complete in this many milliseconds 
	 * 	(null means wait for command to complete, no matter how long it takes) 
	 * @param logRecord - a log record whose Level and Parameters will be used to do all
	 * the command output logging.  eg, a logRecord whose Level is INFO means log all the
	 * output at INFO level.  
	 * @param liveLogOutput - if true, log output as the command runs.  Good for long running
	 * commands, or commands that could potentially hang or timeout.  If false, don't log 
	 * any output until the command has finished running.
	 * @return the integer return code of the command
	 */ 
	public SSHCommandResult runCommandAndWait(String command, Long timeoutMS, LogRecord logRecord, boolean liveLogOutput){
		runCommand(command,logRecord);
		SplitStreamLogger logger = null;
		if (liveLogOutput){
			logger = new SplitStreamLogger(this);
			logger.log(logRecord.getLevel(), logRecord.getLevel());
		}
		waitForWithTimeout(timeoutMS);
		SSHCommandResult sshCommandResult = null;
		if (liveLogOutput) {
			s_out = logger.getStdout();
			s_err = logger.getStderr();
		}
		
		sshCommandResult = getSSHCommandResult();
		
		if (!liveLogOutput){
			String o = (this.getStdout().split("\n").length>1)? "\n":"";
			String e = (this.getStderr().split("\n").length>1)? "\n":"";
			log.log(logRecord.getLevel(), "Stdout: "+o+sshCommandResult.getStdout());
			log.log(logRecord.getLevel(), "Stderr: "+e+sshCommandResult.getStderr());
		}
		log.log(logRecord.getLevel(), "ExitCode: "+sshCommandResult.getExitCode());

		return sshCommandResult;
	}
	
	
	
	/**
	 * Stop waiting for the command to complete.
	 */
	public synchronized void kill(){
		kill= true;
	}
	
	public InputStream getStdoutStream() {		
		synchronized (lock) {
			return out;
		}
	}

	public InputStream getStdErrStream() {		
		synchronized (lock) {
			return err;
		}
	}
	
	public void reset(){
		try {
			if (out!= null) out.close();
			if (err != null) err.close();
			if (session!= null)session.close();			
		}
		catch(IOException ioe) {
			log.log(Level.FINER, "Couldn't close input stream", ioe);
		}
		s_out = null;
		s_err = null;
		command = null;
	}

	
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Runs a command via SSH as specified user, logs all output to INFO
	 * logging level, returns String[] containing stdout in 0 position
	 * and stderr in 1 position
	 * @param hostname hostname of system
	 * @param user user to execute command as
	 * @param command command to execute
	 * @return output as String[], stdout in 0 pos and stderr in 1 pos
	 */
	public static String[] executeViaSSHWithReturn(String hostname, 
			String user, String command){
		return executeViaSSHWithReturnWithTimeout(hostname,
				user,
				command,
				null);
	}
	
	/**
	 * Runs a command via SSH as specified user, logs all output to INFO
	 * logging level, returns String[] containing stdout in 0 position
	 * and stderr in 1 position
	 * @param hostname hostname of system
	 * @param user user to execute command as
	 * @param command command to execute
	 * @param timeout amount of time to wait for command completion, in seconds
	 * @return output as String[], stdout in 0 pos and stderr in 1 pos
	 */
	public static String[] executeViaSSHWithReturnWithTimeout(String hostname,
			String user, String command, Long timeoutMS){
		SSHCommandRunner runner = null;
		SplitStreamLogger logger;

//		log.info("SSH: Running '"+command+"' on '"+hostname+"'"); // moved log.info into run() method - jsefler 1/4/2010
		try{
			runner = new SSHCommandRunner(hostname,
					user,
					new File(System.getProperty("user.dir")+"/.ssh/id_auto_dsa"),
					System.getProperty("jon.server.sshkey.passphrase"),command);
			runner.run();
			logger = new SplitStreamLogger(runner);
			logger.log();
			Integer exitcode = runner.waitForWithTimeout(timeoutMS);
			
			if (exitcode == null){
				log.log(Level.INFO, "SSH command did not complete within timeout window");
				return failSSH();
			}
		}
		catch (Exception e){
			log.log(Level.INFO, "SSH command failed:", e);
			return failSSH();
		}
		return new String[] {logger.getStdout(), logger.getStderr()};
	}
	
	private static String[] failSSH(){
		return new String[] {"fail", "fail"};
	}
	


	/**
	 * Test code
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		/*Connection conn = new Connection("jweiss-rhel3.usersys.redhat.com");
		conn.connect();
		if (!conn.authenticateWithPassword("jonqa", "dog8code"))
			throw new IllegalStateException("Authentication failed.");
		SSHCommandRunner runner = new SSHCommandRunner(conn, "sleep 3");
		runner.run();
		Integer exitcode = runner.waitForWithTimeout(null);
		System.out.println("exit code: " + exitcode);*/
		

		Logger log = Logger.getLogger(SSHCommandRunner.class.getName());
		SSHCommandRunner scr = new SSHCommandRunner("f14-1.usersys.redhat.com", "root", "dog8code", "sdf", "sdfs", null);
		scr.runCommandAndWait("sleep 5;echo 'hi there';sleep 3", true);
		System.out.println("Result: " + scr.getStdout());
		
		/*SCPClient scp = new SCPClient(conn);
		scp.put(System.getProperty("user.dir")+ "/../jon/bin/DummyJVM.class", "/tmp");
		SSHCommandRunner jrunner = new SSHCommandRunner(conn, "java -Dcom.sun.management.jmxremote.port=1500 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -cp /tmp DummyJVM");

		jrunner.run();

		new SplitStreamLogger(jrunner).log();
		
		
		Thread.sleep(10000);
		SSHCommandRunner runner = new SSHCommandRunner(conn, "ps -ef | grep [D]ummy | awk '{print $2}'");
		runner.run();
		String pid = runner.getStdout().trim();
		log.info("Found pid " + pid);
		runner = new SSHCommandRunner(conn, "kill " + pid);
		runner.run();
		
		
		new SplitStreamLogger(runner).log();
		runner.waitFor();
		jrunner.waitFor();*/
		
		
		/*SSHCommandRunner jrunner = new SSHCommandRunner(conn, "grep sdf /tmp/sdsdfs");

		jrunner.run();
		System.out.println(jrunner.waitFor());*/
	/*	System.out.println("Output: " + runner.getStdout());
		System.out.println("Stderr: " + runner.getStderr());*/

	}

	

}
