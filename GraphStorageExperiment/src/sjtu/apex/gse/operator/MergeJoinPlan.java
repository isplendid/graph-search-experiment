package sjtu.apex.gse.operator;

import java.util.ArrayList;
import java.util.List;

import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


/**
 * The node that merges two scan according to foreign keys
 * @author Tian Yuan
 *
 */
public class MergeJoinPlan implements Plan {
	
	private Plan l, r;
	private List<Integer> li, ri;
	private QuerySchema sch;
	
	public MergeJoinPlan(Plan l, Plan r, List<QueryGraphNode> jn, QuerySchema sch) {
		QuerySchema tl, tr;
		
		tl = l.getSchema();
		tr = r.getSchema();
		li = new ArrayList<Integer>(jn.size());
		ri = new ArrayList<Integer>(jn.size());
		for (int i = 0; i < jn.size(); i++) {
			li.add(tl.getNodeID(jn.get(i)));
			ri.add(tr.getNodeID(jn.get(i)));
		}
		
		
		this.l = new SortPlan(l, jn);
		this.r = new SortPlan(r, jn);
		this.sch = sch;
	}

	public Scan open() {
		return new MergeJoinScan((RestorableScan)l.open(), (RestorableScan)r.open(), li, ri, sch);
	}
	
	public void close() {
		// TODO Auto-generated method stub

	}

	public QuerySchema getSchema() {
		return sch;
	}

	@Override
	public int diskIO() {
		return l.diskIO() + r.diskIO() + resultCount();
	}

	/**
	 * This is a rough estimation
	 */
	@Override
	public int resultCount() {
		return Math.min(l.resultCount(), r.resultCount());
	}

	@Override
	public String toString() {
		return "MergeJoin(" + r.toString() + "," + l.toString() + ")";
	}

}
