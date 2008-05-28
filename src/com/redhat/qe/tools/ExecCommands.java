package com.redhat.qe.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExecCommands {
	private static Logger log = Logger.getLogger(ExecCommands.class.getName());


	public void submitCommandToLocal(String command, String arguments) throws IOException{
//		 Execute a command with an argument
		String fullCommand;
        fullCommand = command + " " + arguments;
        log.info("Executing " + fullCommand);
        Process child = Runtime.getRuntime().exec(fullCommand);
        //Process child = new ProcessBuilder().start();
        BufferedReader buffer = new BufferedReader(
                new InputStreamReader(child.getInputStream()));
        BufferedReader buffer2 = new BufferedReader(
                new InputStreamReader(child.getErrorStream()));

        String s = null;
        String s2 = null;
        try {
          while (((s = buffer.readLine()) != null) && ((s2 = buffer2.readLine()) == null)) {
        	  log.info("Output: " + s);
          }

          if(s2 != null){
        	  log.info("Error Encountered");
        	  log.info("Error Output: " + s2);
        	  while ((s2 = buffer2.readLine()) != null) {
        		  log.info("Error Output: " + s2);
                }
          }
          buffer.close();
          buffer2.close();
        }catch (Exception e) {
        	log.log(Level.INFO, "error occured",e);
          }

	}

	public String submitCommandToLocalWithReturn(boolean showLogResults, String command, String arguments) throws IOException{
		String results;

		//FIXME - need to read in from config file
		//HarnessConfiguration.BROWSER_TYPE.equalsIgnoreCase("*iehta")
		
		if(false){
			log.info("in ms windows");
			results = submitCommandtoWindows(showLogResults, command, arguments);
		}
		else{
			results = submitCommandToLocalWithReturnPrivate(showLogResults, command, arguments);
		}
		return results;
	}

	private String submitCommandtoWindows(boolean showLogResults,String command, String arguments) throws IOException{

		String fullCommand;
		String returncode = "null";
		String line;
		String windowsPath = "c:\\cygwin\\bin\\";
		command = windowsPath + command;
        fullCommand = command + " " + arguments;
        log.info("Executing: " + fullCommand);

        Process p = Runtime.getRuntime().exec(fullCommand);
        if(showLogResults){
         try{
        	BufferedReader input =
                new BufferedReader
                  (new InputStreamReader(p.getInputStream()));
              while ((line = input.readLine()) != null) {
                log.info("Command Output:  " + line);
                returncode = line;
              }
              input.close();
            }
            catch (Exception err) {
              err.printStackTrace();
            }
	        /*try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    int result = p.exitValue();
		     returncode = String.valueOf(result);
		     logInfo("return code = "+returncode);*/
        }

	    return returncode;

	}

	private String submitCommandToLocalWithReturnPrivate(boolean showLogResults, String command, String arguments) throws IOException{
//		 Execute a command with an argument
		String result ="";
		String fullCommand;
        fullCommand = command + " " + arguments;
        log.info("Executing " + fullCommand);

       Process child = Runtime.getRuntime().exec(fullCommand);
       //Process child = new ProcessBuilder().start();
       BufferedReader buffer = new BufferedReader(
               new InputStreamReader(child.getInputStream()));
       BufferedReader buffer2 = new BufferedReader(
               new InputStreamReader(child.getErrorStream()));

       String s = null;
       String s2 = null;
       try {
    	   		if(showLogResults){
    	       	  while ((s2 = buffer2.readLine()) != null) {
    	                 result=(s2);
    	                 log.info("Command Output: " + result);
    	               }
    	   		}
    	   		//Cant think of another way to log the while loop when I want to or not..
    	   		else{
    	   			while ((s2 = buffer2.readLine()) != null) {
   	                 result=(s2);
   	              log.info("Error Output: " + result);
    	   			}
    	   		}


    	   		if(showLogResults){
		    	   while (((s = buffer.readLine()) != null))  {
		           //result=("<li> Output: " + s);
		    		 result=(s);
		    		 log.info("Output: " + result);
		    	   	}
    	   		}
    	   		else{
    	   			while (((s = buffer.readLine()) != null))  {
    			           //result=("<li> Output: " + s);
    			    		 result=(s);
    			    		 log.info("Output: " + result);
    	   					}
    	   			}

         buffer.close();
         buffer2.close();
       }catch (Exception e) {
           result="Error occured";
           return result;
         }
       return result;
	}



}
