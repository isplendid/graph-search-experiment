package com.ibm.gse.indexer.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * 
 * @author Tian Yuan
 * 
 */
public class InstanceKeywordRepository {

	BufferedReader rd;
	String[] words;
	String uri;

	public InstanceKeywordRepository(String filename) {
		try {
			rd = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			rd = null;
			e.printStackTrace();
		}
	}

	public boolean next() {
		String temp;

		if (rd == null)
			return false;

		do {
			try {
				temp = rd.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			if (temp == null) return false;

			String[] split = temp.split("\t");
			
			if (split.length < 2) continue;

			uri = split[0];
			words = split[1].split(" ");

			return true;
		} while (true);
	}

	public void close() {
		try {
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getInstance() {
		return uri;
	}

	public String[] getWords() {
		return words;
	}

}
