package com.redhat.qe.api.helper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcStruct;


public class TestHelper {
	
	/**
	 * Prints a out an array to console
	 * String array
	 * @param array
	 */
	public void printArray(String[] array){
		for(String str : array){
			System.out.println(str);
		}
	}
	
	/**
	 * Casts the object as a list array and prints
	 * @param array
	 */
	public void printArray(Object array){
		List myArray = (List) array;
		 Iterator i = myArray.iterator();
	        while (i.hasNext()) {
	            System.out.println(i.next());
	        }
	}
	
	/*public void printOneRowOfArray(Object array, int start, int end){
		List myArray = (List) array;
	       List listSub = myArray.subList(start, end);
	        System.out.println("sub"+listSub.toString());
	}*/
	
	/**
	 * prints a specific row of an array
	 */
	public void getRowOfArray(List array,int row){
		//System.out.println("Object = " + array.getClass().toString());
		System.out.println("getArray"+  array.get(row).toString());
	}
	
	/**
	 * Gets the keys from a List, and pulls the keys into an array and returns
	 * @param results
	 * @return ArrayList
	 */
	public ArrayList getKeys(List results){
		Object o = (Object) results.get(0);
		HashMap h = (HashMap) o;
		Iterator i = h.keySet().iterator();
		ArrayList<String> myKeys = new ArrayList<String>();
		
		while(i.hasNext()){
			String key = (String) i.next();
			myKeys.add(key);
		}
		return myKeys;	
	}
	
	/**
	 * Gets the keys from a Map, and pulls the keys into an array and returns
	 * @param results
	 * @return ArrayList
	 */
	public ArrayList getKeys(Map results){
		ArrayList<String> myKeys = new ArrayList<String>();
		myKeys = (ArrayList<String>) results.keySet();
		return myKeys;	
	}
	

	
	/**
	 * Returns the key of a hashmap as an object
	 * This is not really needed
	 * @param results
	 * @param keyString
	 * @return
	 * @deprecated
	 */
	private Object getKey(List results, String keyString){
		Object keyObject = null;
		Object o = (Object) results.get(0);
		HashMap h = (HashMap) o;
		Iterator i = h.keySet().iterator();
		while(i.hasNext()){
			Object tmp =  i.next();
			if(keyString.equalsIgnoreCase(tmp.toString())){
				keyObject = tmp;
			}
		}	
		return keyObject;
	}
	
	/**
	 * Takes an array of objects or list and returns all the values of each for a given key
	 * @param results
	 * @param key
	 * @return ArrayList
	 */
	public ArrayList getResultsForKey(List results,String key){
		ArrayList<String> myResults = new ArrayList<String>();
		for(Object obj : results){
			HashMap hash = (HashMap) obj;
			myResults.add(hash.get(key).toString());
		}
		return myResults;
	}
	
	/**
	 * Return a string of the value for a specificied key found by an associated know key and its known value
	 * @param results
	 * @param key
	 * @param associatedKey
	 * @param associatedKeyValue
	 * @return String
	 */
	public String getValueOfKeyWithAttributes(List results, String key, String associatedKey, String associatedKeyValue){
		String result = null;
		for(Object obj : results){
			HashMap hash = (HashMap) obj;
			if((hash.get(associatedKey).toString()).equalsIgnoreCase(associatedKeyValue)){
				result = hash.get(key).toString();
			}
		}
		return result;	
	}
	
	/**
	 * If the value of the specified key matches the specified value, return the whole hash map 
	 * @param results
	 * @param key
	 * @param value
	 * @return HashMap
	 */
	public HashMap findHashFromListWithAttribute(List results, Object key, Object value){
		for(Object obj : results){
			HashMap hash = (HashMap) obj;
			if(hash.get(key).equals(value))
				return hash;
		}
		return null;
	}
	
	
}