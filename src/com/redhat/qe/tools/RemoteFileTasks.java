package com.redhat.qe.tools;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.redhat.qe.auto.selenium.LogMessageUtil;

public class RemoteFileTasks {
	protected static Logger log = Logger.getLogger(RemoteFileTasks.class.getName());

	
	
	public static int searchReplaceFile (SSHCommandRunner runner, String filePath, String sedSearch, String sedReplace) {
		return runCommand(runner, "sed -i 's/"+sedSearch+"/"+sedReplace+"/g' " + filePath, LogMessageUtil.action());
	}
	
	public static int grepFile (SSHCommandRunner runner, String filePath, String searchTerm) {
		return runCommand(runner, "grep -E '" + searchTerm + "' " + filePath, LogMessageUtil.info());
	}
	
	/**
	 * Use sed to delete lines from a file.
	 * @param runner
	 * @param filePath
	 * @param containingText - delete lines containing a match to this text
	 * @return - exit code from sed
	 * @author jsefler
	 */
	public static int deleteLines (SSHCommandRunner runner, String filePath, String containingText) {
		return runCommand(runner, "sed -i '/"+containingText+"/d' " + filePath, MyLevel.ACTION);
	}
	
	/**
	 * Test for the existence of a file.
	 * @param runner
	 * @param filePath - full path to the file
	 * @return 1 (file exists), 0 (file does not exist), -1 (could not determine existence)
	 * @author jsefler
	 */
	public static int testFileExists (SSHCommandRunner runner, String filePath) {
		runCommand(runner, "test -e "+filePath+" && echo 1 || echo 0", LogMessageUtil.info());
		if (runner.getStdout().trim().equals("1")) return 1;
		if (runner.getStdout().trim().equals("0")) return 0;
		return -1;
	}
	
	public static int runCommand(SSHCommandRunner runner, String command, LogRecord logRecord){
		runner.reset();
		runner.setCommand(command);
		runner.run(logRecord);
		int returnCode = runner.waitFor();
		return returnCode;
	}
	
	public static int runAugeasCommand(SSHCommandRunner runner, String command, LogRecord logRecord){
		return runCommand(runner, String.format("echo -e \"%s\nsave\n\" | augtool", command), LogMessageUtil.action());
	}

	public static int updateAugeasConfig(SSHCommandRunner runner, String augeusPath, String newValue){
		if (newValue == null)
			return runAugeasCommand(runner, String.format("rm %s", augeusPath), LogMessageUtil.action());
		else
			return runAugeasCommand(runner, String.format("set %s '%s'", augeusPath, newValue), LogMessageUtil.action());
	}
}
