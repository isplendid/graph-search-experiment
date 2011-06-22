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
		int cmp;
		boolean u0 = usedNodes.contains(arg0.getNode());
		boolean u1 = usedNodes.contains(arg1.getNode());
		
		if (u0 ^ u1) {
			if (u0)
				cmp = 1;
			else
				cmp = -1;
		}
		else {
			cmp = arg0.getEdge().getLabel() - arg1.getEdge().getLabel();
			
			if (cmp == 0)
				cmp = new QueryNodeComparator(usedNodes).compare(arg0.getNode(), arg1.getNode());
		}
		
		return cmp;
	}
	
}
