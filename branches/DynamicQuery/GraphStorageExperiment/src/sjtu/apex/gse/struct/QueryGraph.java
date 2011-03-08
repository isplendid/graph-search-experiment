package sjtu.apex.gse.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.hash.HashFunction;

/**
 * The structure of a query graph, i.e. a query pattern
 * @author Tian Yuan
 *
 */
public class QueryGraph {
	List<QueryGraphNode> nodes = new ArrayList<QueryGraphNode>();
	List<QueryGraphEdge> edges = new ArrayList<QueryGraphEdge>();
	
	/**
	 * Create an empty graph
	 */
	public QueryGraph() {
		
	}
	
	/**
	 * Create an induced subgraph according to node constraints set and edge constraints set
	 * @param ns - Node set
	 * @param es - Edge set
	 * @param toHash - Nodes that are to be converted to hash nodes
	 * @param hf - The hash function. If this is set to be null, no hash transformation will be made on the induced subgraph.
	 * @return
	 */
	public QueryGraph getInducedSubgraph(Set<QueryGraphNode> ns, Set<QueryGraphEdge> es, Set<QueryGraphNode> toHash, HashFunction hf) {
		QueryGraph result = new QueryGraph();
		Map<QueryGraphNode, QueryGraphNode> map = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		if (es != null)
		for (QueryGraphEdge e : es) {
			QueryGraphNode from, to;
			
			if ((from = map.get(e.from)) == null) {
				if (ns != null && ns.contains(e.from))
					if (toHash != null && toHash.contains(e.from))
						from = e.from.getAncestor().getHashClone(hf);
					else
						from = e.from.getAncestor().clone();
				else
					from = e.from.getGeneralClone();
				map.put(e.from, from);
				result.addNode(from);
			}
			
			if ((to = map.get(e.to)) == null) {
				if (ns != null && ns.contains(e.to))
					if (toHash != null && toHash.contains(e.to))
						to = e.to.getAncestor().getHashClone(hf);
					else
						to = e.to.getAncestor().clone();
				else
					to = e.to.getGeneralClone();
				map.put(e.to, to);
				result.addNode(to);
			}
			
			result.addEdge(e.clone(map));
		}
		
		if (ns != null) {
			Set<QueryGraphNode> added = map.keySet();
			for (QueryGraphNode n : ns)
				if (!added.contains(n)) {
					QueryGraphNode nn;
					
					if (toHash != null && toHash.contains(n))
						nn = n.getHashClone(hf);
					else
						nn = n.clone();
					
					result.addNode(nn);
				}
		}
		
		return result;
	}
	
	/**
	 * Create an induced subgraph according to node
	 * @param ns The nodes contained in the subgraph
	 */
/*	public QueryGraph getNodeInducedSubgraph(Set<QueryGraphNode> ns) {
		QueryGraph result = new QueryGraph();
		Map<QueryGraphNode, QueryGraphNode> map = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (QueryGraphNode n : nodes) 
			if (ns == null || ns.contains(n)){
				QueryGraphNode nn = n.clone();
			
				map.put(n, nn);
				result.addNode(nn);
			}
		
		QueryGraphEdge ne;
		for (QueryGraphEdge e : edges)
			if ((ne = e.clone(map)) != null)
				result.addEdge(ne);
		
		return result;
	}*/
	
	/**
	 * Create a induced subgraph according to edge
	 * @param es The edges contained in the subgraph
	 */
/*	public QueryGraph getEdgeInducedSubgraph(Set<QueryGraphEdge> es) {
		QueryGraph result = new QueryGraph();
		Map<QueryGraphNode, QueryGraphNode> map = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (QueryGraphEdge e : es) {
			QueryGraphNode from, to;
			
			if ((from = map.get(e.from)) == null) {
				from = e.from.clone();
				map.put(e.from, from);
				result.addNode(from);
			}
			
			if ((to = map.get(e.to)) == null) {
				to = e.to.clone();
				map.put(e.to, to);
				result.addNode(to);
			}
			
			result.addEdge(e.clone(map));
		}
		
		return result;
	}*/
	
