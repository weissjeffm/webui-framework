package com.redhat.qe.auto.selenium;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A CombinedLocatorTemplate is used to create a higher level LocatorStrategy
 * that re-uses multiple LocatorTemplates.  Basically is concatenate the selenium
 * locator template for a multiple LocatorTemplates into a single LocatorStrategy.
 *
 * @author jsefler
 */
public class CombinedLocatorTemplate extends LocatorTemplate {
	
	public CombinedLocatorTemplate(String name, LocatorStrategy... locatorStrategies) {
		super(name, "");  // 
		
		// construct the combined template
		StringBuffer combinedTemplate = new StringBuffer();
		int numArgsInPriorLocatorStrategy =0;
		for (LocatorStrategy locatorStrategy : locatorStrategies) {
			String template = locatorStrategy.getTemplate();
			combinedTemplate.append(increment(numArgsInPriorLocatorStrategy, template));
			numArgsInPriorLocatorStrategy += countArgs(template);
		}
		this.template = combinedTemplate.toString();
	}
	
	protected static int countArgs(String template){
		return template.split("\\$\\d+").length -1;
	}
	
	protected static String increment(int count, String template){
		Pattern p = Pattern.compile("\\$(\\d+)");
		Matcher m = p.matcher(template);
		while (m.find()){
			int num = Integer.parseInt(m.group(1));
			int newNum = num + count;
			template = template.replaceAll("\\$"+num, "\\$"+newNum);
			//System.out.println(template);
			
		}
		return template;
	}
	
	public static void main(String[] args){
		
		System.out.println(CombinedLocatorTemplate.increment(3, "//s[@id='$1' a$33nd @class='$2']"));
	}
	
}
