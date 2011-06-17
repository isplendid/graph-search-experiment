package sjtu.apex.gse.exp.util;

import java.util.Scanner;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;

public class UriLookUp {
	public static void main(String[] args) {
		IDManager sourceMan = new SleepyCatIDManager("tmp/id");
		Scanner kb = new Scanner(System.in);
		
		while (true) {
			int num = kb.nextInt();
			
			System.out.println(sourceMan.getURI(num));
		}
	}
}
