package sjtu.apex.gse.struct;

/**
 * 
 * @author Tian Yuan
 * 
 */
public class GeneralQueryGraphNode extends QueryGraphNode {
	
	public GeneralQueryGraphNode() {
		this(serialCounter++, null);
	}
	
	GeneralQueryGraphNode(int serialNo, QueryGraphNode ancestor) {
		super(serialNo, ancestor);
	}

	@Override
	public String getLabel() {
		return "*";
	}
	
	/**
	 * Get the clone of a node with no edge
	 */
	public QueryGraphNode clone() {
		return new GeneralQueryGraphNode(serialNo, this.getAncestor());
	}
	
	/**
	 * Get the clone of a node with no edge and set the constraint empty
	 * @param isEmpty The emptiness property
	 */
	public QueryGraphNode getGeneralClone() {
		return clone();
	}

	@Override
	public boolean isGeneral() {
		return true;
	}
	

}
