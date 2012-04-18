package com.redhat.qe.tools.remotelog;

/**
 * This annotation defines a remote log destination. Basically you need to tell, username, hostname and password 
 * for remote machine, then you need to specify log file on the remote machine.
 * <b>ANY</b> of above properties can contain:
 * <ul>
 * <li><b>${system.property}</b> to reference java system property</li>
 * <li><b>${env:VARIABLE}</b> to reference environment variable</li>
 * </ul>
 * These are processed by {@link RemoteLogCheckTestNGListener} and replaced with right values at runtime
 * @author lzoubek@redhat.com
 *
 */
public @interface RemoteLog {	
	/**
	 * hostname or IP where to find remote log file 
	 * <br><br>
	 * <b>${env:VARIABLE}</b> can be used to reference environment variable<br>
	 * <b>${system.property}</b> can be used to reference java system property
	 */
	String host() default "${env:HOST_NAME}";	
	/**
	 * user to login via SSH
	 */
	String user() default "${env:HOST_USER}";
	/**
	 * user's pasword
	 * <br> {@link RemoteLog#keyfile()} has priority over {@link RemoteLog#pass()} 
	 */
	String pass() default "${env:HOST_PASSWORD}";
	/**
	 * path to watched log file relative to {@link CheckRemoteLog#user()} HOME or absolute path 
	 */
	String logFile() default "${env:REMOTE_LOGFILE}";
	/**
	 * an expression passed to <b>grep</b> tool to detect BAD lines in log file
	 */
	String failExpression() default "\' ERROR \'";
}
