package com.redhat.qe.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.auto.testng.LogMessageUtil;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;

public class RemoteFileTasks {
	protected static Logger log = Logger.getLogger(RemoteFileTasks.class.getName());

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
	public static void putFiles(Connection conn, String destDir, String... sources ) throws IOException  {
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
	 * @param mask - permissions on file, eg, "0755"
	 * @throws IOException
	 * @author jweiss
	 */
	public static void putFile(Connection conn, String source, String dest, String mask) throws IOException  {
		log.log(Level.INFO, "Copying local file " + source + " to " + dest + " on " + conn.getHostname() + " with mask " + mask, LogMessageUtil.Style.Action);
		SCPClient scp = new SCPClient(conn);
		if (dest.endsWith("/")) {
			scp.put(new String[] {source}, null, dest, mask);
		}
		else {
			String destDir = new File(dest).getParentFile().getCanonicalPath();
			String destFile = new File(dest).getName();
			scp.put(new String[] {source}, new String[] {destFile}, destDir, mask);
		}
	}
	
	/**
	 * Copy file(s) from a remote machine 
	 * @param conn - can be retrieved from your SSHCommandRunner instance
	 * @param localTargetDirectory - Local directory to put the downloaded file(s).
	 * @param remoteFiles - Path and name(s) of the remote file(s)
	 * @throws IOException
	 * @author jsefler
	 */
	public static void getFiles(Connection conn, String localTargetDirectory, String... remoteFiles ) throws IOException {
		for (String remoteFile: remoteFiles)
			log.log(Level.INFO, "Copying remote file "+remoteFile+" on "+conn.getHostname()+" to local directory "+localTargetDirectory+".", LogMessageUtil.Style.Action);
		SCPClient scp = new SCPClient(conn);
		scp.get(remoteFiles, localTargetDirectory);
	}
	public static void getFile(Connection conn, String localTargetDirectory, String remoteFile ) throws IOException {
		getFiles(conn,localTargetDirectory,remoteFile);
	}
	
	/**
	 * Use sed to search and replace content within a file.<br>
	 * sed -i 's/regexp/replacement/g' filePath
	 * @param runner
	 * @param filePath - absolute path to the file to be searched and replaced
	 * @param regexp - the regular expression used to match a pattern for replacement
	 * @param replacement - the replacement content
	 * <BR>Note: in case your regexp or replacement contains a / character in it, you will need to call .replaceAll("/", "\\/") as you pass them to this method.
	 * @return - exit code from sed
	 * 
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
	 * Use echo to append a marker string to the end of a file (e.g. a log file).<br>
	 * @param runner
	 * @param filePath - path to the file on the runner machine
	 * @param marker - this string will be appended to the file and act as a marker.  DO NOT USE CHARACTERS THAT WILL BE INTERPRETED BY BASH IN THIS STRING (e.g. PARENTHESIS).
	 * @return exit code from echo
	 * @author jsefler
	 */
	public static int markFile (SSHCommandRunner runner, String filePath, String marker) {
		return runCommandAndWait(runner, "echo '"+marker+"' >> "+filePath, LogMessageUtil.action());
	}
	
	/**
	 * Return the tail of a file up to, but not including, the marker string (that was previously appended to the file).<br>
	 * This method is intended for use with the static markFile() method.  The idea is that a log file is first marked by your test,
	 * then the program that you are testing will append information to the log file, then by calling this method your test can get the tail
	 * of the log from the point after the mark to the end of file. 
	 * @param runner
	 * @param filePath - path to the file on the runner machine
	 * @param marker - string that was previously appended to the file by calling the markFile(...) method
	 * @param grepPattern - if not null, the tail of the file past the marker string will be greped for this pattern and the matching lines are returned.
	 * @return stdout
	 * @author jmolet
	 * @author jsefler
	 */
	public static String getTailFromMarkedFile (SSHCommandRunner runner, String filePath, String marker, String grepPattern) {
		String grepCommand = "";
		if (grepPattern!=null) grepCommand =  " | grep -E '"+grepPattern+"'";
		SSHCommandResult result = runCommandAndAssert(runner,"(TAIL=''; IFS=$'\\n'; for line in $(tac "+filePath+"); do if [[ $line = '"+marker+"' ]]; then break; fi; if [[ $TAIL = '' ]]; then TAIL=$line; else TAIL=$line'\\n'${TAIL}; fi; done; echo -e $TAIL)"+grepCommand,0);
		return result.getStdout();
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
		
		// Note: Another more informative way to implement this is using: stat filePath
	}
	
	public static int runCommandAndWait(SSHCommandRunner runner, String command, LogRecord logRecord){
		return runner.runCommandAndWait(command,logRecord).getExitCode();
		//return runner.runCommandAndWait(command,Long.valueOf(30000),logRecord);	// timeout after 30 sec
	}
	
	public static int runAugeasCommand(SSHCommandRunner runner, String command, LogRecord logRecord){
		return runCommandAndWait(runner, String.format("echo -e \"%s\nsave\" | augtool", command), logRecord);
	}

	public static int updateAugeasConfig(SSHCommandRunner runner, String augeusPath, String newValue){
		if (newValue == null)
			return runAugeasCommand(runner, String.format("rm %s", augeusPath), LogMessageUtil.action());
		else
			return runAugeasCommand(runner, String.format("set %s '%s'", augeusPath, newValue), LogMessageUtil.action());
	}
	
	
	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, Integer exitCode, List<String> stdoutRegexs, List<String> stderrRegexs) {
		List<Integer> exitCodes = null;
		if (exitCode != null) {
			exitCodes = new ArrayList<Integer>(); exitCodes.add(exitCode);
		}
		return runCommandAndAssert(sshCommandRunner, command, exitCodes, stdoutRegexs, stderrRegexs);
	}
	
