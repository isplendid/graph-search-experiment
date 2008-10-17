package com.ibm.gse.planner;

import com.ibm.gse.query.Plan;
import com.ibm.gse.struct.QuerySchema;

/**
 * The planner that plans a sequence of execution generating answers to the given
 * query pattern
 * @author Tian Yuan
 *
 */
public interface Planner {
	
	/**
	 * Plan a query graph into an execution tree
	 * @param g The given query
	 * @return The root of the execution tree
	 */
	public Plan plan(QuerySchema g);
}
