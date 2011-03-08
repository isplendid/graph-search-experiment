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
	
	/**
	 * 
	 * @param plan
	 * @param contained
	 * @param lastSatisfied
	 * @param joinedPattern
	 */
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
	
	/**
	 * Get the set of edges covered in the current DP state
	 * 
	 * @return The set of edges
	 */
	public Set<QueryGraphEdge> getCoveredEdges() {
		return edges;
	}
	
	/**
	 * Get the set of nodes covered in the current DP state
	 * 
	 * @return The set of nodes
	 */
	public Set<QueryGraphNode> getCoveredNodes() {
		return nodes;
	}
	
	/**
	 * Get the set of patterns that are used to answer the pattern of this node
	 * 
	 * @return The set of patterns.
	 */
	public Set<PatternInfo> getContainedPatterns() {
		return patterns;
	}
	
	/**
	 * Get the set of nodes whose conditions are compeletely satisfied
	 * 
	 * @return The set of nodes
	 */
	public Set<QueryGraphNode> getSatisfiedNodes() {
		return satisfied;
	}
	
	public Plan getPlan() {
		return plan;
	}
	
}