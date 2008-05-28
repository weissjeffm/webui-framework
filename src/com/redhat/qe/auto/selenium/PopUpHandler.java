package com.redhat.qe.auto.selenium;



import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

public class PopUpHandler implements Runnable{
	private static Logger log = Logger.getLogger(PopUpHandler.class.getName());


	public void run() {
		//HarnessConfiguration.RHN_BVT.equalsIgnoreCase("0")
		//FIXME Harnessconfiguration replace
		if(true){
		
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		//rh.getNumberOfPopUps() replaces 1
		//FIXME 
		for (int i = 0; i < 1; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//HarnessConfiguration.BROWSER_TYPE.equalsIgnoreCase("*iehta")
			//FIXME - if windows
			if(false){
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_Y);
				robot.keyRelease(KeyEvent.VK_Y);
			}
			else{
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			}
			int num = i + 1;
			//System.out.println("<li>"+ rh.getTime()+"     hit enter key " + num + " times");
			log.info("hit enter key " + num + " times");

				}
			}
			else{
				log.info("pop up handler disabled");
			}
		}
		


}