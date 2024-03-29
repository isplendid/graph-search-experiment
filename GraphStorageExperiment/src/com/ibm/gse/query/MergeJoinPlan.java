package com.ibm.gse.query;

import java.util.ArrayList;
import java.util.List;

import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;

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
		return new MergeJoinScan((UpdateScan)l.open(), (UpdateScan)r.open(), li, ri, sch);
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
		return l.resultCount() + r.resultCount();
	}


}
