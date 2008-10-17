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
	private Set<QueryGraphNode> availNode;
	private int insCnt;
	
	public PatternInfo(QueryGraph graph, String patternStr, Set<QueryGraphEdge> usedEdge, int insCnt) {
		this.graph = graph;
		this.patternStr = patternStr;
		this.usedEdge = usedEdge;
		this.availNode = graph.getNodeSet();
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
		return availNode;
	}
	
	/**
	 * Get the number of instances under this pattern 
	 */
	public int getInstanceCount() {
		return insCnt;
	}
}
