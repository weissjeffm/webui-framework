package com.redhat.qe.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.redhat.qe.auto.selenium.LogMessageUtil;
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
			String sshPemFile,
			String passphrase,
			String command) throws IOException{
		this(server, user, new File(sshPemFile), passphrase, command);
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
		run(LogMessageUtil.fine());
	}
	
	public int waitFor(){
		/*getStderr();
		getStdout();*/
		//causes problem when another thread is reading the 'live' output.
		
		int res = 0;
		while (!kill && ((res & ChannelCondition.EXIT_STATUS) == 0)){
		 res = session.waitForCondition(ChannelCondition.EXIT_STATUS, 1000);
		 
		}
		
		int exitCode = session.getExitStatus();
		session.close();

		kill=false;
		//FIXME these log statements need to be moved where they wont cause this error (wdh 1/5/09): java.io.IOException: This StreamGobbler is closed.
	//	log.fine("ssh: Stdout: "+this.getStdout());
	//	log.fine("ssh: Stderr: "+this.getStderr());
		return exitCode;
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
		reset();
		this.command = command;
		run();
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
			runner.waitFor();  
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
		try {
			String autoProp = System.getProperty("automation.dir");
			String fn ="";
			if (autoProp != null) {
				fn = autoProp + File.separator + "log.properties";
			}
			else {
				 String autoSubdir = System.getProperty("auto.subdir", "");
				 fn = System.getProperty("user.dir") + File.separator + autoSubdir + File.separator + "log.properties";
			}
			//LogManager.getLogManager().readConfiguration(new FileInputStream(fn));
			//log.fine("Loaded logger configuration.");
		} catch (Exception e) {
			System.err.println("Failed to load log settings.");
			e.printStackTrace();
			//log.log(Level.WARNING,
			//		"Unable to read logging settings.  Keeping default.", e);
		}
		/*SSHCommandRunner runner = new SSHCommandRunner("witte.usersys.redhat.com", "jonqa", "dog8code", "java -Dcom.sun.management.jmxremote.port=1500 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -cp /tmp DummyJVM");
		Connection conn =runner.connect();*/
		//String jh = runner.getStdout().trim();
		Connection conn = new Connection("jweiss-rhel2.usersys.redhat.com");
		conn.connect();
		if (!conn.authenticateWithPassword("jonqa", "dog8code"))
			throw new IllegalStateException("Authentication failed.");
		/*SCPClient scp = new SCPClient(conn);
		scp.put(System.getProperty("user.dir")+ "/../jon-2.0/bin/DummyJVM.class", "/tmp");
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
		
		
		SSHCommandRunner jrunner = new SSHCommandRunner(conn, "grep sdf /tmp/sdsdfs");

		jrunner.run();
		System.out.println(jrunner.waitFor());
	/*	System.out.println("Output: " + runner.getStdout());
		System.out.println("Stderr: " + runner.getStderr());*/

	}

	

}
