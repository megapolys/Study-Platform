package rdt.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.LinkedList;

public class ReadingThread extends Thread {
	
	private LinkedList<DataPacket> packetQueue;
	
	private InputStream input;
	private boolean interrupted;

	public ReadingThread(Socket socket) throws IOException {
		super(socket.getInetAddress().toString() + "-reading-thread");
		
		this.packetQueue = new LinkedList<DataPacket>();
		
		this.input = socket.getInputStream();
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
				if (type == -1)
					throw new IOException();
				
				int inputDataLength  = (((int) input.read()) & 0xFF) << 0;
					inputDataLength += (((int) input.read()) & 0xFF) << 8;
					inputDataLength += (((int) input.read()) & 0xFF) << 16;
					inputDataLength += (((int) input.read()) & 0xFF) << 24;
				
				byte[] data = new byte[inputDataLength];
				
				for (int i = 0; i < inputDataLength; i++) 
					data[i] = (byte) (input.read() & 0xFF);
				
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
