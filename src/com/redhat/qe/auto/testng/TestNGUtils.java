package com.redhat.qe.auto.testng;

import java.util.List;

public class TestNGUtils {
	
	public static Object[][] convertListOfListsTo2dArray(List<List<Object>> list) {
		if (list.size() == 0) return new Object[0][0]; // avoid a null pointer exception
		
		//convert list to 2-d array
		Object[][] array = new Object[list.size()][ list.get(0).size()];
		int i=0;
		for (List<Object> item: list){
			int j=0;
			for (Object param: item){
				array[i][j] = param;
				j++;
			}
			i++;
		}
		return array;
	}

}
