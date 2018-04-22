package rdt.main;

import rdt.net.NetworkServer;
import rdt.util.Utils;

public class ServerMain {

	public ServerMain() {
		
		NetworkServer server = new NetworkServer(13197);
		
		System.out.println(Utils.getHash("C:\\Users\\Artyom\\YandexDisk\\Programming\\Java\\Eclipse\\WorkSpace\\StudyPlatform\\src\\rdt\\main\\ServerMain.java"));
		
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
	
}
