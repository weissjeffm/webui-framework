package com.redhat.qe.auto.testng;

import org.testng.IMethodInstance; 
import org.testng.IMethodInterceptor; 
import org.testng.ITestContext;
import java.lang.reflect.Method; 
import java.util.Arrays; 
import java.util.Comparator; 
import java.util.List;

public class TestNgPriorityInterceptor implements IMethodInterceptor {
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) { 
		Comparator<IMethodInstance> comparator = new Comparator<IMethodInstance>() {

			private int getPriority(IMethodInstance mi) { 
				int result = 0; Method method = mi.getMethod().getMethod(); 
				TestNgPriority a1 = method.getAnnotation(TestNgPriority.class); 
				//System.out.println("I entered method interceptor");

				if (a1 != null) { 
					result = a1.value(); 
				} else { 
					Class<?> cls = method.getDeclaringClass(); 
					TestNgPriority classPriority = cls.getAnnotation(TestNgPriority.class); 
					if (classPriority != null) { 
						result = classPriority.value(); 
					} 
				} 
				System.out.println("method: " +method.getName() + "   result: " +result);
			return result; 
		}

		public int compare(IMethodInstance m1, IMethodInstance m2) { 
			System.out.println("Priority is "+m1); 
			return getPriority(m1) - getPriority(m2); 
		}

	}; 
	
	System.out.println("size: " + methods.size());
	IMethodInstance[] array = methods.toArray(new IMethodInstance [methods.size()]); 
	Arrays.sort(array, comparator);

	return Arrays.asList(array); }
} 