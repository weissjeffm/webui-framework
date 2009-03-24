package com.redhat.qe.auto.selenium;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	private static File localHtmlDir = null;
	private static final DecimalFormat numFormat = new DecimalFormat("##0.#");
	protected static final String DEFAULT_WAITFORPAGE_TIMEOUT = "60000";
	protected static String WAITFORPAGE_TIMEOUT = DEFAULT_WAITFORPAGE_TIMEOUT;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssS");
	
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
		sleep(3);
		windowMaximize();
	}
	

	@Override
	public void stop() {
		super.stop();
		//added this as part of a fix to guarantee that only instance of selenium
		//is running.  So be sure that there is only one browser session up at a time
		killInstance();
		
		//debugging this, because screenshots are getting taken too late on hudson wdh
		//log.fine("Selenium stopped.");
		//log.log(MyLevel.FINER,"Selenium stopped");
		//log.info("Selenium stopped");

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
		waitForPageToLoad(WAITFORPAGE_TIMEOUT);
	}
	
	@Override
	public void waitForPageToLoad(String timeout){
		long start = System.currentTimeMillis();
		super.waitForPageToLoad(timeout);
		Double waitedInSecs = ((System.currentTimeMillis() - start)) / 1000.0;
		
		log.finer("Waited " + numFormat.format(waitedInSecs) + "s for page to load.");

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
	
	@Override
	public void mouseOver(String locator) {
		super.mouseOver(locator);
		log.log(MyLevel.ACTION, "Hovered over locator: " + locator);

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
		try {
			super.waitForCondition("selenium.isElementPresent(\"" + locator + "\");", timeout1);
		}
		catch(Exception e){
			RuntimeException rte = new RuntimeException("Element did not appear: " + locator);
			rte.initCause(e);
			throw rte;
		}
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
	
	public void checkUncheck(String locator, boolean check){
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
		log.log(Level.INFO,"Found element: "+element);
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
			log.log(Level.INFO,"Found element: "+element);
		highlight(element);
		return true;
		}
		else{
			if(logResults)
				log.fine(" Did not find element: '"+ element+"'");
			return false;
		}
	}
	
	
	public boolean isTextPresent(String txt, boolean logResults){
		if(super.isTextPresent(txt)){
			if(logResults){
			log.log(Level.INFO,"Success, Found text: '"+txt+"'");
			}
			//sel.highlight(txt);
			return true;
			}
			else{
				log.log(Level.INFO,"Did not find text: '"+ txt+"'");
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
	
	
	public String screenCapture() throws Exception {
		String dirName = System.getProperty("user.dir") + File.separator
		+ "screenshots";
		return screenCapture(dirName);
	}
	
	public String screenCapture(String dirName) throws Exception {
		String fullPathtoFile = null;
		mkdir(dirName);
		

		Date rightNow = new Date();
		String outFileName = dateFormat.format(rightNow) + ".png";
		try {
			File htmlDir = localHtmlDir != null? localHtmlDir : screenshotDir;
			writeHtmlOnError(htmlDir);
			//if success use that next time
			localHtmlDir = htmlDir;
			fullPathtoFile = screenshotDir.getCanonicalPath()+ File.separator + outFileName;
			super.captureScreenshot(fullPathtoFile);
			log.log(Level.FINE, "Captured screenshot to "+ fullPathtoFile);
			
		}
		catch(Exception e ){
			log.fine("Couldn't capture screenshot, trying to write to tmp dir instead.");
			//if this failed, try the temp dir
			screenshotDir = new File("/tmp");
			super.captureScreenshot("/tmp"+ File.separator + outFileName);
			//log.log(Level.FINE, "Captured ScreenShot to "+"/tmp"+ File.separator + outFileName);
			fullPathtoFile = "/tmp"+ File.separator + outFileName;
			
			//writeHtmlOnError(screenshotDir);		
		}
		return fullPathtoFile;
		
	}
	
	protected void mkdir(String dirName){
		if (screenshotDir == null) {
			screenshotDir = new File(dirName);
		}
		if (!(screenshotDir.exists() && screenshotDir.isDirectory())) {
			screenshotDir.mkdirs();
		}
	}
	
	
	
	protected void writeHtmlOnError(File dir) throws Exception{

		Date rightNow = new Date();
		BufferedWriter out = new BufferedWriter(new FileWriter(dir.getCanonicalPath()
				 + File.separator + dateFormat.format(rightNow) + ".html"));
		out.write(getHtmlSource());
		out.close();
	}
	
	

	public static ExtendedSelenium getInstance(){
		if (instance == null) throw new NullPointerException("Selenium instance not set yet.");
		return instance;
	}
	
	public static ExtendedSelenium killInstance(){
		instance = null;//
		return instance;
	}
	
	public static ExtendedSelenium newInstance(String serverHost, int serverPort, String browserStartCommand, String browserURL){
		instance = new ExtendedSelenium(serverHost, serverPort, browserStartCommand, browserURL);
		return instance;
	}
	
	
}
