package rdt.net;

import rdt.util.Logger;

public class DataByteBuffer {

	private byte[] buffer;
	private int endPointer;
	
	public DataByteBuffer(int capacity) {
		this.buffer = new byte[capacity];
		this.endPointer = 0;
	}
	
	public DataByteBuffer(byte[] data) {
		this.buffer = data;
		this.endPointer = data.length;
	}
	
	public void put(int integer) {
		
		buffer[endPointer + 0] = (byte) ((integer >> 0)  & 0xFF);
		buffer[endPointer + 1] = (byte) ((integer >> 8)  & 0xFF);
		buffer[endPointer + 2] = (byte) ((integer >> 16)  & 0xFF);
		buffer[endPointer + 3] = (byte) ((integer >> 24)  & 0xFF);
		
		endPointer += 4;
		
	}
	
	public void put(String string) {
		
		int length = string.length();
		
		System.arraycopy(string.getBytes(), 0, buffer, endPointer, length);
		endPointer += length;
		
		put(length);
		
	}
	
	public int getInt() {
		
		int result  = (((int) buffer[endPointer - 4]) & 0xFF) << 0;
			result += (((int) buffer[endPointer - 3]) & 0xFF) << 8;
			result += (((int) buffer[endPointer - 2]) & 0xFF) << 16;
			result += (((int) buffer[endPointer - 1]) & 0xFF) << 24;
			
		endPointer -= 4;
			
		return result;
		
	}
	
	public String getString() {
		
		int length = getInt();
		byte[] stringBytes = new byte[length];
		
		Logger.log(getClass(), length);
		
		System.arraycopy(buffer, endPointer - length, stringBytes, 0, length);
		endPointer -= length;
		
		return new String(stringBytes);
		
	}
	
	public byte[] asArray() {
		return buffer;
	}
	
}
