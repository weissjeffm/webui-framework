package com.redhat.qe.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.redhat.qe.auto.testng.LogMessageUtil;
import com.redhat.qe.auto.testopia.Assert;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;

public class RemoteFileTasks {
	protected static Logger log = Logger.getLogger(RemoteFileTasks.class.getName());
	public static final String stdoutFile	= "/tmp/stdout";
	public static final String stderrFile	= "/tmp/stderr";


	/**
	 * Create a file on a remote machine with given contents
	 * @param conn - A connection object already created to connect to ssh server
	 * @param filePath - path to the file you want to create (including dir and filename)
	 * @param contents - contents of the file you want to create
	 * @throws IOException
	 * @author jweiss
	 */
	public static void createFile(Connection conn, String filePath, String contents, String mode) throws IOException  {
		String dir = new File(filePath).getParent();
		String fn =  new File(filePath).getName();
		
		log.log(Level.INFO, "Creating " + fn + " in " + dir + " on " + conn.getHostname(), LogMessageUtil.Style.Action);
		SCPClient scp = new SCPClient(conn);
		scp.put(contents.getBytes(), fn, dir, mode);
	}

	public static void createFile(Connection conn, String filePath, String contents) throws IOException  {
		createFile(conn, filePath, contents, "0755");
	}
	
	/**
	 * Use echo to create a file with the given contents.  Then use chmod to give permissions to the file.
	 * @param runner
	 * @param filePath - absolute path to the file create
	 * @param contents - contents of the file
	 * @param perms - optional chmod options to apply to the filePath (e.g. "a+x")
	 * @return - exit code
	 * @author jsefler
	 */
	public static int createFile(SSHCommandRunner runner, String filePath, String contents, String perms) {
		int exitCode = runCommandAndWait(runner, "echo -n -e '"+contents+"' > "+filePath, LogMessageUtil.action());
		if (exitCode==0 && perms!=null) exitCode = runCommandAndWait(runner, "chmod "+perms+" "+filePath, LogMessageUtil.action());
		return exitCode;
	}
	
	/**
	 * Copy file(s) onto a remote machine 
	 * @param conn - A connection object already created to connect to ssh server
	 * @param destDir -  path where the file(s) should go on the remote machine (must be dir)
	 * @param source - one or more paths to the file(s) you want to copy to the remote dir
	 * @throws IOException
	 * @author jweiss
	 */
	public static void copyFiles(Connection conn, String destDir, String... sources ) throws IOException  {
		for (String source: sources)
			log.log(Level.INFO, "Copying " + source + " to " + destDir + " on " + conn.getHostname(), LogMessageUtil.Style.Action);
		SCPClient scp = new SCPClient(conn);
		scp.put(sources, destDir);
	}
	
	/**
	 * @param conn - A connection object already created to connect to ssh server
	 * @param source - path to the file you want to copy
	 * @param dest - full path to the destination where you want the file to go 
	 * 	(if path ends in trailing slash, it's assumed to be a dir, and the source filename is used) 
	 * @throws IOException
	 * @author jweiss
	 */
	public static void copyFile(Connection conn, String source, String dest) throws IOException  {
		log.log(Level.INFO, "Copying " + source + " to " + dest + " on " + conn.getHostname(), LogMessageUtil.Style.Action);
		SCPClient scp = new SCPClient(conn);
		if (dest.endsWith("/")) {
			scp.put(new String[] {source}, null, dest, "0755");
		}
		else {
			String destDir = new File(dest).getParentFile().getCanonicalPath();
			String destFile = new File(dest).getName();
			scp.put(new String[] {source}, new String[] {destFile}, destDir, "0755");
		}
	}
	
	/**
	 * Use sed to search and replace content within a file.<br>
	 * sed -i 's/regexp/replacement/g' filePath
	 * @param runner
	 * @param filePath - absolute path to the file to be searched and replaced
	 * @param regexp - the regular expression used to match a pattern for replacement
	 * @param replacement - the replacement content
	 * @return - exit code from sed
	 */
	public static int searchReplaceFile (SSHCommandRunner runner, String filePath, String regexp, String replacement) {
		return runCommandAndWait(runner, "sed -i 's/"+regexp+"/"+replacement+"/g' " + filePath, LogMessageUtil.action());
	}
	
	/**
	 * Use grep to search for the existence of an extended regular expression within a file.<br>
	 * grep -E 'searchTerm' filePath
	 * @param runner
	 * @param filePath - absolute path to the file to be searched
	 * @param pattern - an  extended  regular  expression (man grep for help)
	 * @return - exit code from grep
	 */
	public static int grepFile (SSHCommandRunner runner, String filePath, String pattern) {
		return runCommandAndWait(runner, "grep -E '" + pattern + "' " + filePath, LogMessageUtil.info());
	}
	
