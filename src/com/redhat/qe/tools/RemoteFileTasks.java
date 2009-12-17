package com.redhat.qe.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.auto.selenium.MyLevel;

public class RemoteFileTasks {
	
	protected static Logger log = Logger.getLogger(RemoteFileTasks.class.getName());

	public static int searchReplaceFile (SSHCommandRunner runner, String filePath, String sedSearch, String sedReplace) {
		return runCommand(runner, "sed -i 's/"+sedSearch+"/"+sedReplace+"/g' " + filePath, MyLevel.ACTION);
	}
	
	public static int grepFile (SSHCommandRunner runner, String filePath, String searchTerm) {
		return runCommand(runner, "grep -E '" + searchTerm + "' " + filePath, Level.INFO);
	}
	
	public static int runCommand(SSHCommandRunner runner, String command, Level loglevel){
		runner.reset();
		runner.setCommand(command);
		runner.run();
		log.log(loglevel, "Running Command (on hostname "+runner.getConnection().getHostname()+"): " + runner.getCommand());
		int returnCode = runner.waitFor();
		log.fine("Stdout: "+runner.getStdout());
		log.fine("Stderr: "+runner.getStderr());
		return returnCode;
	}
	
	public static int runAugeasCommand(SSHCommandRunner runner, String command, Level loglevel){
		return runCommand(runner, String.format("echo -e \"%s\nsave\n\" | augtool", command), loglevel);
	}

	public static int updateAugeasConfig(SSHCommandRunner runner, String augeusPath, String newValue){
		if (newValue == null)
			return runAugeasCommand(runner, String.format("rm %s", augeusPath), MyLevel.ACTION);
		else
			return runAugeasCommand(runner, String.format("set %s '%s'", augeusPath, newValue), MyLevel.ACTION);
	}
}
