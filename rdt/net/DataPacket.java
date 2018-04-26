package rdt.net;

import java.io.IOException;
import java.io.OutputStream;

import rdt.platform.backend.DataFile;
import rdt.platform.backend.Subject;
import rdt.util.Logger;

public class DataPacket {
	
	/*
	 * 	98 - ок
	 *  99 - запрос на получение кодов предметов
	 * 100 - запрос на получение описания предмета
	 * 101 - запрос на получение файла(-ов)
	 * 102 - запрос на добавление предмета
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
	
	public static DataPacket requestFilesPacket(String subjectName, String hash) {
		
		int length = subjectName.getBytes().length + hash.getBytes().length + 8;
		DataByteBuffer buffer = new DataByteBuffer(length);
		
		buffer.put(hash);
		buffer.put(subjectName);
		
		return new DataPacket(101, buffer);
		
	}
	
	public static DataPacket responseFilesPacket(DataFile[] files) {
		
		int length = 4;
		for (int i = 0; i < files.length; i++) {
			files[i].readToMemory();
			length += files[i].getSizeBytes();
		}
		
		DataByteBuffer buffer = new DataByteBuffer(length);
		
		buffer.put(files);
		return new DataPacket(101, buffer);
		
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
			
			for (int i = 0; i < data.length; i++)
				Logger.log(data[i]);
			
		} catch (IOException e) {
			Logger.logError(e);
			System.exit(-1);
		}
	}
	
}
