package org.minima.mifi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;
import java.util.StringTokenizer;

import org.utils.messages.Message;

public class NodeRequestHandler implements Runnable {

	Controller mController;
	
	
	/**
	 * The Net Socket
	 */
	Socket mSocket;
	
	/**
	 * Main COnstructor
	 * @param zSocket
	 */
	public NodeRequestHandler(Socket zSocket, Controller zController) {
		//Store..
		mSocket = zSocket;
		
		mController = zController;
	}

	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in 	 		 	= null; 
		PrintWriter out 	 			= null; 
		
		String fileRequested 			= null;
		
		try {
			// Input Stream
			in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			
			// Output Stream
			out = new PrintWriter(mSocket.getOutputStream());
			
			// get first line of the request from the client
			String input = in.readLine();
			
			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			
			// we get file requested
			fileRequested = parse.nextToken().toLowerCase();
			
			// we support only GET and HEAD methods, we check
			if (method.equals("GET")){
				String function=new String(fileRequested);
				if(function.startsWith("/")) {
					function = function.substring(1);
				}
				
				//decode URL message
				function = URLDecoder.decode(function,"UTF-8").trim();
				
				System.out.println("NodeServer Connection.. "+mSocket.getLocalPort()+" "+function);
				
				//Post A message to the Controller..
				Message msg = new Message(Controller.CONTROLLER_NODEREQ).addString("request", function);
				mController.PostMessage(msg);
				
				//A response message
				String resp = "OK";
				
				// send HTTP Headers
				out.println("HTTP/1.1 200 OK");
				out.println("Server: HTTP WebProxy Server from Minima : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: text/plain");
				out.println("Content-length: " + resp.length());
				out.println("Access-Control-Allow-Origin: *");
				out.println(); // blank line between headers and content, very important !
				out.println(resp);
				out.flush(); // flush character output stream buffer
			}
			
		} catch (Exception ioe) {
			System.err.println("Server error : " + ioe);
			
		} finally {
			try {
				in.close();
				out.close();
				mSocket.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
		}
		
	}

}
