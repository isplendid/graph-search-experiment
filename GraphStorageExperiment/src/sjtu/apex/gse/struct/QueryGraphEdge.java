package sjtu.apex.gse.struct;

import java.util.Map;

/**
 * An edge in query graph
 * @author Tian Yuan
 *
 */
public class QueryGraphEdge {
	static int serialCnt = 0;
	
	QueryGraphNode from, to;
	int label;
	int serialNo = 0;
	
	QueryGraphEdge(QueryGraphNode from, QueryGraphNode to, int label) {
		this(from, to, label, serialCnt++);
	}
	
	QueryGraphEdge(QueryGraphNode from, QueryGraphNode to, int label, int sn) {
		this.from = from;
		this.to = to;
		this.label = label;
		this.serialNo = sn;
		from.outEdge.add(this);
		to.inEdge.add(this);		
	}
	
	/**
	 * Get the label of this edge
	 */
	public int getLabel() {
		return label;
	}
	
	/**
	 * Get the node where this edge is from
	 */
	public QueryGraphNode getNodeFrom() {
		return from;
	}
	
	/**
	 * Get the node which this edge points to 
	 */
	public QueryGraphNode getNodeTo() {
		return to;
	}
	
	/**
	 * Clone this edge in graph G to generate an edge in graph G' according to 
	 * the specified mapping
	 * @param map The mapping of nodes from G to G' 
	 * @return The clone of this edge if the mapping contains both adjacent nodes, <b>null</b> if not 
	 */
	public QueryGraphEdge clone(Map<QueryGraphNode, QueryGraphNode> map) {
		QueryGraphNode from = map.get(this.from);
		QueryGraphNode to = map.get(this.to);
		
		if (from != null && to != null)
			return new QueryGraphEdge(from, to, label, this.serialNo);
		else
			return null;
	}
	
	/**
	 * Return the serial number of this edge as its hash code
	 */
	public int hashCode() {
		return serialNo;
	}
	
	/**
	 * Judge whether two instances of edge represent the same edge
	 */
	public boolean equals(Object e) {
		return ((e instanceof QueryGraphEdge) && (e.hashCode() == hashCode()));
	}
	
	public String toString() {
		return from.toString() + "->" + to.toString();
	}
}
