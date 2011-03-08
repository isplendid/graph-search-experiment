package sjtu.apex.gse.struct;

import sjtu.apex.gse.hash.HashFunction;

public class HashQueryGraphNode extends QueryGraphNode {
	int label;
	
	public HashQueryGraphNode(int label) {
		this(serialCounter++, null, label);
	}

	HashQueryGraphNode(int serialNo, QueryGraphNode ancestor, int label) {
		super(serialNo, ancestor);
		this.label = label;
	}

	@Override
	protected QueryGraphNode clone() {
		return new HashQueryGraphNode(serialNo, this.getAncestor(), label);
	}

	@Override
	QueryGraphNode getGeneralClone() {
		return new GeneralQueryGraphNode(serialNo, this.getAncestor());
	}

	@Override
	public int getLabel() {
		return label;
	}

	@Override
	public boolean isGeneral() {
		return false;
	}

	@Override
	public int getHashLabel(HashFunction hash) {
		return label;
	}

	@Override
	public boolean isGeneralized() {
		if (ancestor instanceof ConcreteQueryGraphNode)
			return true;
		else
			return false;
	}

	@Override
	QueryGraphNode getHashClone(HashFunction hf) {
		return clone();
	}

	public String toString() {
		return "HN[" + serialNo + ",'" + label + "']";
	}
}
