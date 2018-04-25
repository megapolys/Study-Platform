package rdt.util;

import java.io.PrintStream;

public class Logger {
	
	private static PrintStream output = System.out;
	
	public static void setOutputStream(PrintStream output) {
		Logger.output = output;
	}

	public static void log(Class<?> c, Object obj) {
		output.println(c.getName() + ": " + obj.toString());
	}
	
	public static void logError(Class<?> c, Throwable e) {
		e.printStackTrace(output);
	}
	
}
