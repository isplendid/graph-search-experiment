package sjtu.apex.gse.query;

import java.util.List;

import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


/**
 * The scan for a merge join
 * @author Tian Yuan
 *
 */
public class MergeJoinScan implements Scan {
	
	private UpdateScan l, r;
	List<Integer> lo, ro;
	int[] joinVal;
	QuerySchema sch;
	
	public MergeJoinScan(UpdateScan l, UpdateScan r, List<Integer> lo, List<Integer> ro, QuerySchema sch) {
		this.sch = sch;
		this.l = l;
		this.r = r;
		this.lo = lo;
		this.ro = ro;
		joinVal = new int[lo.size()];
		for (int i = 0; i < joinVal.length; i++)
			joinVal[i] = -1;
		beforeFirst();
	}

	public void beforeFirst() {
		l.beforeFirst();
		r.beforeFirst();
	}

	public void close() {
		l.close();
		r.close();
	}
	
	public boolean next() {
		
		boolean hasNextR = r.next();
		if (hasNextR && equalJV(r, ro))
			return true;
		
		boolean hasNextL = l.next();
		if (hasNextL && equalJV(l, lo)) {
			r.restorePosition();
			return true;
		}
		
		while (hasNextL && hasNextR) {
			if (compare(l, r, lo, ro) < 0)
				hasNextL = l.next();
			else if (compare(l, r, lo, ro) > 0)
				hasNextR = r.next();
			else {
				r.savePosition();
				setJV(r, ro);
				return true;
			}
		}
			
		return false;
	}

	public int getID(QueryGraphNode n) {
		if (l.hasNode(n))
			return l.getID(n);
		else if (r.hasNode(n))
			return r.getID(n);
		else
			return -1;
	}
	
	public int getID(int nodeID) {
		return getID(sch.getSelectedNode(nodeID));
	}
	
	public boolean equalJV(Scan src, List<Integer> n) {
		for (int i = 0; i < n.size(); i++)
			if (src.getID(n.get(i)) != joinVal[i]) return false;
		return true;
	}
	
	public void setJV(Scan src, List<Integer> n) {
		for (int i = 0; i < n.size(); i++)
			joinVal[i] = src.getID(n.get(i));
	}
	
	public int compare(Scan l, Scan r, List<Integer> lo, List<Integer> ro) {
		for (int i = 0; i < lo.size(); i++) {
			int lv = l.getID(lo.get(i));
			int rv = r.getID(ro.get(i));
			if (lv != rv)
				return lv - rv;
		}
		return 0;
	}

	public boolean hasNode(QueryGraphNode n) {
		return sch.hasNode(n);
	}

}
