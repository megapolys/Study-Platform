package rdt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
			Logger.logError(e);
			System.exit(-1);
		}
		
		return Long.toHexString(hash).toUpperCase();
		
	}
	
	public static byte[] readFileBytes(String path) {
		
		try {
			
			File file = new File(path);
			FileInputStream in = new FileInputStream(file);
			
			ArrayList<byte[]> parts = new ArrayList<byte[]>();
			
			while (true) {
				
				byte[] part = new byte[4096];
				int read = in.read(part);
				
				if (read != -1) 
					parts.add(part);
				
				if (read < 4096) 
					break;
				
			}
			
			in.close();
			
			byte[] result = new byte[(parts.size() - 1) * 4096 + parts.get(parts.size() - 1).length];
			for (int i = 0; i < parts.size() - 1; i++) 
				System.arraycopy(parts.get(i), 0, result, 4096 * i, 4096);
			
			System.arraycopy(parts.get(parts.size() - 1), 0, result, 4096 * (parts.size() - 1), parts.get(parts.size() - 1).length);
			
			return result;
			
		} catch (IOException e) {
			Logger.logError(e);
			System.exit(-1);
		}
		
		return null;
		
	}
	
	public static void writeFileBytes(String path, byte[] bytes) {
		
		try {
			
			File file = new File(path);
			FileOutputStream out = new FileOutputStream(file);
			
			out.write(bytes);
			
			out.flush();
			out.close();
			
		} catch (IOException e) {
			Logger.logError(e);
			System.exit(-1);
		}
		
	}
	
}
