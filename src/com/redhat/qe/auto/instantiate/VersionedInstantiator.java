package com.redhat.qe.auto.instantiate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



public class VersionedInstantiator {
	protected static Logger log = Logger.getLogger(VersionedInstantiator.class.getName());
	
	protected LinkedHashMap<String,String> packageMap = new LinkedHashMap<String,String>();
	protected Integer versionedPackageIndex = 1;
	protected String runningVersion = "";
	protected static Map<Class<?>,Object> instances = new HashMap<Class<?>,Object>();
	
	/**
	 * @param packageMap - a map of version strings to package names.  For example, a product version 2.2.0 might have 
	 * a package com.xyz.product22.stuff.  You'd add an entry ("2.2.0", "product22") to the map.  You should use add entries
	 * in increasing order (newer versions go last).
	 * @param versionedPackageIndex - the index of where the versioned part of the package name is.  It's a 0 based index, 
	 * so for package w.x.y.z.product22.a.b, the index is 4.
	 * @param runningVersion - a valid key for the packageMap that indicates the currently running version.  This might
	 * be detected at runtime from the product itself, or read in from a properties file.  For example "2.2.0".
	 */
	public VersionedInstantiator(LinkedHashMap<String,String> packageMap, Integer versionedPackageIndex, String runningVersion){
		this.packageMap = packageMap;
		this.versionedPackageIndex = versionedPackageIndex;
		this.runningVersion = runningVersion;
	}
	
	public  void setPackageMap(LinkedHashMap<String, String> packageMap){
		this.packageMap = packageMap;
	}
	
	/**
	 * Takes a baseclass and returns an instance of either it or a valid subclass.  The product version 
	 * of the instance will either be the currently running version, or the next newest product version for 
	 * which a class exists.  If no other classes exist, it'll just return an instance of the base class.
	 * @param baseClass 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object getVersionedInstance(Class baseClass) {
		return getVersionedInstance(baseClass, new Object[] {});
	}
	
	public Object getVersionedInstance(Class baseClass, Object... args) {
		log.finer("Product version currently running is '" + runningVersion + "'.");
		List<String> validVersions = getValidVersionList(packageMap.get(runningVersion));
		Iterator<String> it = validVersions.iterator();
		Object o = null;
		Class clazz = null;
		while (it.hasNext() && o==null) {
			String ver = it.next();	
			 
			try {
				 String className = getClassName(baseClass, ver);
				 log.finer("Trying to instantiate: " + className);

				 clazz = Class.forName(className);
				 if (args.length == 0) {
					 //see if we already have a no-arg instance
					 o = instances.get(clazz);
					 //if not, create a new one
					 if (o == null) o = clazz.newInstance();
				 }
				 else {
					 //create a new instance with the args
					 o = newInstance(clazz, args);
				 }
			}
			catch(ClassNotFoundException cnfe){
				log.log(Level.FINEST, "Couldn't instantiate: " + ver, cnfe);
				continue;
			}
			catch(IllegalAccessException iae){
				log.log(Level.FINEST, "Couldn't instantiate: " + ver, iae);
				continue;
			}
			catch(InstantiationException ie){
				log.log(Level.FINEST, "Couldn't instantiate: " + ver, ie);
				continue;
			}
		}
		if (o==null)throw new RuntimeException("Couldn't find any valid instance of " + baseClass.getName());
		
		if (baseClass.isAssignableFrom(o.getClass())) {
			instances.put(clazz, o);
			return o;
		}
		else throw new RuntimeException ("The versioned class of " + baseClass.getName() + ", '" + o.getClass().getName() + "', don't have a parent/subclass relationship.");
		

	}
	
	protected Object newInstance(Class clazz, Object... args) {
		Constructor[] constrs = clazz.getConstructors();
		for (int i=0; i<constrs.length; i++) {
			if (argsMatch(constrs[i], args)) {
				try {
					return constrs[i].newInstance(args);
				}
				catch(InvocationTargetException ite) {
					log.log(Level.FINEST, "Could not create new instance with constructor " + constrs[i] + " and args " + args);
					continue;
				}
				catch(IllegalAccessException ite) {
					log.log(Level.FINEST, "Could not create new instance with constructor " + constrs[i] + " and args " + args);
					continue;
				}
				catch(InstantiationException ite) {
					log.log(Level.FINEST, "Could not create new instance with constructor " + constrs[i] + " and args " + args);
					continue;
				}
			}
		}
		throw new RuntimeException("Could not instantiate " + clazz.getName() + " with the given arguments.");
	}
	
	protected boolean argsMatch(Constructor constr, Object... args) {
		Class[] c_args = constr.getParameterTypes();
		if (args.length != c_args.length) return false;
		
		for (int i=0; i<args.length;i++) {
			if (! c_args[i].isInstance(args[i])) return false;
		}
		return true;
	}
	
	protected String getClassName(Class<Object> baseClass, String versionedPackage){
		String[] names = baseClass.getName().split("\\.");
		names[versionedPackageIndex]= versionedPackage;
		String name = "";
		for(int i=0; i<names.length; i++){
			name = name + names[i] + (i==names.length-1 ? "":".");
			
		}
		return name;
	}
	
	protected List<String> getValidVersionList(String runningVersion){
		List<String> list = new ArrayList<String>(packageMap.values());
		List<String> newList = new ArrayList<String>();
		int i =0;
		try {
			while(!list.get(i).equals(runningVersion)) i++;
		}
		catch(IndexOutOfBoundsException ioobe){
			throw new RuntimeException("The running product version '" + runningVersion +  "' was not found in the map!", ioobe);
		}
		newList = list.subList(0, i+1);
		Collections.reverse(newList);
		return newList;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}


}
