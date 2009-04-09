package sjtu.apex.gse.struct;

import java.util.HashMap;

/**
 * GraphUtility provides more complex operations on query graph
 * 
 * @author Tian Yuan
 *
 */
public class GraphUtility {
	
	/**
	 * Extend a query graph by adding constraint on a general node
	 * @param g The original query graph
	 * @param toExt The id of the node to be constrained
	 * @param con The label of the constraint	
	 * @return The graph generated
	 */
	public static QueryGraph extendConstraint(QueryGraph g, int toExt, String con) {
		QueryGraph ng = new QueryGraph();
		HashMap<QueryGraphNode, QueryGraphNode> map = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (int i = g.nodeCount() - 1; i >= 0; i--) {
			QueryGraphNode tn = g.getNode(i); 
			if (i == toExt)
				map.put(tn, g.addNode(con));
			else
				if (tn.isGeneral())
					map.put(tn, g.addNode());
				else
					map.put(tn, g.addNode(tn.getLabel()));
		}
		
		return ng;
	}
	
	public static QueryGraph extendEdge(QueryGraph g, int toExt, String e, boolean dir) {
		QueryGraph ng = new QueryGraph();
		HashMap<QueryGraphNode, QueryGraphNode> map = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (int i = g.nodeCount() - 1; i >= 0; i--) {
			QueryGraphNode tn = g.getNode(i);
			
			if (tn.isGeneral())
				map.put(tn, g.addNode());
			else
				map.put(tn, g.addNode(tn.getLabel()));
		}
		
		if (dir)
			ng.addEdge(ng.getNode(toExt), ng.addNode(), e);
		else
			ng.addEdge(ng.addNode(), ng.getNode(toExt), e);
		
		return ng; 
	}
}
