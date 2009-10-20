package com.redhat.qe.auto.selenium;

public class StringSandwichLocatorStrategy implements LocatorStrategy {

	protected String[] bread = null;
	protected String name = null;
	
	public StringSandwichLocatorStrategy(String name, String...bread){
		this.name = name;
		this.bread = bread;
	}
	
	@Override
	public String getLocator(String... meat) {
		//construct the sandwich - bread, meat, bread, meat, bread - until one runs out
		//always end with bread unless there isn't enough bread.  
		//bread always comes first.
		
		int smallerStack = bread.length > meat.length ? meat.length : bread.length;
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<smallerStack; i++){
			sb.append(bread[i]);
			sb.append(meat[i]);
		}
		if (bread.length > meat.length) sb.append(bread[meat.length]);
		return sb.toString();
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getTemplate() {
		String template=bread[0];
		for (int i = 1; i < bread.length; i++) {
			template += "$"+i+bread[i];
		}
		return template;
	}
	
	public static void main (String[] args){
		StringSandwichLocatorStrategy ssls1 = new StringSandwichLocatorStrategy("pbj", "bread1", "bread2");
		System.out.println(ssls1.getLocator("meat1"));
		System.out.println(ssls1.getLocator("meat1", "meat2"));
		System.out.println(ssls1.getLocator("meat1", "meat2", "meat3"));
		System.out.println(ssls1.getTemplate());
		
	}

}
