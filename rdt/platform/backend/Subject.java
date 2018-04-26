package rdt.platform.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import rdt.net.DataByteBuffer;

public class Subject {
	
	private String name;
	
	private ArrayList<String> levels;
	private HashMap<HeadPath, String> head;
	private ArrayList<FileDescription> fileDescriptions;
	
	public Subject(String name) {
		
		this.name = name;
		
		this.levels = new ArrayList<String>();
		this.head = new HashMap<HeadPath, String>();
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public String[] getLevels() {
		
		String[] result = new String[levels.size()];
		levels.toArray(result);
		
		return result;
	}
	
	public int[][] getHeadPathes() {
		
		Set<HeadPath> pathSet = head.keySet();
		int[][] result = new int[pathSet.size()][];
		
		int i = 0;
		for (HeadPath path : pathSet) {
			
			result[i] = path.getPath();
			i++;
			
		}
		
		return result;
	}
	
	public void addLevel(String name) {
		this.levels.add(name);
	}
	
	public void addHeadElement(int[] path, String name) {
		this.head.put(new HeadPath(path), name);
	}
	
	public void addFileDescription(FileDescription description) {
		this.fileDescriptions.add(description);
	}
	
	public String getLevel(int index) {
		return this.levels.get(index);
	}
	
	public String getHeadElement(int[] path) {
		return this.head.get(new HeadPath(path));
	}
	
	public FileDescription getFileDescription(int index)  {
		return this.fileDescriptions.get(index);
	}
	
	public FileDescription[] getFileDescriptions() {
		
		FileDescription[] result = new FileDescription[fileDescriptions.size()];
		fileDescriptions.toArray(result);
		
		return result;
	}
	
	public int getSizeBytes() {
		
		int length = name.getBytes().length + 4;
		
		for (int i = 0; i < levels.size(); i++)
			length += levels.get(i).getBytes().length;
		
		for (int i = 0; i < fileDescriptions.size(); i++)
			length += fileDescriptions.get(i).getSizeBytes();
		
		length += levels.size() * 4 + 4;
		
		Set<HeadPath> pathes = head.keySet();
		for (HeadPath path : pathes) {
			length += (path.getPath().length + 2) * 4;
			length += head.get(path).getBytes().length;
		}
		
		return length + 8;
		
	}
	
	public byte[] getBytes() {
		
		DataByteBuffer buffer = new DataByteBuffer(getSizeBytes());
		buffer.put(this);
		
		return buffer.asArray();
		
	}
	
	public static Subject fromBytes(byte[] bytes) {
		
		DataByteBuffer buffer = new DataByteBuffer(bytes);
		return buffer.getSubject();
		
	}
	
}
