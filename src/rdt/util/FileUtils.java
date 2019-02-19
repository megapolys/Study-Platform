package rdt.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class FileUtils {

	private FileUtils() {}
	
	public static BufferedImage[] ppt2img(String filePath, String outputFolder) {
		
		try {
			
			File pptFile = new File(filePath);
			XMLSlideShow slideShow = new XMLSlideShow(new FileInputStream(pptFile));
			
			Dimension slideSize = slideShow.getPageSize();
			XSLFSlide[] slides = slideShow.getSlides().toArray(new XSLFSlide[0]);
			
			BufferedImage[] result = new BufferedImage[slides.length];
			for (int i = 0; i < result.length; i++) {
				
				result[i] = new BufferedImage(slideSize.width, slideSize.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D imgGraphics = result[i].createGraphics();
				
				imgGraphics.setPaint(Color.white);
				imgGraphics.fill(new Rectangle2D.Float(0, 0, slideSize.width, slideSize.height));
				
				slides[i].draw(imgGraphics);
				
				if (outputFolder != null) {
					File imgFile = new File(outputFolder + "/slide_" + i + ".png");
					if (!imgFile.exists())
						imgFile.createNewFile();
					
					ImageIO.write(result[i], "PNG", imgFile);
				}
				
			}
			
			slideShow.close();
			
			return result;
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return null;
		
	}
	
	private static byte[] readFile(File file) throws IOException {
		
		FileInputStream is = new FileInputStream(file);
			
		byte[] fileBytes = new byte[(int) file.length()];
		is.read(fileBytes);
		
		is.close();
		
		return fileBytes;
		
	}
	
	private static void packFilesRec(String currentPath, String relativePath, ArrayList<String> pathes, ArrayList<File> files) throws IOException {
		
		File currentFile = new File(currentPath);
		if (currentFile.isDirectory()) {
			
			String[] fileNames = currentFile.list();
			for (int i = 0; i < fileNames.length; i++)
				packFilesRec(currentPath + "/" + fileNames[i], relativePath + "/" + currentFile.getName(), pathes, files);
			
		} else {
			
			pathes.add(relativePath + "/" + currentFile.getName());
			files.add(currentFile);
			
		}
		
	}
	
	public static void packFiles(String inputPath, String outputPath) {
		
		try {
			
			File result = new File(outputPath);
			if (!result.exists())
				result.createNewFile();
			
			ArrayList<String> pathes = new ArrayList<String>();
			ArrayList<File> files = new ArrayList<File>();
			
			packFilesRec(inputPath, "", pathes, files);
			
			DataOutputStream output = new DataOutputStream(new FileOutputStream(result));
			output.writeInt(0xCAFEFACE);
			output.writeInt(pathes.size());
			
			for (int i = 0; i < pathes.size(); i++) {
				
				byte[] pathBytes = pathes.get(i).getBytes();
				
				output.writeInt(pathBytes.length);
				output.write(pathBytes);
				
			}
			
			output.flush();
			
			for (int i = 0; i < files.size(); i++) {
				Logger.log(i + "  " + files.get(i).getPath());
				output.writeInt((int) files.get(i).length());
				
				FileInputStream input = new FileInputStream(files.get(i));
				byte[] buffer = new byte[512];
				
				int read;
				while ((read = input.read(buffer)) != -1)
					output.write(buffer, 0, read);
				
				input.close();
				output.flush();
			}
			
			output.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		
	}
	
	public static boolean unpackFiles(String inputPath, String outputPath) {
		
		
		try {
			
			File inputFile = new File(inputPath);
			DataInputStream input = new DataInputStream(new FileInputStream(inputFile));
			
			int magic = input.readInt();
			if (magic != 0xCAFEFACE) {
				input.close();
				return false;
			}
			
			int filesNum = input.readInt();
			String[] pathes = new String[filesNum];
			
			for (int i = 0; i < filesNum; i++) {
				
				int pathLength = input.readInt();
				byte[] pathBytes = new byte[pathLength];
				
				input.read(pathBytes);
				
				pathes[i] = new String(pathBytes);
				
			}
			
			for (int i = 0; i < filesNum; i++) {
				
				int fileLength = input.readInt();
				byte[] fileBytes = new byte[fileLength];
				
				input.read(fileBytes);
				
				File outputFile = new File(outputPath + pathes[i]);
				if (!outputFile.exists()) {
					new File(outputFile.getPath().substring(0, outputFile.getPath().lastIndexOf('\\'))).mkdirs();
					outputFile.createNewFile();
				}
				
				FileOutputStream output = new FileOutputStream(outputFile);
				output.write(fileBytes);
				output.close();
				
			}
			
			input.close();
		
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return true;
	}

	public static boolean isFilePack(String inputPath){
		File inputFile = new File(inputPath);
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(inputFile));
			int magic = input.readInt();
			if (magic != 0xCAFEFACE) {
				input.close();
				return false;
			}
		} catch (Exception e) {
			Logger.logError(e);
		}
		return true;
	}
	
	public static void archive(String inputPath, String outputPath) {
		
		try {
			
			File inputFile = new File(inputPath);
			byte[] fileBytes = readFile(inputFile);
			byte[] out = new byte[fileBytes.length];
			
			Deflater deflater = new Deflater();
			deflater.setInput(fileBytes);
			deflater.finish();
			int outputLength = deflater.deflate(out);
			
			File result = new File(outputPath);
			if (!result.exists())
				result.createNewFile();
			
			FileOutputStream output =new FileOutputStream(result);
			
			output.write((fileBytes.length & 0xFF) >> 0);
			output.write((fileBytes.length & 0xFF00) >> 8);
			output.write((fileBytes.length & 0xFF0000) >> 16);
			output.write((fileBytes.length & 0xFF000000) >> 24);
			
			output.write(out, 0, outputLength);
			
			output.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	public static void unarchive(String inputPath, String outputPath) {
		
		try {
			
			File inputFile = new File(inputPath);
			File outputFile = new File(outputPath);
			
			if (!outputFile.exists())
				outputFile.createNewFile();
			
			byte[] inputFileBytes = readFile(inputFile);
			int outputFileLength = (((int) inputFileBytes[0]) & 0xFF) + ((((int) inputFileBytes[1]) & 0xFF) << 8)  + ((((int) inputFileBytes[2]) & 0xFF) << 16) + ((((int) inputFileBytes[3]) & 0xFF) << 24);
			byte[] outputFileBytes = new byte[outputFileLength];
			
			Inflater inflater = new Inflater();
			inflater.setInput(inputFileBytes, 4, inputFileBytes.length - 4);
			inflater.inflate(outputFileBytes);
			inflater.end();
			
			FileOutputStream output = new FileOutputStream(outputFile);
			output.write(outputFileBytes);
			output.close();
			
		} catch (IOException | DataFormatException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
}
