package sjtu.apex.gse.operator.join;

import java.util.ArrayList;
import java.util.List;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class HashJoinPlan implements Plan {
	
	private Plan l;
	private Plan r;
	private List<Integer> li, ri;
	private QuerySchema sch;
	
	public HashJoinPlan(Plan l, Plan r, List<QueryGraphNode> jn, QuerySchema sch) {
		QuerySchema tl, tr;
		
		tl = l.getSchema();
		tr = r.getSchema();
		li = new ArrayList<Integer>(jn.size());
		ri = new ArrayList<Integer>(jn.size());
		for (int i = 0; i < jn.size(); i++) {
			li.add(tl.getNodeID(jn.get(i)));
			ri.add(tr.getNodeID(jn.get(i)));
		}
		
		this.l = l;
		this.r = r;
		this.sch = sch;
	}

	@Override
	public Scan open() {
		return new HashJoinScan(l.open(), r.open(), li, ri, sch);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public QuerySchema getSchema() {
		return sch;
	}

	@Override
	public int diskIO() {
		return l.diskIO() + r.diskIO() + resultCount();
	}

	@Override
	public int resultCount() {
		return Math.min(l.resultCount(), r.resultCount());
	}
	
	@Override
	public String toString() {
		return "HashJoinPlan(" + l.toString() + "," + r.toString() + ")";
	}

	@Override
	public int executionCost() {
		return l.executionCost() + r.executionCost() + diskIO();
	}

	@Override
	public int webAccess() {
		return l.webAccess() + r.webAccess();
	}

}
