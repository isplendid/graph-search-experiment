package com.ibm.gse.struct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A node in query graph
 * @author Tian Yuan
 *
 */
public class QueryGraphNode {
	static int serialCounter = 1;
	
	String label;
	Set<QueryGraphEdge> inEdge = new HashSet<QueryGraphEdge>();
	Set<QueryGraphEdge> outEdge = new HashSet<QueryGraphEdge>();
	int serialNo;
	
	/**
	 * Create a node with the specified label
	 * @param label
	 */
	public QueryGraphNode(String label) {
		this(label, serialCounter ++);
	}
	
	/**
	 * Create a node with the specified serial number and label
	 * @param label The label of the node
	 * @param serialNo The serial number of this node. Serial numbers
	 *   distinguish one node from another. Two instances with the same
	 *   serial number are viewed as the same node.  
	 */
	QueryGraphNode(String label, int serialNo) {
		this.label = label;
		this.serialNo = serialNo;
	}
	
	/**
	 * Get the label of this node 
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Get the in-degree of this node 
	 */
	public int getInDegree() {
		return inEdge.size();
	}
	
	/**
	 * Get the out-degree of this node 
	 */
	public int getOutDegree() {
		return outEdge.size();
	}
	
	/**
	 * Get the total degree of this node 
	 * @return
	 */
	public int getDegree() {
		return inEdge.size() + outEdge.size();
	}
	
	/**
	 * Get all connectivity information of a node
	 * @param idx
	 * @return
	 */
	public List<Connectivity> getConnectivities() {
		List<Connectivity> res = new ArrayList<Connectivity>();
		
		for (QueryGraphEdge e : inEdge)
			res.add(new Connectivity(e, e.from));
		for (QueryGraphEdge e : outEdge)
			res.add(new Connectivity(e, e.to));
		
		return res;
	}
	
	/**
	 * Get the clone of a node with no edge
	 */
	public QueryGraphNode clone() {
		return new QueryGraphNode(label, serialNo);
	}
	
	/**
	 * Hash according to the node's serial number
	 */
	public int hashCode() {
		return serialNo;
	}
	
	/**
	 * Judge if two instances represent the same node
	 */
	public boolean equals(Object n) {
//		System.out.println(n + " THIS " + this);
		if (n instanceof QueryGraphNode)
			return ((QueryGraphNode)n).serialNo == serialNo;
		else
			return false;
	}
}
