package rdt.net;

import java.io.IOException;
import java.io.OutputStream;

import rdt.util.Logger;

public class DataPacket {

	private int type;
	private byte[] data;
	
	public DataPacket(int type, byte[] data) {
		this.type = type;
		this.data = data;
	}
	
	public int getType() {
		return this.type;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public void write(OutputStream out) {
		
		try {
			
			out.write(type);
			
			out.write((data.length >> 0) & 0xFF);
			out.write((data.length >> 8) & 0xFF);
			out.write((data.length >> 16) & 0xFF);
			out.write((data.length >> 24) & 0xFF);
			
			out.write(data);
			
			out.flush();
			
		} catch (IOException e) {
			Logger.logError(this.getClass(), e);
			System.exit(-1);
		}
	}
	
}
