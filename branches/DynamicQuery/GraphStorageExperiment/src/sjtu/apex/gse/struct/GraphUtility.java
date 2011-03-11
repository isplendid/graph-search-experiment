package sjtu.apex.gse.struct;

import java.util.HashMap;

/**
 * GraphUtility provides more complex operations on query graph
 * 
 * @author Tian Yuan
 *
 */
public class GraphUtility {
	
	public static QueryGraph extendConstraint(QueryGraph g, int toExt, int con) {
		return extendConstraint(g, toExt, con, false);
	}
	
	private static QueryGraphNode getNewNode(int lbl, int sn, boolean isHash) {
		if (isHash)
			return new HashQueryGraphNode(sn, null, lbl);
		else
			return new ConcreteQueryGraphNode(lbl, sn, null);
	}
	
	/**
	 * Extend a query graph by adding constraint on a general node
	 * @param g The original query graph
	 * @param toExt The id of the node to be constrained
	 * @param con The label of the constraint	
	 * @return The graph generated
	 */
	public static QueryGraph extendConstraint(QueryGraph g, int toExt, int con, boolean isHash) {
		QueryGraph ng = new QueryGraph();
		HashMap<QueryGraphNode, QueryGraphNode> map = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (int i = g.nodeCount() - 1; i >= 0; i--) {
			QueryGraphNode tn = g.getNode(i);
			QueryGraphNode nn;
			
			if (i == toExt)
				nn = getNewNode(con, tn.serialNo, isHash);
			else
				if (tn.isGeneral())
					nn = new GeneralQueryGraphNode(tn.serialNo, null);
				else
					nn = getNewNode(tn.getLabel(), tn.serialNo, isHash);
			
			ng.addNode(nn);
			map.put(tn, nn);
		}
		
		for (int i = g.edgeCount() - 1; i >= 0; i--) {
			QueryGraphEdge te = g.getEdge(i);
			ng.addEdge(map.get(te.getNodeFrom()), map.get(te.getNodeTo()), te.getLabel());
		}
		
		return ng;
	}
	
	/**
	 * Add an edge with a new general node to the given graph
	 * @param g - The original query graph.
	 * @param toExt - The ID of the node on which the edge is extended
	 * @param e - The label of the edge to be extended.
	 * @param dir - The direction of the edge. True - pointing to the new node; false - pointing from the new node.
	 * @return The new graph
	 */
	public static QueryGraph extendEdge(QueryGraph g, int toExt, int e, boolean dir) {
		return extendEdge(g, toExt, e, dir, false);
	}
	
	public static QueryGraph extendEdge(QueryGraph g, int toExt, int e, boolean dir, boolean isHash) {
		QueryGraph ng = new QueryGraph();
		HashMap<QueryGraphNode, QueryGraphNode> map = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (int i = g.nodeCount() - 1; i >= 0; i--) {
			QueryGraphNode tn = g.getNode(i);
			
			if (tn.isGeneral())
				map.put(tn, ng.addNode(new GeneralQueryGraphNode(tn.serialNo, null)));
			else
				map.put(tn, ng.addNode(getNewNode(tn.getLabel(), tn.serialNo, isHash)));
		}
		
		for (int i = g.edgeCount() - 1; i >= 0; i--) {
			QueryGraphEdge te = g.getEdge(i);
			ng.addEdge(map.get(te.getNodeFrom()), map.get(te.getNodeTo()), te.getLabel());
		}
		
		if (dir)
			ng.addEdge(map.get(g.getNode(toExt)), ng.addNode(), e);
		else
			ng.addEdge(ng.addNode(), map.get(g.getNode(toExt)), e);
		
		return ng; 
	}
}