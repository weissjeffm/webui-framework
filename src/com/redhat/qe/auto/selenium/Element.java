package com.redhat.qe.auto.selenium;

public class Element {

	protected String locator = null;
	


	protected LocatorStrategy locatorStrategy= null;
	protected String[] locatorStrategyArgs = null;
	protected Element humanReadable = null;
	
	

	

	public Element(){
		
	}

	public Element(String locator){
		this.locator = locator;
	}
	
	public Element(LocatorStrategy locatorStrategy){
		this.locatorStrategy = locatorStrategy;
		this.locatorStrategyArgs = new String[]{};
	}
	public Element(LocatorStrategy locatorStrategy, String... locatorStrategyArgs){
		this.locatorStrategy = locatorStrategy;
		this.locatorStrategyArgs = locatorStrategyArgs;
	}
	
	public Element(String locator, Element humanReadable) {
		this.locator = locator;
		this.humanReadable = humanReadable;
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
	
	public Element getHumanReadable() {
		return humanReadable;
	}

	
	public void setLocator(String locator) {
		this.locator = locator;
	}

	public void setLocatorStrategy(LocatorStrategy locatorStrategy) {
		this.locatorStrategy = locatorStrategy;
	}
	
	public void setHumanReadable(Element humanReadable) {
		this.humanReadable = humanReadable;
	}
	
	
	public void setLocatorStrategyArgs(String[] locatorStrategyArgs) {
		this.locatorStrategyArgs = locatorStrategyArgs;
	}
	
	/**
	 * @param index - 1 based index of the arg list to set
	 * @param locatorStrategyArg - new value
	 */
	public void setLocatorStrategyArg(int index, String locatorStrategyArg) {
		this.locatorStrategyArgs[index-1]=locatorStrategyArg;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		if (locatorStrategy != null) {
			sb.append(locatorStrategy.getName());
			if (locatorStrategyArgs!=null) {
				for (String arg:locatorStrategyArgs){
					sb.append(" '" + arg + "'");
				}
			}
			sb.append(". (");
		}
		sb.append(getLocator());
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
					@Override
					public String getTemplate(String... args) {
						return "//a[.='$1']";
					}
				}
				, "myarg");
		
		System.out.println("Found " + genericLink.getArguments() + " using " + 
				genericLink.getLocatorStrategy().getName() + ", " + genericLink.getLocator("Login") + "");
	}
	
}
