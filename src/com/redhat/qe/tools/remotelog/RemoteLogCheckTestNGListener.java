package com.redhat.qe.tools.remotelog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
/**
 * TestNG listener that intercepts {@link RemoteLogAccess} watcher on test method calls.
 * Watching and checking log file can be enabled either globally by setting <b>com.redhat.qe.tools.remote.log.check</b> property, 
 * that will result to global log checker defined by {@link RemoteLog} defaults.<br>
 * And/or you can enable it  on test class or method by adding {@link CheckRemoteLog} annotation.
 * @see CheckRemoteLog
 * @author lzoubek@redhat.com
 *
 */
public class RemoteLogCheckTestNGListener implements ITestListener,ISuiteListener {

	protected static Logger log = Logger.getLogger(RemoteLogCheckTestNGListener.class.getName());
	
	private static final Pattern envVarPattern = Pattern.compile("\\$\\{env\\:([^\\}]+)\\}");
	private static final Pattern systemPropPattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
	private AgentLogHandle classWatcher = null;
	private AgentLogHandle globalWatcher = null;
	@Override
	public void onStart(ISuite arg0) {
		if (System.getProperty("com.redhat.qe.tools.remote.log.check")!=null) {
			globalWatcher = new AgentLogHandle("global",true);

			try {
				String user = RemoteLog.class.getMethod("user", new Class<?>[0]).getDefaultValue().toString();
				String host = RemoteLog.class.getMethod("host", new Class<?>[0]).getDefaultValue().toString();
				String pass = RemoteLog.class.getMethod("pass", new Class<?>[0]).getDefaultValue().toString();
				String logFile = RemoteLog.class.getMethod("logFile", new Class<?>[0]).getDefaultValue().toString();
				String filter = RemoteLog.class.getMethod("failExpression", new Class<?>[0]).getDefaultValue().toString();
				
				RemoteLogAccess rla = new RemoteLogAccess(substValues(user), substValues(host), substValues(pass), substValues(logFile));
				rla.setFilter(filter);
				globalWatcher.getLogs().add(rla);	
				log.fine("Created global checker "+globalWatcher.toString());
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onFinish(ISuite arg0) {
		disconnectWatcher(globalWatcher);		
	}

	@Override
	public void onStart(ITestContext context) {
	
		
	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		disconnectWatcher(classWatcher);
	}

	@Override
	public void onFinish(ITestContext context) {
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		disconnectWatcher(classWatcher);		
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		disconnectWatcher(classWatcher);
		
	}

	@Override
	public void onTestStart(ITestResult result) {		
		Class<?> klass = result.getTestClass().getRealClass();
		// find our annotation on method level
		CheckRemoteLog check = result.getMethod().getMethod().getAnnotation(CheckRemoteLog.class);
		if (check==null) {
			// not found .. lets look at class level
			check = getClassAnnotation(klass);
		}
		// class/method watcher has always higher priority
		AgentLogHandle watcher = create(check);
		if (watcher==null) {
			watcher = globalWatcher;
		}		
		if (watcher!=null && watcher.isEnabled()) {
			log.fine("Enabling checker "+watcher.toString()+ " for class "+klass.getCanonicalName());
			watcher.watch();
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		
		// class/method watcher has always higher priority
		AgentLogHandle watcher = classWatcher;
		if (watcher==null) {
			watcher = globalWatcher;
		}			
		if (watcher!=null && watcher.isEnabled()) {
			StringBuilder message = new StringBuilder();
			for (RemoteLogAccess rla : watcher.getLogs()) {
				log.fine("Examining "+rla.toString()+"...");
				List<String> errorLines = rla.filteredLines();
				if (!errorLines.isEmpty()) {
					log.warning("Founds lines matching ["+rla.getFilter()+"] in "+rla.toString()+" , seting test result as FAILED");
					message.append(rla.toString()+":\n");
					message.append(linesToStr(errorLines)+"\n");
				}
			}
			if (message.length()>0) {
				result.setStatus(ITestResult.FAILURE);
				result.setThrowable(new RuntimeException("Following error lines were found in\n"+message.toString()));
			}
		}			
		disconnectWatcher(classWatcher);
	}
	
	private String substValues(String value) {
		Matcher m = envVarPattern.matcher(value);
		while (m.find()) {
			String repl = System.getenv(m.group(1));
			if (repl==null) {
				repl = "${env:"+m.group(1)+"}";
			}
			value = value.replaceAll(envVar(m.group(1)).toString(),Matcher.quoteReplacement(repl));
		}
		m = systemPropPattern.matcher(value);
		while (m.find()) {
			value = value.replaceAll(
				sysProp(m.group(1)).toString(),Matcher.quoteReplacement(System.getProperty(m.group(1), "${" + m.group(1) + "}")));
		}
		return value;	
	}
	private Pattern envVar(String value) {
		value = value.replaceAll("\\.", "\\\\.");
		return Pattern.compile("\\$\\{env\\:"+value+"\\}");
	}
	private Pattern sysProp(String value) {
		value = value.replaceAll("\\.", "\\\\.");
		return Pattern.compile("\\$\\{"+value+"\\}");
	}
	/**
	 * finds {@link CheckRemoteLog} annotation in given class or recursive in super classes 
	 * @param klass
	 * @return
	 */
	private CheckRemoteLog getClassAnnotation(Class<?> klass) {
		if (klass==null || Object.class.equals(klass)) {
			return null;
		}
		CheckRemoteLog check = klass.getAnnotation(CheckRemoteLog.class);
		if (check!=null) {
			return check;
		}		
		return getClassAnnotation(klass.getSuperclass());
	}
	private RemoteLogAccess create(RemoteLog rl) {
		RemoteLogAccess inst = null;
		try {
			inst = new RemoteLogAccess(substValues(rl.user()), substValues(rl.host()), substValues(rl.pass()), substValues(rl.logFile()));
			inst.setFilter(rl.failExpression());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inst;
	}
	/**
	 * creates new instance of agent log based on {@link CheckRemoteLog annotation}
	 */
	private AgentLogHandle create(CheckRemoteLog check) {
		if (check==null) {
			return null;
		}
		if (!check.enabled()) {
			// user requires to turn off checker
			return new AgentLogHandle(null,false);
		}
		AgentLogHandle inst = new AgentLogHandle("class",true);
		for (RemoteLog rl : check.logs()) {
			RemoteLogAccess rla = create(rl);
			if (rla!=null) {
				inst.getLogs().add(rla);
			}
		}		
		return inst;
	}
	private void disconnectWatcher(AgentLogHandle watcher) {
		if (watcher!=null && watcher.getLogs()!=null) {
			watcher.disconnect();
			watcher.setEnabled(false);
		}
		watcher = null;
	}
	private String linesToStr(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line+"\n");
		}
		return sb.toString();
	}
	/**
	 * this handles watcher together with a flag whether it's enabled or not
	 * @author lzoubek
	 *
	 */
	private static class AgentLogHandle {
		private final List<RemoteLogAccess> logs = new ArrayList<RemoteLogAccess>();
		private boolean enabled = true;
		private final String level;
		private AgentLogHandle(String level,boolean enabled) {
			this.enabled = enabled;
			this.level = level;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public boolean isEnabled() {
			return enabled;
		}
		public List<RemoteLogAccess> getLogs() {
			return logs;
		}
		public void disconnect() {
			for (RemoteLogAccess rla : getLogs()) {
				rla.disconnect();
			}
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("[level="+level+",enabled="+isEnabled());
			for (RemoteLogAccess rla : getLogs()) {
				sb.append(","+rla.toString());
			}
			sb.append("]");
			return sb.toString();
		}

		public void watch() {
			for (RemoteLogAccess rla : getLogs()) {
				rla.watch();
			}
		}
	}
}