	/**
	 * Generate a general node without any keyword constraints
	 */
	public QueryGraphNode addNode() {
		QueryGraphNode node = new GeneralQueryGraphNode();
		
		nodes.add(node);
		return node;
	}
	
	public QueryGraphNode addNode(int label) {
		return addNode(label, false);
	}
	
	/**
	 * Generate a node with the specified label and add into the graph
	 * @param label The given label
	 * @param isHash indicates whether to add an hash node
	 * @return The node generated
	 */
	public QueryGraphNode addNode(int label, boolean isHash) {
		QueryGraphNode node;
		
		if (isHash)
			node = new HashQueryGraphNode(label);
		else
			node = new ConcreteQueryGraphNode(label);
		
		nodes.add(node);
		return node;
	}
	
	/**
	 * Add an existing node into the graph
	 * @param node The existing node
	 * @return The node itself
	 */
	public QueryGraphNode addNode(QueryGraphNode node) {
		nodes.add(node);
		return node;
	}
	
	/**
	 * Delete the specified node and the edges linked to it
	 * @param node The given node
	 */
	public void deleteNode(QueryGraphNode node) {
		nodes.remove(node);
		for (QueryGraphEdge ie : node.inEdge)
			ie.to.outEdge.remove(ie);
		for (QueryGraphEdge oe : node.outEdge)
			oe.from.outEdge.remove(oe);
	}
	
	/**
	 * Delete the specified edge
	 * @param edge The given edge
	 */
	public void deleteEdge(QueryGraphEdge edge) {
		edges.remove(edge);
		edge.from.outEdge.remove(edge);
		edge.to.inEdge.remove(edge);
	}
	
	/**
	 * Generate a directed edge between the two given node and add into the graph 
	 * @param from The start of the edge
	 * @param to The end of the edge
	 * @param label The label of the edge
	 * @return The edge generated
	 */
	public QueryGraphEdge addEdge(QueryGraphNode from, QueryGraphNode to, int label) {
		QueryGraphEdge edge = new QueryGraphEdge(from, to, label);
		
		edges.add(edge);
		return edge;
	}
	
	/**
	 * Add an existing edge into the graph
	 * @return The edge itself
	 */
	public QueryGraphEdge addEdge(QueryGraphEdge edge) {
		edges.add(edge);
		
		return edge;
	}
	
	/**
	 * Get the number of nodes
	 */
	public int nodeCount() {
		return nodes.size();
	}
	
	/**
	 * Get the number of edges
	 */
	public int edgeCount() {
		return edges.size();
	}
	
	
	
	/**
	 * Get a node of the graph
	 * @param idx The id of the node
	 */
	public QueryGraphNode getNode(int idx) {
		return nodes.get(idx);
	}
	
	/**
	 * Get an edge of the graph
	 * @param idx The id of the edge
	 */
	public QueryGraphEdge getEdge(int idx) {
		return edges.get(idx);
	}
	
	/**
	 * Get the edge set of this graph
	 */
	public Set<QueryGraphEdge> getEdgeSet() {
		return new HashSet<QueryGraphEdge>(edges);
	}
	
	/**
	 * Get the node set of this graph 
	 */
	public Set<QueryGraphNode> getNodeSet() {
		return new HashSet<QueryGraphNode>(nodes);
	}
	
	/**
	 * Get the node set of this graph that are not empty
	 */
	public Set<QueryGraphNode> getConstrainedNodeSet() {
		Set<QueryGraphNode> result = new HashSet<QueryGraphNode>();
		for (QueryGraphNode n : nodes)
			if (!n.isGeneral())
				result.add(n);
		
		return result;
	}
	
	/**
	 * Get the set of nodes that satisfied the constraints on them
	 * @return
	 */
	public Set<QueryGraphNode> getSatisfiedNodeSet() {
		Set<QueryGraphNode> result = new HashSet<QueryGraphNode>();
		
		for (QueryGraphNode n : nodes)
			if (!n.isGeneralized())
				result.add(n);
		
		return result;
	}
	
	/**
	 * Get a clone of this graph
	 */
	public QueryGraph clone() {
		return getInducedSubgraph(getConstrainedNodeSet(), getEdgeSet(), null, null);
		
	}
}
