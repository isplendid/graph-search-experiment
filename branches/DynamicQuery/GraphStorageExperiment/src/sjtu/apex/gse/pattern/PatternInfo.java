package sjtu.apex.gse.pattern;

import java.util.Set;

import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;


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
	private Set<QueryGraphNode> satisfiedNode;
	private int insCnt;
	
	/**
	 * Create a new PatternInfo
	 * @param graph - The structure representation of the query graph
	 * @param patternStr - The string representation of the query graph
	 * @param insCnt - The number of bindings will possibly be returned
	 */
	public PatternInfo(QueryGraph graph, String patternStr, int insCnt) {
		this.satisfiedNode = graph.getSatisfiedNodeSet();
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
	 * Get the nodes that satisfy their constraints
	 */
	public Set<QueryGraphNode> getSatisfiedNodes() {
		return satisfiedNode;
	}
	
	/**
	 * Get the number of bindings under this pattern 
	 * @return The number of bindings if the pattern is in the index. Otherwise, -1.
	 */
	public int getInstanceCount() {
		return insCnt;
	}
	
	public String toString() {
		return patternStr;
	}
}
