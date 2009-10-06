package com.redhat.qe.auto.selenium;

public class Element {

	protected String locator = null;
	protected LocatorStrategy locatorStrategy= null;
	protected String[] locatorStrategyArgs = null;
	
	public Element(){
		
	}

	public Element(String locator){
		this.locator = locator;
	}
	
	public Element(LocatorStrategy locatorStrategy){
		this.locatorStrategy = locatorStrategy;
	}
	public Element(LocatorStrategy locatorStrategy, String... locatorStrategyArgs){
		this.locatorStrategy = locatorStrategy;
		this.locatorStrategyArgs = locatorStrategyArgs;
	}
	
	public String getLocator(){
/*		if (locatorStrategy != null ) && (locator != null )) 
			throw new IllegalStateException("Must not set both locator and locatorStrategy for an element (locator=" 
					+ locator + ", locatorStrategy=" + locatorStrategy + ")");
*/	
		if (! (locatorStrategyArgs==null))
			return getLocator(locatorStrategyArgs);
		return locator;
	}
	
	public String getLocator(String... locatorStrategyArgs){
		return locatorStrategy.getLocator(locatorStrategyArgs);
	}
	
	public LocatorStrategy getLocatorStrategy(){
		return locatorStrategy;
	}
	
	public String[] getArguments() {
		return locatorStrategyArgs;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		if (locatorStrategy != null) {
			sb.append(locatorStrategy.getName());
			for (String arg:locatorStrategyArgs){
				sb.append(" " + arg);
			}
			sb.append(". (");
		}
		sb.append( "locator '" + getLocator() + "'");
		if (locatorStrategy != null) {
			sb.append(")");
		}
		return sb.toString();
	}
	
	
	public static void main(String[] args){
		Element mylocator = new Element("//a[.='Login']");
		
		Element genericLink = new Element(
				new LocatorStrategy(){
					public String getLocator(String... locatorStrategyArgs){
						return "//a[.='" + locatorStrategyArgs[0] + "']";
						}
					public String getName(){
						return "my silly strategy"; }
					}
				, "myarg");
		
		System.out.println("Found " + genericLink.getArguments() + " using " + 
				genericLink.getLocatorStrategy().getName() + ", " + genericLink.getLocator("Login") + "");
	}
	
}
