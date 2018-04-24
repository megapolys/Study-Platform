package rdt.net;

public class FileDescription {

	private int type;
	private int[] path;
	private String name;
	private String hash;
	
	public FileDescription(int type, int[] path, String name, String hash) {
		this.type = type;
		this.path = path;
		this.name = name;
		this.hash = hash;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int[] getPath() {
		return path;
	}
	
	public void setPath(int[] path) {
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public int getSizeBytes() {
		return (4 + path.length) * 4 + name.getBytes().length + hash.length();
	}
	
}
