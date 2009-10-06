package com.redhat.qe.auto.selenium;

public interface LocatorStrategy {
	public String getLocator(String... arg);
	public String getName();
}
