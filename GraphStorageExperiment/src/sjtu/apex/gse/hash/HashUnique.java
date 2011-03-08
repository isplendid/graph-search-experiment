package sjtu.apex.gse.hash;

import java.util.ArrayList;
import java.util.HashSet;

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
	
	public Integer[] unique(int[] in) {
		HashSet<Integer> hm = new HashSet<Integer>();
		ArrayList<Integer> ret = new ArrayList<Integer>();
		
		for (int s : in) {
			Integer hashCode = hf.hashInt(s);
			
			if (!hm.contains(hashCode)) {
				hm.add(hashCode);
				ret.add(s);
			}
		}
		
		Object[] o = ret.toArray();
		Integer[] s = new Integer[ret.size()];
		
		System.arraycopy(o, 0, s, 0, o.length);
		return s;
	}
}
