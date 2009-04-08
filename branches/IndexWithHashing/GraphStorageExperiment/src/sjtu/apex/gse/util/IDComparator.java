package sjtu.apex.gse.util;

import java.util.List;

import sjtu.apex.gse.query.Scan;


/**
 * The comparator that compares two entry according to instance ID
 * @author Tian Yuan
 *
 */
public class IDComparator implements EntryComparator {
	
	List<Integer> order;
	
	/**
	 * Create a comparator that compares two entries in the given order
	 * @param order The comparing order, by nodeID
	 */
	public IDComparator(List<Integer> order) {
		this.order = order;
	}
	

	public int compare(Scan a, Scan b) {
		for (int i = 0; i < order.size(); i++) {
			int idx = order.get(i);
			int insa = a.getID(idx);
			int insb = b.getID(idx);
			
			if (insa != insb)	return insa - insb;
		}
		return 0;
	}

}
