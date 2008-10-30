package com.ibm.gse.indexer;

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
	String[] wordList;
	String uri;
	String wordStr;

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
			wordStr = split[1];
			wordList = wordStr.split(" ");

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

	public String[] getWordList() {
		return wordList;
	}
	
	public String getWordStr() {
		return wordStr;
	}

}
