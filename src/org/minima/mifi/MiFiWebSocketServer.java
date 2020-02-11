package org.minima.mifi;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

public class MiFiWebSocketServer extends WebSocketServer {

	int mPort;
	
	//Links a string to the Connection..
	ConcurrentHashMap<String, WebSocket> mWebID = new ConcurrentHashMap<String, WebSocket>();

	public MiFiWebSocketServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		
		mPort = port;
	}
	
	/**
	 * Fix CORS errors when trying to connect from a different host
	 */
	@Override
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer( WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
		ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer( conn, draft, request );
		builder.put( "Access-Control-Allow-Origin" , "*");
		return builder;
	}
	
	public ConcurrentHashMap<String, WebSocket> getAllWebID() {
		return mWebID;
	}
	
	public WebSocket getConnection(String zWebID) {
		return mWebID.get(zWebID);
	}
	
	public boolean sendMessage(String zWebID, String zMessage) {
		WebSocket conn = getConnection(zWebID);
		
		if(conn != null) {
			conn.send(zMessage);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("WS Open : "+conn);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("WS Close : "+conn);
		
		//Remove it..
		mWebID.values().remove(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		//Received a message
		System.out.println("WS Message : "+message);
		
		//This will be the website identifying itself..
		mWebID.put(message, conn);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.out.println("WS Error : "+conn + " "+ex );
	}
	
	@Override
	public void onStart() {
		System.out.println("MiFiWebSocketServer started on "+mPort);
		
		//Disconnect after a certain amount of time
//		setConnectionLostTimeout(0);
//		setConnectionLostTimeout(100);
	}
}
