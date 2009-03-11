package com.ibm.gse.pattern;

/**
 * This interface defines a hash function from string to 
 * 
 * @author Tian Yuan
 */
public interface HashFunction {
	
	/**
	 * Map the input string to the output string
	 */
	public String hash(String str);
}
