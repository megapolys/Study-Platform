package rdt.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

import rdt.util.Logger;

public class ReadingThread extends Thread {
	
	private LinkedList<DataPacket> packetQueue;
	
	private BufferedReader input;

	public ReadingThread(Socket socket) throws IOException {
		super(socket.getInetAddress().toString() + "-reading-thread");
		
		this.packetQueue = new LinkedList<DataPacket>();
		
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
	}
	
	public boolean hasPackets() {
		synchronized(packetQueue) {
			return packetQueue.size() != 0;
		}
	}
	
	public DataPacket readPacket() {
		synchronized(packetQueue) {
			return packetQueue.pollFirst();
		}
	}
	
	public void close() throws IOException {
		this.interrupt();
		input.close();
	}
	
	@Override
	public void run() {
		
		while (!Thread.interrupted()) {
			
			try {
				
				int type = input.read();
				
				int inputDataLength  = (((int) input.read()) & 0xFF) << 0;
					inputDataLength += (((int) input.read()) & 0xFF) << 8;
					inputDataLength += (((int) input.read()) & 0xFF) << 16;
					inputDataLength += (((int) input.read()) & 0xFF) << 24;
				
				byte[] data = new byte[inputDataLength];
				
				for (int i = 0; i < inputDataLength; i++)
					data[i] = (byte) input.read();
				
				DataPacket packet = new DataPacket(type, new DataByteBuffer(data));
				
				synchronized(packetQueue) {
					packetQueue.add(packet);
				}
				
				
			} catch (IOException e) {
				Logger.logError(this.getClass(), e);
				System.exit(-1);
			}
			
		}
		
	}
	
}
