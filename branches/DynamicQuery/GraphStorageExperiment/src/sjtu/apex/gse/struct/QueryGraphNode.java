package sjtu.apex.gse.struct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.hash.HashFunction;

public abstract class QueryGraphNode {
	
	static int serialCounter = 1;
	
	Set<QueryGraphEdge> inEdge = new HashSet<QueryGraphEdge>();
	Set<QueryGraphEdge> outEdge = new HashSet<QueryGraphEdge>();
	
	int serialNo;
	QueryGraphNode ancestor;
	
	QueryGraphNode(int serialNo, QueryGraphNode ancestor) {
		this.serialNo = serialNo;
		this.ancestor = ancestor;
	}

	/**
	 * Get the label of this node 
	 */
	abstract public int getLabel();
	
	/**
	 * Get the hashed label
	 * @return
	 */
	abstract public int getHashLabel(HashFunction hash);
	
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
	 * Get the node from which this node is cloned
	 */
	public QueryGraphNode getAncestor() {
		if (ancestor == null)
			return this;
		else
			return ancestor;
	}
	
	/**
	 * Get the clone of a node with no edge
	 */
	protected abstract QueryGraphNode clone();
	
	/**
	 * Get the clone of a node with no edge and set the constraint empty
	 */
	abstract QueryGraphNode getGeneralClone();
	
	/**
	 * Get the clone of a node with no edge and set the constraint to be hashed
	 */
	abstract QueryGraphNode getHashClone(HashFunction hf);
	
	/**
	 * Tell whether this node is without constraint
	 */
	public abstract boolean isGeneral();
	
	/**
	 * Tell whether this node is a generalization of its ancestor
	 */
	public abstract boolean isGeneralized();
	
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
		if (n instanceof QueryGraphNode)
			return ((QueryGraphNode)n).serialNo == serialNo;
		else
			return false;
	}
}
