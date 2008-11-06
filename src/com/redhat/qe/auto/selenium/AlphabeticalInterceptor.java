package com.redhat.qe.auto.selenium;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

public class AlphabeticalInterceptor implements IMethodInterceptor {

	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> methods,
			ITestContext context) {
		Comparator<IMethodInstance> alphaByMethodName = new Comparator<IMethodInstance>(){

			@Override
			public int compare(IMethodInstance m1, IMethodInstance m2) {
				String name1 = m1.getMethod().getMethodName();
				String name2 = m2.getMethod().getMethodName();			
				return name1.compareTo(name2);
			}
			
		};
		Collections.sort(methods, alphaByMethodName);
		return methods;
	}

}
