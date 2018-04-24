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
	private boolean interrupted;

	public ReadingThread(Socket socket) throws IOException {
		super(socket.getInetAddress().toString() + "-reading-thread");
		
		this.packetQueue = new LinkedList<DataPacket>();
		
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.interrupted = false;
		
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
	
	public boolean isInterrupted() {
		return interrupted;
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
					
				Logger.log(getClass(), type + " " + inputDataLength);
				
				byte[] data = new byte[inputDataLength];
				
				for (int i = 0; i < inputDataLength; i++)
					data[i] = (byte) input.read();
				
				Logger.log(getClass(), type);
				
				DataPacket packet = new DataPacket(type, new DataByteBuffer(data));
				
				synchronized(packetQueue) {
					packetQueue.add(packet);
				}
				
			} catch (IOException e) {
				
				this.interrupted = true;
				this.interrupt();
			}
			
		}
		
	}
	
}
