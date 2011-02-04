package sjtu.apex.gse.operator;

import java.util.Set;

import sjtu.apex.gse.struct.QueryGraphNode;

/**
 * A scanner or iterator that runs through all results
 * @author Tian Yuan
 *
 */
public interface Scan {
	
	/**
	 * Set the pointer before the first entry
	 */
	public void beforeFirst();
	
	/**
	 * Set the pointer to the next entry
	 * @return Return true if the next entry exists otherwise return false
	 */
	public boolean next();
		
	/**
	 * Close the scan and release all resources
	 */
	public void close();
	
	/**
	 * Get the instance ID corresponding to the given node in the current entry
	 * @param n The given node on the query graph
	 * @return Instance ID
	 */
	public int getID(QueryGraphNode n);
	
	public Set<Integer> getSourceSet();
	
	/**
	 * Get the instance ID corresponding to the given node in the current entry
	 * @param nodeID The given node on the query graph, by ID in the query graph
	 * @return Instance ID
	 */
	public int getID(int nodeID);
	
	/**
	 * Get if the result set contains the specified node
	 * @param n The given node
	 */
	public boolean hasNode(QueryGraphNode n);
	
}
