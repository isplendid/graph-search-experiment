package com.ibm.gse.hash;

import java.util.HashMap;

/**
 * HashUnique executes a unique operation on a string array eliminating duplicate strings with the same hashing value
 * 
 * @author Tian Yuan
 */
public class HashUnique {
	HashFunction hf;
	
	public HashUnique(HashFunction hf) {
		this.hf = hf;
	}
	
	public String[] unique(String[] in) {
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		
		for (String s : in) {
			Integer hashCode = hf.hashInt(s);
			
			if (!hm.containsKey(hashCode)) {
				hm.put(hashCode, s);
			}
		}
		
		return (String[])hm.values().toArray();
	}
}
