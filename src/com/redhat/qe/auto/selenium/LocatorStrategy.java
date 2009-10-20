package com.redhat.qe.auto.selenium;

public interface LocatorStrategy {
	public String getName();
	public String getLocator(String... arg);
	public String getTemplate();
}
