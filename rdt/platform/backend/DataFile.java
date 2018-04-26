package rdt.platform.backend;

import rdt.util.Utils;

public class DataFile {

	private String fileFolder;
	private FileDescription description;
	
	private byte[] bytes;
	
	public DataFile(String fileFolder, FileDescription description) {
		
		this.fileFolder = fileFolder;
		this.description = description;
		
		this.bytes = null;
		
	}
	
	public boolean isInMemory() {
		return this.bytes != null;
	}
	
	public void readToMemory() {
		this.bytes = Utils.readFileBytes(fileFolder + description.getName());
	}
	
	public void readToMemory(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public void writeToHDD() {
		
		if (bytes == null) 
			throw new RuntimeException("File not in memory! Use readToMemory() first!");
		
		Utils.writeFileBytes(fileFolder + description.getName(), bytes);
		
	}
	
	public void clearMemory() {
		bytes = null;
	}
	
	public void setFolderPath(String path) {
		this.fileFolder = path;
	}
	
	public String getFileFolder() {
		return this.fileFolder;
	}
	
	public FileDescription getFileDescription() {
		return this.description;
	}
	
	public byte[] getBytes() {
		return this.bytes;
	}
	
	public int getSizeBytes() {
		
		if (bytes == null) 
			throw new RuntimeException("File not in memory! Use readToMemory() first!");
		
		return bytes.length + description.getSizeBytes() + 4;
	}
	
}
