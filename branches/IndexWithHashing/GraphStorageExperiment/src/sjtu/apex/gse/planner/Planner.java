package sjtu.apex.gse.planner;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.struct.QuerySchema;


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
