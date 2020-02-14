package org.minima.mifi;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.utils.messages.Message;
import org.utils.messages.MessageProcessor;

public class Controller extends MessageProcessor {

	public static String CONTROLLER_START 	 = "CONTROLLER_START";
	public static String CONTROLLER_SHUTDOWN = "CONTROLLER_SHUTDOWN";
	
	public static String CONTROLLER_NODEREQ = "CONTROLLER_NODEREQ";
	
	public static String CONTROLLER_MSG = "CONTROLLER_MSG";
	
	/**
	 * Web Pages connect to this one
	 */
	int mWebSocketPort = 8889;
	MiFiWebSocketServer mWSServer;
	
	/**
	 * Minima Nodes send GET requests to this
	 */
	int mNodeServerPort = 8890;
	NodeServer mNodeServer;
	
	/**
	 * Default Constructor
	 */
	public Controller() {
		super("CONTROLLER");
	}
	
	public MiFiWebSocketServer getWeebSocketSewrver() {
		return mWSServer;
	}
	
	public NodeServer getNodeServer() {
		return mNodeServer;
	}
	
	@Override
	protected void processMessage(Message zMessage) throws Exception {
		
		if(zMessage.isMessageType(CONTROLLER_START)) {
			
			//Which web socket port
			if(zMessage.exists("websockport")) {
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
			
			
			//Which web socket port
			if(zMessage.exists("nodeserverport")) {
				mNodeServerPort = zMessage.getInteger("nodeserverport");
			}
			
			//Create the Node Server
			mNodeServer = new NodeServer(mNodeServerPort, this);
		
			//Run it
			Thread nodesthread = new Thread(mNodeServer);
			nodesthread.start();
			
		}else if(zMessage.isMessageType(CONTROLLER_SHUTDOWN)) {
			//Stop this..
			if(mWSServer != null) { 
				mWSServer.stop(1000);
			}
			
			//Stop that..
			if(mNodeServer != null) {
				mNodeServer.stop();
			}
			
			//And stop the message processor
			stopMessageProcessor();
		
		}else if(zMessage.isMessageType(CONTROLLER_NODEREQ)) {
			//Request sent from node telling a webpage where to look.
			String req = zMessage.getString("request");
			
			//Split the request up..
			int split = req.indexOf("#");
			
			String WebID = req.substring(0,split); 
			String IP    = req.substring(split+1);
			
			//Send to the correct Web Socket..
			mWSServer.sendMessage(WebID, IP);
			
			//#TODO Really need a login system / public ley etc etc
			
		}else if(zMessage.isMessageType(CONTROLLER_MSG)) {
			
			String message = zMessage.getString("message");
			
			if(message.equals("info")) {
				//get the details..
				int count = 0;
				ConcurrentHashMap<String, WebSocket> webids = mWSServer.getAllWebID();
				
				System.out.println("WS Connections found "+webids.size());
				
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
