package rdt.net;

import java.io.IOException;
import java.io.OutputStream;

import rdt.util.Logger;

public class DataPacket {
	
	/*
	 *  99 - запрос на получение кодов предметов
	 * 100 - запрос на получение кодов и названий уровней иерархии
	 * 101 - запрос на получение оглавления
	 * 102 - запрос на получнение всех наименований
	 * 103 - запрос на добавление предмета
	 * 104 - запрос на добавление/изменение названия уровня иерархии
	 * 105 - запрос на изменение названия предмета
	 * 
	 */

	public static DataPacket getSubjectsPacket() {
		return new DataPacket(99, new DataByteBuffer(new byte[0]));
	}
	
	public static DataPacket getHierarchyPacket(int subjectCode) {
		
		DataByteBuffer packet = new DataByteBuffer(4);
		packet.put(subjectCode);
		
		return new DataPacket(100, packet);
	}
	
	public static DataPacket getHeadPacket(int subjectCode) {
		
		DataByteBuffer packet = new DataByteBuffer(4);
		packet.put(subjectCode);
		
		return new DataPacket(101, packet);
	}
	
	public static DataPacket getNamesPacket(int subjectCode) {
		
		DataByteBuffer packet = new DataByteBuffer(4);
		packet.put(subjectCode);
		
		return new DataPacket(102, packet);
	}
	
	public static DataPacket addSubjectPacket(String name) {
		
		DataByteBuffer packet = new DataByteBuffer(name.length() + 4);
		packet.put(name);
		
		return new DataPacket(103, packet);
	}
	
	public static DataPacket addLevelPacket(String name) {
		
		DataByteBuffer packet = new DataByteBuffer(name.length() + 4);
		packet.put(name);
		
		return new DataPacket(103, packet);
	}
	
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
			
		} catch (IOException e) {
			Logger.logError(this.getClass(), e);
			System.exit(-1);
		}
	}
	
}
