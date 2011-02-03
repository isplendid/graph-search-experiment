package sjtu.apex.gse.operator;

import sjtu.apex.gse.struct.QuerySchema;


/**
 * A node in plan tree
 * @author Tian Yuan
 *
 */
public interface Plan {
	
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
	 * Return the number of IO operations occurred by this node
	 * @return
	 */
	public int diskIO();
	
	/**
	 * Return the number of instances of the schema
	 * @return
	 */
	public int resultCount();
}
