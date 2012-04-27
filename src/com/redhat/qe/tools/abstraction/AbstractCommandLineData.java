package com.redhat.qe.tools.abstraction;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AbstractCommandLineData {
	protected static String simpleDateFormat = "yyyy-MM-dd";	// 2010-07-01	// default SimpleDateFormat 
	protected static Logger log = Logger.getLogger(AbstractCommandLineData.class.getName());
	
	public AbstractCommandLineData(Map<String, String> productData){
		if (productData == null)
			return;
		
		for (String keyField : productData.keySet()){
			Field abstractionField = null;
			try {
				abstractionField = this.getClass().getField(keyField);
// this is wrong since an empty string is a valid non-null value
//				if (productData.get(keyField).equals("")) {
//					abstractionField.set(this, null);
//					log.finer("No value was parsed for abstractionField '"+this.getClass().getName()+"."+abstractionField.getName()+"'.  Setting it to null.");
//					continue;
//				}
				if (abstractionField.getType().equals(Calendar.class))
					abstractionField.set(this, this.parseDateString(productData.get(keyField)));
				else if (abstractionField.getType().equals(Integer.class))
					abstractionField.set(this, this.parseInt(productData.get(keyField)));
				else if (abstractionField.getType().equals(File.class))
					abstractionField.set(this, this.parseFile(productData.get(keyField)));
				else if (abstractionField.getType().equals(Long.class))
					abstractionField.set(this, this.parseLong(productData.get(keyField)));
				else if (abstractionField.getType().equals(BigInteger.class))
					abstractionField.set(this, this.parseBigInteger(productData.get(keyField)));
				else if (abstractionField.getType().equals(Boolean.class))
					abstractionField.set(this, this.parseBoolean(productData.get(keyField)));
				else
					abstractionField.set(this, productData.get(keyField));
			} catch (Exception e){
				log.warning("Exception caught while parsing the value for this abstraction field: " + e.getMessage());
				if (abstractionField != null)
					try {
						abstractionField.set(this, null);
					} catch (Exception x){
						log.warning("...and an exception was thrown setting it to null.");
					}
				for (StackTraceElement ste:e.getStackTrace()){
					log.warning(ste.toString());
				}
			}
		}
	}
	
	
	//@Override
	public boolean equals(Object obj) {
		AbstractCommandLineData certObj = (AbstractCommandLineData)obj;
		for(Field certField:certObj.getClass().getDeclaredFields()){
			
			try {
				Field correspondingField = this.getClass().getField(certField.getName());
				if (correspondingField.get(this)==null && certField.get(certObj)==null) continue;
				if (correspondingField.get(this)==null && certField.get(certObj)!=null) return false;
				if (!correspondingField.get(this).equals(certField.get(certObj))) return false;
			} catch (Exception e)  {
				log.warning("Exception caught while comparing abstraction fields: " + e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	
	// protected methods ************************************************************

	protected Calendar parseDateString(String dateString){
		return parseDateString(dateString, simpleDateFormat);
	}
	
	protected Calendar parseDateString(String dateString, String simpleDateFormat){
		try{
			DateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);
			dateFormat.setTimeZone(TimeZone.getDefault());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateFormat.parse(dateString));
			return calendar;
		}
		catch (ParseException e){
			log.warning("Failed to parse date string '"+dateString+"' with format '"+simpleDateFormat+"':\n"+e.getMessage());
			return null;
		}
	}

	public static String formatDateString(Calendar date){
		DateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);
		dateFormat.setTimeZone(TimeZone.getDefault());
		return dateFormat.format(date.getTime());
	}
	
	protected Boolean parseBoolean(String booleanString){
		if (booleanString.toLowerCase().equals("true")) return Boolean.TRUE;
		if (booleanString.toLowerCase().equals("false")) return Boolean.FALSE;
		if (booleanString.toLowerCase().equals("yes")) return Boolean.TRUE;
		if (booleanString.toLowerCase().equals("no")) return Boolean.FALSE;
		if (booleanString.equals("1")) return Boolean.TRUE; 
		if (booleanString.equals("0")) return Boolean.FALSE;
		log.warning("Do not know how to infer a Boolean value from '"+booleanString+"'.");
		return null;
	}
	
	protected Integer parseInt(String intString){
		return Integer.parseInt(intString);
	}
	
	protected Long parseLong(String longString){
		return Long.parseLong(longString);
	}
	
	protected BigInteger parseBigInteger(String bigIntegerString){
		return new BigInteger(bigIntegerString);
	}
	
	protected File parseFile(String pathname){
		return new File(pathname);
	}
	
	static protected boolean addRegexMatchesToList(Pattern regex, String to_parse, List<Map<String,String>> matchList, String sub_key) {
		boolean foundMatches = false;
		Matcher matcher = regex.matcher(to_parse);
		int currListElem=0;
		while (matcher.find()){
			if (matchList.size() < currListElem + 1) matchList.add(new HashMap<String,String>());
			Map<String,String> matchMap = matchList.get(currListElem);
			matchMap.put(sub_key, matcher.group(1).trim());
			matchList.set(currListElem, matchMap);
			currListElem++;
			foundMatches = true;
		}
        if (!foundMatches) {
        	//log.warning("Could not find regex '"+regex+"' match for field '"+sub_key+"' while parsing: "+to_parse );
        	log.finer("Could not find regex '"+regex+"' match for field '"+sub_key+"' while parsing: "+to_parse );
        }
		return foundMatches;
	}
			
	static protected boolean addRegexMatchesToMap(Pattern regex, String to_parse, Map<String, Map<String,String>> matchMap, String sub_key) {
        Matcher matcher = regex.matcher(to_parse);
        boolean foundMatches = false;
        while (matcher.find()) {
            Map<String,String> singleCertMap = matchMap.get(matcher.group(1));
            if(singleCertMap == null){
            	Map<String,String> newBranch = new HashMap<String,String>();
            	singleCertMap = newBranch;
            }
            singleCertMap.put(sub_key, matcher.group(2));
            matchMap.put(matcher.group(1), singleCertMap);
            foundMatches = true;
        }
        if (!foundMatches) {
        	//log.warning("Could not find regex '"+regex+"' match for field '"+sub_key+"' while parsing: "+to_parse );
        	log.finer("Could not find regex '"+regex+"' match for field '"+sub_key+"' while parsing: "+to_parse );
        }
        
        return foundMatches;
	}
	
	
	
	
	/**
	 * Given a List of instances of some class that derives from AbstractCommandLineData, this
	 * method is useful for finding the first instance whose public field by the name "fieldName"
	 * has a value that matches fieldValue.  If no match is found, null is returned.
	 * @param <T>
	 * @param fieldName
	 * @param fieldValue
	 * @param dataInstances
	 * @return
	 */
	public static <T> T findFirstInstanceWithMatchingFieldFromList(String fieldName, Object fieldValue, List<T> dataInstances) {
		List<T> dataInstancesWithMatchingField = findAllInstancesWithMatchingFieldFromList(fieldName,fieldValue,dataInstances);
		if (dataInstancesWithMatchingField.isEmpty()) {
			return null;
		}
		return dataInstancesWithMatchingField.get(0);
	}
	/**
	 * Same as findFirstInstanceWithMatchingFieldFromList except that when comparing the fieldValue, an .equalsIgnoreCase(fieldValue) comparison is made.
	 * Note that this method works only for fieldValues of type String.
	 * @param <T>
	 * @param fieldName
	 * @param fieldValue
	 * @param dataInstances
	 * @return
	 */
	public static <T> T findFirstInstanceWithCaseInsensitiveMatchingFieldFromList(String fieldName, String fieldValue, List<T> dataInstances) {
		List<T> dataInstancesWithMatchingField = findAllInstancesWithCaseInsensitiveMatchingFieldFromList(fieldName,fieldValue,dataInstances);
		if (dataInstancesWithMatchingField.isEmpty()) {
			return null;
		}
		return dataInstancesWithMatchingField.get(0);
	}
	
	/**
	 * Given a List of instances of some class that derives from AbstractCommandLineData, this
	 * method is useful for finding a subset of instances whose public field by the name "fieldName"
	 * has a value that matches fieldValue.  If no match is found, an empty list is returned.
	 * @param <T>
	 * @param fieldName
	 * @param fieldValue
	 * @param dataInstances
	 * @return
	 */
	public static <T> List<T> findAllInstancesWithMatchingFieldFromList(String fieldName, Object fieldValue, List<T> dataInstances) {
		List<T> dataInstancesWithMatchingField = new ArrayList<T>();
		for (T dataInstance : dataInstances) {
			try {
				if (dataInstance.getClass().getField(fieldName).get(dataInstance).equals(fieldValue)) {
					dataInstancesWithMatchingField.add(dataInstance);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataInstancesWithMatchingField;
	}
	/**
	 * Same as findAllInstancesWithMatchingFieldFromList except that when comparing the fieldValue, an .equalsIgnoreCase(fieldValue) comparison is made.
	 * Note that this method works only for fieldValues of type String.
	 * @param <T>
	 * @param fieldName
	 * @param fieldValue
	 * @param dataInstances
	 * @return
	 */
	public static <T> List<T> findAllInstancesWithCaseInsensitiveMatchingFieldFromList(String fieldName, String fieldValue, List<T> dataInstances) {
		List<T> dataInstancesWithMatchingField = new ArrayList<T>();
		for (T dataInstance : dataInstances) {
			try {
				if (dataInstance.getClass().getField(fieldName).get(dataInstance) instanceof String ) {
					String instance = (String) dataInstance.getClass().getField(fieldName).get(dataInstance);
					if (instance.equalsIgnoreCase(fieldValue)) {
						dataInstancesWithMatchingField.add(dataInstance);
					}
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataInstancesWithMatchingField;
	}
	
}
