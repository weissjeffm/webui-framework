package com.redhat.qe.auto.selenium;


/**
 * A LocatorSandwich is used to define a functional selenium locator when creating an Element.
 * These are useful when the selenium locator depends on some argument(s) and is therefore not
 * a fixed value.
 * The constructor of a LocatorSandwich takes three arguments.  The first argument, the bottomBun,
 * represents the initial constant string of the locator.  The second argument, the middleBun,
 * represents a repeating constant string that will be interleaved between slices of meat
 * supplied by the getLocator(String... meat) arguments.  The third argument, the topBun,
 * is the final constant string that closes off the locator.
 * <br> Example:	<br> LocatorSandwich bigMac = new LocatorSandwich("Big Mac","(","|",")");
 * 					<br> bigMac.getLocator("a","b","c"));  // returns: (a|b|c)
 * 					<br>
 * WARNING: Due to the repeating nature of the middleBun in this strategy.  
 * @author jsefler
 * 
 */
public class LocatorSandwich implements LocatorStrategy {

	//private static Logger log = Logger.getLogger(ExtendedSelenium.class.getName());
	protected String name = null;
	protected String topBun = null;
	protected String middleBun = null;
	protected String bottomBun = null;

	/**
	 * @param name - a brief human readable name for the template.
	 * @param bottomBun - represents the initial constant string of the locator
	 * @param middleBun - represents a repeating constant string that will be interleaved between slices of meat supplied by the getLocator(String... meat) arguments
	 * @param topBun - represents the final constant string that closes off the locator
	 */
	public LocatorSandwich(String name, String bottomBun, String middleBun, String topBun){
		this.name = name;
		this.bottomBun = bottomBun;
		this.middleBun = middleBun;
		this.topBun = topBun;
	}
	
	/* (non-Javadoc)
	 * @see com.redhat.qe.auto.selenium.LocatorStrategy#getLocator(java.lang.String[])
	 */
	@Override
	public String getLocator(String... args) {
		
		// build the sandwich...
		
		// start with the bottom bun
		String locator = this.bottomBun;
		
		// add the first slice of meat
		if (args.length>0) locator += args[0];
		
		// add middle layers of bun, meat, bun, meat, bun, meat... 
		for (int i=1; i<args.length; i++) {
			locator += middleBun+args[i];
		}
		
		//add the top bun
		locator += topBun;
		
		return locator;
	}

	/* (non-Javadoc)
	 * @see com.redhat.qe.auto.selenium.LocatorStrategy#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * Warning! The return value from this call is ONLY valid when this locatorSandwich is made with two slices of meat.
	 */
	public String getTemplate() {
		return this.getLocator("$1","$2");
	}


	public static void main (String[] args){
		// this is just a developers test
		
		LocatorSandwich bigMac = new LocatorSandwich("Big Mac","(","|",")");
		System.out.println("bigMac.getName= "+bigMac.getName());
		System.out.println("bigMac.getTemplate= "+bigMac.getTemplate());
		System.out.println("bigMac.getLocator= "+bigMac.getLocator());
		System.out.println("bigMac.getLocator= "+bigMac.getLocator("a"));
		System.out.println("bigMac.getLocator= "+bigMac.getLocator("a","b"));
		System.out.println("bigMac.getLocator= "+bigMac.getLocator("a","b","c"));
		System.out.println("bigMac.getLocator= "+bigMac.getLocator("a","b","c","d"));
		System.out.println("bigMac.getLocator= "+bigMac.getLocator("a","b","c","d","e"));
	}
}
