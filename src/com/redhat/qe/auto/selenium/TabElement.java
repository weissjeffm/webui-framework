package com.redhat.qe.auto.selenium;

public class TabElement extends Element{
	private LocatorStrategy alternateLocatorStrategy = null;

	public TabElement(LocatorStrategy locatorStrategy,  LocatorStrategy alternateLocatorStrategy, String... locatorStrategyArgs) {
		this.locatorStrategy = locatorStrategy;
		
		this.alternateLocatorStrategy = alternateLocatorStrategy;
		this.locatorStrategyArgs = locatorStrategyArgs;
	}
	
	public TabElement(String locator){
		this.locator = locator;
	}

	public Element getAlternateElement() {
		if (alternateLocatorStrategy == null && locatorStrategy == null){
			return this; 
		}
		else return new Element(
				alternateLocatorStrategy == null ? locatorStrategy : alternateLocatorStrategy,
				locatorStrategyArgs);
	}	
}