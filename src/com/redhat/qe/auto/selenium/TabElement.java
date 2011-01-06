package com.redhat.qe.auto.selenium;

/**
 * A Tab Element represents a gui control that contains two states (typically selected and unselected).
 */
public class TabElement extends Element{
	private LocatorStrategy selectedLocatorStrategy = null;

	/**
	 * @param unselectedLocatorStrategy - this strategy should resolve to the selenium locator needed to click an existing tab in its unselected state
	 * @param selectedLocatorStrategy - this strategy should resolve to the selenium locator that identifies an existing tab after it has been selected and is now in its selected state
	 * @param locatorStrategyArgs - arguments fed to both the locatorStrategy and alternateLocatorStrategy to create the selenium locators
	 */
	public TabElement(LocatorStrategy unselectedLocatorStrategy,  LocatorStrategy selectedLocatorStrategy, String... locatorStrategyArgs) {
		this.locatorStrategy = unselectedLocatorStrategy;
		
		this.selectedLocatorStrategy = selectedLocatorStrategy;
		this.locatorStrategyArgs = locatorStrategyArgs;
	}
	
	/**
	 * This constructor is only used when the the same locator can be used to identify both the selected and unselected state of the gui control.
	 * @param locator
	 */
	public TabElement(String locator){
		this.locator = locator;
	}

	public Element getSelectedElement() {
		if (selectedLocatorStrategy == null && locatorStrategy == null){
			return this; 
		}
		else return new Element(
				selectedLocatorStrategy == null ? locatorStrategy : selectedLocatorStrategy,
				locatorStrategyArgs);
	}	
}