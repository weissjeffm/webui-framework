package com.redhat.qe.auto.selenium;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;

/**
 * This class extends the DefaultSelenium functionality.  It 
 * provides logging of UI actions (via java standard logging),
 * and some convenience methods.
 * @author jweiss
 *
 */
public class ExtendedSelenium extends DefaultSelenium implements IScreenCapture {

	private static ExtendedSelenium instance = null;
	
	private static Logger log = Logger.getLogger(ExtendedSelenium.class.getName());
	private static File screenshotDir = null;
	private static final DecimalFormat numFormat = new DecimalFormat("##0.#");
	protected static final String DEFAULT_WAITFORPAGE_TIMEOUT = "60000";
	protected static String WAITFORPAGE_TIMEOUT = DEFAULT_WAITFORPAGE_TIMEOUT;
	
	public ExtendedSelenium(CommandProcessor processor) {
		super(processor);

	}

	public ExtendedSelenium(String serverHost, int serverPort,
			String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}

	@Override
	public void start() {
		super.start();
		log.finest("Selenium started.");

		// TODO this is ugly
		TestNGListener.setScreenCaptureUtility(this);
		
		windowMaximize();
	}
	

	@Override
	public void stop() {
		super.stop();
		log.finest("Selenium stopped.");

	}
	
	

	public void clickAndWait(String locator) {
		clickAndWait(locator, WAITFORPAGE_TIMEOUT, true);
	}
	

	public void clickAndWait(String locator, String timeout) {
		clickAndWait(locator, timeout, true);
	}

	public void clickAndWait(String locator, String timeout, boolean highlight) {
		click(locator, highlight);
		waitForPageToLoad(timeout);
	}
	
	public void selectAndWait(String selectLocator, String optionLocator){
		select(selectLocator, optionLocator);
		waitForPageToLoad();
	}
	
	
	public void waitForPageToLoad(){
		//waitForPageToLoad(WAITFORPAGE_TIMEOUT);
	}
	
	@Override
	public void waitForPageToLoad(String timeout){
		//long start = System.currentTimeMillis();
		//super.waitForPageToLoad(timeout);
		//Double waitedInSecs = ((System.currentTimeMillis() - start)) / 1000.0;
		
		//log.finer("Waited " + numFormat.format(waitedInSecs) + "s for page to load.");

	}
	/**
	 * @param locator
	 * @param highlight - if true, highlight the element for a fraction of a second before clicking it.
	 *   This makes it easier to see what selenium is doing "live".
	 */
	public void click(String locator, boolean highlight) {
		if (highlight)
			highlight(locator);
		super.click(locator);
		log.log(MyLevel.ACTION, "Clicked on locator: " + locator);
	}

	@Override
	public void click(String locator) {
		click(locator, true);
	}
	
	/**
	 * @param locator
	 * @param highlight - if true, highlight the element for a fraction of a second before clicking it.
	 *   This makes it easier to see what selenium is doing "live".
	 */
	public void click(String locator, String humanReadableName,boolean highlight) {
		if (highlight)
			highlight(locator);
		super.click(locator);
		log.log(MyLevel.ACTION, "Clicked on locator: " + humanReadableName);
	}


	public void click(String locator, String humanReadableName) {
		click(locator,humanReadableName, true);
	}
	
	
	/**
	 * Waits for an element to appear on the page, then clicks it.  This method is useful for interacting with 
	 * elements that are created by AJAX calls.  You should be reasonably sure that the element will in fact appear,
	 * otherwise the execution will not continue until the timeout is hit (and then an exception will be thrown).
	 * @param locator A locator for the element to click on when it appears
	 * @param timeout How long to wait for the element to appear before timing out and throwing an exception
	 */
	public void waitAndClick(String locator, String timeout){
		super.waitForCondition("selenium.isElementPresent(\"" + locator + "\");", timeout);
		click(locator);
	}
	
	/**
	 * Similar to waitAndClick-  waits for an element to appear on the page, then clicks it, then waits for the page
	 * to load. 
	 * @param locator A locator for the element to click on when it appears
	 * @param timeout1 How long to wait for the element to appear before timing out and throwing an exception
	 * @param timeout2 How long to wait for the page to load after clicking the element.
	 */
	public void waitAndClickAndWait(String locator, String timeout1, String timeout2){
		super.waitForCondition("selenium.isElementPresent(\"" + locator + "\");", timeout1);
		clickAndWait(locator, timeout2);
	}
	
	public void waitAndClickAndWait(String locator, String timeout1){
		waitAndClickAndWait(locator, timeout1, WAITFORPAGE_TIMEOUT);
	}
	
	public void waitForElement(String locator, String timeout){
		log.info("Waiting for element '" + locator  + "', with timeout of " + timeout + ".");
		super.waitForCondition("selenium.isElementPresent(\"" + locator + "\");", timeout);

	}
	
	@Override
	public void type(String locator, String value) {
		highlight(locator);
		super.type(locator, value);
		log.log(MyLevel.ACTION, "Typed '" + value + "' into textbox '"
				+ locator + "'");
	}
	
	
	public void type(String locator,String humanReadableName, String value) {
		highlight(locator);
		super.type(locator, value);
		log.log(MyLevel.ACTION, "Typed '" + value + "' into textbox '"
				+ humanReadableName + "'");
	}
	
	public void setText(String locator, String value){
		type(locator, value);
	}
	
	public void setText(String locator, String humanReadableName,String value){
		type(locator,humanReadableName, value);
	}
	

	@Override
	public void open(String url) {
		super.open(url);
		log.log(MyLevel.ACTION, "Opened URL '" + url + "'.  Current location is " + getLocation() + " .");
	}

	
	
