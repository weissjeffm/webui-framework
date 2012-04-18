package com.redhat.qe.tools.remotelog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be on class or method and is processed by {@link RemoteLogCheckTestNGListener} listener. 
 * Adding this annotation to class (or even superclass) or method does following:
 * <ol>
 * <li>Before each test method runs, new {@link RemoteLogAccess} instance is created and particular remote log is being watched</li>
 * <li>After test method finishes, gathered output is checked for <b>fail expression</b> lines. If such line is found, test 
 * is marked as FAILED and appropriate Throwable is set to test result.</li>
 * </ol>
 * Method level declaration has precedence before class level. Class level has precedence before superclass and global setting. 
 * <br><br>
 * <b>Example code:</b>
 * <pre>
 * &#64;CheckRemoteLog()
 * public class AbstractTest { }
 * 
 * public class LogCheckEnabled extends AbstractTest {
 * 
 * 	&#64;CheckRemoteLog(enabled=false)
 * 	&#64;Test()
 * 	public void methodLogCheckDisabled() {}
 *
 * 	&#64;CheckRemoteLog(
 * 		RemoteLog(host="localhost",user="hudson",pass="${secret.property}",logFile="/tmp/output.log")
 * 		RemoteLog(host="otherhost",user="hudson",pass="${secret.property2}",logFile="${env:APP_HOME}/debug.log",filterExpression="INFO")
 * 	)
 * 	&#64;Test()
 * 	public void methodCheck2DifferentLogs() {}
 * 	}
 * 
 *	&#64;CheckRemoteLog(enabled=false)
 *	public class LogCheckDisabled extends AbstractTest { }
 * </pre>
 * 
 * @author lzoubek@redhat.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface CheckRemoteLog {
	/**
	 * remote log destination definitions (yes, we can observe more different log files on different hosts)
	 * @return
	 */
	 RemoteLog[] logs() default @RemoteLog;
	/**
	 * says whether checking is enabled. Set this to false to disable log checking for particular class or method
	 */
	boolean enabled() default true;
}
