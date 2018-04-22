package rdt.main;

import java.io.IOException;
import java.net.Socket;

import rdt.net.DataByteBuffer;
import rdt.net.DataPacket;
import rdt.net.NetworkClient;

public class ClientMain {

	public ClientMain() {
		
		try {
			
			NetworkClient client = new NetworkClient(new Socket("localhost", 13197));
			
			String str = "Hello, Server!";
			DataByteBuffer buffer = new DataByteBuffer(str.length() + 4);
			buffer.put(str);
			
			client.sendPacket(new DataPacket(0, buffer));
			client.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new ClientMain();
	}
	
}
