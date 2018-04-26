package rdt.util;

import java.io.PrintStream;

public class Logger {
	
	private static PrintStream output = System.out;
	private static PrintStream error = System.err;
	
	public static void setOutputStream(PrintStream output) {
		Logger.output = output;
	}
	
	public static void setErrorStream(PrintStream error) {
		Logger.error = error;
	}

	public static void log(Object obj) {
		output.println(Thread.currentThread().getStackTrace()[2].getClassName() + ": " + obj.toString());
	}
	
	public static void logError(Throwable e) {
		error.println("In class: " + Thread.currentThread().getStackTrace()[2].getClassName());
		e.printStackTrace(error);
	}
	
}
