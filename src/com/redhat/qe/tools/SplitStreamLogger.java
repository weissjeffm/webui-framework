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

	protected InputStream stdout;
	protected InputStream stderr;
	protected static Logger log = Logger.getLogger(SplitStreamLogger.class.getName());

	public SplitStreamLogger(SSHCommandRunner runner){
		this.stdout = runner.getStdoutStream();
		this.stderr = runner.getStdErrStream();
	}
	
	public SplitStreamLogger(InputStream stdout, InputStream stderr){
		this.stdout = stdout;
		this.stderr = stderr;
	}
	
	public void log(Level outlevel, Level errlevel){

		Thread out = new Thread(new StreamLogger(stdout, outlevel));
		Thread err = new Thread(new StreamLogger(stderr, errlevel));
		out.start();
		err.start();

	}
	
	
	public void log(){
		log(Level.INFO, Level.SEVERE);
	}
		
	class StreamLogger implements Runnable{
		protected InputStream stream;
		protected Level level;
		public StreamLogger(InputStream stream, Level level){
			this.stream = stream;
			this.level = level;
		}
		
		public void run(){
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			try {
				while ((line = reader.readLine()) != null){
					log.log(level, line);
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
