package com.mark;


/**
 * @author ****
 * 
 * Proto code test main...
 *
 */


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProtoParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Path p = Paths.get("./txnlog.dat");
		//Path p = Paths.get("./proto/txnlog.dat");

		try {
			BinaryParser parser = new BinaryParser();
			
			if(parser.readFile(p)) {
				parser.printReport();
				System.out.println("Happy Path!");
			}else
				System.out.println("Parse Failed-Not So Happy...");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
}
