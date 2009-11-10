package com.redhat.qe.tools.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides a way to naturally sort a Collection.
 * Reference: http://stackoverflow.com/questions/740299/how-do-i-sort-a-set-in-java
 */
public class CollectionSorter {

//	@SuppressWarnings("unchecked")
//	public static <T extends Comparable> List<T> asSortedList(Collection<T> collection) {
//	  T[] array = collection.toArray(
//	    (T[])new Comparable[collection.size()]);
//	  Arrays.sort(array);
//	  return Arrays.asList(array);
//	}
	
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

}
