package com.redhat.qe.tools;

import java.util.logging.Logger;

import com.redhat.qe.auto.selenium.MyLevel;

public class RemoteFileTasks {
	
	protected static Logger log = Logger.getLogger(RemoteFileTasks.class.getName());

	public static int editFile (SSHCommandRunner runner, String filePath, String sedSearch, String sedReplace) {
		runner.reset();
		runner.setCommand("sed -i 's/"+sedSearch+"/"+sedReplace+"/g' " + filePath);
		runner.run();
		log.log(MyLevel.ACTION, "Running Command (on hostname "+runner.getConnection().getHostname()+"): " + runner.getCommand());
		int returnCode = runner.waitFor();
		log.fine("Stdout: "+runner.getStdout());
		log.fine("Stderr: "+runner.getStderr());
		return returnCode;
	}
	
	public static int grepFile (SSHCommandRunner runner, String filePath, String searchTerm) {
		runner.reset();
		runner.setCommand("grep " + searchTerm + " " + filePath);
		runner.run();
		log.info("Running Command (on hostname "+runner.getConnection().getHostname()+"): " + runner.getCommand());
		int returnCode = runner.waitFor();
		log.fine("Stdout: "+runner.getStdout());
		log.fine("Stderr: "+runner.getStderr());
		return returnCode;
	}

}
