package com.ibm.gse.planner.dp;

import java.util.Set;

import com.ibm.gse.query.Plan;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;

/**
 * A container that contains the information of a DP state
 * @author Tian Yuan
 *
 */
public class OptimalArrayElem {
	private Set<QueryGraphEdge> edges;
	private Set<QueryGraphNode> nodes;
	private Set<QueryGraphNode> constrained;
	private Set<String> patterns;
	private Plan plan;
	
	public OptimalArrayElem(Plan plan, Set<String> contained) {
		this.patterns = contained;
		this.edges = plan.getSchema().getQueryGraph().getEdgeSet();
		this.nodes = plan.getSchema().getQueryGraph().getNodeSet();
		this.constrained = plan.getSchema().getQueryGraph().getConstrainedNodeSet();
		this.plan = plan;
	}
	
	public Set<QueryGraphEdge> getCoveredEdges() {
		return edges;
	}
	
	public Set<QueryGraphNode> getCoveredNodes() {
		return nodes;
	}
	
	public Set<String> getContainedPatterns() {
		return patterns;
	}
	
	public Set<QueryGraphNode> getConstrainedNodes() {
		return constrained;
	}
	
	public Plan getPlan() {
		return plan;
	}
	
}