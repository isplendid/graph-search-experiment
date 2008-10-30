package com.ibm.gse.query;

import com.ibm.gse.struct.QuerySchema;

/**
 * The node that selects specified nodes out of the result
 * @author Tian Yuan
 *
 */
public class SelectPlan implements Plan {
	
	Plan src;
	QuerySchema sch;
	
	public SelectPlan(Plan src, QuerySchema sch) {
		this.src = src;
		this.sch = sch;
	}
	
	public Scan open() {
		return src.open();
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public QuerySchema getSchema() {
		return sch;
	}

	@Override
	public int diskIO() {
		return src.diskIO();
	}

	/**
	 * This is a rough estimation
	 */
	@Override
	public int resultCount() {
		return src.resultCount();
	}


}
