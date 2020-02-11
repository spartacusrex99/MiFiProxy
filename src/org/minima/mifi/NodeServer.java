package org.minima.mifi;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class NodeServer implements Runnable{

	Controller mController;
	
	ServerSocket mServerSocket;
	int mPort;
	
	boolean mRunning = true;
	
	public NodeServer(int zPort, Controller zController) {
		mPort 		= zPort;
		mController = zController;
	}
	
	public int getPort() {
		return mPort;
	}
		
	public void stop() {
		mRunning = false;
		
		try {
			if(mServerSocket != null) {
				mServerSocket.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			//Start a server Socket..
			mServerSocket = new ServerSocket(mPort);
	
			System.out.println("Node server started on : "+mPort);
			
			//Keep listening..
			while(mRunning) {
				//Listen in for connections
				Socket clientsock = mServerSocket.accept();
				
				//create a new RPC Handler ..
				NodeRequestHandler req = new NodeRequestHandler(clientsock, mController);
				
				//Run in a new Thread
				Thread rpcthread = new Thread(req);
				rpcthread.start();
			}
			
		} catch (BindException e) {
			//Socket shut down..
			System.out.println("NodeServer : Port "+mPort+" already in use!.. restart required..");
			
		} catch (SocketException e) {
			if(mRunning) {
				//Socket shut down..
				System.out.println("NodeServer : Socket Shutdown.. "+e);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Node server Stopped");
	}
}
