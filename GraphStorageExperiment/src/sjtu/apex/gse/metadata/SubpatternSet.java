package sjtu.apex.gse.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.pattern.PatternInfo;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;

class SubpatternSet {
	Map<String, List<PatternInfo>> map;
	
	public SubpatternSet() {
		map = new HashMap<String, List<PatternInfo>>();
	}
	
	public void add(PatternInfo pi) {
		List<PatternInfo> el;
		String ps = pi.getPatternString();
		
		if (map.containsKey(ps))
			el = map.get(ps);
		else {
			el = new ArrayList<PatternInfo>();
			map.put(ps, el);
		}
		el.add(pi);
	}
	
	public boolean contains(String ps, Set<QueryGraphEdge> ce, Set<QueryGraphNode> cn) {
		return get(ps, ce, cn) != null;
	}
	
	public PatternInfo get(String ps, Set<QueryGraphEdge> ce, Set<QueryGraphNode> cn) {
		if (map.containsKey(ps)) {
			List<PatternInfo> el = map.get(ps);
			
			for (PatternInfo e : el) {
				Set<QueryGraphEdge> coveredEdge = e.getCoveredEdges();
				Set<QueryGraphNode> coveredNode = e.getCoveredNodes();
				if ((e.getPatternString().equals(ps)) && coveredEdge.containsAll(ce) && coveredNode.containsAll(cn))
					return e;
			}
			
			return null;
		}
		else
			return null;
	}
}
