package sjtu.apex.gse.exp.sgm.ext.cmp;

import java.util.Comparator;
import java.util.Set;

import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.QueryGraphNode;

public class QueryEdgeComparator implements Comparator<Connectivity> {
	
	private Set<QueryGraphNode> usedNodes;
	
	public QueryEdgeComparator(Set<QueryGraphNode> usedNodes) {
		this.usedNodes = usedNodes;  
	}

	@Override
	public int compare(Connectivity arg0, Connectivity arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
