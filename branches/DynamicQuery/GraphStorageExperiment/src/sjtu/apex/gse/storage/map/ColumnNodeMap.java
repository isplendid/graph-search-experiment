package sjtu.apex.gse.storage.map;

import java.util.Map;

import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;


/**
 * This class generates mappings from nodes in pattern to columns in database
 * @author Tian Yuan
 *
 */
public interface ColumnNodeMap {
	
	/**
	 * Map nodes in the given pattern to columns in database 
	 * @param graph The given pattern
	 * @return A mapping from nodes to columns
	 */
	public Map<QueryGraphNode, Integer> getMap(QueryGraph graph);
}
