package com.redhat.qe.auto.selenium;

public interface LocatorStrategy {
	/**
	 * @return - a brief human readable name for this strategy that can be used
	 * to describe the selenium locator in a log. Example: "link in resource breadcrumb trail"
	 */
	public String getName();
	
	/**
	 * Use the args as functional input to formulate a valid selenium element locator.
	 * @param args - these argument values will be substituted into the place holders
	 * 					of the template associated with this LocatorStrategy
	 * @return - a valid selenium element locator
	 */
	public String getLocator(String... args);
	
	/**
	 * Associated with a LocatorStrategy is a template that contains place holders
	 * of the form $d that will sequentially be replaced with the arguments passed
	 * to method getLocator(String... args).  The following is an example template:
	 * "//span[normalize-space(.)='$1']/../a[normalize-space(.)='$2']"
	 * @param args - these are the arguments that will eventually be passed to
	 *  getLocator(String... args).  They may or may not be needed to assemble the
	 *  returned template.
	 * @return - the template for this LocatorStrategy
	 */
	public String getTemplate(String... args);
}
