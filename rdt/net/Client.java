package rdt.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import rdt.util.Logger;

public class Client {

	private Socket socket;
	
	private ReadingThread readingThread;
	private OutputStream output;
	
	public Client(Socket socket) {
		
		try {
			
			this.socket = socket;
			
			this.readingThread = new ReadingThread(socket);
			this.output = socket.getOutputStream();
			
			this.readingThread.start();
			
		} catch (IOException e) {
			Logger.logError(this.getClass(), e);
			System.exit(-1);
		}
		
	}
	
	public void sendPacket(DataPacket packet) {
		packet.write(output);
	}
	
	public boolean hasInputPackets() {
		return readingThread.hasPackets();
	}
	
	public DataPacket getPacket() {
		return readingThread.readPacket();
	}
	
	public void close() throws IOException {
		socket.close();
		readingThread.close();
	}
	
}
