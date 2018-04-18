package rdt.main;

import rdt.net.NetworkServer;

public class ServerMain {

	public ServerMain() {
		
		NetworkServer server = new NetworkServer(13197);
		
		server.update();
		server.close();
		
		
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
	
}
