package com.ibm.gse.util;

import com.ibm.gse.query.Scan;

/**
 * The comparator that compares respective entries from two scan which are
 * currently pointed to 
 * @author Tian Yuan
 *
 */
public interface EntryComparator {
	
	/**
	 * Compare the two entries
	 * @param a The first scan
	 * @param b The second scan
	 * @return An integer greater than 0 if entry from the first scan is greater
	 * than the second one. An interger smaller than 0 if the first entry is 
	 * smaller. 0 if they are equal.
	 */
	public int compare(Scan a, Scan b);
}
