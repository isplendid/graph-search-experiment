package com.ibm.gse.debug;

import com.ibm.gse.indexer.IDManager;
import com.ibm.gse.indexer.file.SleepyCatIDManager;

/**
 * 
 * Bug20081119p1
 * @author Tian Yuan
 */
public class Bug20081119p1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IDManager idman = new SleepyCatIDManager();
		String s;
		
		for (int i = 0; i < 100; i++ ) {
			System.out.println(s = idman.getURI(i));
			System.out.println(idman.getID(s));
		}

	}

}
