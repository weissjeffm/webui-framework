package com.redhat.qe.auto.classloading;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import testopia.API.TestopiaObject;



public class VersionedLoader {
	protected static Logger log = Logger.getLogger(VersionedLoader.class.getName());
	
	protected Map<String,String> packageMap = new HashMap<String,String>();
	
	public  void setPackageMap(Map<String, String> packageMap){
		this.packageMap = packageMap;
	}
	
	public Object getVersionedClass(String version, Class<Object> baseClass) throws ClassNotFoundException{
		String[] names = baseClass.getName().split("\\.");
		
		String className = packageMap.get(version) + "." + names[names.length-1];
		Object o = Class.forName(className);
		if (baseClass.getClass().isAssignableFrom(o.getClass())) return o;
		else throw new RuntimeException ("The versioned class of " + baseClass.getName() + ", '" + className + "', don't have a parent/subclass relationship.");
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//find(null);
		 // Get a File object for the package
		String pckgname = "testopia.API";
		String name = new String(pckgname);
	        if (!name.startsWith("/")) {
	            name = "/" + name;
	        }        
	        name = name.replace('.','/');

        URL url = VersionedLoader.class.getResource(name);
       
        File directory = new File(url.getFile());
        // New code
        // ======
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String [] files = directory.list();
            for (int i=0;i<files.length;i++) {
                 
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                	log.info("Found file " + files[i]);
                    String classname = files[i].substring(0,files[i].length()-6);
                    try {
                        // Try to create an instance of the object
                        Object o = Class.forName(pckgname+"."+classname).newInstance();
                        if (o instanceof TestopiaObject) {
                            log.info(classname);
                        }
                    } catch (ClassNotFoundException cnfex) {
                        log.log(Level.SEVERE, "Class not found", cnfex);
                    } catch (InstantiationException iex) {
                        // We try to instantiate an interface
                        // or an object that does not have a 
                        // default constructor
                    	log.log(Level.INFO, "Coudn't instantiate", iex);
                    } catch (IllegalAccessException iaex) {
                        // The class is not public
                    	log.log(Level.INFO, "Coudn't access", iaex);

                    }
                }
            }
        }
        find(null);
	}
	

	public static void find(String tosubclassname) {
        try {
            //Class tosubclass = Class.forName(tosubclassname);
            Package [] pcks = Package.getPackages();
            for (int i=0;i<pcks.length;i++) {
                log.info(pcks[i].toString());
            }
        } catch (Exception ex) {
            log.severe("Class "+tosubclassname+" not found!");
        }
    }


}
