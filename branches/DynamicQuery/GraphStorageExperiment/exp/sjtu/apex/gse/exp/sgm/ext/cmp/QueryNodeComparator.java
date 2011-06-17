package sjtu.apex.gse.exp.sgm.ext.cmp;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.QueryGraphNode;

public class QueryNodeComparator implements Comparator<QueryGraphNode>{
	
	private static int compareNodes(QueryGraphNode n0, QueryGraphNode n1, Set<QueryGraphNode> usedNode) {
		if (n0.getLabel() != n1.getLabel()) {
			return n0.getLabel() - n1.getLabel();
		} else {
			List<Connectivity> c0 = n0.getConnectivities();
			List<Connectivity> c1 = n1.getConnectivities();
			
			if (c0.size() != c1.size())
				return c0.size() - c1.size();

			Collections.sort(c0, new QueryEdgeComparator(usedNode));
			Collections.sort(c1, new QueryEdgeComparator(usedNode));
			
			int p0 = 0, p1 = 0;
			
			while (p0 >= 0 && p1 >= 0) {
				int div = c0.get(p0).getEdge().getLabel() - c1.get(p1).getEdge().getLabel();
				
				if (div < 0)
					return -1;
			}
		}
		return 0;
	}

	@Override
	public int compare(QueryGraphNode arg0, QueryGraphNode arg1) {
		Set<QueryGraphNode> usedNode = new HashSet<QueryGraphNode>();
		usedNode.add(arg0);
		usedNode.add(arg1);
		return compareNodes(arg0, arg1, usedNode);
	}

}
