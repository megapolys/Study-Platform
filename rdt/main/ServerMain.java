package rdt.main;

import java.util.HashMap;

import rdt.net.ClientMessage;
import rdt.net.DataByteBuffer;
import rdt.net.NetworkServer;
import rdt.util.Logger;

public class ServerMain {
	
	private NetworkServer server;
	
	private HashMap<Integer, String> subjects;
	private HashMap<Integer, String> levels;
	private HashMap<Integer[], String> head;

	public ServerMain() {
		
		setup();
		
		while (!Thread.interrupted()) {
			
			loop();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Logger.logError(this.getClass(), e);
				System.exit(-1);
			}
			
		}
		
	}
	
	public void setup() {
		
		this.server = new NetworkServer(13197);
		
		this.subjects = new HashMap<Integer, String>();
		this.levels = new HashMap<Integer, String>();
		
	}
	
	public void loop() {
		
		server.update();
		
		while (server.hasMessages()) {
			
			ClientMessage message = server.getMessage();
			DataByteBuffer data = message.getPacket().getData();
			
			Logger.log(getClass(), message.getPacket().getType());
			
			switch (message.getPacket().getType()) {
			
			case 103: { //добавление предмета
				
				String name = data.getString();
				int code = subjects.size();
				
				Logger.log(this.getClass(), 103 + " " + name + " " + code);
				
				subjects.put(code, name);
				
				break;
			}
				
			case 104: { //добавление уровня
			
				String name = data.getString();
				int code = levels.size();
				
				Logger.log(this.getClass(), 104 + " " + name + " " + code);
				
				levels.put(code, name);
				
				break;
			}
			
			case 105: { //добавление оглавления
				
				String name = data.getString();
				int[] path = data.getIntArray();
				
				Integer[] pathInts = new Integer[path.length];
				for (int i = 0; i < path.length; i++)
					pathInts[i] = path[i];
				
				Logger.log(this.getClass(), 105 + " path " + name);
				
				head.put(pathInts, name);
				
				break;
			}
				
			
			}
			
		}
		
	}
	
	public void save() {
		
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
	
}