	/**
	 * Use sed to delete lines from a file.<br>
	 * sed -i '/containingText/d' filePath
	 * @param runner
	 * @param filePath - absolute path to the file from which lines will be deleted
	 * @param containingText - delete lines containing a match to this text
	 * @return - exit code from sed
	 * @author jsefler
	 */
	public static int deleteLines (SSHCommandRunner runner, String filePath, String containingText) {
		return runCommandAndWait(runner, "sed -i '/"+containingText+"/d' " + filePath, LogMessageUtil.action());
	}
	
	/**
	 * Test for the existence of a file.<br>
	 * test -e filePath && echo 1 || echo 0
	 * @param runner
	 * @param filePath - absolute path to the file to test for existence
	 * @return 1 (file exists), 0 (file does not exist), -1 (could not determine existence)
	 * @author jsefler
	 */
	public static int testFileExists (SSHCommandRunner runner, String filePath) {
		runCommandAndWait(runner, "test -e "+filePath+" && echo 1 || echo 0", LogMessageUtil.info());
		if (runner.getStdout().trim().equals("1")) return 1;
		if (runner.getStdout().trim().equals("0")) return 0;
		return -1;
	}
	
	public static int runCommandAndWait(SSHCommandRunner runner, String command, LogRecord logRecord){
		return runner.runCommandAndWait(command,logRecord);
		//return runner.runCommandAndWait(command,Long.valueOf(30000),logRecord);	// timeout after 30 sec
	}
	
	public static int runAugeasCommand(SSHCommandRunner runner, String command, LogRecord logRecord){
		return runCommandAndWait(runner, String.format("echo -e \"%s\nsave\n\" | augtool", command), logRecord);
	}

	public static int updateAugeasConfig(SSHCommandRunner runner, String augeusPath, String newValue){
		if (newValue == null)
			return runAugeasCommand(runner, String.format("rm %s", augeusPath), LogMessageUtil.action());
		else
			return runAugeasCommand(runner, String.format("set %s '%s'", augeusPath, newValue), LogMessageUtil.action());
	}
	
	
	/**
	 * Use the sshCommandRunner to execute the given command and verify the output
	 * contains an expected grep expression.  Moreover, the stdout and stderr strings are
	 * redirected to this.stdoutFile and this.stderrFile which you can subsequently
	 * use for further post processing before the next call to runCommandAndAssert(...).
	 * @param command - command to execute with options
	 * @param stdoutGrepExpression - if !null, stdout is asserted to contain a match to this grep expression
	 * @param stderrGrepExpression - if !null, stderr is asserted to contain a match to this grep expression
	 * @param expectedExitCode - expected exit code from the command (usually 0 on success, non-0 on failure)
	 * @author jsefler
	 */
	public static void runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, String stdoutGrepExpression, String stderrGrepExpression, int expectedExitCode) {

		//String runCommand = String.format("(%s | tee %s) 3>&1 1>&2 2>&3 | tee %s", command, stdoutFile, stderrFile);	// the problem with this is that the exit code is lost
		String runCommand = String.format("%s 1>%s 2>%s", command, stdoutFile, stderrFile);
		int exitCode = sshCommandRunner.runCommandAndWait(runCommand);
		if (exitCode!=expectedExitCode) {
			sshCommandRunner.runCommandAndWait("echo 'Stdout from: "+command+"'; cat "+stdoutFile);	// cheap way to log stdoutFile
			sshCommandRunner.runCommandAndWait("echo 'Stderr from: "+command+"'; cat "+stderrFile);	// cheap way to log stderrFile		
		}
		Assert.assertEquals(exitCode,expectedExitCode);
		if (stdoutGrepExpression!=null) {
			sshCommandRunner.runCommandAndWait("echo 'Stdout from: "+command+"'; cat "+stdoutFile);	// cheap way to log stdoutFile
			Assert.assertEquals(RemoteFileTasks.grepFile(sshCommandRunner, stdoutFile, stdoutGrepExpression),0,"Stdout contains a match grepping for extended regular expression '"+stdoutGrepExpression+"' (0 means match)");
		}
		if (stderrGrepExpression!=null) {
			sshCommandRunner.runCommandAndWait("echo 'Stderr from: "+command+"'; cat "+stderrFile);	// cheap way to log stderrFile
			Assert.assertEquals(RemoteFileTasks.grepFile(sshCommandRunner, stderrFile, stderrGrepExpression),0,"Stderr contains a match grepping for extended regular expression '"+stderrGrepExpression+"' (0 means match)");
		}

	}
}
