package rdt.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import rdt.util.Logger;

public class ClientAcceptionThread extends Thread {
	
	private ServerSocket serverSocket;
	private LinkedList<Socket> clientSockets;

	public ClientAcceptionThread(ServerSocket socket) {
		super("Server-client-acception-thread");
		
		this.serverSocket = socket;
		this.clientSockets = new LinkedList<Socket>();
		
	}
	
	public boolean hasClientSocket() {
		return clientSockets.size() != 0;
	}
	
	public Socket getClientSocket() {
		return clientSockets.pollFirst();
	}
	
	public void close() throws IOException {
		
		this.interrupt();
		
		for (Socket socket : clientSockets)
			socket.close();
		
	}
	
	@Override
	public void run() {
		
		while (!Thread.interrupted()) {
			
			try {
				
				Socket clientSocket = serverSocket.accept();
				clientSockets.add(clientSocket);
				
			} catch (IOException e) {
				Logger.logError(this.getClass(), e);
				System.exit(-1);
			}
		}
		
	}
	
}