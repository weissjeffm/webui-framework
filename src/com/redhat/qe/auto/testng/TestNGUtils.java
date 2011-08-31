package com.redhat.qe.auto.testng;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class TestNGUtils {
	
	public static Object[][] convertListOfListsTo2dArray(List<List<Object>> list) {
		if (list.size() == 0) return new Object[0][0]; // avoid a null pointer exception
		
		//convert list to 2-d array
		Object[][] array = new Object[list.size()][];
		int i=0;
		for (List<Object> item: list){
			array[i] = item.toArray();
			i++;
		}
		return array;
	}
	
	public static Object[][] convertListTo2dArray(ArrayList<HashMap<String, String>> list) {
		if (list.size() == 0) return new Object[0][0]; // avoid a null pointer exception
		Object[][] array = new Object[list.size()][];
		int i=0;
		for (Object item: list){
			array[i] = new Object[]{item};
			i++;
		}
		return array;
	}

}
