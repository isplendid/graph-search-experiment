package sjtu.apex.gse.exp.sgm.ext.cmp;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.QueryGraphNode;

public class QueryNodeComparator implements Comparator<QueryGraphNode>{
	
	private Set<QueryGraphNode> usedNodes;
	
	public QueryNodeComparator(Set<QueryGraphNode> usedNodes) {
		this.usedNodes = usedNodes;
	}
	
	@Override
	public int compare(QueryGraphNode n0, QueryGraphNode n1) {
		int cmp = 0;
		
		usedNodes.add(n0);
		usedNodes.add(n1);
		
		if (n0.getLabel() != n1.getLabel()) {
			cmp = n0.getLabel() - n1.getLabel();
		} else {
			List<Connectivity> c0 = n0.getConnectivities();
			List<Connectivity> c1 = n1.getConnectivities();
			
			if (c0.size() != c1.size()) {
				cmp = c0.size() - c1.size();
			}
			else {
	
				Collections.sort(c0, new QueryEdgeComparator(usedNodes));
				Collections.sort(c1, new QueryEdgeComparator(usedNodes));
				
				int p0 = 0, p1 = 0;
				
				while (p0 >= 0 && p1 >= 0) {
					int div = c0.get(p0).getEdge().getLabel() - c1.get(p1).getEdge().getLabel();
					
					if (div < 0)
						cmp = -1;
				}
			}
		}
		
		usedNodes.remove(n0);
		usedNodes.remove(n1);
		
		return cmp;
	}

}
