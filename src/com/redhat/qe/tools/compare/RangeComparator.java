package com.redhat.qe.tools.compare;

import java.util.Comparator;

public class RangeComparator<T> implements Comparator<T> {
	protected double maxDifference = 0.0;
	
	public RangeComparator(double maxDifference){
		this.maxDifference = maxDifference;
	}
	
	public int compare(Object o1, Object o2){
		//first make sure it's a number
		if (!(o1 instanceof Number && o2 instanceof Number)){
			throw new IllegalArgumentException("Can't compare non-Number objects " + o1.toString() +  " and " + o2.toString());
		}
		if (o1 == null && o2 == null)return 0;
		Double n1,n2;
		if (o1 instanceof Integer) n1 = ((Integer)o1).doubleValue();
		else n1 = (Double)o1;
		if (o1 instanceof Integer) n2 = ((Integer)o2).doubleValue();
		else n2 = (Double)o2;
		
		if (Math.abs(n1 - n2) <= maxDifference) return 0;
		else if (n1 > n2) return 1;
		else return -1;
	}
	
	public String toString(){
		return " within " + maxDifference + " of ";
	}

}
