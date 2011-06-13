package sjtu.apex.gse.exp.util;

import java.io.IOException;
import java.util.Scanner;

import sjtu.apex.gse.indexer.SourceManager;
import sjtu.apex.gse.indexer.file.SleepyCatSourceManager;

public class SourceLookUp {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SourceManager sourceMan = new SleepyCatSourceManager("tmp/src");
		Scanner kb = new Scanner(System.in);
		
		while (true) {
			int num = kb.nextInt();
			
			System.out.println(sourceMan.getSource(num));
		}
	}

}
