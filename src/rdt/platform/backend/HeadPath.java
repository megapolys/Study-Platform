package rdt.platform.backend;

public class HeadPath {

	private int[] path;
	
	public HeadPath(int[] path) {
		this.path = path;
	}
	
	public int[] getPath() {
		return this.path;
	}
	
	@Override
	public int hashCode() {
		
		int result = 0;
		for (int i = 0; i < path.length; i++)
			result = path[i] + 31 * result + i * 3;
		
		return result;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof HeadPath))
			return false;
		
		HeadPath objPath = (HeadPath) obj;
		if (objPath.path.length != path.length)
			return false;
		
		for (int i = 0; i < path.length; i++)
			if (path[i] != objPath.path[i])
				return false;
		
		return true;
		
	}
	
}
