package rdt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Utils {
	
	private static long hash(byte[] arr) {
		
		long hash = 0;
		for (int i = 0; i < arr.length; i++)
			hash = (((int) arr[i]) & 0xFF) + 31 * hash;
		
		return hash;
		
	}
	
	public static String getHash(String filePath) {
		
		long hash = 0;
		
		try {
			
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			
			byte[] buffer = new byte[512];
			while (fis.read(buffer) != -1) 
				hash = hash(buffer) + 31 * hash;
			
			fis.close();
			
		} catch (IOException e) {
			Logger.logError(Utils.class, e);
			System.exit(-1);
		}
		
		return Long.toHexString(hash).toUpperCase();
		
	}
	
}
