package org.minima.mifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.utils.messages.Message;

public class webproxy {

	public static void main(String[] zArgs) {
		
		//Lets get started
		System.out.println("Starting MiFi Web proxy..");
		
		//The main controller
		Controller main = new Controller(); 
		main.setLOG(true);
		
		//Start her up..
		main.PostMessage(Controller.CONTROLLER_START);
		
		//Listen for input
	    InputStreamReader is    = new InputStreamReader(System.in);
	    BufferedReader bis      = new BufferedReader(is);

	    //Loop until finished..
	    while(true){
	        try {
	            //Get a line of input
	            String input = bis.readLine().trim();
	            
	            if(!input.equals("")) {
	                //Is it quit..
	                if(input.toLowerCase().equals("quit")) {
		            	main.PostMessage(Controller.CONTROLLER_SHUTDOWN);
	                	break;
		            }
	                 
	                //Create a message
	                Message in = new Message(Controller.CONTROLLER_MSG);
	                in.addString("message", input);
	                
	                //And post it..
	                main.PostMessage(in);
	            }
	            
	            
	        } catch (IOException ex) {
	            System.out.println(ex+"");
	        }
	    }
	    
	    //Cross the streams..
	    try {
	        bis.close();
	        is.close();
	    } catch (IOException ex) {
	    	System.out.println(ex.toString());
	    }
		
	    System.out.println("Main thread stopped");
	}
	
}
