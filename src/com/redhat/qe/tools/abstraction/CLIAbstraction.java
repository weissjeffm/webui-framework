package com.redhat.qe.tools.abstraction;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

public abstract class CLIAbstraction {
	protected static Logger log = Logger.getLogger(CLIAbstraction.class.getName());
	protected Hashtable<String, String> regexCriterion;
	
	public CLIAbstraction() {
		regexCriterion = new Hashtable<String, String>();
	}

	public void appendRegexCriterion(String name, String regex) {
		regexCriterion.put(name, regex);
	}

	public void appendRegexCriterion(Hashtable<String, String> regex) {
		regexCriterion.putAll(regex);
	}

	public ArrayList<Hashtable<String, String>> match(String input) throws NullPointerException{
		ArrayList<Hashtable<String, String>> rtn = new ArrayList<Hashtable<String, String>>();
		// TODO: Find a better way than O(n^2)
		try {

			for (String key : regexCriterion.keySet()) {
				// Possible Issues:
				// 	If therre's more than one regex match per group, this method
				// 	will produce incorrect results. 
				Pattern pattern = Pattern.compile(regexCriterion.get(key), Pattern.MULTILINE);
				Matcher matcher = pattern.matcher(input);
				int elementNum = 0;
				while (matcher.find()) {
					if (rtn.size() < elementNum + 1) {
						rtn.add(new Hashtable<String, String>());
					}
					
					if (matcher.groupCount() > 0) { // captured result from regex
						rtn.get(elementNum).put(key, matcher.group(1).trim());
					}
					else {
						rtn.get(elementNum).put(key, matcher.group(0).trim());
					}
					elementNum++;
				}
			}
		}
		catch (NullPointerException e) {
			throw e;			
		}
		return rtn;
	}
}
