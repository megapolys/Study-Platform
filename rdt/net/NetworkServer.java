package rdt.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import rdt.util.Logger;

public class NetworkServer {
	
	private ServerSocket serverSocket;
	
	private ClientAcceptionThread clientAcceptionThread;
	private ArrayList<Client> clients;
	
	public NetworkServer(int port) {
		
		try {
			
			Logger.log(this.getClass(), "Server is starting..");
			
			this.serverSocket = new ServerSocket(port);
			
			this.clientAcceptionThread = new ClientAcceptionThread(serverSocket);
			this.clients = new ArrayList<Client>();
			
			this.clientAcceptionThread.start();
			
			Logger.log(this.getClass(), "Server started!");
			
		} catch (IOException e) {
			Logger.logError(this.getClass(), e);
			System.exit(-1);
		}
		
	}
	
	public void update() {
		
		while (clientAcceptionThread.hasClientSocket()) 
			clients.add(new Client(clientAcceptionThread.getClientSocket()));
		
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
