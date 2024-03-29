package com.ibm.gse.pattern;

import java.util.Set;

import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;

/**
 * 
 * @author Tian Yuan
 * 
 */
public class PatternInfo {
	private QueryGraph graph;
	private String patternStr;
	private Set<QueryGraphEdge> usedEdge;
	private Set<QueryGraphNode> usedNode;
	private Set<QueryGraphNode> constrainedNode;
	private int insCnt;
	
	public PatternInfo(QueryGraph graph, String patternStr, int insCnt) {
		this.graph = graph;
		this.patternStr = patternStr;
		this.usedEdge = graph.getEdgeSet();
		this.usedNode = graph.getNodeSet();
		this.constrainedNode = graph.getConstrainedNodeSet();
		this.insCnt = insCnt;
	}
	
	/**
	 * Get the structure of this pattern
	 */
	public QueryGraph getPattern() {
		return graph;
	}
	
	/**
	 * Get the encode of this pattern
	 */
	public String getPatternString() {
		return patternStr;
	}

	/**
	 * Get the edges covered by this pattern 
	 */
	public Set<QueryGraphEdge> getCoveredEdges() {
		return usedEdge;
	}
	
	/**
	 * Get the nodes covered by this pattern
	 */
	public Set<QueryGraphNode> getCoveredNodes() {
		return usedNode;
	}
	
	/**
	 * Get the nodes that have constraints
	 */
	public Set<QueryGraphNode> getConstrainedNodes() {
		return constrainedNode;
	}
	
	/**
	 * Get the number of instances under this pattern 
	 */
	public int getInstanceCount() {
		return insCnt;
	}
	
	public String toString() {
		return patternStr;
	}
}
