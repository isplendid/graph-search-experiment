package com.ibm.gse.index.file.test;

import static org.junit.Assert.assertEquals;

import com.ibm.gse.temp.file.TempFileEntry;
import com.ibm.gse.temp.file.TempRepositoryFileReader;

public class ContentPrinter {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TempRepositoryFileReader tr = new TempRepositoryFileReader("data/raw/raw0", 1);
		TempFileEntry tfe;
		int cnt = 0;
		
		while ((tfe = tr.readRecord()) != null) {
			if (cnt < 100) {
				System.out.println(tfe.pattern);
			}
			cnt++;
		}
		
		System.out.println(cnt);
	}

}
