package com.redhat.qe.auto.selenium;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ITestResult;
import org.testng.Reporter;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;

/**
 * This class extends the DefaultSelenium functionality.  It 
 * provides logging of UI actions (via java standard logging),
 * and some convenience methods.
 * @author jweiss
 *
 */
public class ExtendedSelenium extends DefaultSelenium implements ITestNGScreenCapture, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4832886620261520916L;

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
		log.finer("Start selenium.");
		super.start();

		// TODO this is ugly
		TestNGListener.setScreenCaptureUtility(this);
		sleep(3);
		windowFocus();
		windowMaximize();
		String delay = System.getProperty("selenium.delay");
		if (delay != null)  {
			try {
				setSpeed(delay);
			}
			catch(Exception e){
				log.log(Level.FINER, "Could not set delay: " + delay, e);
			}
		}
	}
	

	@Override
	public void stop() {
		log.finer("Stop selenium.");
		super.stop();
		//added this as part of a fix to guarantee that only instance of selenium
		//is running.  So be sure that there is only one browser session up at a time
		killInstance();
		
		//debugging this, because screenshots are getting taken too late on hudson wdh
		//log.fine("Selenium stopped.");
		//log.log(MyLevel.FINER,"Selenium stopped");
		//log.info("Selenium stopped");

	}
	
	
	public boolean isEditable(Element element) {
		return super.isEditable(element.getLocator());
	}

	public boolean isChecked(Element element) {
		return super.isChecked(element.getLocator());
	}

	public String getValue(Element element) {
		return getValue(element.getLocator());
	}

	public void clickAndWait(String locator) {
		clickAndWait(locator, WAITFORPAGE_TIMEOUT, true);
	}
	
	public void clickAndWait(Element element) {
		click(element);
		waitForPageToLoad(WAITFORPAGE_TIMEOUT);
	}

		
	public void clickAndWait(String locator, String timeout) {
		clickAndWait(locator, timeout, true);
	}

	public void clickAndWait(Element element, String timeout) {
		clickAndWait(element.getLocator(), timeout);
	}

	public void clickAndWait(String locator, String timeout, boolean highlight) {
		click(locator, highlight);
		waitForPageToLoad(timeout);
	}
	
	public void clickAndWait(Element element, String timeout, boolean highlight) {
		clickAndWait(element.getLocator(), timeout, highlight);
	}

	public void selectAndWait(String selectLocator, String optionLocator){
		select(selectLocator, optionLocator);
		waitForPageToLoad();
	}
	
	public void selectAndWait(Element element, String optionLocator){
		selectAndWait(element.getLocator(), optionLocator);
	}
	
	public void waitForPageToLoad(){
		waitForPageToLoad(WAITFORPAGE_TIMEOUT);
	}
	
	@Override
	public void waitForPageToLoad(String timeout){
		log.finer("Wait for page to load.");
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
	public void click(String locator, boolean highlight)  {
		log.log(MyLevel.ACTION, "Click on " + getDescription(locator));
		if (highlight)
			highlight(locator);
		super.click(locator);
	}

	@Override
	public void click(String locator) {
		click(locator, true);
	}
	
	//TODO need to have logging like this on all similar methods  --JMW 10/5/09
	public void click(Element element) {
		logClick(element);
		super.click(element.getLocator());
	}
	
	protected void logClick(Element element){
		Element humanReadable = element.getHumanReadable();
		if (humanReadable != null) {
			try {
				log.log(MyLevel.ACTION, "Click on element: " + this.getText(humanReadable));
				return;
			} catch(Exception e) {
				log.log(Level.FINEST, "Unable to get text for associated human readable element: " + humanReadable, e);
			}		
		}
	    log.log(MyLevel.ACTION, "Click on " + getDescription(element));
	}
	
	public String getText(Element element){
		return getText(element.getLocator());
	}
	
	@Override
	public void mouseOver(String locator) {
		log.log(MyLevel.ACTION, "Hover over " + getDescription(locator));
		super.mouseOver(locator);

	}
	
	public void mouseOver(Element element) {
		log.log(MyLevel.ACTION, "Hover over " + getDescription(element));
		super.mouseOver(element.getLocator());
	}

	
	
	public void keyPress(Element element, String keySequence) {
		// TODO Auto-generated method stub
		super.keyPress(element.getLocator(), keySequence);
	}

	/**
	 * @param locator
	 * @param highlight - if true, highlight the element for a fraction of a second before clicking it.
	 *   This makes it easier to see what selenium is doing "live".
	 */
	public void click(String locator, String humanReadableName,boolean highlight) {
		log.log(MyLevel.ACTION, "Click on : " + humanReadableName);
		if (highlight)
			highlight(locator);
		super.click(locator);
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
	
	public void waitAndClick(Element element, String timeout) {
		super.waitForCondition("selenium.isElementPresent(\"" + element.getLocator() + "\");", timeout);
		click(element);
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
	
	public void waitAndClickAndWait(Element element, String timeout1, String timeout2) {
		waitAndClickAndWait(element.getLocator(), timeout1, timeout2);
	}

	
	public void waitAndClickAndWait(String locator, String timeout1){
		waitAndClickAndWait(locator, timeout1, WAITFORPAGE_TIMEOUT);
	}
	
	public void waitAndClickAndWait(Element element, String timeout1) {
		waitAndClickAndWait(element.getLocator(), timeout1);
	}
	
	public void waitForElement(String locator, String timeout){
		log.info("Waiting for element '" + locator  + "', with timeout of " + timeout + ".");
		super.waitForCondition("selenium.isElementPresent(\"" + locator + "\");", timeout);
	}
	
	public void waitForElement(Element element, String timeout){
		log.info("Wait for element '" + element  + "', with timeout of " + timeout + ".");
		super.waitForCondition("selenium.isElementPresent(\"" + element.getLocator() + "\");", timeout);
	}
	
	public void waitForInvisible(String locator, String timeout){
		// if the locator is not present, then it is effectively invisible
		if (!super.isElementPresent(locator)) return;
		log.info("Wait for element to be invisible '" + locator  + "', with timeout of " + timeout + ".");
		super.waitForCondition("!selenium.isVisible(\"" + locator + "\");", timeout);
	}
	
	public void waitForInvisible(Element element, String timeout){
		waitForInvisible(element.getLocator(), timeout);
	}
	
	@Override
	public void type(String locator, String value) {
		log.log(MyLevel.ACTION, "Type '" + value + "' into " + getDescription(locator));
		highlight(locator);
		super.type(locator, value);
	}
	
	public void type(Element element, String value) {
		type(element.getLocator(), value);
	}
	
	@Override
	public void typeKeys(String locator, String value) {
		log.log(MyLevel.ACTION, "Type keys '" + value + "' into " + getDescription(locator));
		highlight(locator);
		super.typeKeys(locator, value);
	}
	
	public void typeKeys(Element element, String value) {
		typeKeys(element.getLocator(), value);
	}
	
	public void type(String locator,String humanReadableName, String value) {
		log.log(MyLevel.ACTION, "Type '" + value + "' into " + getElementType(locator) + ": "
				+ humanReadableName + "'");
		highlight(locator);
		super.type(locator, value);
	}
	
	public void setText(String locator, String value){
		type(locator, value);
	}
	
	public void setText(Element element, String value){
		log.log(MyLevel.ACTION, "Type '" + value + "' into " + getDescription(element));
		highlight(element.getLocator());
		super.type(element.getLocator(), value);
	}
	
	public void setText(String locator, String humanReadableName,String value){
		type(locator,humanReadableName, value);
	}
	

	@Override
	public void open(String url) {
		log.log(MyLevel.ACTION, "Open URL '" + url + "'.");  
		super.open(url);
		log.info("Current URL is " + getLocation() + " .");
	}

	
	
	@Override
	public void check(String locator) {
		log.log(MyLevel.ACTION, "Check " + getDescription(locator));
		checkUncheck(locator, true);
	}
	
	public void check(Element element){
		log.log(MyLevel.ACTION, "Check " + element);
		checkUncheck(element, true);
	}
	
	
	public void checkUncheck(String locator, boolean check){
		if (isChecked(locator) != check) {
			super.click(locator);
			if (isChecked(locator) != check)super.check(locator); //just to be sure
		}
		else log.log(Level.FINE, getDescription(locator) + " is already " + (check ? "checked.": "unchecked."));
	}
	
	public void checkUncheck(Element element, boolean check){
		checkUncheck(element.getLocator(), check);
	}

	@Override
	public void select(String selectLocator, String optionLocator) {
		log.log(MyLevel.ACTION, "Select item '"
				+ optionLocator + "' in list '" + selectLocator + "'.");
		super.select(selectLocator, optionLocator);
	}
	
	public void select(Element element, String optionLocator) {
		Element humanReadable = element.getHumanReadable();
		if (humanReadable != null) {
			try {
				log.log(MyLevel.ACTION, "Select item '"
						+ optionLocator + "' in list " + getText(humanReadable));
				return;
			} catch(Exception e) {
				log.log(Level.FINEST, "Unable to get text for associated human readable element: " + humanReadable, e);
			}		
		}

		log.log(MyLevel.ACTION, "Select item '"
				+ optionLocator + "' in list " + element);
		super.select(element.getLocator(), optionLocator);
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
	
	public void select(Element element){
		select(element.getLocator());
	}

	@Override
	public void uncheck(String locator) {
		log.log(MyLevel.ACTION, "Uncheck " + getDescription(locator));
		checkUncheck(locator, false);
	}
	
	public void uncheck(Element element){
		log.log(MyLevel.ACTION, "Uncheck " + element);
		checkUncheck(element, false);
	}
	
	/*
	 * @see com.thoughtworks.selenium.DefaultSelenium#isElementPresent(java.lang.String)
	 */
	@Override
	public boolean isElementPresent(String element){
		return isElementPresent(element, Level.FINER);
	}
	
	public boolean isElementPresent(Element element){
		return isElementPresent(element.getLocator());
	}
	
	public boolean isElementPresent(TabElement element){
		return (isElementPresent(element.getLocator()) |
				isElementPresent(element.getAlternateElement().getLocator()));
	}
	
	public boolean isElementSelected(TabElement tabElement) {
		if (tabElement.getAlternateElement().equals(tabElement)) {
			log.log(MyLevel.WARNING, "Do not know how to determine if this tab element is selected: "+this);
			return false;
		}
		return isElementPresent(tabElement.getAlternateElement().getLocator()) && !isElementPresent(tabElement.getLocator());
	}
	
	public boolean isElementPresent(String element,Level level){
		if(super.isElementPresent(element)){
			log.log(level,"Found " + getDescription(element));
			highlight(element);
			return true;
		}
		else {	
			log.log(level, "Did not find " + getDescription(element));
			return false;
		}
	}
	
	public boolean isElementPresent(Element element,Level level){
		return isElementPresent(element.getLocator(), level);
	}
	
	public boolean isTextPresent(String txt, Level level){
		if(super.isTextPresent(txt)){
			log.log(level,"Found text: '"+ txt+"'");
			return true;
		}
		else {	
			log.log(level, "Did not find text: '"+ txt+"'");
			return false;
		}
	}
	
	@Override
	public void goBack(){
		log.log(MyLevel.ACTION, "Click Browser Back Button");
		super.goBack();
		waitForPageToLoad();
	}
	
	@Override
	public void refresh(){
		log.log(MyLevel.ACTION, "Click Browser Refresh Button");
		super.refresh();
		waitForPageToLoad();
	}
	

	public boolean isElementPresentWithRefreshing(String locator, long timeout_ms, long refreshInterval_ms){
		if (isElementPresent(locator))
			return true;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < timeout_ms ){
			sleep(refreshInterval_ms);
			refresh();
		
			if (isElementPresent(locator))
				return true;
		}
		return false;
	}
	
	public boolean isElementPresentWithRefreshing(Element element, long timeout_ms, long refreshInterval_ms){
		return isElementPresentWithRefreshing(element.getLocator(), timeout_ms, refreshInterval_ms);
	}

	@Override
	public String getAlert() {
		log.log(MyLevel.ACTION, "Click OK on alert dialog.");
		String text = super.getAlert();
		log.log(Level.INFO, "Dismissed alert dialog: " + text);
		return text;
	}

	@Override
	public String getConfirmation() {
		log.log(MyLevel.ACTION, "Click OK on confirmation dialog.");
		String text = super.getConfirmation();
		log.log(Level.INFO, "Dismissed confirmation dialog: " + text);
		return text;
	}

	@Override
	public String getPrompt() {
		log.log(MyLevel.ACTION, "Click OK on prompt dialog.");
		String text = super.getPrompt();
		log.log(Level.INFO, "Dismissed prompt dialog: " + text);
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
	
	/**
	 * Retrieves the current value for Selenium wait for page timeout. 
	 * @return Current value of: WAITFORPAGE_TIMEOUT
	 */
	public String getTimeout(){
		return WAITFORPAGE_TIMEOUT;
	}
	
	public void selectPopupWindowAndWait(){
		String[] winnames = getAllWindowNames();
		String name = winnames[winnames.length-1]; //select last opened window
		waitForPopUp(name, "60000");
		selectWindow(name);
	}

	public void sleep(long millis){
		try {
			log.log(Level.INFO, "Sleep for " + millis + "ms.");
			Thread.sleep(millis);
		}
		catch(InterruptedException ie){
			log.log(Level.WARNING, "Sleep interrupted!", ie);
		}
	}
	
	/**
	 * Gets the HTML attributes for a given locator
	 * @param locator 
	 * @return a Properties object containing all the attributes of the 
	 * element.  Also includes a "tagName" attribute which contains the tag name,
	 * eg, input, a, div, etc.
	 * @throws IOException
	 */
	public Properties getAttributes(String locator) {
		String attributesScript =
			"{" +
				"var elem =  this.browserbot.findElement(\"" + locator + "\");" +
				"var attrs = elem.attributes;" +
				"var str='tagName=' + elem.tagName + '\\n';" +
				"for(var i = 0; i < attrs.length; i++) {" +
				"  	str = str + attrs[i].name + '=' + attrs[i].value + '\\n';" +
				"};" +
				"str;" +  // the value of str is the returned String result from getEval(attributesScript);
			"}";
		Properties props = new Properties();
		String result = getEval(attributesScript);
		StringBuffer StringBuffer1 = new StringBuffer(result);
		try {
			ByteArrayInputStream Bis1 = new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));
			props.load(Bis1);
		}catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
		return props;
	}
	
	public Properties getAttributes(Element element) {
		return getAttributes(element.getLocator());
	}
	public String getElementType(Element element) {
		return getElementType(element.getLocator());
	}
	
	/**
	 * Return a string description of what was acted upon by selenium
	 * @param element
	 * @return
	 */
	public String getDescription(Element element) {
		String elementStr = element.toString();
		String elementType = "";
		try {
			 elementType = getElementType(element);
		}catch(Exception e) {
			log.log(Level.FINER, "Could not retrieve element type, perhaps it is not present: " + elementStr, e);
		}
		elementStr =elementStr.replaceAll("^" +elementType + " ", ""); //remove duplicate element type strings
		return elementType + ": " + elementStr;
	}
	
	public String getDescription(String locator) {
		try {
			return getElementType(locator) + ": " + locator;
		} catch(Exception e) {
			log.log(Level.FINER, "Could not get element type for '" + locator + "', perhaps it is not present?", e);
		}
		return locator;
	}
	
	
	
	public String getElementType(String locator) {
		
		Properties attrs;
		try {
			attrs = getAttributes(locator);
		}
		catch (Exception e){
			//if attributes can't be retrieved, log and return the locator
			log.log(Level.FINER, "Can't retrieve attributes for locator: " + locator, e);
			return locator;
		}
		String tagName = attrs.getProperty("tagName").toLowerCase();
		if (tagName.equals("input")) {
			String type = null;
			try {
				type = attrs.getProperty("type").toLowerCase();
			}
			catch(NullPointerException npe) {
				return "textbox";
			}
			if (type.equals("text")) return "textbox";
			if (type.equals("button")) return "button";
			if (type.equals("checkbox")) return "checkbox";
			if (type.equals("image")) return "input image";
			if (type.equals("password")) return "password textbox";
			if (type.equals("radio")) return "radio button";
			if (type.equals("submit")) return "submit button";
			return tagName + " " + type;
		}
		else if (tagName.equals("a")) return "link";
		else if (tagName.equals("select")) return "selectlist";
		else if (tagName.equals("div")) return "link";
		else if (tagName.equals("img")) return "image";
		else if (tagName.equals("td")) return "table cell";
		else if (tagName.equals("span")) return "link";
		else return tagName;	
	}
	
	
	public String screenCapture() throws Exception {
		String dirName = System.getProperty("selenium.screenshot.dir", System.getProperty("user.dir") + File.separator
		+ "screenshots");
		return screenCapture(dirName);
	}
	
	protected void writeBase64ScreenCapture(String data, File file) throws FileNotFoundException, IOException{
		byte[] pngBytes = Base64.decode(data);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(pngBytes);
		fos.flush();
		fos.close();
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
			fullPathtoFile = localHtmlDir.getCanonicalPath()+ File.separator + outFileName;
			pngRemoteScreenCapture(fullPathtoFile);
		}
		catch(Exception e ){
			log.log(Level.FINER, "Couldn't capture screenshot, trying to write to tmp dir instead.",e);
			//if this failed, try the temp dir
			screenshotDir = new File("/tmp");
			super.captureScreenshot("/tmp"+ File.separator + outFileName);
			//log.log(Level.FINE, "Captured ScreenShot to "+"/tmp"+ File.separator + outFileName);
			fullPathtoFile = "/tmp"+ File.separator + outFileName;
			
			//writeHtmlOnError(screenshotDir);		
		}
		return fullPathtoFile;
	}
	
	public void testNGScreenCapture(ITestResult result) throws Exception{
		String dirName = System.getProperty("selenium.screenshot.dir", System.getProperty("user.dir") + File.separator
				+ "screenshots");
		Date rightNow = new Date();
		String outFileName = dateFormat.format(rightNow) + "-" + result.getTestClass().getName() + "." + result.getMethod().getMethodName() + ".png";
		String fullpath = dirName + File.separator + outFileName;
		pngRemoteScreenCapture(fullpath);
		
		//embed link in testng report
		Reporter.setCurrentTestResult(result);
		Reporter.log("<a href='" + new File(fullpath).toURI().toURL() + "'>Screenshot</a>");
	}
	
	protected void pngRemoteScreenCapture(String filepath) throws Exception{
		String base64Png = super.captureEntirePageScreenshotToString("");
		File ssFile = new File(filepath);
		writeBase64ScreenCapture(base64Png, ssFile);
		log.log(Level.FINE, "screenshot URL= "+ getLocation());
		log.log(Level.FINE, "Captured screenshot to "+ ssFile.toURI().toURL());
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
	
	public static void killInstance(){
		instance = null;//
	}
	
	
	
	public static ExtendedSelenium newInstance(String serverHost, int serverPort, String browserStartCommand, String browserURL){
		instance = new ExtendedSelenium(serverHost, serverPort, browserStartCommand, browserURL);
		return instance;
	}
	
	
}
