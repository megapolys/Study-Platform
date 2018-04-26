package rdt.net;

import java.io.IOException;
import java.io.OutputStream;

import rdt.platform.backend.Subject;
import rdt.util.Logger;

public class DataPacket {
	
	/*
	 * 	98 - ок
	 *  99 - запрос на получение кодов предметов
	 * 100 - запрос на получение описания предмета
	 * 101 - запрос на добавление предмета
	 * 
	 */

	public static DataPacket requestSubjectsPacket() {
		return new DataPacket(99, new DataByteBuffer(new byte[0]));
	}
	
	public static DataPacket responseSubjectsPacket(String[] subjectNames) {
		
		int length = 0;
		for (int i = 0; i < subjectNames.length; i++)
			length += subjectNames[i].getBytes().length;
		
		length += (subjectNames.length + 1) * 4;
		
		DataByteBuffer packet = new DataByteBuffer(length);
		packet.put(subjectNames);
		
		return new DataPacket(99, packet);
		
	}
	
	public static DataPacket requestSubjectPacket(String name) {
		
		int length = name.getBytes().length + 4;
		DataByteBuffer buffer = new DataByteBuffer(length);
		
		buffer.put(name);
		return new DataPacket(100, buffer);
		
	}
	
	public static DataPacket responseSubjectPacket(Subject subject) {
		
		int length = subject.getSizeBytes();
		DataByteBuffer buffer = new DataByteBuffer(length);
		
		buffer.put(subject);
		
		return new DataPacket(100, buffer);
		
	}
	
	/*public static DataPacket requestHierarchyPacket(String subjectName) {
		
		DataByteBuffer packet = new DataByteBuffer(subjectName.getBytes().length);
		packet.put(subjectName);
		
		return new DataPacket(100, packet);
	}
	
	public static DataPacket responseHierarchyPacket(String[] names) {
		
		int length = 0;
		for (int i = 0; i < names.length; i++)
			length += names[i].getBytes().length;
		
		length += (names.length + 1) * 4;
		
		DataByteBuffer packet = new DataByteBuffer(length);
		
		packet.put(names);
		
		return new DataPacket(100, packet);
		
	}
	
	public static DataPacket requestHeadPacket(String subjectName) {
		
		DataByteBuffer packet = new DataByteBuffer(subjectName.getBytes().length);
		packet.put(subjectName);
		
		return new DataPacket(101, packet);
	}
	
	public static DataPacket responseHeadPacket(int[][] pathes, String[] names) {
		
		int length = 0;
		for (int i = 0; i < names.length; i++)
			length += names[i].getBytes().length;
		
		length += (names.length + 1) * 4;
		
		for (int i = 0; i < pathes.length; i++) {
			length += (pathes[i].length + 1) * 4;
		}
		
		length += 4;
		
		DataByteBuffer packet = new DataByteBuffer(length);
		
		for (int i = 0; i < pathes.length; i++) 
			packet.put(pathes[i]);
		
		packet.put(pathes.length);
		packet.put(names);
		
		return new DataPacket(101, packet);
		
	}
	
	public static DataPacket requestNamesPacket(int subjectCode) {
		
		DataByteBuffer packet = new DataByteBuffer(4);
		packet.put(subjectCode);
		
		return new DataPacket(102, packet);
	}
	
	public static DataPacket responseNamesPacket(FileDescription[] descriptions) {
		
		int length = 0;
		for (int i = 0; i < descriptions.length; i++)
			length += descriptions[i].getSizeBytes();
		
		DataByteBuffer packet = new DataByteBuffer(length);
		packet.put(descriptions);
		
		return new DataPacket(102, packet);
	}
	
	public static DataPacket requestAddSubjectPacket(String name) {
		
		DataByteBuffer packet = new DataByteBuffer(name.getBytes().length + 4);
		packet.put(name);
		
		return new DataPacket(103, packet);
	}
	
	public static DataPacket requestAddLevelPacket(String name) {
		
		DataByteBuffer packet = new DataByteBuffer(name.getBytes().length + 4);
		packet.put(name);
		
		return new DataPacket(103, packet);
	}
	
	public static DataPacket requestAddHeadPacket(int[] path, String name) {
		
		int length = name.getBytes().length + 4;
		length += (path.length + 1) * 4;
		
		DataByteBuffer packet = new DataByteBuffer(length);
		
		packet.put(path);
		packet.put(name);
		
		return new DataPacket(101, packet);
		
	}*/
	
	private int type;
	private DataByteBuffer data;
	
	public DataPacket(int type, DataByteBuffer data) {
		this.type = type;
		this.data = data;
	}
	
	public int getType() {
		return this.type;
	}
	
	public DataByteBuffer getData() {
		return this.data;
	}
	
	public void write(OutputStream out) {
		
		try {
			
			out.write(type);
			
			byte[] data = this.data.asArray();
			
			out.write((data.length >> 0) & 0xFF);
			out.write((data.length >> 8) & 0xFF);
			out.write((data.length >> 16) & 0xFF);
			out.write((data.length >> 24) & 0xFF);
			
			out.write(data);
			
			out.flush();
			
			for (int i = 0; i < data.length; i++)
				Logger.log(getClass(), data[i]);
			
		} catch (IOException e) {
			Logger.logError(this.getClass(), e);
			System.exit(-1);
		}
	}
	
}
