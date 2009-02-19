package com.redhat.qe.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;

public class SSHCommandRunner implements Runnable {

	protected boolean isDone = false;
	protected Connection connection;
	protected Session session;
	protected InputStream out;
	protected InputStream err;
	protected String s_out = null;
	protected String s_err = null;
	protected static Logger log = Logger.getLogger(SSHCommandRunner.class.getName());
	protected boolean kill = false;
	protected String command = null;

	public SSHCommandRunner(Connection connection, String command) {
		super();
		this.connection = connection;
		this.command = command;
	}
	
	public SSHCommandRunner(String server,
							String user,
							File sshPemFile,
							String password,
							String command) throws Exception{
		super();
		Connection newConn = new Connection(server);
		newConn.connect();
		newConn.authenticateWithPublicKey(user, sshPemFile, password);
		this.connection = newConn;
		this.command = command;
	}
	
	public boolean isDone(){
		return isDone;
	}
	
	public void run() {
		isDone=false;
		try {
			
			// sshSession.requestDumbPTY();
			session = connection.openSession();
			//session.startShell();
			session.execCommand(command);
			out = new StreamGobbler(session.getStdout());
			err = new StreamGobbler(session.getStderr());
			int res = 0;
			while (!kill && ((res & ChannelCondition.EOF) == 0)){
			 res = session.waitForCondition(ChannelCondition.EOF, 1000);
			 
			}

			s_out = convertStreamToString(out);
			log.log(Level.FINER, "Command output: " + s_out);
			s_err = convertStreamToString(err);
			log.log(Level.FINER, "Command stderr: " + s_err);
			session.close();
			kill=false;
			isDone=true;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	public String getStdout() {
		return s_out;
	}

	public String getStderr() {
		return s_err;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public synchronized void kill(){
		kill= true;
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
			LogManager.getLogManager().readConfiguration(new FileInputStream(fn));
			log.fine("Loaded logger configuration.");
		} catch (Exception e) {
			System.err.println("Failed to load log settings.");
			e.printStackTrace();
			log.log(Level.WARNING,
					"Unable to read logging settings.  Keeping default.", e);
		}
		/*SSHCommandRunner runner = new SSHCommandRunner("witte.usersys.redhat.com", "jonqa", "dog8code", "java -Dcom.sun.management.jmxremote.port=1500 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -cp /tmp DummyJVM");
		Connection conn =runner.connect();*/
		//String jh = runner.getStdout().trim();
		Connection conn = new Connection("witte.usersys.redhat.com");
		conn.connect();
		if (!conn.authenticateWithPassword("jonqa", "dog8code"))
			throw new IllegalStateException("Authentication failed.");
		SCPClient scp = new SCPClient(conn);
		scp.put(System.getProperty("user.dir")+ "/bin/DummyJVM.class", "/tmp");
		SSHCommandRunner runner = new SSHCommandRunner(conn, "java -Dcom.sun.management.jmxremote.port=1500 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -cp /tmp DummyJVM");
		Thread t = new Thread(runner);
		t.start();

		
		Thread.sleep(10000);
		runner = new SSHCommandRunner(conn, "ps -ef | grep [D]ummy | awk '{print $2}'");
		runner.run();
		String pid = runner.getStdout().trim();
		runner = new SSHCommandRunner(conn, "kill " + pid);
		runner.run();
		Thread.sleep(1000);

		runner.kill();
		t.join();
	/*	System.out.println("Output: " + runner.getStdout());
		System.out.println("Stderr: " + runner.getStderr());*/

	}

	

}
