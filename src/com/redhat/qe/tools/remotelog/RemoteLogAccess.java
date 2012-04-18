package com.redhat.qe.tools.remotelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.tools.SSHCommandRunner;

/**
 * this class provides access to any remote log file using SSH
 * @author lzoubek@redhat.com
 *
 */
public class RemoteLogAccess {
	
	protected static final Logger log = Logger.getLogger(RemoteLogAccess.class.getName());
	private final String logFile;
	private SSHCommandRunner client;
	private SSHCommandRunner backgroundClient;
	private int startLine = -1;
	private final String user;
	private final String host;
	private final String pass;
	private String filter = null;
	/**
	 * creates new instance of RemoteLogAccess
	 * @throws IOException 
	 */
	public RemoteLogAccess(String user, String host, String pass, String logFile) throws IOException {
		this.logFile = logFile;
		this.host = host;
		this.user = user;
		this.pass = pass;
		log.fine("Creating "+toString());
		this.client = new SSHCommandRunner(host, user, pass, "");
		this.backgroundClient = new SSHCommandRunner(host, user, pass, "");		
	}

	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getLogfile() {
		return logFile;
	}
	/**
	 * checks whether agent.log file exists
	 * @return
	 */
	public boolean existsLogFile() {
		return existsFile(getLogfile());
	}
	private boolean existsFile(String file) {
		return client.runCommandAndWait("ls "+file).getExitCode().equals(0);
	}
	private int getLineNumbers(String file) {
		String line = client.runCommandAndWait("cat "+file+" | wc -l").getStdout();
		try 
		{
			return Integer.parseInt(line.trim());
		}
		catch (Exception ex) {
			throw new RuntimeException("Unable to parse line number count of agent log "+getLogfile(),ex);
		}
	}
	/**
	 * starts watching log file {@link RemoteLogAccess#getLogfile()}
	 */
	public void watch() {
		this.startLine = getLineNumbers(getLogfile());
	}
	public void disconnect() {
		client.getConnection().close();
		backgroundClient.getConnection().close();
	}
	/**
	 * returns content of <b>agent.log</b> since {@link RemoteLogAccess#watch()} 
	 * or {@link RemoteLogAccess#getContent()} was called. Note that calling this also calls {@link RemoteLogAccess#watch()}
	 * so you get only appended content
	 * @return
	 */
	public String getContent() {
		return getContent(null);
	}
	/**
	 * returns content of <b>agent.log</b> since {@link RemoteLogAccess#watch()} 
	 * or {@link RemoteLogAccess#getContent()} was called. Note that calling this also calls {@link RemoteLogAccess#watch()}
	 * so you get only appended content
	 * @param grep expression to filter results, if null, grep is not used at all
	 * @return
	 */
	public String getContent(String grep) {
		if (this.startLine<0) {
			return "";
		}
		String grepCmd = "";
		if (grep!=null) {
			grepCmd = " | grep "+grep;
		}
		int current = getLineNumbers(getLogfile());
		int lines = current - this.startLine;		
		if (lines==0) {
			this.startLine = -1;
			return "";
		}
		if (lines>0) {
			this.startLine = -1;
			return client.runCommandAndWait("tail -n "+lines+" "+getLogfile()+grepCmd).getStdout();
		}
		else {
			// it seems'like log files was rotated, we'll return tail of agent.log.1 + whole agent.log
			String rotatedLog = getLogfile()+".1";
			StringBuilder sb = new StringBuilder();
			if (existsFile(rotatedLog)) {
				current = getLineNumbers(rotatedLog);
				lines = current - this.startLine;
				if (lines>0) {
					sb.append(client.runCommandAndWait("tail -n "+lines+" "+rotatedLog+grepCmd).getStdout());
				}				
			}
			sb.append(client.runCommandAndWait("cat "+getLogfile()+grepCmd).getStdout());
			this.startLine = -1;
			return sb.toString();
		}
	}
	/**
	 * @param filter expression passed to <b>grep</b> tool
	 * @return lines from file {@link RemoteLogAccess#getLogfile()} matching <b>grep</b> param expression since {@link RemoteLogAccess#watch()} was called
	 */
	public List<String> lines(String grep) {
		String content = getContent(grep).trim();
		String[] lines = content.split("\n");
		List<String> ret = new ArrayList<String>();
		for (String line : lines) {
			if (line.length()>0) {
				ret.add(line);
			}
		}
		return ret;
	}

	/**
	 * 
	 * @return lines from file {@link RemoteLogAccess#getLogfile()} matching expression {@link RemoteLogAccess#getFilter()} since {@link RemoteLogAccess#watch()} was called
	 */
	public List<String> filteredLines() {
		return lines(getFilter());
	}
	/**
	 * this method enables <b>agent.log</b> messages being redirected to this logger with level {@link Level#FINE} and prefix <b>[agent]</b>
	 * this starts separate SSH session and background thread - so its completely independent from other methods of this class
	 */
	public void startRedirectingLog() {
		startRedirectingLog("[agent]",Level.FINE);
	}
	/**
	 * this method enables all log messages from {@link RemoteLogAccess#getLogfile()} being redirected to this/current logger
	 * this starts separate SSH session and background thread - so its completely independent from other methods of this class
	 * @param level of agent messages
	 * @param prefix of each message line
	 */
	public void startRedirectingLog(final String prefix, final Level level) {
		this.backgroundClient.runCommand("tail -f "+getLogfile());	
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				InputStream is = backgroundClient.getStdoutStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						log.log(level,prefix+" "+line);
					}
					Thread.currentThread().join(200);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}});
		thread.start();
		log.fine("Redirecting enabled for log "+toString());
	}
	/**
	 * this method disables agent.log messages being redirected to classic logger
	 * @throws IOException 
	 */
	public void stopRedirectingLog() throws IOException {
		this.backgroundClient.reset();
		this.backgroundClient = new SSHCommandRunner(host, user, pass, "");
		log.fine("Redirecting disabled for "+toString());
	}
	@Override
	public String toString() {
		return "["+user+"@"+host+":"+getLogfile()+"]";
	}
}
