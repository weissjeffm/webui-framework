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
	protected List<String> groupId;
	
	public CLIAbstraction() {
		regexCriterion = new Hashtable<String, String>();
		groupId = new ArrayList<String>();
	}

	public void appendRegexCriterion(String name, String regex) {
		regexCriterion.put(name, regex);
	}

	public void appendRegexCriterion(Hashtable<String, String> regex) {
		regexCriterion.putAll(regex);
	}

	public void appendGroupId(String id) {
		groupId.add(id);
	}

	public void appendGroup(List ids) {
		try {
			groupId.addAll(()ids);
		}
		catch (Exception e) {
			log.severe("Cannot add ids to groupId.");
			e.printStackTrace();
		}
	}
	
	public ArrayList<Hashtable<String, String>> match(String input) throws NullPointerException{
		ArrayList<Hashtable<String, String>> rtn = new ArrayList<Hashtable<String, String>>();
		Iterator<String> itr = Arrays.asList(input.trim().split("\n")).iterator();
		// TODO: Find a better way than O(n^2)
		try {
			while (itr.hasNext()) {
				String inputStmt = itr.next();
				Iterator<String> regexKeyItr = regexCriterion.keySet().iterator();
				Hashtable<String, String> matchedResult = new Hashtable<String, String>();
				while (regexKeyItr.hasNext()) {
					String regexKey = regexKeyItr.next();
					String regexStmt = regexCriterion.get(regexKey);

					Pattern pattern = Pattern.compile(regexStmt);
					Matcher matcher = pattern.matcher(inputStmt);
					if (matcher.find()) {
						if (matcher.groupCount() > 0) {
							// for now let's just record the first captured group
							matchedResult.put(regexKey, matcher.group(1).trim());
						}
						else {
							matchedResult.put(regexKey, inputStmt.trim());
						}
					}
				}
				if (matchedResult.keySet().size() > 0) {
					rtn.add(matchedResult);
				}
			}
		}
		catch (NullPointerException e) {
			throw e;			
		}
		return rtn;
	}

	/*
	public ArrayList<Hashtable<String, String>> groupMatch(String input) throws NullPointerException{
		ArrayList<Hashtable<String, String>> rtn = new ArrayList<Hashtable<String, String>>();
		try {
			Pattern pattern = Pattern.compile(regexCriterion);
			Matcher matcher = pattern.matcher(input);
			while (matcher.find()) {
			}
		}
		catch (NullPointerException e) {
			throw e;			
		}
		return rtn;
	}
	*/
}
