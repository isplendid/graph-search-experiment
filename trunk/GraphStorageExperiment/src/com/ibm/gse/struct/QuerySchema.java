package com.ibm.gse.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The schema of a query containing graph graph and other information about
 * the query
 * @author Tian Yuan
 *
 */
public class QuerySchema {
	QueryGraph graph;
	List<QueryGraphNode> seln;
	Map<QueryGraphNode, Integer> nodeID = new HashMap<QueryGraphNode, Integer>();
	
	/**
	 * Create a schema that related to the given graph and the columns of results
	 * are in the given order
	 * @param graph The given graph
	 * @param seln The order of result columns
	 */
	public QuerySchema(QueryGraph graph, List<QueryGraphNode> seln) {
		this.graph = graph;
		this.seln = seln;
		for (int i = 0; i < seln.size(); i++)
			nodeID.put(seln.get(i), i);
	}
	
	/**
	 * Create a schema that related to the given graph and the columns of results
	 * contains nodes in the given set
	 * @param graph The given graph
	 * @param seln The set of result columns
	 */
	public QuerySchema(QueryGraph graph, Set<QueryGraphNode> seln) {
		int i = 0;

		this.graph = graph;
		this.seln = new ArrayList<QueryGraphNode>();
		for (QueryGraphNode n : seln) {
			this.seln.add(n);
			nodeID.put(n, i++);
		}
	}
	
	/**
	 * Get the number of node in query graph selected as output
	 */
	public int getSelectedNodeCount() {
		return seln.size();
	}
	
	/**
	 * Get the idx-th selected node
	 */
	public QueryGraphNode getSelectedNode(int idx) {
		return seln.get(idx);
	}
	
	/**
	 * Get the index of a given node in this schema
	 * @param n The given node
	 * @return The index of the given node
	 */
	public Integer getNodeID(QueryGraphNode n) {
		return nodeID.get(n);
	}
	
	public Set<QueryGraphNode> getSelectedNodeSet() {
		return new HashSet<QueryGraphNode>(seln);
	}
	
	/**
	 * Get if the given node exists in the query graph or not
	 */
	public boolean hasNode(QueryGraphNode n) {
		return nodeID.containsKey(n);
	}
	
	/**
	 * Get the query graph corresponding to this schema
	 */
	public QueryGraph getQueryGraph() {
		return graph;
	}
}
