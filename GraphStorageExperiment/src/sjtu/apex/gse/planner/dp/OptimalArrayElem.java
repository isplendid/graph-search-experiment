package sjtu.apex.gse.planner.dp;

import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.pattern.PatternInfo;
import sjtu.apex.gse.query.Plan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;


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
	
	public OptimalArrayElem(Plan plan, Set<String> contained, Set<QueryGraphNode> lastConstrained, PatternInfo joinedPattern) {
		
		if (lastConstrained != null)
			this.constrained = new HashSet<QueryGraphNode>(lastConstrained);
		else
			this.constrained = new HashSet<QueryGraphNode>();
		if (joinedPattern.getPattern().nodeCount() == 1)
			constrained.add(joinedPattern.getPattern().getNode(0));
		
		QueryGraph qg = plan.getSchema().getQueryGraph();
		this.patterns = contained;
		this.edges = qg.getEdgeSet();
		this.nodes = qg.getNodeSet();
		
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