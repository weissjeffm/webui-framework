package com.redhat.qe.auto.sahi;

import java.util.logging.Logger;

import net.sf.sahi.client.Browser;
import net.sf.sahi.config.Configuration;

/**
 * This class extends the Browser functionality.  It 
 * provides logging of UI actions (via java standard logging),
 * and some convenience methods.
 * @author dgao
 */
public class ExtendedSahi extends Browser {
	private static Logger log = Logger.getLogger(ExtendedSahi.class.getName());

	public ExtendedSahi(String browserPath, String browserProcessName, String browserOpt, String sahiDir, String userDataDir) {
		super(browserPath, browserProcessName, browserOpt);
		Configuration.initJava(sahiDir, userDataDir);
	}
}
