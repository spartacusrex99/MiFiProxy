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

//	//A link from the hashcode of the connection to tghe string id
//	ConcurrentHashMap<Integer, String> mHashLink = new ConcurrentHashMap<Integer, String>();
	
	public MiFiWebSocketServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		
		mPort = port;
		
		System.out.println("MiFiWebSocketServer started on "+port);
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
	
	public int getPort() {
		return mPort;
	}
	
	public ConcurrentHashMap<String, WebSocket> getAllWebID() {
		return mWebID;
	}
	
	public WebSocket getConnection(String zWebID) {
		return mWebID.get(zWebID);
	}
	
	public void sendMessage(String zWebID, String zMessage) {
		WebSocket conn = getConnection(zWebID);
		conn.send(zMessage);
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
		System.out.println("WS On Start..");
		
//		setConnectionLostTimeout(0);
//		setConnectionLostTimeout(100);
	}
}