	@Override
	public void check(String locator) {
		checkUncheck(locator, true);
		log.log(MyLevel.ACTION, "Checked checkbox '"
				+ locator + "'");
	}
	
	protected void checkUncheck(String locator, boolean check){
		if (isChecked(locator) != check) super.click(locator);
		else log.log(Level.FINE, "Checkbox '"
				+ locator + "' is already " + (check ? "checked.": "unchecked."));
	}

	@Override
	public void select(String selectLocator, String optionLocator) {
		super.select(selectLocator, optionLocator);
		log.log(MyLevel.ACTION, "Selected item '"
				+ optionLocator + "' in list '" + selectLocator + "'.");
	}
	
	/**
	 * Selects a list item by value.  To be used when a select list doesn't have any other 
	 * good locators.  It's up to the
	 * caller to make sure there isn't more than one select list on the page that contains 
	 * the same value.
	 * @param value  The value attribute of the Option.  This is not necessarily the same as what text 
	 * appears in the browser.
	 */
	public void select(String value){
		select("//select[option[@value='" + value + "']]", "value=" + value);
	}
	

	@Override
	public void uncheck(String locator) {
		checkUncheck(locator, false);
		log.log(MyLevel.ACTION, "Unchecked checkbox '"
				+ locator + "'");
	}
	/*
	 * @see com.thoughtworks.selenium.DefaultSelenium#isElementPresent(java.lang.String)
	 */
	@Override
	public boolean isElementPresent(String element){
		if(super.isElementPresent(element)){
		log.log(MyLevel.ACTION,"Found element: "+element);
		highlight(element);
		return true;
		}
		else{
			log.fine(" Did not find element: '"+ element+"'");
			return false;
		}
	}
	
	public boolean isElementPresent(String element,boolean logResults){
		if(super.isElementPresent(element)){
		if(logResults)	
			log.log(MyLevel.ACTION,"Found element: "+element);
		highlight(element);
		return true;
		}
		else{
			if(logResults)
				log.fine(" Did not find element: '"+ element+"'");
			return false;
		}
	}
	
	@Override
	public boolean isTextPresent(String txt){
		if(super.isTextPresent(txt)){
		return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isTextPresent(String txt, boolean logResults){
		if(super.isTextPresent(txt)){
			if(logResults){
			log.log(MyLevel.ACTION,"Success, Found text: '"+txt+"'");
			}
			//sel.highlight(txt);
			return true;
			}
			else{
				log.log(MyLevel.ACTION,"Did not find text: '"+ txt+"'");
				return false;
			}
	}
	
	@Override
	public void goBack(){
		super.goBack();
		log.log(MyLevel.ACTION, "Clicked Browser Back Button");
		waitForPageToLoad();
	}
	
	@Override
	public void refresh(){
		super.refresh();
		log.log(MyLevel.ACTION, "Clicked Browser Refresh Button");
		waitForPageToLoad();
	}
	
	

	@Override
	public String getAlert() {
		String text = super.getAlert();
		log.log(MyLevel.ACTION, "Clicked OK on alert dialog: " + text);
		return text;
	}

	@Override
	public String getConfirmation() {
		String text = super.getConfirmation();
		log.log(MyLevel.ACTION, "Clicked OK on confirmation dialog: " + text);
		return text;
	}

	@Override
	public String getPrompt() {
		String text = super.getPrompt();
		log.log(MyLevel.ACTION, "Clicked OK on prompt dialog: " + text);
		return text;
	}
	
	@Override
	public void answerOnNextPrompt(String answer){
		super.answerOnNextPrompt(answer);
		log.log(MyLevel.ACTION, "Answering prompt with: " + answer);
	}
	
	@Override
	public void setTimeout(String timeout){
		super.setTimeout(timeout);
		WAITFORPAGE_TIMEOUT = timeout;
	}

	public void sleep(long millis){
		try {
			log.log(Level.INFO, "Sleeping for " + millis + "ms.");
			Thread.sleep(millis);
		}
		catch(InterruptedException ie){
			log.log(Level.WARNING, "Sleep interrupted!", ie);
		}
	}
	
	
	public void screenCapture() throws Exception {
		if (screenshotDir == null) {
			String dirName = System.getProperty("user.dir") + File.separator
					+ "screenshots";
			screenshotDir = new File(dirName);
		}
		if (!(screenshotDir.exists() && screenshotDir.isDirectory())) {
			screenshotDir.mkdirs();
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssS");

		Date rightNow = new Date();
		String outFileName = dateFormat.format(rightNow) + ".png";
		try {
			super.captureScreenshot(screenshotDir.getCanonicalPath()
					+ File.separator + outFileName);
		}
		catch(Exception e ){
			//if this failed, try the temp dir
			super.captureScreenshot("/tmp"
					+ File.separator + outFileName);
		}
	}
	
	

	public static ExtendedSelenium getInstance(){
		if (instance == null) throw new NullPointerException("Selenium instance not set yet.");
		return instance;
	}
	
	public static ExtendedSelenium newInstance(String serverHost, int serverPort, String browserStartCommand, String browserURL){
		instance = new ExtendedSelenium(serverHost, serverPort, browserStartCommand, browserURL);
		return instance;
	}
	
	// custom logging level for java logging, to log clicks
	public static class MyLevel extends Level {
		static final long serialVersionUID = 3945372834L;
		// Create the new level
		public static final Level ACTION = new MyLevel("ACTION", Level.INFO
				.intValue() + 1);

		public MyLevel(String name, int value) {
			super(name, value);
		}
	}

}
