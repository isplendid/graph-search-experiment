package sjtu.apex.gse.operator;

import sjtu.apex.gse.operator.visitor.PlanClient;
import sjtu.apex.gse.struct.QuerySchema;


/**
 * A node in plan tree
 * @author Tian Yuan
 *
 */
public interface Plan extends PlanClient {
	
	/**
	 * Open the result set of the node in the plan tree and 
	 * the pointer is set before the first entry
	 * @return a scan of the result set
	 */
	public Scan open();
	
	/**
	 * Close the plan and release all resources
	 */
	public void close();
	
	/**
	 * Return the schema of current result
	 * @return
	 */
	public QuerySchema getSchema();
	
	/**
	 * @return The number of IO operations occurred by this node
	 */
	public int diskIO();
	
	/**
	 * @return The number of instances of the schema
	 */
	public int resultCount();
	
	/**
	 * @return The number of web accesses to the sources
	 */
	public int webAccess();
	
	/**
	 * @return The cost of this plan including the descendant plans
	 */
	public int executionCost();
}