	/**
	 * Use the sshCommandRunner to execute the given command and verify that stdout and stderr
	 * contains one or more matches to an expected regex expression. <br>
	 * Note: Assert.assertContainsMatch(...) will be used verify the output.  That means the regex
	 * does not have to match the entire output to be a successful match.
	 * @param sshCommandRunner
	 * @param command - command to execute with options
	 * @param validExitCodes - a list of expected exit codes from the command (usually 0 on success, non-0 on failure).  If the actual exit code matches 
	 * any code in this list, the assert passes.
	 * @param stdoutRegexs - List of expected regex expressions.  Each regex is asserted  to match a substring from the command's stdout
	 * @param stderrRegexs - List of expected regex expressions.  Each regex is asserted  to match a substring from the command's stderr
	 * @author jsefler
	 */
	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, List<Integer> validExitCodes, List<String> stdoutRegexs, List<String> stderrRegexs) {

		SSHCommandResult sshCommandResult = sshCommandRunner.runCommandAndWait(command);
		if (validExitCodes!=null) {
			Assert.assertContains(validExitCodes, sshCommandResult.getExitCode());
		}
		if (stdoutRegexs!=null) {
			for (String regex : stdoutRegexs) {
				Assert.assertContainsMatch(sshCommandResult.getStdout(),regex,"Stdout",String.format("Stdout from command '%s' contains matches to regex '%s',",command,regex));
			}
		}
		if (stderrRegexs!=null) {
			for (String regex : stderrRegexs) {
				Assert.assertContainsMatch(sshCommandResult.getStderr(),regex,"Stderr",String.format("Stderr from command '%s' contains matches to regex '%s',",command,regex));
			}
		}
		
		return sshCommandResult;
	}
	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, Integer exitCode, String stdoutRegex, String stderrRegex) {
		List<String> stdoutRegexs = null;
		if (stdoutRegex!=null) {
			stdoutRegexs = new ArrayList<String>();	stdoutRegexs.add(stdoutRegex);
		}
		List<String> stderrRegexs = null;
		if (stderrRegex!=null) {
			stderrRegexs = new ArrayList<String>();	stderrRegexs.add(stderrRegex);
		}
		return runCommandAndAssert(sshCommandRunner,command,exitCode,stdoutRegexs,stderrRegexs);
	}

	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, Integer... exitCodes) {
		return runCommandAndAssert(sshCommandRunner,command,Arrays.asList(exitCodes),new ArrayList<String>(),new ArrayList<String>());
	}

	/**
	 * Occasionally, you may need to run commands, expecting a nonzero exit code.
	 * 
	 * If you run into this situation, this is your method.
	 * @param sshCommandRunner your preferred sshCommandRunner
	 * @param command - command to execute with options
	 * @author ssalevan
	 */
	public static SSHCommandResult runCommandExpectingNonzeroExit(SSHCommandRunner sshCommandRunner, String command){
		return runCommandExpectingNonzeroExit(sshCommandRunner, command, null);
	}
	
	/**
	 * Occasionally, you may need to run commands, expecting a nonzero exit code.
	 * 
	 * If you run into this situation, this is your method.
	 * @param sshCommandRunner your preferred sshCommandRunner
	 * @param command - command to execute with options
	 * @param timeout - in milliseconds
	 * @author whayutin
	 */
	public static SSHCommandResult runCommandExpectingNonzeroExit(SSHCommandRunner sshCommandRunner, String command, Long timeout){
// THIS WILL ALWAYS RETURN FALSE SINCE THE COMPARISON IS BETWEEN TWO DIFFERENT INSTANTIATED OBJECTS EVEN THOUGH THEIR VALUES MAY BE EQUAL - jsefler 7/9/2010 		
//		Assert.assertNotSame(sshCommandRunner.runCommandAndWait(command,timeout),
//				0,
//				"Command returns nonzero error code: "+command);
		SSHCommandResult sshCommandResult = sshCommandRunner.runCommandAndWait(command,timeout);
		Assert.assertTrue(!sshCommandResult.getExitCode().equals(0),"Command '"+command+"' returns nonzero error code: "+sshCommandResult.getExitCode());
		return sshCommandResult;
	}
	
	

	public static SSHCommandResult runCommandExpectingNoTracebacks(SSHCommandRunner sshCommandRunner, String command){
		return runCommandExpectingNoTracebacks( sshCommandRunner, command,  null);
	}
	
	public static SSHCommandResult runCommandExpectingNoTracebacks(SSHCommandRunner sshCommandRunner, String command, Long timeout){
		SSHCommandResult sshCommandResult = sshCommandRunner.runCommandAndWait(command,timeout);
		Assert.assertFalse(sshCommandResult.getStdout().toLowerCase().contains("traceback"),
				"Traceback string not in stdout");
		Assert.assertFalse(sshCommandResult.getStderr().toLowerCase().contains("traceback"),
				"Traceback string not in stderr");
		return sshCommandResult;
	}
}
