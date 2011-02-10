package sjtu.apex.gse.operator;

import java.util.Set;

import sjtu.apex.gse.struct.QueryGraphNode;

/**
 * The updatable version of scan
 * @author Tian Yuan
 *
 */
public interface UpdateScan extends RestorableScan {
	
	/**
	 * Insert an entry before the entry currently pointed to
	 * 
	 * The pointer remains pointing to the original node 
	 */
	public void insert();
	
	/**
	 * Delete the entry currently pointed to
	 * 
	 * The pointer points to the newly inserted one
	 */
	public void delete();
	
	/**
	 * Set the value in the entry corresponding to the given node 
	 * @param n The given node
	 * @param insID The new value
	 */
	public void setID(QueryGraphNode n, int insID);
	
	/**
	 * Set the value in the entry corresponding to the given node 
	 * @param nodeID The given node, by ID
	 * @param insID The new value
	 */
	public void setID(int nodeID, int insID);
	
	/**
	 * Set the source list
	 * @param set The set of relevant sources
	 */
	public void setSourceSet(Set<Integer> set);
}
