package org.minima.mifi;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.utils.messages.Message;
import org.utils.messages.MessageProcessor;

public class Controller extends MessageProcessor {

	public static String CONTROLLER_START 	 = "CONTROLLER_START";
	public static String CONTROLLER_SHUTDOWN = "CONTROLLER_SHUTDOWN";
	
	public static String CONTROLLER_MSG = "CONTROLLER_MSG";
	
	
	int mWebSocketPort = 8889;
	MiFiWebSocketServer mWSServer;
	
	public Controller() {
		super("CONTROLLER");
	}
	
	@Override
	protected void processMessage(Message zMessage) throws Exception {
		
		if(zMessage.isMessageType(CONTROLLER_START)) {
			
			//Which port
			if(zMessage.exists("port")) {
				mWebSocketPort = zMessage.getInteger("port");
			}
				
			Thread wsthread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//Create the websocket server
					try {
						mWSServer = new MiFiWebSocketServer(mWebSocketPort);
						mWSServer.run();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			//Start the Web Socket Server
			wsthread.start();
			
		}else if(zMessage.isMessageType(CONTROLLER_SHUTDOWN)) {
			//Stop it..
			if(mWSServer != null) { 
				mWSServer.stop(1000);
			}
			
			//And stop this message processor
			stopMessageProcessor();
			
		}else if(zMessage.isMessageType(CONTROLLER_MSG)) {
			
			String message = zMessage.getString("message");
			
			if(message.equals("info")) {
				//get the details..
				int count = 0;
				ConcurrentHashMap<String, WebSocket> webids = mWSServer.getAllWebID();
				
				//Get all the Keys..
				Enumeration<String> ids = webids.keys();
				while(ids.hasMoreElements()) {
					String id = ids.nextElement();
					System.out.println(count+") "+id+" "+webids.get(id));
					count++;
				}
				
//				Collection<WebSocket> conns =  mWSServer.getConnections();
//				for(WebSocket conn : conns) {
//					System.out.println(count+") "+conn.hashCode());
//					count++;
//				}
			}
		}
	}

}
