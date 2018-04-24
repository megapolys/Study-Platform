package rdt.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import rdt.util.Logger;

public class NetworkServer {
	
	private ServerSocket serverSocket;
	
	private ClientAcceptionThread clientAcceptionThread;
	private ArrayList<NetworkClient> clients;
	
	public NetworkServer(int port) {
		
		try {
			
			Logger.log(this.getClass(), "Server is starting..");
			
			this.serverSocket = new ServerSocket(port);
			
			this.clientAcceptionThread = new ClientAcceptionThread(serverSocket);
			this.clients = new ArrayList<NetworkClient>();
			
			this.clientAcceptionThread.start();
			
			Logger.log(this.getClass(), "Server started!");
			
		} catch (IOException e) {
			Logger.logError(this.getClass(), e);
			System.exit(-1);
		}
		
	}
	
	public boolean hasWaitingClients() {
		return clientAcceptionThread.hasClientSocket();
	}
	
	public boolean hasMessages() {
		
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).hasInputPackets())
				return true;
		
		return false;
		
	}
	
	public ClientMessage getMessage() {
		
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).hasInputPackets())
				return new ClientMessage(clients.get(i), clients.get(i).getPacket());
		
		return null;
		
	}
	
	public void update() {
		
		ArrayList<NetworkClient> toRemove = new ArrayList<NetworkClient>();
		
		for (int i = 0; i < clients.size(); i++)
			if (!clients.get(i).isConnected()) {
				toRemove.add(clients.get(i));
				Logger.log(getClass(), clients.get(i).getInetAddress() + " disconnected!");
			}
		
		
		clients.removeAll(toRemove);
		toRemove.clear();
		
		while (clientAcceptionThread.hasClientSocket()) {
			
			NetworkClient newClient = new NetworkClient(clientAcceptionThread.getClientSocket());
			Logger.log(this.getClass(), newClient.getInetAddress() + " connected!");
			
			clients.add(newClient);
		}
		
	}
	
	public void close() {
		
		try {
			
			Logger.log(this.getClass(), "Server is stopping..");
			
			clientAcceptionThread.close();
			
			for (int i = 0; i < clients.size(); i++)
				clients.get(i).close();
			
			Logger.log(this.getClass(), "Server was stopped..");
			
		} catch (IOException e) {
			Logger.logError(this.getClass(), e);
			System.exit(-1);
		}
	}
	
	
	
}
