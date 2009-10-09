package com.redhat.qe.auto.testng;

import java.util.List;

public class TestNGUtils {
	
	public static Object[][] convertListOfListsTo2dArray(List<List<Object>> list) {
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
