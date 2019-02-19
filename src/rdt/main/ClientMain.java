package rdt.main;

import java.io.IOException;
import java.net.Socket;

import rdt.net.DataPacket;
import rdt.net.NetworkClient;

public class ClientMain {

	public ClientMain() {
		
		try {
			
			NetworkClient client = new NetworkClient(new Socket("localhost", 13197));
			
			//client.sendPacket(DataPacket.requestAddSubjectPacket("Hello, world!"));
			//client.close();
			
			System.exit(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new ClientMain();
	}
	
}
