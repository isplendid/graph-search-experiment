package com.ibm.gse.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * Create a induced subgraph according to node
	 * @param ns The nodes contained in the subgraph
	 */
	public QueryGraph getNodeInducedSubgraph(Set<QueryGraphNode> ns) {
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
	}
	
	/**
	 * Create a induced subgraph according to edge
	 * @param es The edges contained in the subgraph
	 */
	public QueryGraph getEdgeInducedSubgraph(Set<QueryGraphEdge> es) {
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
	}
	
	/**
	 * Generate a node with the specified label and add into the graph
	 * @param label The given label
	 * @return The node generated
	 */
	public QueryGraphNode addNode(String label) {
		QueryGraphNode node = new QueryGraphNode(label);
		
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
	public QueryGraphEdge addEdge(QueryGraphNode from, QueryGraphNode to, String label) {
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
	 * Get a clone of this graph
	 */
	public QueryGraph clone() {
		return getNodeInducedSubgraph(null);
		
	}
}
