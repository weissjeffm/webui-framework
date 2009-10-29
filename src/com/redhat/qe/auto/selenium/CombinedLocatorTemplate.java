package com.redhat.qe.auto.selenium;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A CombinedLocatorTemplate is used to create a higher level LocatorStrategy
 * that re-uses multiple LocatorTemplates.  Basically this concatenates the selenium
 * locator template for multiple LocatorTemplates into a single LocatorStrategy.
 *
 * @author jsefler
 */
public class CombinedLocatorTemplate extends LocatorTemplate {
	
	public CombinedLocatorTemplate(String name, LocatorStrategy... locatorStrategies) {
		super(name, "");  // initialize the name (set the template at the end)
		
		// construct the combined template
		StringBuffer combinedTemplate = new StringBuffer();
		int numArgsInPriorLocatorStrategy =0;
		for (LocatorStrategy locatorStrategy : locatorStrategies) {
			String template = locatorStrategy.getTemplate();
			combinedTemplate.append(increment(numArgsInPriorLocatorStrategy, template));
			numArgsInPriorLocatorStrategy += countArgs(template);
		}
		this.template = combinedTemplate.toString(); // set the template
	}
	
	/**
	 * A template contains place holders for arguments in the format "$d" where d is an integer >= 1.
	 * This method counts the number of unique occurrences of "$d".
	 * @param template - Example: "//tr[td[normalize-space(.)='$1' or normalize-space(.)='$1 *']]//img[@alt='$2']"
	 * @return - Example: 2
	 */
	protected static int countArgs(String template){
		//return template.split("\\$\\d+").length -1; // does not account for unique args
		
		// count the number of unique occurrences of "$d" where d is an integer >= 1
		int numArgs=0;
		for (int i = template.split("\\$\\d+").length; i > 0 ; i--) {  // count backwards so we don't replace the first digit of a two digit arg 
			while (template.contains("$"+i)) {
				numArgs++;
				template = template.replace("$"+i, "");
			}
		}
		return numArgs;
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
		
		System.out.println(CombinedLocatorTemplate.countArgs("//s[@id='$1' a$3rd @class='$2']"));
		System.out.println(CombinedLocatorTemplate.countArgs("//s[@id='$1' a$3rd @class='$2' or '$2 *']"));
		System.out.println(CombinedLocatorTemplate.increment(3, "//s[@id='$1' a$3rd @class='$2']"));
		System.out.println(CombinedLocatorTemplate.increment(3,"//s[@id='$1' a$3rd @class='$2' or '$2 *']"));
	}
	
}
