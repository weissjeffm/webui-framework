package com.redhat.qe.auto.instantiate;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import testopia.API.TestopiaObject;



public class VersionedInstantiator {
	protected static Logger log = Logger.getLogger(VersionedInstantiator.class.getName());
	
	protected LinkedHashMap<String,String> packageMap = new LinkedHashMap<String,String>();
	protected Integer versionedPackageIndex = 1;
	protected String runningVersion = "";
	
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
		List<String> validVersions = getValidVersionList(packageMap.get(runningVersion));
		Iterator<String> it = validVersions.iterator();
		Object o = null;
		while (it.hasNext() && o==null) {
			String ver = it.next();		
			try {
				 String className = getClassName(baseClass, ver);
				 log.finer("Trying to instantiate: " + className);

				 Class clazz = Class.forName(className);
				 o = clazz.newInstance();
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
		
		if (baseClass.isAssignableFrom(o.getClass())) return o;
		else throw new RuntimeException ("The versioned class of " + baseClass.getName() + ", '" + o.getClass().getName() + "', don't have a parent/subclass relationship.");
		
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
