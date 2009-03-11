package com.ibm.gse.pattern;

/**
 * Class ModHash is a hash function based on modulo operation  
 * 
 * @author Yuan Tian
 */
public class ModHash implements HashFunction {
	int mod;
	
	public ModHash() {
		mod = 13;
	}
	
	public ModHash(int mod) {
		this.mod = mod;
	}

	@Override
	public String hash(String str) {
		return Integer.toString(str.hashCode() % mod);
	}

}
