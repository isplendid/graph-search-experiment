package com.ibm.gse.struct;

/**
 * A container that contains connectivity of a specified node
 * @author Tian Yuan
 *
 */
public class Connectivity {
	private QueryGraphEdge edge;
	private QueryGraphNode node;
	
	Connectivity(QueryGraphEdge edge, QueryGraphNode node) {
		this.edge = edge;
		this.node = node;
	}
	
	/**
	 * Get the edge this connectivity represents 
	 */
	public QueryGraphEdge getEdge() {
		return edge;
	}
	
	/**
	 * Get the node in this connectivity other than the given node
	 */
	public QueryGraphNode getNode() {
		return node;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isOutEdge() {
		return edge.to == node;
	}
}
