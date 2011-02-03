package sjtu.apex.gse.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;

class SubpatternSet {
	Map<String, List<Elem>> map;
	
	public SubpatternSet() {
		map = new HashMap<String, List<Elem>>();
	}
	
	public void add(String ps, Set<QueryGraphEdge> ce, Set<QueryGraphNode> cn) {
		List<Elem> el;
		
		if (map.containsKey(ps))
			el = map.get(ps);
		else {
			el = new ArrayList<Elem>();
			map.put(ps, el);
		}
		el.add(new Elem(ps, ce, cn));
	}
	
	public boolean contains(String ps, Set<QueryGraphEdge> ce, Set<QueryGraphNode> cn) {
		if (map.containsKey(ps)) {
			List<Elem> el = map.get(ps);
			
			for (Elem e : el) {
				
				if (e.ce.containsAll(ce) && 
						e.cn.containsAll(cn))
					return true;
			}
			
			return false;
		}
		else
			return false;
	}
	
	private class Elem {
		String ps;
		Set<QueryGraphEdge> ce; //Edge covered
		Set<QueryGraphNode> cn; //Node covered
		
		Elem(String ps, Set<QueryGraphEdge> ce, Set<QueryGraphNode> cn) {
			this.ps = ps;
			this.ce = ce;
			this.cn = cn;
		}
	}
}
