package rdt.net;

import rdt.platform.backend.FileDescription;
import rdt.platform.backend.Subject;

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
	
	public DataByteBuffer put(int integer) {
		
		buffer[endPointer + 0] = (byte) ((integer >> 0)  & 0xFF);
		buffer[endPointer + 1] = (byte) ((integer >> 8)  & 0xFF);
		buffer[endPointer + 2] = (byte) ((integer >> 16)  & 0xFF);
		buffer[endPointer + 3] = (byte) ((integer >> 24)  & 0xFF);
		
		endPointer += 4;
		
		return this;
		
	}
	
	public DataByteBuffer put(String string) {
		
		byte[] stringBytes = string.getBytes();
		
		int length = stringBytes.length;
		
		System.arraycopy(stringBytes, 0, buffer, endPointer, length);
		endPointer += length;
		
		put(length);
		
		return this;
		
	}
	
	public DataByteBuffer put(FileDescription description) {
		
		put(description.getType());
		put(description.getPath());
		put(description.getName());
		put(description.getHash());
		
		return this;
		
	}
	
	public DataByteBuffer put(Subject subject) {
		
		put(subject.getLevels());
		
		int[][] pathes = subject.getHeadPathes();
		
		for (int[] path : pathes) {
			put(path);
			put(subject.getHeadElement(path));
		}
		
		put(pathes.length);
		
		put(subject.getFileDescriptions());
		
		put(subject.getName());
		
		return this;
		
	}
	
	public DataByteBuffer put(int[] integers) {
		
		for (int i = 0; i < integers.length; i++)
			put(integers[i]);
		
		put(integers.length);
		
		return this;
		
	}
	
	public DataByteBuffer put(String[] strings) {
		
		for (int i = 0; i < strings.length; i++)
			put(strings[i]);
		
		put(strings.length);
		
		return this;
		
	}
	
	public DataByteBuffer put(FileDescription[] descriptions) {
		
		for (int i = 0; i < descriptions.length; i++)
			put(descriptions[i]);
		
		put(descriptions.length);
		
		return this;
		
	}
	
	public DataByteBuffer put(Subject[] subjects) {
		
		for (int i = 0; i < subjects.length; i++)
			put(subjects[i]);
		
		put(subjects.length);
		
		return this;
		
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
		
		System.arraycopy(buffer, endPointer - length, stringBytes, 0, length);
		endPointer -= length;
		
		return new String(stringBytes);
		
	}
	
	public FileDescription getFileDescription() {
		
		String hash = getString();
		String name = getString();
		int[] path = getIntArray();
		int type = getInt();
		
		return new FileDescription(type, path, name, hash);
		
	}
	
	public Subject getSubject() {
		
		Subject result = new Subject(getString());
		
		FileDescription[] descriptions = getFileDescriptionArray();
		for (int i = 0; i < descriptions.length; i++)
			result.addFileDescription(descriptions[i]);
		
		int headLength = getInt();
		
		for (int i = 0; i < headLength; i++) {
			
			String name = getString();
			int[] path = getIntArray();
			
			result.addHeadElement(path, name);
			
		}
		
		String[] levels = getStringArray();
		for (int i = 0; i < levels.length; i++)
			result.addLevel(levels[i]);
		
		return result;
		
	}
	
	public int[] getIntArray() {
		
		int[] result = new int[getInt()];
		for (int i = 0; i < result.length; i++)
			result[result.length - i - 1] = getInt();
		
		return result;
		
	}
	
	public String[] getStringArray() {
		
		String[] result = new String[getInt()];
		for (int i = 0; i < result.length; i++)
			result[result.length - i - 1] = getString();
		
		return result;
		
	}
	
	public FileDescription[] getFileDescriptionArray() {
		
		FileDescription[] result = new FileDescription[getInt()];
		for (int i = 0; i < result.length; i++)
			result[result.length - i - 1] = getFileDescription();
		
		return result;
		
	}
	
	public Subject[] getSubjectArray() {
		
		Subject[] result = new Subject[getInt()];
		for (int i = 0; i < result.length; i++)
			result[result.length - i - 1] = getSubject();
		
		return result;
		
	}
	
	public byte[] asArray() {
		return buffer;
	}
	
}
