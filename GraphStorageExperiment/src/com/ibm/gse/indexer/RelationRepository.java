package com.ibm.gse.indexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RelationRepository {

	BufferedReader rd;
	String sub, pred, obj;

	public RelationRepository(String filename) {
		try {
			rd = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			rd = null;
			e.printStackTrace();
		}
	}

	public boolean next() {
		String temp;

		if (rd == null) return false;

		do {
			try {
				if ((temp = rd.readLine()) == null) return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			String[] split = temp.split("> ");
			
			if (split[2].startsWith("\"")) continue;
			sub = split[0] + ">";
			pred = split[1] + ">";
			obj = split[2] + ">";
			return true;
		} while (true);
	}

	public String getSubject() {
		return sub;
	}

	public String getPredicate() {
		return pred;
	}

	public String getObject() {
		return obj;
	}

	public void close() {
		try {
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
