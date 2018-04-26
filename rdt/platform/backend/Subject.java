package rdt.platform.backend;

import java.util.ArrayList;

import rdt.net.DataByteBuffer;

public class Subject {
	
	private String name;
	
	private ArrayList<String> levels;
	
	private ArrayList<HeadPath> headPathes;
	private ArrayList<String> headElements;
	
	private ArrayList<DataFile> files;
	
	public Subject(String name) {
		
		this.name = name;
		
		this.levels = new ArrayList<String>();
		
		this.headPathes = new ArrayList<HeadPath>();
		this.headElements = new ArrayList<String>();
		
		this.files = new ArrayList<DataFile>();
		
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
		
		int[][] result = new int[headPathes.size()][];
		
		for (int i = 0; i < headPathes.size(); i++) 
			result[i] = headPathes.get(i).getPath();
		
		return result;
	}
	
	public void addLevel(String name) {
		this.levels.add(name);
	}
	
	public void addHeadElement(int[] path, String name) {
		this.headPathes.add(new HeadPath(path));
		this.headElements.add(name);
	}
	
	public void addDataFile(DataFile file) {
		files.add(file);
	}
	
	public String getLevel(int index) {
		return this.levels.get(index);
	}
	
	public String getHeadElement(int[] path) {
		return this.headElements.get(headPathes.indexOf(new HeadPath(path)));
	}
	
	public FileDescription[] getFileDescriptions() {
		
		FileDescription[] result = new FileDescription[files.size()];
		for (int i = 0; i < files.size(); i++)
			result[i] = files.get(i).getFileDescription();
		
		return result;
	}
	
	public DataFile[] getDataFiles(String hash) {
		
		for (int i = 0; i < files.size(); i++) {
			
			if (files.get(i).getFileDescription().getHash().equals(hash))
				return new DataFile[] {files.get(i)};
			
		}
		
		return null;
		
	}
	
	public int getSizeBytes() {
		
		int length = name.getBytes().length + 4;
		
		for (int i = 0; i < levels.size(); i++)
			length += levels.get(i).getBytes().length;
		
		for (int i = 0; i < files.size(); i++)
			length += files.get(i).getFileDescription().getSizeBytes();
		
		length += levels.size() * 4 + 4;
		
		for (int i = 0; i < headPathes.size(); i++) {
			length += (headPathes.get(i).getPath().length + 2) * 4;
			length += headElements.get(i).getBytes().length;
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
