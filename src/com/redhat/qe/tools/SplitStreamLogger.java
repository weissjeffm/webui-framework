package com.redhat.qe.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs the output of an SSH command
 * @author weissj
 *
 */
public class SplitStreamLogger {

	protected SSHCommandRunner runner=null;
	protected InputStream stdout;
	protected InputStream stderr;
	protected static Logger log = Logger.getLogger(SplitStreamLogger.class.getName());

	StreamLogger sl_out;
	StreamLogger sl_err;
	
	public SplitStreamLogger(SSHCommandRunner runner){
		this.runner = runner;
		this.stdout = runner.getStdoutStream();
		this.stderr = runner.getStdErrStream();
	}
	
	public SplitStreamLogger(InputStream stdout, InputStream stderr){
		this.stdout = stdout;
		this.stderr = stderr;
	}
	
	public void log(Level outlevel, Level errlevel){
		sl_out = new StreamLogger(stdout, outlevel,"Stdout");
		sl_err = new StreamLogger(stderr, errlevel,"Stderr");
		Thread out = new Thread(sl_out);
		Thread err = new Thread(sl_err);
		out.start();
		err.start();
	}
	
	public String getStdout(){
		return sl_out.toString();
	}
	
	public String getStderr(){
		return sl_err.toString();
	}
	
	public void log(){
		log(Level.INFO, Level.SEVERE);
	}
		
	class StreamLogger implements Runnable{
		protected String name;
		protected InputStream stream;
		protected Level level;
		protected StringBuffer sb = new StringBuffer();
		
		public StreamLogger(InputStream stream, Level level, String name){
			this.name = name;
			this.stream = stream;
			this.level = level;
		}
		
		public String toString(){
			synchronized (sb) {
				return sb.toString();
			}
		}
		
		public void run(){
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			try {
				while ((line = reader.readLine()) != null){
					synchronized (sb) {
						sb.append(line + "\n");
						if (runner!=null)	log.log(level, String.format("[%s@%s] %s: %s", runner.user,runner.getConnection().getHostname(),name,line));
						else				log.log(level, String.format("%s: %s", name,line));
					}		
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
