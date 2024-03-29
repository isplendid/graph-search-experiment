package com.ibm.gse.query;

import java.util.ArrayList;
import java.util.List;

import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;
import com.ibm.gse.util.EntryComparator;
import com.ibm.gse.util.IDComparator;
import com.ibm.gse.util.MergeSorter;

/**
 * The plan that sorts a result table according to the specified order
 * @author Tian Yuan
 *
 */
public class SortPlan implements Plan {
	
	final static int c = 3;
	
	EntryComparator comp;
	Plan src;
	
	public SortPlan(Plan src, List<QueryGraphNode> q) {
		this.src = src;
		QuerySchema sch = src.getSchema();
		
		List<Integer> order = new ArrayList<Integer>(q.size());
		for (int i = 0; i < q.size(); i++)
			order.add(sch.getNodeID(q.get(i)));
		
		comp = new IDComparator(order);
	}
	
	public Scan open() {
		Scan s = src.open();
		
		return MergeSorter.sort(s, src.getSchema(), comp);
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public QuerySchema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int diskIO() {
		return c * src.resultCount();
	}

	@Override
	public int resultCount() {
		return src.resultCount();
	}

}
