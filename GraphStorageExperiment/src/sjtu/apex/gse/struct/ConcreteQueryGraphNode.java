package sjtu.apex.gse.struct;


/**
 * A node in query graph
 * @author Tian Yuan
 *
 */
public class ConcreteQueryGraphNode extends QueryGraphNode {
	
	String label;
	
	/**
	 * Create a node with the specified label
	 * @param label
	 */
	public ConcreteQueryGraphNode(String label) {
		this(label, serialCounter ++, null);
	}
	
	/**
	 * Create a node with the specified serial number and label
	 * @param label The label of the node
	 * @param serialNo The serial number of this node. Serial numbers
	 *   distinguish one node from another. Two instances with the same
	 *   serial number are viewed as the same node.  
	 */
	ConcreteQueryGraphNode(String label, int serialNo, QueryGraphNode ancestor) {
		super(serialNo, ancestor);
		this.label = label;
	}
	
	/**
	 * Get the label of this node 
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Get the clone of a node with no edge
	 */
	public QueryGraphNode clone() {
		return new ConcreteQueryGraphNode(label, serialNo, this.getAncestor());
	}
	
	/**
	 * Get the clone of a node with no edge and set the constraint empty
	 * @param isEmpty The emptiness property
	 */
	public QueryGraphNode getGeneralClone() {
		return new GeneralQueryGraphNode(serialNo, this.getAncestor());
	}

	@Override
	public boolean isGeneral() {
		return false;
	}
	
}
