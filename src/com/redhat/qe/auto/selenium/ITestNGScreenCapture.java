package com.redhat.qe.auto.selenium;

import org.testng.ITestResult;

public interface ITestNGScreenCapture extends IScreenCapture {
	public void testNGScreenCapture(ITestResult result) throws Exception;

}
