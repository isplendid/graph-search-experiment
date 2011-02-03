package sjtu.apex.gse.planner.dp;

import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.pattern.PatternInfo;
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
	private Set<QueryGraphNode> satisfied;
	private Set<PatternInfo> patterns;
	private Plan plan;
	
	public OptimalArrayElem(Plan plan, Set<PatternInfo> contained, Set<QueryGraphNode> lastSatisfied, PatternInfo joinedPattern) {
		
		if (lastSatisfied != null)
			this.satisfied = new HashSet<QueryGraphNode>(lastSatisfied);
		else
			this.satisfied = new HashSet<QueryGraphNode>();
		
		QueryGraph tg = joinedPattern.getPattern();
		for (int i = tg.nodeCount() - 1; i >= 0; i--)
			if (!tg.getNode(i).isGeneral() && !tg.getNode(i).isGeneralized())
				satisfied.add(joinedPattern.getPattern().getNode(i));
		
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
	
	public Set<PatternInfo> getContainedPatterns() {
		return patterns;
	}
	
	public Set<QueryGraphNode> getSatisfiedNodes() {
		return satisfied;
	}
	
	public Plan getPlan() {
		return plan;
	}
	
}